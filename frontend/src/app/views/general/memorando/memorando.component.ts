import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonDirective, CardBodyComponent, CardComponent, ContainerComponent, Tabs2Module } from '@coreui/angular';
import { SmartPaginationComponent } from '@coreui/angular-pro';
import { Subject, debounceTime, distinctUntilChanged, finalize } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ToastrService } from '../../../services/toast.service';
import { ErrorService } from '../../../services/error.service';
import { MemorandoService } from '../../../services/memorando.service';
import { MemorandoTableComponent } from '../../../../components/table/memorando-table/memorando-table.component';
import { MemorandoFormComponent } from '../../../../components/forms/memorando/memorando-form/memorando-form.component';
import { NewMemorandoModalComponent } from '../../../../components/modal/memorando/new-memorando-modal/new-memorando-modal.component';
import { Memorando, MemorandoGroup, MemorandoList, MemorandoStatus, MemorandoSummary, NewMemorando } from '../../../interface/memorando.interface';
import { OrderInfo } from '../../../interface/erp.interface';
import { BackNavigationService } from '../../../services/back-navigation.service';

@Component({
  selector: 'app-memorando',
  imports: [
    ContainerComponent,
    ButtonDirective,
    CardComponent,
    CardBodyComponent,
    Tabs2Module,
    SmartPaginationComponent,
    MemorandoTableComponent,
    NewMemorandoModalComponent,
    MemorandoFormComponent,
  ],
  templateUrl: './memorando.component.html',
  styleUrl: './memorando.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MemorandoComponent implements OnInit {
  private readonly destroyRef = inject(DestroyRef);
  private readonly searchSubject = new Subject<string>();

  protected activeItemKey = 0;
  protected newMemorandoTab = false;
  protected showMemorandoModal = false;
  protected memorandos: Array<MemorandoList> = [];
  protected summary: MemorandoSummary = { total: 0, active: 0, approved: 0, canceled: 0, draft: 0 };
  protected itemsToCreateMemorando: Array<OrderInfo> = [];
  protected loadingTable = false;
  protected currentPage = 1;
  protected totalPages = 1;
  protected totalItems = 0;
  protected currentSearch = '';
  protected fullText = false;
  protected statusFilter?: MemorandoStatus;
  private formHistoryActive = false;
  private historyCloseTargetTab = 0;
  private itemsPerPage = 10;
  protected currentSort?: { column: string; state: 'asc' | 'desc' };

  constructor(
    private memorandoService: MemorandoService,
    private toasterService: ToastrService,
    private errorService: ErrorService,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private backNav: BackNavigationService,
  ) {
    this.searchSubject.pipe(debounceTime(300), distinctUntilChanged(), takeUntilDestroyed(this.destroyRef)).subscribe(value => {
      this.currentSearch = value;
      this.currentPage = 1;
      this.loadMemorandos();
    });
  }

  public ngOnInit(): void {
    this.loadSummary();
    this.loadMemorandos();
  }

  protected onTabChange(key: string | number | undefined): void {
    if (key === undefined) return;
    const parsedKey = Number(key);
    if (Number.isNaN(parsedKey)) return;

    if (parsedKey === this.activeItemKey && !this.newMemorandoTab) return;

    if (parsedKey <= 1 && this.newMemorandoTab) {
      this.closeFormTab(parsedKey);
      return;
    }

    if (parsedKey <= 1) {
      this.activeItemKey = parsedKey;
      this.statusFilter = undefined;
      this.currentPage = 1;
      this.loadMemorandos();
      return;
    }

    this.activeItemKey = parsedKey;
  }

  protected resetTableFilters(): void {
    if (this.newMemorandoTab) {
      this.backNav.runAfterOverlayClose(() => this.resetTableFilters());
      this.closeFormTab(0);
      return;
    }

    this.activeItemKey = 0;
    this.statusFilter = undefined;
    this.currentSearch = '';
    this.fullText = false;
    this.currentSort = undefined;
    this.currentPage = 1;
    this.loadMemorandos();
  }

  protected filterByStatus(status?: MemorandoStatus): void {
    if (this.newMemorandoTab) {
      this.backNav.runAfterOverlayClose(() => this.applyStatusFilter(status));
      this.closeFormTab(0);
      return;
    }

    this.applyStatusFilter(status);
  }

  private applyStatusFilter(status?: MemorandoStatus): void {
    if (!this.newMemorandoTab && this.activeItemKey === 0 && this.statusFilter === status) return;

    this.newMemorandoTab = false;
    this.activeItemKey = 0;
    this.statusFilter = status;
    this.currentPage = 1;
    this.loadMemorandos();
  }

  protected toggleMemorandoModal(status: boolean): void {
    this.showMemorandoModal = status;
  }

  protected toggleNewMemorandoTab(status = !this.newMemorandoTab): void {
    if (status) {
      this.newMemorandoTab = true;
      this.activeItemKey = 2;
      this.registerFormHistory();
      return;
    }

    this.closeFormTab();
  }

  protected openCreateTab(orderNumber: number): void {
    this.memorandoService.searchOrder(orderNumber).subscribe({
      next: (data: Array<OrderInfo>) => {
        if (!data.length) {
          this.toasterService.error('Pedido não encontrado!');
          return;
        }

        this.itemsToCreateMemorando = data;
        this.backNav.runAfterOverlayClose(() => {
          this.toggleNewMemorandoTab(true);
          this.cdr.detectChanges();
        });
        this.toggleMemorandoModal(false);
        this.cdr.detectChanges();
      },
      error: () => this.toasterService.error('Erro ao pesquisar o pedido!'),
    });
  }

  protected createNewMemorando(data: NewMemorando): void {
    this.memorandoService.insert(data).subscribe({
      next: (newMemorando: Memorando) => {
        this.toasterService.success('Memorando criado com sucesso!');
        const navigate = () => this.router.navigate(['/general/memorando', newMemorando.id]);
        if (this.formHistoryActive) {
          this.backNav.runAfterOverlayClose(navigate);
          this.closeFormTab();
        } else {
          navigate();
        }
      },
      error: error => this.errorService.showError(error),
    });
  }

  protected openMemorando(id: number): void {
    this.router.navigate(['/general/memorando', id]);
  }

  protected onPageChange(page: number): void {
    if (page === this.currentPage) return;
    this.currentPage = page;
    this.loadMemorandos();
  }

  protected onSorterChange(sorter: { column?: string; state?: 'asc' | 'desc' }): void {
    const nextSort = sorter?.column && sorter?.state ? { column: sorter.column, state: sorter.state } : undefined;
    if (this.currentSort?.column === nextSort?.column && this.currentSort?.state === nextSort?.state) return;

    this.currentSort = nextSort;
    this.currentPage = 1;
    this.loadMemorandos();
  }

  protected onItemsPerPageChange(itemsNumber: number): void {
    if (itemsNumber === this.itemsPerPage) return;
    this.itemsPerPage = itemsNumber;
    this.currentPage = 1;
    this.loadMemorandos();
  }

  protected onFilterChange(value: string): void {
    this.searchSubject.next(value);
  }

  protected onFullTextChange(value: boolean): void {
    if (value === this.fullText) return;

    this.fullText = value;
    this.currentPage = 1;
    this.loadMemorandos();
  }

  protected clearTableFilters(): void {
    this.statusFilter = undefined;
    this.currentSearch = '';
    this.fullText = false;
    this.currentSort = undefined;
    this.currentPage = 1;
    this.loadMemorandos();
  }

  private loadMemorandos(): void {
    if (this.activeItemKey > 1) return;
    this.loadingTable = true;
    const group: MemorandoGroup = this.activeItemKey === 1 ? 'DRAFT' : 'PUBLISHED';

    this.memorandoService.findAll(
      this.currentPage - 1,
      this.itemsPerPage,
      group,
      this.currentSort?.column,
      this.currentSort?.state,
      this.currentSearch,
      this.statusFilter,
      this.fullText,
    ).pipe(finalize(() => {
      this.loadingTable = false;
      this.cdr.detectChanges();
    })).subscribe({
      next: result => {
        const content = result.content ?? [];
        this.memorandos = this.currentSort?.state === 'desc' ? [...content].reverse() : content;
        this.totalItems = result.totalElements ?? 0;
        this.totalPages = result.totalPages || 1;
        if (this.currentPage > this.totalPages) this.currentPage = this.totalPages;
      },
      error: () => this.toasterService.error('Erro ao carregar memorandos!'),
    });
  }

  private loadSummary(): void {
    this.memorandoService.getSummary().subscribe({
      next: summary => {
        this.summary = summary;
        this.cdr.detectChanges();
      },
      error: () => this.toasterService.error('Erro ao carregar o resumo dos memorandos!'),
    });
  }

  private registerFormHistory(): void {
    if (this.formHistoryActive) return;

    this.formHistoryActive = true;
    this.historyCloseTargetTab = 0;
    this.backNav.register(() => {
      const targetTab = this.historyCloseTargetTab;
      this.formHistoryActive = false;
      this.historyCloseTargetTab = 0;
      this.resetFormTab(targetTab);
    });
  }

  private closeFormTab(targetTab = 0): void {
    const shouldRemoveHistory = this.formHistoryActive;
    this.formHistoryActive = false;
    this.historyCloseTargetTab = targetTab;
    this.resetFormTab(targetTab);

    if (shouldRemoveHistory) {
      this.backNav.unregister();
    } else {
      this.historyCloseTargetTab = 0;
    }
  }

  private resetFormTab(targetTab = 0): void {
    this.newMemorandoTab = false;
    this.activeItemKey = targetTab;
    this.statusFilter = undefined;
    this.currentPage = 1;
    this.loadMemorandos();
    this.cdr.detectChanges();
  }
}
