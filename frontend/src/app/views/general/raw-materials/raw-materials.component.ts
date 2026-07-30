import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonCloseDirective, ButtonDirective, CardBodyComponent, CardComponent, ContainerComponent, DropdownComponent, DropdownItemDirective, DropdownItemPlainDirective, DropdownMenuDirective, DropdownToggleDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective } from '@coreui/angular';
import { SmartPaginationComponent } from '@coreui/angular-pro';
import { RawMaterialsTableComponent } from '../../../../components/table/raw-materials-table/raw-materials-table.component';
import { calculateRawMaterialUnitWeight, RawMaterialCategory, RawMaterialStockStatus, RawMaterialSummary, RawMaterialsTable, RawMaterialUserAccess, RawMaterialView } from '../../../interface/raw-materials.interface';
import { RawMaterialsService } from '../../../services/raw-materials.service';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-raw-materials',
  imports: [
    CommonModule,
    FormsModule,
    ContainerComponent,
    CardComponent,
    CardBodyComponent,
    ButtonDirective,
    DropdownComponent,
    DropdownItemDirective,
    DropdownItemPlainDirective,
    DropdownMenuDirective,
    DropdownToggleDirective,
    ModalComponent,
    ModalHeaderComponent,
    ModalTitleDirective,
    ModalBodyComponent,
    ModalFooterComponent,
    ButtonCloseDirective,
    SmartPaginationComponent,
    RawMaterialsTableComponent,
  ],
  templateUrl: './raw-materials.component.html',
  styleUrl: './raw-materials.component.scss',
})
export class RawMaterialsComponent implements OnInit {
  protected isAdmin = false;
  protected currentView: RawMaterialView = 'operator';
  protected readonly views: Array<{ value: RawMaterialView; label: string; description: string }> = [
    { value: 'admin', label: 'Administrador', description: 'Gestão completa' },
    { value: 'operator', label: 'Operador', description: 'Atualização de estoque' },
    { value: 'consultation', label: 'Consulta', description: 'Somente visualização' },
  ];

  protected data: RawMaterialsTable[] = [];
  protected categories: RawMaterialCategory[] = [];
  protected users: RawMaterialUserAccess[] = [];
  protected summary: RawMaterialSummary = { low: 0, ok: 0, high: 0 };
  protected loadingTable = true;
  protected currentPage = 1;
  protected totalPages = 1;
  protected totalItems = 0;
  protected itemsPerPage = 10;
  protected currentSearch = '';
  protected currentCategory = '';
  protected currentStatus: RawMaterialStockStatus = 'all';
  protected showInactive = false;

  protected itemModalVisible = false;
  protected stockModalVisible = false;
  protected accessModalVisible = false;
  protected editingItem: RawMaterialsTable | null = null;
  protected stockTarget: RawMaterialsTable | null = null;
  protected stockQuantity = 0;
  protected stockKg = 0;
  protected newCategoryName = '';
  protected categoryDeleteError = '';
  protected saving = false;

  private currentSort?: { column: string; state: 'asc' | 'desc' };
  private searchTimer?: ReturnType<typeof setTimeout>;

  constructor(
    private rawMaterialsService: RawMaterialsService,
    private userService: UserService,
    private cdf: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.resolveAccess();
    this.loadCategories();
    this.loadUsers();
    this.loadSummary();
    this.loadItems();
  }

  protected setView(view: RawMaterialView): void {
    if (!this.isAdmin) return;
    this.currentView = view;
    this.currentPage = 1;
    this.currentSearch = '';
    this.currentCategory = '';
    this.currentStatus = 'all';
    this.showInactive = false;
    this.loadItems();
  }

  protected filterByStatus(status: RawMaterialStockStatus): void {
    this.currentStatus = status;
    this.currentPage = 1;
    this.loadItems();
  }

  protected onFilterChange(value: string): void {
    this.currentSearch = value;
    this.currentPage = 1;
    clearTimeout(this.searchTimer);
    this.searchTimer = setTimeout(() => this.loadItems(), 300);
  }

  protected onCategoryChange(value: string): void {
    this.currentCategory = value;
    this.currentPage = 1;
    this.loadItems();
  }

  protected onStatusChange(value: RawMaterialStockStatus): void {
    this.currentStatus = value;
    this.currentPage = 1;
    this.loadItems();
  }

  protected onInactiveChange(value: boolean): void {
    this.showInactive = value;
    this.currentPage = 1;
    this.loadItems();
  }

  protected onSorterChange(sorter: any): void {
    this.currentSort = sorter?.state && sorter?.column
      ? { column: sorter.column, state: sorter.state }
      : undefined;
    this.currentPage = 1;
    this.loadItems();
  }

  protected onItemsPerPageChange(value: number): void {
    this.itemsPerPage = value;
    this.currentPage = 1;
    this.loadItems();
  }

  protected onPageChange(page: number): void {
    this.currentPage = page;
    this.loadItems();
  }

  protected clearFilters(): void {
    this.currentSearch = '';
    this.currentCategory = '';
    this.currentStatus = 'all';
    this.showInactive = false;
    this.currentSort = undefined;
    this.currentPage = 1;
    this.loadItems();
  }

  protected openItemModal(item?: RawMaterialsTable): void {
    if (!this.isAdmin && !(this.currentView === 'consultation' && item)) return;
    this.editingItem = item
      ? { ...item }
      : {
          id: 0,
          code: '',
          name: '',
          description: '',
          type: this.categories[0]?.name ?? '',
          currentStorage: 0,
          currentStorageKg: 0,
          minStorage: 0,
          minStorageKg: 0,
          maxStorage: 0,
          maxStorageKg: 0,
          length: '',
          width: '',
          weightPerMillimeter: '',
          active: true,
          updateAt: new Date().toISOString(),
          user: this.userService.getCurrentUser()?.name ?? 'Administrador',
        };
    this.itemModalVisible = true;
  }

  protected saveItem(): void {
    if (this.currentView === 'consultation' || !this.editingItem || !this.editingItem.code.trim() || !this.editingItem.name.trim() || !this.editingItem.type) return;
    this.saving = true;
    this.rawMaterialsService.saveItem(this.editingItem).subscribe(() => {
      this.saving = false;
      this.itemModalVisible = false;
      this.loadItems();
      this.loadSummary();
      this.cdf.detectChanges();
    });
  }

  protected openStockModal(item: RawMaterialsTable): void {
    if (this.currentView !== 'operator') return;
    this.stockTarget = { ...item };
    this.stockQuantity = item.currentStorage;
    this.stockKg = item.currentStorageKg;
    this.stockModalVisible = true;
  }

  protected openTableItem(item: RawMaterialsTable): void {
    if (this.currentView === 'admin' || this.currentView === 'consultation') {
      this.openItemModal(item);
      return;
    }

    if (this.currentView === 'operator') this.openStockModal(item);
  }

  protected changeStock(delta: number): void {
    if (!this.stockTarget) return;
    this.stockQuantity = Math.max(0, this.stockQuantity + delta);
    this.updateStockWeight();
  }

  protected updateStockWeight(): void {
    if (!this.stockTarget) return;
    this.stockKg = Number((this.stockQuantity * this.calculatedUnitWeight(this.stockTarget)).toFixed(2));
  }

  protected calculatedUnitWeight(item: RawMaterialsTable): number {
    return calculateRawMaterialUnitWeight(item);
  }

  protected saveStock(): void {
    if (!this.stockTarget) return;
    this.saving = true;
    const user = this.userService.getCurrentUser()?.name ?? 'Operador';
    this.rawMaterialsService.updateStock(this.stockTarget.id, this.stockQuantity, this.stockKg, user).subscribe(() => {
      this.saving = false;
      this.stockModalVisible = false;
      this.loadItems();
      this.loadSummary();
      this.cdf.detectChanges();
    });
  }

  protected addCategory(): void {
    const name = this.newCategoryName.trim();
    if (!name || this.categories.some(category => category.name.toLocaleLowerCase('pt-BR') === name.toLocaleLowerCase('pt-BR'))) return;
    this.rawMaterialsService.addCategory(name).subscribe(category => {
      this.categories = [...this.categories, category];
      this.newCategoryName = '';
      this.categoryDeleteError = '';
      this.cdf.detectChanges();
    });
  }

  protected deleteCategory(category: RawMaterialCategory): void {
    this.rawMaterialsService.deleteCategory(category.id).subscribe(deleted => {
      if (!deleted) {
        this.categoryDeleteError = `A categoria “${category.name}” possui itens vinculados e não pode ser apagada.`;
        this.cdf.detectChanges();
        return;
      }

      this.categories = this.categories.filter(current => current.id !== category.id);
      this.users = this.users.map(user => ({
        ...user,
        categoryIds: user.categoryIds.filter(categoryId => categoryId !== category.id),
      }));
      this.categoryDeleteError = '';
      this.cdf.detectChanges();
    });
  }

  protected userHasCategory(user: RawMaterialUserAccess, categoryId: number): boolean {
    return user.categoryIds.includes(categoryId);
  }

  protected toggleUserCategory(user: RawMaterialUserAccess, categoryId: number): void {
    user.categoryIds = user.categoryIds.includes(categoryId)
      ? user.categoryIds.filter(id => id !== categoryId)
      : [...user.categoryIds, categoryId];
  }

  protected saveUserAccess(): void {
    this.saving = true;
    this.rawMaterialsService.updateUserAccess(this.users).subscribe(() => {
      this.saving = false;
      this.accessModalVisible = false;
      this.cdf.detectChanges();
    });
  }

  protected viewLabel(): string {
    return this.views.find(view => view.value === this.currentView)?.label ?? '';
  }

  protected viewDescription(): string {
    return this.views.find(view => view.value === this.currentView)?.description ?? '';
  }

  protected categoryNames(user: RawMaterialUserAccess): string {
    if (user.categoryIds.length === this.categories.length) return 'Acesso a todas as categorias';
    return this.categories
      .filter(category => user.categoryIds.includes(category.id))
      .map(category => category.name)
      .join(', ') || 'Sem categorias';
  }

  private resolveAccess(): void {
    const roles = this.userService.getCurrentUser()?.roles.map(role => role.authority) ?? [];
    this.isAdmin = roles.length === 0 || roles.includes('ROLE_ADMIN') || roles.includes('ROLE_RAW_MATERIALS_ADMIN');
    if (this.isAdmin) this.currentView = 'admin';
    else if (roles.includes('ROLE_RAW_MATERIALS_CONSULTATION')) this.currentView = 'consultation';
    else this.currentView = 'operator';
  }

  private loadItems(): void {
    this.loadingTable = true;
    this.rawMaterialsService.query({
      page: this.currentPage - 1,
      size: this.itemsPerPage,
      search: this.currentSearch,
      category: this.currentCategory,
      status: this.currentStatus,
      inactive: this.showInactive,
      sortColumn: this.currentSort?.column,
      sortDirection: this.currentSort?.state,
    }).subscribe(result => {
      this.data = result.content;
      this.totalItems = result.totalElements;
      this.totalPages = Math.ceil(this.totalItems / this.itemsPerPage) || 1;
      if (this.currentPage > this.totalPages) this.currentPage = this.totalPages;
      this.loadingTable = false;
      this.cdf.detectChanges();
    });
  }

  private loadSummary(): void {
    this.rawMaterialsService.getSummary().subscribe(summary => {
      this.summary = summary;
      this.cdf.detectChanges();
    });
  }

  private loadCategories(): void {
    this.rawMaterialsService.getCategories().subscribe(categories => {
      this.categories = categories;
      this.cdf.detectChanges();
    });
  }

  private loadUsers(): void {
    this.rawMaterialsService.getUsers().subscribe(users => {
      this.users = users;
      this.cdf.detectChanges();
    });
  }
}
