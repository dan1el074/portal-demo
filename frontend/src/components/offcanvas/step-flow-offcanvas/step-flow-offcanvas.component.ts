import { ChangeDetectorRef, Component, ElementRef, Input, LOCALE_ID, ViewChild, ChangeDetectionStrategy } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ToastrService } from '@app/services/toast.service';
import { AccordionButtonDirective, AccordionComponent, AccordionItemComponent, ButtonCloseDirective, ButtonDirective, OffcanvasService, Tabs2Module, TemplateIdDirective } from '@coreui/angular';
import { StepFlowService } from '../../../app/services/step-flow.service';
import { StepFlowOrder, StepFlowVideo } from '../../../app/interface/step-flow.interface';
import localePt from '@angular/common/locales/pt';
import { CommonModule, NgTemplateOutlet, registerLocaleData } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../environments/environment';
import { TruncatePipe } from '../../../app/pipes/truncate.pipe';
import { BackNavigationService } from '../../../app/services/back-navigation.service';
import { VideoModalComponent } from '../../modal/media/video-modal/video-modal.component';
import { getSortedStepFlowMedia, StepFlowMedia, StepFlowMediaFilter } from '../../../app/interface/step-flow-media.interface';

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
    FormsModule,
    VideoModalComponent
  ],
  providers: [{ provide: LOCALE_ID, useValue: 'pt-BR' }],
  templateUrl: './step-flow-offcanvas.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './step-flow-offcanvas.component.scss',
})
export class StepFlowOffcanvasComponent {
  @ViewChild('mediaSearchInput') mediaSearchInput?: ElementRef<HTMLInputElement>;
  @Input() isAdmin!: boolean;
  @Input() showMoney!: boolean;
  protected order: StepFlowOrder | null = null;
  protected orderId: number | null = null;
  protected visible = false;
  protected showVideoModal = false;
  protected selectedVideo: (StepFlowVideo & { safeUrl: SafeResourceUrl }) | null = null;
  protected apiUrl = environment.apiUrl;
  protected mediaFilter: StepFlowMediaFilter = 'all';
  protected mediaSearch = '';
  protected mediaSearchOpen = false;

  protected get filteredMedia(): Array<StepFlowMedia> {
    return getSortedStepFlowMedia(this.order?.pictures ?? [], this.order?.videos ?? [], this.mediaFilter, this.mediaSearch);
  }

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
    this.mediaFilter = 'all';
    this.mediaSearch = '';
    this.mediaSearchOpen = false;
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

  protected onPreviewError(event: Event): void {
    (event.target as HTMLImageElement).hidden = true;
  }

  protected setMediaFilter(filter: StepFlowMediaFilter): void {
    this.mediaFilter = filter;
  }

  protected toggleMediaSearch(): void {
    if (this.mediaSearchOpen) {
      this.mediaSearchOpen = false;
      this.mediaSearch = '';
      return;
    }

    this.mediaSearchOpen = true;
    setTimeout(() => this.mediaSearchInput?.nativeElement.focus());
  }
}
