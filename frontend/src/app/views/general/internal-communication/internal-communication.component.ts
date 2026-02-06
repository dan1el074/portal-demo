import { OrderInfo } from './../../../interface/erp.interface';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { cilPlus, cilX } from '@coreui/icons';
import { IconDirective } from '@coreui/icons-angular';
import { ToastrService } from 'ngx-toastr';
import { ButtonDirective, CardBodyComponent, CardComponent, CardTitleDirective, ColComponent, RowComponent, Tabs2Module } from '@coreui/angular';
import { CiTableComponent } from '../../../../components/table/ci-table/ci-table.component';
import { InternalCommunicationService } from './../../../services/internal-communication.service';
import { InternalCommunication, NewInternalCommunication } from '../../../interface/internal-communication.interface';
import { NewCiModalComponent } from '../../../../components/modal/new-ci-modal/new-ci-modal.component';
import { CiFormComponent } from './../../../../components/forms/ci-form/ci-form.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-internal-communication',
  imports: [
    IconDirective,
    RowComponent,
    ColComponent,
    ButtonDirective,
    CardComponent,
    CardBodyComponent,
    CardTitleDirective,
    Tabs2Module,
    CiTableComponent,
    NewCiModalComponent,
    CiFormComponent
  ],
  templateUrl: './internal-communication.component.html',
  styleUrl: './internal-communication.component.scss',
})
export class InternalCommunicationComponent implements OnInit {
  protected tabs: Array<string> = ['Todos', 'Desativados'];
  protected icons = { cilPlus, cilX };
  protected activeItemKey = 0;
  protected newCITab = false;
  protected showCIModal = false;
  protected allCIs: Array<InternalCommunication> = [];
  protected publishedCI: Array<InternalCommunication> = [];
  protected canceledCI: Array<InternalCommunication> = [];
  protected savedCI: Array<InternalCommunication> = [];
  protected itemsToCreateCI: Array<OrderInfo> = [];

  constructor(
    private internalCommunicationService: InternalCommunicationService,
    private toasterService: ToastrService,
    private cdr: ChangeDetectorRef,
    private route: Router
  ) {}

  public ngOnInit(): void {
    this.internalCommunicationService.findAll().subscribe({
      next: (data: Array<InternalCommunication>) => {
        this.allCIs = data;
        this.updateCIs();
        this.cdr.detectChanges();
      },
      error: () => this.toasterService.error("Erro ao carregar informações!"),
    });
  }

  protected onTabChange(key: string | number | undefined): void {
    if (key === undefined) return;
    const parsedKey = Number(key);
    if (Number.isNaN(parsedKey)) return;
    this.activeItemKey = parsedKey;
  }

  protected toggleCIModal(status: boolean): void {
    this.showCIModal = status;
  }

  protected toggleNewCITab(status = !this.newCITab): void {
    this.newCITab = status;
    status ? this.activeItemKey = 3 : this.activeItemKey = 0;
  }

  protected openCreateTab(orderNumber: number): void {
    this.internalCommunicationService.searchOrder(orderNumber).subscribe({
      next: (data: Array<OrderInfo>) => {
        if (data.length == 0) {
          this.toasterService.error('Pedido não encontrado!');
          return;
        }

        this.itemsToCreateCI = data;
        this.toggleCIModal(false);
        this.toggleNewCITab(true);
        this.cdr.detectChanges();
      },
      error: () => {
        this.toasterService.error('Erro ao pesquisar o pedido!');
      }
    });
  }

  private updateCIs(): void {
    this.publishedCI = this.allCIs.filter(ci => ci.status == "PUBLISH" || ci.status == "APPROVED");
    this.canceledCI = this.allCIs.filter(ci => ci.status == "CANCELED");
    this.savedCI = this.allCIs.filter(ci => ci.status == "CREATED");
  }

  protected createNewCI(data: NewInternalCommunication) {
    this.internalCommunicationService.insert(data).subscribe({
      next: (newCI: InternalCommunication) => {
        this.toasterService.success("CI criada com sucesso!");
        this.route.navigate(['general/internal-communication/' + newCI.id]);
      },
      error: () => this.toasterService.success("Erro ao criar CI!")
    });
  }

}
