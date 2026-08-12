import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonDirective, CardBodyComponent, CardComponent } from '@coreui/angular';
import {
  RawMaterialCategory,
  RawMaterialStockStatus,
  RawMaterialSummary,
  RawMaterialUserAccess,
  RawMaterialView,
} from '../../../app/interface/raw-materials.interface';

@Component({
  selector: 'app-raw-materials-overview',
  imports: [CommonModule, FormsModule, CardComponent, CardBodyComponent, ButtonDirective],
  templateUrl: './raw-materials-overview.component.html',
  styleUrl: './raw-materials-overview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RawMaterialsOverviewComponent {
  @Input({ required: true }) currentView!: RawMaterialView;
  @Input({ required: true }) summary!: RawMaterialSummary;
  @Input({ required: true }) categories: RawMaterialCategory[] = [];
  @Input({ required: true }) users: RawMaterialUserAccess[] = [];
  @Input() apiUrl = '';
  @Input() newCategoryName = '';
  @Input() categoryDeleteError = '';

  @Output() statusFilter = new EventEmitter<RawMaterialStockStatus>();
  @Output() newCategoryNameChange = new EventEmitter<string>();
  @Output() addCategory = new EventEmitter<void>();
  @Output() startCategoryEdit = new EventEmitter<RawMaterialCategory>();
  @Output() deleteCategory = new EventEmitter<RawMaterialCategory>();
  @Output() manageAccess = new EventEmitter<void>();

  protected categoryNames(user: RawMaterialUserAccess): string {
    if (user.categoryIds.length === this.categories.length) return 'Acesso a todas as categorias';
    return this.categories
      .filter(category => user.categoryIds.includes(category.id))
      .map(category => category.name)
      .join(', ') || 'Sem categorias';
  }

  protected useDefaultAvatar(event: Event): void {
    const image = event.target as HTMLImageElement;
    if (image.dataset['fallbackApplied']) return;
    image.dataset['fallbackApplied'] = 'true';
    image.src = 'assets/images/avatars/default.png';
  }
}
