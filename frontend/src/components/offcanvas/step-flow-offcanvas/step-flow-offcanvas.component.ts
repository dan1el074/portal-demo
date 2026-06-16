import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { AccordionButtonDirective, AccordionComponent, AccordionItemComponent, ButtonCloseDirective, ButtonDirective, OffcanvasBodyComponent, OffcanvasComponent, OffcanvasHeaderComponent, OffcanvasService, OffcanvasToggleDirective, Tabs2Module, TemplateIdDirective } from '@coreui/angular';

@Component({
  selector: 'app-step-flow-offcanvas',
  imports: [
    OffcanvasToggleDirective,
    OffcanvasComponent,
    OffcanvasHeaderComponent,
    ButtonCloseDirective,
    OffcanvasBodyComponent,
    Tabs2Module,
    ButtonDirective,
    AccordionComponent,
    AccordionItemComponent,
    TemplateIdDirective,
    AccordionButtonDirective
  ],
  templateUrl: './step-flow-offcanvas.component.html',
  styleUrl: './step-flow-offcanvas.component.scss',
})
export class StepFlowOffcanvasComponent {
  @Input() orderId!: number;
  protected visible = false;

  constructor(private offcanvasService: OffcanvasService) {}

  public open(orderId: number): void {
    this.orderId = orderId;
    this.visible = true;

    console.log("ID da ordem carregada: " + this.orderId);
    // TODO: carregar informações do pedido
  }

  public close(): void {
    this.visible = false;
  }
}
