import { Component, OnInit, ViewChild } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { CardBodyComponent, CardComponent, ContainerComponent } from '@coreui/angular';
import { IItem } from '@coreui/angular-pro';
import { StepFlowTableComponent } from './../../../../components/table/step-flow-table/step-flow-table.component';
import { StepFlowOffcanvasComponent } from '../../../../components/offcanvas/step-flow-offcanvas/step-flow-offcanvas.component';

@Component({
  selector: 'app-step-flow',
  imports: [
    ContainerComponent,
    CardComponent,
    CardBodyComponent,
    StepFlowTableComponent,
    StepFlowOffcanvasComponent
  ],
  templateUrl: './step-flow.component.html',
  styleUrl: './step-flow.component.scss',
})
export class StepFlowComponent implements OnInit {
  @ViewChild('stepFlowOffcanvas')stepFlowOffcanvas!: StepFlowOffcanvasComponent;
  protected data!: Array<IItem>;
  protected resume!: Array<any>;
  protected steps!: Array<any>;

  constructor(private sanitizer: DomSanitizer) {}

  public ngOnInit(): void {
    this.resume = [
      {
        id: 1,
        title: "Total de pedidos",
        value: 23,
        description: "todos os registros",
        color: "#314158",
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#314158" stroke-width="1.8"><circle cx="8" cy="21" r="1"></circle><circle cx="19" cy="21" r="1"></circle><path d="M2.05 2.05h2l2.66 12.42a2 2 0 0 0 2 1.58h9.78a2 2 0 0 0 1.95-1.57l1.65-7.43H5.12"></path></svg>`),
      },
      {
        id: 2,
        title: "Em andamento",
        value: 9,
        description: "em processamento",
        color: "#0369dd",
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#0369dd" stroke-width="1.8"><path d="M21 12a9 9 0 1 1-6.219-8.56"></path></svg>`),
      },
      {
        id: 3,
        title: "Concluídos",
        value: 12,
        description: "entregues com sucesso",
        color: "#009966",
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#009966" stroke-width="1.8"><path d="M18 6 7 17l-5-5"></path><path d="m22 10-7.5 7.5L13 16"></path></svg>`),
      },
      {
        id: 4,
        title: "Atrasados",
        value: 2,
        description: "fora do prazo",
        color: "#c5232a",
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#c5232a" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M12 16h.01"></path><path d="M12 8v4"></path><path d="M15.312 2a2 2 0 0 1 1.414.586l4.688 4.688A2 2 0 0 1 22 8.688v6.624a2 2 0 0 1-.586 1.414l-4.688 4.688a2 2 0 0 1-1.414.586H8.688a2 2 0 0 1-1.414-.586l-4.688-4.688A2 2 0 0 1 2 15.312V8.688a2 2 0 0 1 .586-1.414l4.688-4.688A2 2 0 0 1 8.688 2z"></path></svg>`),
      },
    ];

    this.steps = [
      {
        id: 1,
        title: 'Montagem Final',
        description: 'Produção e montagem do produto',
        activeCount: 3,
        lateCount: 0,
        icon: 'mf'
      },
      {
        id: 2,
        title: 'PCP',
        description: 'Planejamento e controle da produção',
        activeCount: 2,
        lateCount: 1,
        icon: 'pcp'
      },
      {
        id: 3,
        title: 'Frete',
        description: 'Cotação e contratação do transporte',
        activeCount: 2,
        lateCount: 1,
        icon: 'frete'
      },
      {
        id: 4,
        title: 'Faturamento',
        description: 'Emissão de nota fiscal',
        activeCount: 2,
        lateCount: 0,
        icon: 'faturamento'
      },
      {
        id: 5,
        title: 'Expedição',
        description: 'Separação e despacho do pedido',
        activeCount: 0,
        lateCount: 0,
        icon: 'expedicao'
      },
    ];

    this.data = [
      { id: 1, order: 10424, client: 'Indústria Bertucci S.A.', initialDate: '30/06/2026', finalDate: '30/06/2026', step: 'Expedição', status: 'completed', progress: [2,2,2,2,2] },
      { id: 2, order: 10425, client: 'Confab Industrial', initialDate: '01/08/2026', finalDate: '01/08/2026', step: 'Frete', status: 'late', progress: [2,2,1,0,0] },
      { id: 3, order: 10426, client: 'Grupo Votorantim Metais', initialDate: '01/08/2026', finalDate: '01/08/2026', step: 'Expedição' , status: 'completed', progress: [2,2,2,2,2] },
      { id: 4, order: 10427, client: 'WEG Equipamentos Elétricos', initialDate: '01/07/2026', finalDate: '01/07/2026', step: 'Expedição', status: 'inProgress', progress: [2,2,2,2,1] },
      { id: 5, order: 10428, client: 'Embraer Componentes S.A.', initialDate: '21/06/2026', finalDate: '21/06/2026', step: 'Expedição' , status: 'completed', progress: [2,2,2,2,2] },
      { id: 6, order: 10429, client: 'Ferbasa Ferroligas da Bahia', initialDate: '07/06/2026', finalDate: '07/06/2026', step: 'Expedição', status: 'completed', progress: [2,2,2,2,2] },
      { id: 7, order: 10430, client: 'Companhia Siderúrgica Nacional', initialDate: '08/08/2026', finalDate: '08/08/2026', step: 'Montagem Final', status: 'late', progress: [1,0,0,0,0] },
      { id: 8, order: 10431, client: 'Petrobras Distribuidora S.A.', initialDate: '01/08/2026', finalDate: '01/08/2026', step: 'Expedição', status: 'completed', progress: [2,2,2,2,2] },
      { id: 9, order: 10432, client: 'Randon Implementos e Part.', initialDate: '01/07/2026', finalDate: '01/07/2026', step: 'Frete', status: 'inProgress', progress: [2,2,1,0,0] },
      { id: 10, order: 10433, client: 'Marcopolo S.A.', initialDate: '21/06/2026', finalDate: '21/06/2026', step: 'Expedição', status: 'completed', progress: [2,2,2,2,2] },
      { id: 11, order: 10434, client: 'Gerdau Aços Longos S.A.', initialDate: '01/06/2026', finalDate: '01/06/2026', step: 'Expedição', status: 'completed', progress: [2,2,2,2,2] },
      { id: 12, order: 10435, client: 'Metalúrgica São Jorge Ltda.', initialDate: '01/06/2026', finalDate: '01/06/2026', step: 'Montagem Final', status: 'inProgress', progress: [1,0,0,0,0] },
      { id: 13, order: 10436, client: 'Indústria Bertucci S.A.', initialDate: '30/06/2026', finalDate: '30/06/2026', step: 'Expedição', status: 'completed', progress: [2,2,2,2,2] },
      { id: 14, order: 10437, client: 'Confab Industrial', initialDate: '01/08/2026', finalDate: '01/08/2026', step: 'Frete', status: 'late', progress: [2,2,1,0,0] },
      { id: 15, order: 10438, client: 'Grupo Votorantim Metais', initialDate: '01/08/2026', finalDate: '01/08/2026', step: 'Expedição' , status: 'completed', progress: [2,2,2,2,2] },
      { id: 16, order: 10439, client: 'WEG Equipamentos Elétricos', initialDate: '01/07/2026', finalDate: '01/07/2026', step: 'Expedição', status: 'inProgress', progress: [2,2,2,2,1] },
      { id: 17, order: 10440, client: 'Embraer Componentes S.A.', initialDate: '21/06/2026', finalDate: '21/06/2026', step: 'Expedição' , status: 'completed', progress: [2,2,2,2,2] },
      { id: 18, order: 10441, client: 'Ferbasa Ferroligas da Bahia', initialDate: '07/06/2026', finalDate: '07/06/2026', step: 'Expedição', status: 'completed', progress: [2,2,2,2,2] },
      { id: 19, order: 10442, client: 'Companhia Siderúrgica Nacional', initialDate: '08/08/2026', finalDate: '08/08/2026', step: 'Montagem Final', status: 'late', progress: [1,0,0,0,0] },
      { id: 20, order: 10443, client: 'Metalúrgica São Jorge Ltda.', initialDate: '01/06/2026', finalDate: '01/06/2026', step: 'Montagem Final', status: 'inProgress', progress: [1,0,0,0,0] }
    ]
  }

  public openOrder(orderId: number) {
    this.stepFlowOffcanvas.open(orderId);
  }

}
