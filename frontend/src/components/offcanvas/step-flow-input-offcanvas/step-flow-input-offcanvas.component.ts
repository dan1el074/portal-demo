import { ChangeDetectorRef, Component, computed, ElementRef, EventEmitter, Input, Output, signal, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs';
import { Upload } from 'tus-js-client';
import { AccordionButtonDirective, AccordionComponent, AccordionItemComponent, ButtonCloseDirective, ButtonDirective, FormControlDirective, FormLabelDirective, FormSelectDirective, SpinnerComponent, TemplateIdDirective } from '@coreui/angular';
import { CancelStepFlowModalComponent } from '../../modal/step-flow/cancel-step-flow-modal/cancel-step-flow-modal.component';
import { VideoModalComponent } from '../../modal/media/video-modal/video-modal.component';
import { CurrencyMaskDirective } from './../../../app/directive/currency-mask.directive';
import { StepFlowVideosService } from './../../../app/services/step-flow-videos.service';
import { BackNavigationService } from '../../../app/services/back-navigation.service';
import { StepFlowService } from '../../../app/services/step-flow.service';
import { Step, StepFlowOrder, StepFlowOrderItem, StepFlowVideo, UploadingVideo, UploadedFile } from '../../../app/interface/step-flow.interface';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-step-flow-input-offcanvas',
  imports: [
    CommonModule,
    ButtonDirective,
    ButtonCloseDirective,
    AccordionComponent,
    AccordionItemComponent,
    TemplateIdDirective,
    AccordionButtonDirective,
    ReactiveFormsModule,
    FormsModule,
    FormSelectDirective,
    FormControlDirective,
    FormLabelDirective,
    CurrencyMaskDirective,
    CancelStepFlowModalComponent,
    SpinnerComponent,
    VideoModalComponent
  ],
  templateUrl: './step-flow-input-offcanvas.component.html',
  styleUrl: './step-flow-input-offcanvas.component.scss',
})
export class StepFlowInputOffcanvasComponent {
  @ViewChild('filesSection') filesSection?: ElementRef<HTMLDivElement>;
  @Input() orderId!: number;
  @Input() isAdmin!: boolean;
  @Input() steps!: Array<Step>;
  @Output() nextStepTask = new EventEmitter<number>();
  @Output() reloadOrders = new EventEmitter<void>();

  protected order: StepFlowOrder | null = null;
  protected visible = false;
  protected apiUrl: string = environment.apiUrl;
  protected form!: FormGroup;
  protected itemsForm!: FormArray;
  protected showCancelModel = false;
  protected adminStepControl = new FormControl<number | null>(null);
  protected saveLoading: boolean = false;

  // file
  protected readonly isMobileDevice: boolean = /Android|iPhone|iPad|iPod/i.test(navigator.userAgent);
  readonly acceptedTypes = ['image/png', 'image/jpeg', 'image/jpg', 'image/webp'];
  readonly acceptedExtensions = '.png,.jpeg,.jpg,.webp';
  readonly acceptedVideoTypes = ['video/mp4', 'video/webm', 'video/quicktime'];
  readonly acceptedVideoExtensions = '.mp4,.webm,.mov';
  readonly acceptedAllExtensions = this.acceptedExtensions + ',' + this.acceptedVideoExtensions;
  protected isDragOver = signal(false);
  protected files = signal<UploadedFile[]>([]);
  protected hasFiles = computed(() => this.files().length > 0);
  protected uploadingVideos = signal<UploadingVideo[]>([]);
  protected showVideoModal = false;
  protected selectedVideo: (StepFlowVideo & { safeUrl: SafeResourceUrl }) | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private stepFlowService: StepFlowService,
    private stepFlowVideoService: StepFlowVideosService,
    private toasterService: ToastrService,
    private cdf: ChangeDetectorRef,
    private sanitizer: DomSanitizer,
    private backNav: BackNavigationService
  ) {
    this.form = this.formBuilder.group({
      carrier: [''],
      shippment: [null],
      comment: [''],
    });

    this.itemsForm = this.formBuilder.array([]);
  }

  protected get itemsFormControls(): FormGroup[] {
    return this.itemsForm.controls as FormGroup[];
  }

  public open(id: number): void {
    this.visible = true;
    this.orderId = id;
    this.loadOrder();
    this.backNav.register(() => this.hide());
  }

  public close(): void {
    this.visible = false;
    this.toggleCancelModel(false);
    this.backNav.unregister();
  }

  private hide(): void {
    this.visible = false;
    this.toggleCancelModel(false);
  }

  private resetForm(): void {
    this.form.reset();
    this.files.set([]);
  }

  protected loadOrder(): void {
    this.order = null;
    this.itemsForm.clear();

    this.stepFlowService.findById(this.orderId).subscribe({
      next: (data: StepFlowOrder) => {
        this.order = data;
        this.buildItemsForm(data.items);
        this.cdf.detectChanges();

        if (this.isAdmin) {
          const current = this.steps.find(s => s.title === data.currentStep);
          this.adminStepControl.setValue(current?.id ?? null);
        }
      },
      error: () => {
        this.cdf.detectChanges();
        this.toasterService.error("Erro ao carregar informações do pedido!");
      },
    });
  }

  private buildItemsForm(items: Array<StepFlowOrderItem>): void {
    this.itemsForm.clear();

    items.forEach(item => {
      this.itemsForm.push(
        this.formBuilder.group({
          id: [item.id],
          code: [item.code],
          description: [item.description],
          quantity: [item.quantity],
          producedQuantity: [
            item.producedQuantity,
            [Validators.required, Validators.min(0), Validators.max(item.quantity)],
          ],
        })
      );
    });
  }

  protected onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    let hasQuantityChanges = false;

    if (this.order?.currentStep === 'Montagem Final') {
      if (this.itemsForm.invalid) {
        this.itemsForm.markAllAsTouched();
        return;
      }

      hasQuantityChanges = this.itemsForm.controls.some(
        control => control.get('producedQuantity')?.dirty
      );

      const hasAtLeastOneItem = this.itemsForm.controls.some(
        control => control.get('producedQuantity')?.value > 0
      );

      if (!hasAtLeastOneItem) {
        this.toasterService.error('Informe a quantidade produzida de ao menos um item.');
        return;
      }
    }

    const formData = this.buildFormData();
    const hasPendingVideos = this.files().some(f => f.kind === 'video');

    if (this.isFormDataEmpty(formData) && !hasPendingVideos) {
      this.toasterService.warning('Preencha ao menos um campo ou anexe um arquivo antes de salvar.');
      return;
    }

    if (hasPendingVideos) {
      this.triggerPendingVideoUploads();
      this.scrollToFilesSection();
    }

    if (this.isFormDataEmpty(formData)) {
      return;
    }

    this.saveLoading = true;

    this.stepFlowService.updateStep(this.orderId, formData)
      .pipe(finalize(() => {
        this.saveLoading = false;
        this.cdf.detectChanges();
      }))
      .subscribe({
        next: (data: StepFlowOrder) => {
          this.toasterService.success('Informações atualizada com sucesso.');

          if (hasQuantityChanges) {
            this.close();
            this.reloadOrders.emit();
            return;
          }

          this.order = data;
          this.buildItemsForm(data.items);
          this.resetForm();

          if (this.isAdmin) {
            const current = this.steps.find(s => s.title === data.currentStep);
            this.adminStepControl.setValue(current?.id ?? null, { emitEvent: false });
            this.adminStepControl.markAsPristine();

            if (formData.has('setStage')) {
              this.reloadOrders.emit();
            }
          }
        },
        error: (error) => {
          if (error.error.status == 422) {
            this.toasterService.error(error.error.error);
            return;
          }
          this.toasterService.error('Erro ao atualizar a etapa.');
        }
      });
  }

  private buildFormData(): FormData {
    const formData = new FormData();
    const { carrier, shippment, comment } = this.form.value;

    if (this.isAdmin && this.adminStepControl.dirty) {
      const stepId = this.adminStepControl.value;

      if (stepId !== null) {
        formData.append('setStage', String(stepId - 1));
      }
    }

    if (this.order?.currentStep === 'Montagem Final') {
      const itemsPayload = this.itemsForm.value.map((item: any) => ({
        id: item.id,
        producedQuantity: item.producedQuantity,
      }));

      formData.append('itemsJson', JSON.stringify(itemsPayload));
    }

    if (this.order?.currentStep === 'Montagem Final' || this.order?.currentStep === 'Expedição') {
      this.files()
        .filter(uploaded => uploaded.kind === 'image')
        .forEach(uploaded => {
          formData.append('images', uploaded.file, uploaded.name);
        });
    }

    if (this.order?.currentStep === 'Frete') {
      this.appendIfNotEmpty(formData, 'carrier', carrier);
      this.appendIfNotEmpty(formData, 'shippment', shippment);
    }

    this.appendIfNotEmpty(formData, 'comment', comment);

    return formData;
  }

  private appendIfNotEmpty(formData: FormData, key: string, value: unknown): void {
    if (value === null || value === undefined) {
      return;
    }

    const stringValue = String(value).trim();
    if (stringValue.length > 0) {
      formData.append(key, stringValue);
    }
  }

  private isFormDataEmpty(formData: FormData): boolean {
    return Array.from(formData.keys()).length === 0;
  }

  protected onNextStep(): void {
    const id = this.order?.id as number;
    this.stepFlowService.nextStep(id).subscribe({
      next: () => {
        this.close();
        this.nextStepTask.emit(id);
        this.cdf.detectChanges();
        this.toasterService.success('Informações atualizada com sucesso.');
      },
      error: (error) => this.toasterService.error(error.error.error),
    });
  }

  protected onDeleteImage(id: number): void {
    this.stepFlowService.deleteImageById(id).subscribe({
      next: () => {
        if (!this.order) throw new Error("This order is null!");

        this.order.pictures = this.order?.pictures.filter(p => p.id != id);
        this.cdf.detectChanges();

        this.toasterService.success("Imagem apagada com sucesso!");
      },
      error: () => this.toasterService.error("Erro ao deletar imagem!")
    })
  }

  protected onDeleteVideo(id: number): void {
    this.stepFlowVideoService.deleteById(id).subscribe({
      next: () => {
        if (!this.order) throw new Error("This order is null!");
        this.order.videos = this.order.videos.filter(v => v.id !== id);
        this.cdf.detectChanges();
        this.toasterService.success("Vídeo apagado com sucesso!");
      },
      error: () => this.toasterService.error("Erro ao deletar vídeo!"),
    });
  }

  protected toggleCancelModel(status: boolean): void {
    this.showCancelModel = status;
    this.cdf.detectChanges();
  }

  protected onCancel(message: string): void {
    if (this.order?.currentStep != 'PCP') return;

    const formData = new FormData();

    this.appendIfNotEmpty(formData, 'comment', message);
    this.appendIfNotEmpty(formData, 'cancelled', 'true');

    this.stepFlowService.updateStep(this.orderId, formData).subscribe({
      next: () => {
        this.nextStepTask.emit(this.orderId);
        this.close();
        this.toasterService.success('Pedido cancelado com sucesso!');
      },
      error: (error) => {
        if (error.error.status == 422) {
          this.toasterService.error(error.error.error);
          return;
        }

        this.toasterService.error('Erro ao cancelar pedido!');
      }
    });
  }

  protected onOpenVideoModal(video: StepFlowVideo): void {
    this.selectedVideo = { ...video, safeUrl: this.sanitizer.bypassSecurityTrustResourceUrl(video.viewUrl) };
    this.showVideoModal = true;
  }

  protected onCloseVideoModal(): void {
    this.showVideoModal = false;
    this.selectedVideo = null;
  }

  // file input
  protected onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver.set(true);
  }

  protected onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver.set(false);
  }

  protected onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver.set(false);

    const droppedFiles = event.dataTransfer?.files;
    if (droppedFiles) {
      this.processFiles(Array.from(droppedFiles));
    }
  }

  protected onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.processFiles(Array.from(input.files));
      input.value = '';
    }
  }

  private generateId(): string {
    if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
      return crypto.randomUUID();
    }

    return `${Date.now()}-${Math.random().toString(36).slice(2, 11)}`;
  }

  /*
   * Apenas seleciona/valida os arquivos (imagens e vídeos) e os mantém
   * pendentes na mesma lista. Nenhum upload é disparado aqui — isso só
   * acontece quando o usuário clica em "Salvar" (ver onSubmit).
   */
  private processFiles(newFiles: File[]): void {
    const images = newFiles.filter(f => this.acceptedTypes.includes(f.type));
    const videos = newFiles.filter(f => this.acceptedVideoTypes.includes(f.type));
    const invalid = newFiles.filter(f =>
      !this.acceptedTypes.includes(f.type) && !this.acceptedVideoTypes.includes(f.type)
    );

    if (invalid.length > 0) {
      this.toasterService.error('Use apenas PNG, JPEG, JPG, WEBP, MP4, WEBM ou MOV.');
    }

    const mapped: UploadedFile[] = [...images, ...videos].map(f => ({
      id: this.generateId(),
      file: f,
      name: f.name,
      size: this.formatSize(f.size),
      kind: this.acceptedTypes.includes(f.type) ? 'image' : 'video',
    }));

    if (mapped.length) {
      this.files.update(current => [...current, ...mapped]);
    }
  }

  /*
   * Move os vídeos pendentes (selecionados, ainda não enviados) da lista de
   * seleção para a lista de "enviando", e dispara o upload TUS de cada um.
   */
  private triggerPendingVideoUploads(): void {
    const pendingVideos = this.files().filter(f => f.kind === 'video');
    if (!pendingVideos.length) return;

    this.files.update(current => current.filter(f => f.kind !== 'video'));

    pendingVideos.forEach(pending => this.uploadVideo(pending.file));
  }

  private uploadVideo(file: File): void {
    const tempId = this.generateId();

    this.uploadingVideos.update(current => [
      ...current,
      { tempId, id: null, name: file.name, progress: 0 },
    ]);
    this.cdf.detectChanges();

    this.stepFlowVideoService.create(this.orderId, file.name).subscribe({
      next: (info) => {
        this.uploadingVideos.update(current =>
          current.map(v => (v.tempId === tempId ? { ...v, id: info.id } : v))
        );

        const upload = new Upload(file, {
          endpoint: info.uploadEndpoint,
          retryDelays: [0, 1000, 3000, 5000],
          headers: {
            AuthorizationSignature: info.authorizationSignature,
            AuthorizationExpire: String(info.authorizationExpire),
            VideoId: info.bunnyVideoId,
            LibraryId: info.libraryId,
          },
          metadata: {
            filetype: file.type,
            title: file.name,
          },
          onError: () => {
            this.toasterService.error(`Erro ao enviar o vídeo "${file.name}".`);
            this.uploadingVideos.update(current => current.filter(v => v.tempId !== tempId));
            this.stepFlowVideoService.deleteById(info.id).subscribe();
            this.cdf.detectChanges();
          },
          onProgress: (bytesSent, bytesTotal) => {
            const progress = Math.round((bytesSent / bytesTotal) * 100);
            this.uploadingVideos.update(current =>
              current.map(v => (v.tempId === tempId ? { ...v, progress } : v))
            );
            this.cdf.detectChanges();
          },
          onSuccess: () => {
            this.stepFlowVideoService.complete(info.id).subscribe({
              next: () => {
                this.uploadingVideos.update(current => current.filter(v => v.tempId !== tempId));
                this.toasterService.success("Vídeo enviado com sucesso!");
                this.loadOrder();
              },
            });
          },
        });

        upload.start();
      },
      error: () => {
        this.toasterService.error(`Erro ao preparar o envio do vídeo.`);
        this.uploadingVideos.update(current => current.filter(v => v.tempId !== tempId));
      },
    });
  }

  private scrollToFilesSection(): void {
    this.cdf.detectChanges();

    setTimeout(() => {
      this.filesSection?.nativeElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
    });
  }

  protected removeFile(id: string): void {
    this.files.update(current => current.filter(f => f.id !== id));
  }

  private formatSize(bytes: number): string {
    if (bytes < 1024) return `${bytes} B`;

    const kb = bytes / 1024;
    if (kb < 1024) return `${kb.toFixed(0)} KB`;

    const mb = kb / 1024;
    return `${mb.toFixed(1)} MB`;
  }
}
