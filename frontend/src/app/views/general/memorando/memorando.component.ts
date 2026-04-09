import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { ButtonDirective, CardBodyComponent, CardComponent, CardTitleDirective, ColComponent, RowComponent, Tabs2Module } from '@coreui/angular';
import { cilPlus, cilX } from '@coreui/icons';
import { IconDirective } from '@coreui/icons-angular';
import { ErrorService } from './../../../services/error.service';
import { MemorandoService } from './../../../services/memorando.service';
import { MemorandoTableComponent } from '../../../../components/table/memorando-table/memorando-table.component';
import { MemorandoFormComponent } from './../../../../components/forms/memorando/memorando-form/memorando-form.component';
import { NewMemorandoModalComponent } from '../../../../components/modal/new-memorando-modal/new-memorando-modal.component';
import { Memorando, MemorandoList, NewMemorando } from '../../../interface/memorando.interface';
import { OrderInfo } from './../../../interface/erp.interface';

@Component({
  selector: 'app-memorando',
  imports: [
    IconDirective,
    RowComponent,
    ColComponent,
    ButtonDirective,
    CardComponent,
    CardBodyComponent,
    CardTitleDirective,
    Tabs2Module,
    MemorandoTableComponent,
    NewMemorandoModalComponent,
    MemorandoFormComponent
  ],
  templateUrl: './memorando.component.html',
  styleUrl: './memorando.component.scss',
})
export class MemorandoComponent implements OnInit {
  protected icons = { cilPlus, cilX };
  protected activeItemKey = 0;
  protected newMemorandoTab = false;
  protected showMemorandoModal = false;
  protected allMemorandos: Array<MemorandoList> = [];
  protected publishedMemorando: Array<MemorandoList> = [];
  protected canceledMemorando: Array<MemorandoList> = [];
  protected savedMemorando: Array<MemorandoList> = [];
  protected itemsToCreateMemorando: Array<OrderInfo> = [];

  constructor(
    private memorandoService: MemorandoService,
    private toasterService: ToastrService,
    private errorService: ErrorService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) {}

  public ngOnInit(): void {
    this.memorandoService.findAll().subscribe({
      next: (data: Array<MemorandoList>) => {
        console.log(data);
        this.allMemorandos = data;
        this.updateMemorando();
        this.cdr.detectChanges();
      },
      error: () => this.toasterService.error("Erro ao carregar informações!")
    });
  }

  private updateMemorando(): void {
    this.publishedMemorando = this.allMemorandos.filter(memorando => memorando.status == "PUBLISH" || memorando.status == "APPROVED");
    this.canceledMemorando = this.allMemorandos.filter(memorando => memorando.status == "CANCELED");
    this.savedMemorando = this.allMemorandos.filter(memorando => memorando.status == "CREATED");
  }

  protected onTabChange(key: string | number | undefined): void {
    if (key === undefined) return;
    const parsedKey = Number(key);
    if (Number.isNaN(parsedKey)) return;
    this.activeItemKey = parsedKey;
  }

  protected toggleMemorandoModal(status: boolean): void {
    this.showMemorandoModal = status;
  }

  protected toggleNewMemorandoTab(status = !this.newMemorandoTab): void {
    this.newMemorandoTab = status;
    status ? this.activeItemKey = 3 : this.activeItemKey = 0;
  }

  protected openCreateTab(orderNumber: number): void {
    this.memorandoService.searchOrder(orderNumber).subscribe({
      next: (data: Array<OrderInfo>) => {
        if (data.length == 0) {
          this.toasterService.error('Pedido não encontrado!');
          return;
        }

        this.itemsToCreateMemorando = data;
        this.toggleMemorandoModal(false);
        this.toggleNewMemorandoTab(true);
        this.cdr.detectChanges();
      },
      error: () => this.toasterService.error('Erro ao pesquisar o pedido!')
    });
  }

  protected createNewMemorando(data: NewMemorando) {
    this.memorandoService.insert(data).subscribe({
      next: (newMemorando: Memorando) => {
        this.toggleNewMemorandoTab(false)
        this.cdr.detectChanges();
        this.toasterService.success("Memorando criada com sucesso!");
        this.router.navigate(['general/memorando/' + newMemorando.id]);
      },
      error: (error) => this.errorService.showError(error)
    });
  }
}
