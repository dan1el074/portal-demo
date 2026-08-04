import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonCloseDirective, ButtonDirective, CardBodyComponent, CardComponent, ContainerComponent, DropdownComponent, DropdownItemDirective, DropdownItemPlainDirective, DropdownMenuDirective, DropdownToggleDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, Tabs2Module } from '@coreui/angular';
import { SmartPaginationComponent } from '@coreui/angular-pro';
import { forkJoin } from 'rxjs';
import { RawMaterialsTableComponent } from '../../../../components/table/raw-materials-table/raw-materials-table.component';
import { calculateRawMaterialUnitWeight, formatRawMaterialDecimal, RawMaterialCategory, RawMaterialStockStatus, RawMaterialSummary, RawMaterialsTable, RawMaterialUserAccess, RawMaterialView } from '../../../interface/raw-materials.interface';
import { RawMaterialsService } from '../../../services/raw-materials.service';
import { UserService } from '../../../services/user.service';
import { environment } from '../../../../environments/environment';
import { BackNavigationService } from '../../../services/back-navigation.service';

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
    Tabs2Module,
    SmartPaginationComponent,
    RawMaterialsTableComponent,
  ],
  templateUrl: './raw-materials.component.html',
  styleUrl: './raw-materials.component.scss',
})
export class RawMaterialsComponent implements OnInit {
  protected readonly apiUrl = environment.apiUrl;
  protected isAdmin = false;
  protected canSwitchViews = false;
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
  protected activeInventoryTab = '';

  protected itemModalVisible = false;
  protected stockModalVisible = false;
  protected accessModalVisible = false;
  protected editingItem: RawMaterialsTable | null = null;
  protected stockTarget: RawMaterialsTable | null = null;
  protected stockQuantity = 0;
  protected stockKg = 0;
  protected newCategoryName = '';
  protected editingCategoryId: number | null = null;
  protected editingCategoryName = '';
  protected categoryDeleteError = '';
  protected saving = false;

  private currentSort?: { column: string; state: 'asc' | 'desc' };
  private searchTimer?: ReturnType<typeof setTimeout>;
  private formHistoryActive: 'item' | 'stock' | null = null;

  constructor(
    private rawMaterialsService: RawMaterialsService,
    private userService: UserService,
    private cdf: ChangeDetectorRef,
    private backNav: BackNavigationService,
  ) {}

  ngOnInit(): void {
    this.resolveAccess();
    this.loadSummary();
    this.loadInventoryAccess();
  }

  protected setView(view: RawMaterialView): void {
    if (!this.canSwitchViews) return;
    this.currentView = view;
    this.currentPage = 1;
    this.currentSearch = '';
    this.currentStatus = 'all';
    this.showInactive = false;
    this.selectDefaultCategory();
    this.loadItems();
  }

  protected onInventoryTabChange(key: string | number | undefined): void {
    if (key === undefined) return;
    const tab = String(key);

    if (tab === 'inactive') {
      if (!this.canAccessInactive()) return;
      this.activeInventoryTab = tab;
      this.currentCategory = '';
      this.showInactive = true;
    } else if (tab.startsWith('category-')) {
      const categoryId = Number(tab.replace('category-', ''));
      const category = this.categories.find(item => item.id === categoryId);
      if (!category || !this.canAccessCategory(category)) return;
      this.activeInventoryTab = tab;
      this.currentCategory = category.name;
      this.showInactive = false;
    } else return;

    this.currentPage = 1;
    this.loadItems();
  }

  protected canAccessCategory(category: RawMaterialCategory): boolean {
    if (this.isAdmin || this.currentView !== 'operator') return true;
    return this.currentOperatorAccess()?.categoryIds.includes(category.id) ?? false;
  }

  protected canAccessInactive(): boolean {
    return this.isAdmin;
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

  protected onStatusChange(value: RawMaterialStockStatus): void {
    this.currentStatus = value;
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
    this.currentStatus = 'all';
    this.currentSort = undefined;
    this.currentPage = 1;
    this.loadItems();
  }

  protected openItemModal(item?: RawMaterialsTable): void {
    if (!this.isAdmin && !(this.currentView === 'consultation' && item)) return;
    this.editingItem = item
      ? this.formatItemDimensions({ ...item })
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
          length: '0,000',
          width: '0,000',
          thickness: '0,000',
          weightPerSquareMeter: '0,000',
          active: true,
          updateAt: new Date().toISOString(),
          user: this.userService.getCurrentUser()?.name ?? 'Administrador',
        };
    this.itemModalVisible = true;
    this.registerFormHistory('item');
  }

  protected onItemModalVisibleChange(visible: boolean): void {
    if (!visible && this.itemModalVisible) this.closeItemModal();
  }

  protected closeItemModal(): void {
    this.itemModalVisible = false;
    this.editingItem = null;
    this.unregisterFormHistory('item');
  }

  protected formatDimensionField(field: 'length' | 'width' | 'thickness' | 'weightPerSquareMeter'): void {
    if (!this.editingItem) return;
    this.editingItem[field] = formatRawMaterialDecimal(this.editingItem[field]);
  }

  protected saveItem(): void {
    if (this.currentView === 'consultation' || !this.editingItem || this.hasInvalidStockRange() || !this.editingItem.code.trim() || !this.editingItem.name.trim() || !this.editingItem.type) return;
    this.editingItem = this.formatItemDimensions(this.editingItem);
    this.saving = true;
    this.rawMaterialsService.saveItem(this.editingItem).subscribe(() => {
      this.saving = false;
      this.closeItemModal();
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
    this.registerFormHistory('stock');
  }

  protected onStockModalVisibleChange(visible: boolean): void {
    if (!visible && this.stockModalVisible) this.closeStockModal();
  }

  protected closeStockModal(): void {
    this.stockModalVisible = false;
    this.stockTarget = null;
    this.unregisterFormHistory('stock');
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

  protected hasInvalidStockRange(): boolean {
    return !!this.editingItem && Number(this.editingItem.maxStorage) < Number(this.editingItem.minStorage);
  }

  private formatItemDimensions(item: RawMaterialsTable): RawMaterialsTable {
    return {
      ...item,
      length: formatRawMaterialDecimal(item.length),
      width: formatRawMaterialDecimal(item.width),
      thickness: formatRawMaterialDecimal(item.thickness),
      weightPerSquareMeter: formatRawMaterialDecimal(item.weightPerSquareMeter),
    };
  }

  protected saveStock(): void {
    if (!this.stockTarget) return;
    this.saving = true;
    const user = this.userService.getCurrentUser()?.name ?? 'Operador';
    this.rawMaterialsService.updateStock(this.stockTarget.id, this.stockQuantity, this.stockKg, user).subscribe(() => {
      this.saving = false;
      this.closeStockModal();
      this.loadItems();
      this.loadSummary();
      this.cdf.detectChanges();
    });
  }

  protected addCategory(): void {
    const name = this.newCategoryName.trim();
    if (!name || this.categories.some(category => category.name.toLocaleLowerCase('pt-BR') === name.toLocaleLowerCase('pt-BR'))) return;
    this.rawMaterialsService.addCategory(name).subscribe(category => {
      this.categories = this.sortCategories([...this.categories, category]);
      this.newCategoryName = '';
      this.categoryDeleteError = '';
      this.cdf.detectChanges();
    });
  }

  protected startCategoryEdit(category: RawMaterialCategory): void {
    this.editingCategoryId = category.id;
    this.editingCategoryName = category.name;
    this.categoryDeleteError = '';
  }

  protected cancelCategoryEdit(): void {
    this.editingCategoryId = null;
    this.editingCategoryName = '';
  }

  protected saveCategoryEdit(category: RawMaterialCategory): void {
    const name = this.editingCategoryName.trim();
    if (!name) return;
    this.rawMaterialsService.updateCategory(category.id, name).subscribe(updated => {
      if (!updated) {
        this.categoryDeleteError = 'Já existe uma categoria com esse nome.';
        this.cdf.detectChanges();
        return;
      }

      if (this.currentCategory === category.name) this.currentCategory = updated.name;
      this.categories = this.sortCategories(
        this.categories.map(current => current.id === updated.id ? updated : current),
      );
      this.cancelCategoryEdit();
      this.categoryDeleteError = '';
      this.loadItems();
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
      if (this.activeInventoryTab === `category-${category.id}`) {
        this.selectDefaultCategory();
        this.loadItems();
      }
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

  protected useDefaultAvatar(event: Event): void {
    const image = event.target as HTMLImageElement;
    if (image.dataset['fallbackApplied']) return;
    image.dataset['fallbackApplied'] = 'true';
    image.src = 'assets/images/avatars/default.png';
  }

  private resolveAccess(): void {
    const roles = this.userService.getCurrentUser()?.roles.map(role => role.authority) ?? [];
    this.canSwitchViews = roles.includes('ROLE_ADMIN');
    this.isAdmin = this.canSwitchViews || roles.includes('ROLE_RAW_MATERIALS_ADMIN');
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
      allowedCategories: this.allowedCategoryNames(),
      status: this.currentStatus,
      inactive: this.showInactive,
      sortColumn: this.currentSort?.column,
      sortDirection: this.currentSort?.state,
    }).subscribe(result => {
      this.data = this.currentSort?.state === 'desc'
        ? [...result.content].reverse()
        : result.content;
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

  private loadInventoryAccess(): void {
    forkJoin({
      categories: this.rawMaterialsService.getCategories(),
      users: this.rawMaterialsService.getUsers(),
    }).subscribe(({ categories, users }) => {
      this.categories = this.sortCategories(categories);
      this.users = users;
      this.selectDefaultCategory();
      this.loadItems();
      this.cdf.detectChanges();
    });
  }

  private currentOperatorAccess(): RawMaterialUserAccess | undefined {
    const currentUserId = this.userService.getCurrentUser()?.id;
    return this.users.find(user => user.id === currentUserId);
  }

  private allowedCategoryNames(): string[] | undefined {
    if (this.isAdmin || this.currentView !== 'operator') return undefined;
    const categoryIds = this.currentOperatorAccess()?.categoryIds ?? [];
    return this.categories
      .filter(category => categoryIds.includes(category.id))
      .map(category => category.name);
  }

  private selectDefaultCategory(): void {
    const category = this.categories.find(item => this.canAccessCategory(item));
    this.activeInventoryTab = category ? `category-${category.id}` : '';
    this.currentCategory = category?.name ?? '';
    this.showInactive = false;
  }

  private sortCategories(categories: RawMaterialCategory[]): RawMaterialCategory[] {
    return [...categories].sort((a, b) => a.name.localeCompare(b.name, 'pt-BR', {
      numeric: true,
      sensitivity: 'base',
    }));
  }

  private registerFormHistory(form: 'item' | 'stock'): void {
    if (this.formHistoryActive) return;
    this.formHistoryActive = form;
    this.backNav.register(() => {
      const activeForm = this.formHistoryActive;
      this.formHistoryActive = null;
      if (activeForm === 'item') {
        this.itemModalVisible = false;
        this.editingItem = null;
      } else if (activeForm === 'stock') {
        this.stockModalVisible = false;
        this.stockTarget = null;
      }
      this.cdf.detectChanges();
    });
  }

  private unregisterFormHistory(form: 'item' | 'stock'): void {
    if (this.formHistoryActive !== form) return;
    this.formHistoryActive = null;
    this.backNav.unregister();
  }
}
