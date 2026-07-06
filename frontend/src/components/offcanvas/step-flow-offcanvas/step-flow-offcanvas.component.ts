import { ChangeDetectorRef, Component, Input, LOCALE_ID } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { AccordionButtonDirective, AccordionComponent, AccordionItemComponent, ButtonCloseDirective, ButtonDirective, OffcanvasService, Tabs2Module, TemplateIdDirective } from '@coreui/angular';
import { StepFlowService } from '../../../app/services/step-flow.service';
import { StepFlowOrder } from '../../../app/interface/step-flow.interface';
import localePt from '@angular/common/locales/pt';
import { CommonModule, registerLocaleData } from '@angular/common';
import { environment } from '../../../environments/environment';
import { TruncatePipe } from '../../../app/pipes/truncate.pipe';

registerLocaleData(localePt);

@Component({
  selector: 'app-step-flow-offcanvas',
  imports: [
    CommonModule,
    ButtonCloseDirective,
    Tabs2Module,
    ButtonDirective,
    AccordionComponent,
    AccordionItemComponent,
    TemplateIdDirective,
    AccordionButtonDirective,
    TruncatePipe
  ],
  providers: [{ provide: LOCALE_ID, useValue: 'pt-BR' }],
  templateUrl: './step-flow-offcanvas.component.html',
  styleUrl: './step-flow-offcanvas.component.scss',
})
export class StepFlowOffcanvasComponent {
  @Input() isAdmin!: boolean;
  protected order: StepFlowOrder | null = null;
  protected orderId: number | null = null;
  protected visible = false;
  protected apiUrl = environment.apiUrl;

  constructor(
    private stepFlowService: StepFlowService,
    private toasterService: ToastrService,
    private cdf: ChangeDetectorRef
  ) {}

  public open(orderId: number): void {
    this.visible = true;
    this.orderId = orderId;
    this.getData();
  }

  public close(): void {
    this.visible = false;
  }

  protected getData(): void {
    this.order = null;

    if (this.orderId) {
      this.stepFlowService.findById(this.orderId).subscribe({
        next: data => {
          this.order = data;
          this.cdf.detectChanges();
        },
        error: () => {
          this.toasterService.error('Erro ao buscar informações do pedido!')
        }
      });

      return;
    }

    this.toasterService.error('O número da ordem é nulo!')
  }
}
