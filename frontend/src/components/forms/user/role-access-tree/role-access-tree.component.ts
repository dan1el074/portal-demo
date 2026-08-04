import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output } from '@angular/core';
import { RoleGroup, RoleSummary } from '../../../../app/interface/role.interface';

interface AccessLevel {
  role: RoleSummary;
  label: string;
  depth: number;
  base: boolean;
}

@Component({
  selector: 'app-role-access-tree',
  imports: [CommonModule],
  templateUrl: './role-access-tree.component.html',
  styleUrl: './role-access-tree.component.scss',
})
export class RoleAccessTreeComponent implements OnChanges {
  @Input() groups: RoleGroup[] = [];
  @Input() selectedIds: number[] = [];
  @Input() disabled = false;
  @Output() selectedIdsChange = new EventEmitter<number[]>();

  protected selection = new Set<number>();

  ngOnChanges(): void {
    this.selection = new Set((this.selectedIds ?? []).map(Number));
  }

  protected hasLevels(tool: RoleSummary): boolean {
    return Boolean(tool.childrens?.length);
  }

  protected isToolEnabled(tool: RoleSummary): boolean {
    return this.selection.has(Number(tool.id))
      || this.descendantIds(tool).some(id => this.selection.has(id));
  }

  protected levels(tool: RoleSummary): AccessLevel[] {
    const levels = this.flattenLevels(tool.childrens ?? []);
    const hasOperator = levels.some(level => this.normalize(level.label) === 'operador');

    if (!hasOperator) {
      levels.unshift({ role: tool, label: 'Operador', depth: 0, base: true });
    }

    return levels;
  }

  protected isLevelSelected(tool: RoleSummary, level: AccessLevel): boolean {
    const selectedDescendants = this.descendantIds(tool).filter(id => this.selection.has(id));

    if (selectedDescendants.length) {
      return !level.base && this.selection.has(Number(level.role.id));
    }

    if (!this.selection.has(Number(tool.id))) return false;
    if (level.base) return true;

    return this.normalize(level.label) === 'operador';
  }

  protected toggleTool(tool: RoleSummary, checked: boolean): void {
    if (this.disabled) return;

    const next = this.withoutTool(tool);
    if (checked) {
      const defaultLevel = this.levels(tool).find(level => this.normalize(level.label) === 'operador');
      next.add(Number(defaultLevel?.role.id ?? tool.id));
    }

    this.updateSelection(next);
  }

  protected selectLevel(tool: RoleSummary, level: AccessLevel): void {
    if (this.disabled) return;

    const next = this.withoutTool(tool);
    next.add(Number(level.role.id));
    this.updateSelection(next);
  }

  protected levelName(level: AccessLevel): string {
    const label = level.label.includes(' - ')
      ? level.label.substring(level.label.lastIndexOf(' - ') + 3)
      : level.label;

    return this.normalize(label) === 'admin' ? 'Administrador' : label;
  }

  private flattenLevels(roles: RoleSummary[], depth = 0): AccessLevel[] {
    return roles.flatMap(role => [
      { role, label: role.authority, depth, base: false },
      ...this.flattenLevels(role.childrens ?? [], depth + 1),
    ]);
  }

  private withoutTool(tool: RoleSummary): Set<number> {
    const next = new Set(this.selection);
    [Number(tool.id), ...this.descendantIds(tool)].forEach(id => next.delete(id));
    return next;
  }

  private descendantIds(role: RoleSummary): number[] {
    return (role.childrens ?? []).flatMap(child => [
      Number(child.id),
      ...this.descendantIds(child),
    ]);
  }

  private normalize(value: string): string {
    return value.trim().toLocaleLowerCase('pt-BR');
  }

  private updateSelection(next: Set<number>): void {
    this.selection = next;
    this.selectedIdsChange.emit([...next]);
  }
}
