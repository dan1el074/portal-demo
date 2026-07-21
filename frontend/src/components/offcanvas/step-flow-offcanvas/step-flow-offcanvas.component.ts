import { ChangeDetectorRef, Component, Input, LOCALE_ID } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ToastrService } from 'ngx-toastr';
import { AccordionButtonDirective, AccordionComponent, AccordionItemComponent, ButtonCloseDirective, ButtonDirective, OffcanvasService, Tabs2Module, TemplateIdDirective } from '@coreui/angular';
import { StepFlowService } from '../../../app/services/step-flow.service';
import { StepFlowOrder, StepFlowVideo } from '../../../app/interface/step-flow.interface';
import localePt from '@angular/common/locales/pt';
import { CommonModule, NgTemplateOutlet, registerLocaleData } from '@angular/common';
import { environment } from '../../../environments/environment';
import { TruncatePipe } from '../../../app/pipes/truncate.pipe';
import { BackNavigationService } from '../../../app/services/back-navigation.service';
import { VideoModalComponent } from '../../modal/media/video-modal/video-modal.component';

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
    TruncatePipe,
    NgTemplateOutlet,
    VideoModalComponent
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
  protected showVideoModal = false;
  protected selectedVideo: (StepFlowVideo & { safeUrl: SafeResourceUrl }) | null = null;
  protected apiUrl = environment.apiUrl;

  constructor(
    private stepFlowService: StepFlowService,
    private toasterService: ToastrService,
    private cdf: ChangeDetectorRef,
    private backNav: BackNavigationService,
    private sanitizer: DomSanitizer
  ) {}

  public open(orderId: number): void {
    this.visible = true;
    this.orderId = orderId;
    this.getData();
    this.backNav.register(() => this.close());
  }

  public close(): void {
    this.visible = false;
    this.cdf.detectChanges();
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

  protected onOpenVideoModal(video: StepFlowVideo): void {
    this.selectedVideo = { ...video, safeUrl: this.sanitizer.bypassSecurityTrustResourceUrl(video.viewUrl) };
    this.showVideoModal = true;
  }

  protected onCloseVideoModal(): void {
    this.showVideoModal = false;
    this.selectedVideo = null;
  }
}
