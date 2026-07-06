import { ChangeDetectorRef, Component, computed, EventEmitter, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { CurrencyMaskDirective } from './../../../app/directive/currency-mask.directive';
import { AccordionButtonDirective, AccordionComponent, AccordionItemComponent, ButtonCloseDirective, ButtonDirective, FormControlDirective, FormFloatingDirective, FormLabelDirective, InputGroupComponent, TemplateIdDirective } from '@coreui/angular';
import { StepFlowService } from '../../../app/services/step-flow.service';
import { StepFlowOrder } from '../../../app/interface/step-flow.interface';
import { UploadedFile } from '../../../app/interface/file.interface';
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
    FormControlDirective,
    FormLabelDirective,
    CurrencyMaskDirective
  ],
  templateUrl: './step-flow-input-offcanvas.component.html',
  styleUrl: './step-flow-input-offcanvas.component.scss',
})
export class StepFlowInputOffcanvasComponent {
  @Input() orderId!: number;
  @Output() nextStepTask = new EventEmitter<number>();
  protected order: StepFlowOrder | null = null;
  protected visible = false;
  protected apiUrl: string = environment.apiUrl;
  protected form!: FormGroup;
  protected submitting = signal(false);

  // file
  readonly acceptedTypes = ['image/png', 'image/jpeg', 'image/jpg', 'image/webp'];
  readonly acceptedExtensions = '.png,.jpeg,.jpg,.webp';
  protected isDragOver = signal(false);
  protected files = signal<UploadedFile[]>([]);
  protected hasFiles = computed(() => this.files().length > 0);

  constructor(
    private formBuilder: FormBuilder,
    private stepFlowService: StepFlowService,
    private toasterService: ToastrService,
    private cdf: ChangeDetectorRef
  ) {
    this.form = this.formBuilder.group({
      carrier: [''],
      shippment: [null],
      comment: [''],
    });
  }

  public open(id: number): void {
    this.visible = true;
    this.orderId = id;
    this.loadOrder();
  }

  public close(): void {
    this.visible = false;
  }

  private resetForm(): void {
    this.form.reset();
    this.files.set([]);
  }

  protected loadOrder(): void {
    this.order = null;

    this.stepFlowService.findById(this.orderId).subscribe({
      next: (data: StepFlowOrder) => {
        this.order = data;
        this.cdf.detectChanges();
      },
      error: () => {
        this.cdf.detectChanges();
        this.toasterService.error("Erro ao carregar informações do pedido!");
      },
    });
  }

  protected onSubmit(): void {
    // TODO: transportadora e frete estão indo mesmo vazios

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const formData = this.buildFormData();

    if (this.isFormDataEmpty(formData)) {
      this.toasterService.warning('Preencha ao menos um campo ou anexe um arquivo antes de salvar.');
      return;
    }

    this.submitting.set(true);

    this.stepFlowService.updateStep(this.orderId, formData).subscribe({
      next: (data: StepFlowOrder) => {
        this.order = data;
        this.cdf.detectChanges();
        this.toasterService.success('Etapa atualizada com sucesso.');
        this.resetForm();
      },
      error: () => {
        this.toasterService.error('Erro ao atualizar a etapa.');
      },
      complete: () => this.submitting.set(false),
    });
  }

  private buildFormData(): FormData {
    const formData = new FormData();
    const { carrier, shippment, comment } = this.form.value;

    if (this.order?.currentStep === 'Frete') {
      this.appendIfNotEmpty(formData, 'carrier', carrier);
      this.appendIfNotEmpty(formData, 'shippment', shippment);
    }

    this.appendIfNotEmpty(formData, 'comment', comment);

    if (this.order?.currentStep === 'Montagem Final' || this.order?.currentStep === 'Expedição') {
      this.files().forEach(uploaded => {
        formData.append('images', uploaded.file, uploaded.name);
      });
    }

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
        this.visible = false;
        this.nextStepTask.emit(id);
        this.cdf.detectChanges();
        this.toasterService.success('Etapa atualizada com sucesso.');
      },
      error: () => this.toasterService.error("Erro ao avançar etapa!"),
    });
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

  private processFiles(newFiles: File[]): void {
    const invalid = newFiles.filter(f => !this.acceptedTypes.includes(f.type));
    if (invalid.length > 0) this.toasterService.error('Use apenas PNG, JPEG, JPG ou WEBP.');

    const valid = newFiles.filter(f => this.acceptedTypes.includes(f.type));
    const mapped: UploadedFile[] = valid.map(f => ({
      id: crypto.randomUUID(),
      file: f,
      name: f.name,
      size: this.formatSize(f.size),
    }));

    this.files.update(current => [...current, ...mapped]);
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
