import { ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, Output, ChangeDetectionStrategy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ButtonCloseDirective, ColDirective, ModalBodyComponent, ModalComponent, ModalHeaderComponent, PlaceholderAnimationDirective, PlaceholderDirective } from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';
import { cilCursor, cilFork } from '@coreui/icons';
import { FileService } from '../../../../app/services/file.service';
import { ModalBackNavigationDirective } from '@app/directive/modal-back-navigation.directive';
import { Role } from '../../../../app/interface/role.interface';
import { getNavigationTools, NavigationTool } from '../../../../app/shared/navigation-tool';
import { BackNavigationService } from '../../../../app/services/back-navigation.service';

@Component({
  selector: 'app-layout-search-modal',
  imports: [
    IconDirective,
    ModalComponent,
    ModalBackNavigationDirective,
    ModalHeaderComponent,
    ModalBodyComponent,
    ButtonCloseDirective,
    FormsModule,
    PlaceholderDirective,
    PlaceholderAnimationDirective,
    ColDirective
  ],
  templateUrl: './layout-search-modal.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './layout-search-modal.component.scss',
})
export class LayoutSearchModalComponent implements OnChanges {
  @Input() visible!: boolean;
  @Input() roles: Array<Role> = [];
  @Output() closeModal = new EventEmitter<any>();

  protected icons = { cilCursor, cilFork };
  protected resultList: Array<string> = [];
  protected toolList: Array<NavigationTool> = [];
  protected searchInput = "";
  protected loadSeach = false;
  protected showResult = false;

  constructor(
    private fileService: FileService,
    private router: Router,
    private backNavigation: BackNavigationService,
    private cdr: ChangeDetectorRef
  ) {}

  public ngOnChanges(): void {
    if (!this.visible) {
      this.resetForm();
      return;
    }

    this.filterTools();
  }

  public onSearchChange(value: string) {
    this.searchInput = value;
    this.showResult = false;
    this.resultList = [];
    this.filterTools();
  }

  public onSubmit(value: string) {
    const term = value.trim();
    if (!term) return;

    this.showResult = true;
    this.loadSeach = true;
    this.filterTools();

    this.fileService.searchProject(term).subscribe({
      next: (value) => {
        this.resultList = value;
        this.loadSeach = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.resultList = [];
        this.loadSeach = false;
        this.cdr.detectChanges();
      }
    })
  }

  protected closeSearchModal(): void {
    this.closeModal.emit();
    this.resetForm()
  }

  protected openPdf(projectName: string): void {
    this.fileService.openProject(projectName);
  }

  protected openTool(tool: NavigationTool): void {
    this.backNavigation.runAfterOverlayClose(() => {
      void this.router.navigateByUrl(tool.url);
    });
    this.closeSearchModal();
  }

  private filterTools(): void {
    const term = this.normalize(this.searchInput);
    const tools = getNavigationTools(this.roles);

    this.toolList = tools
      .filter(tool => !term || this.normalize(`${tool.parent} ${tool.title}`).includes(term))
      .sort((a, b) => {
        const parentComparison = a.parent.localeCompare(b.parent, 'pt-BR', { sensitivity: 'base' });
        return parentComparison || a.title.localeCompare(b.title, 'pt-BR', { sensitivity: 'base' });
      });
  }

  private normalize(value: string): string {
    return value
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLocaleLowerCase('pt-BR');
  }

  private resetForm(): void {
    this.resultList = [];
    this.toolList = [];
    this.searchInput = "";
    this.loadSeach = false;
    this.showResult = false;
  }
}
