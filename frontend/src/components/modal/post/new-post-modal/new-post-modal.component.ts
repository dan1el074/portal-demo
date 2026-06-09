import { ChangeDetectorRef, Component, ElementRef, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AvatarComponent, ButtonCloseDirective, ButtonDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, TooltipDirective } from '@coreui/angular';
import { LoadingButtonComponent } from '@coreui/angular-pro';
import { Me } from '../../../../app/interface/user.interface';
import { environment } from '../../../../environments/environment';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-new-post-modal',
  imports: [
    FormsModule,
    ModalComponent,
    ModalHeaderComponent,
    ModalTitleDirective,
    ModalBodyComponent,
    ModalFooterComponent,
    ButtonDirective,
    ButtonCloseDirective,
    TooltipDirective,
    AvatarComponent,
    LoadingButtonComponent
  ],
  templateUrl: './new-post-modal.component.html',
  styleUrl: './new-post-modal.component.scss',
})
export class NewPostModalComponent implements OnChanges {
  @Input() visible!: boolean;
  @Input() isWarning!: boolean;
  @Input() openDialog!: boolean;
  @Input() user!: Me;
  @Output() closeTask = new EventEmitter<void>();
  @Output() publishTask = new EventEmitter<FormData>();
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  protected apiUrl: string = environment.apiUrl;
  protected readonly maxImages = 5;
  protected textContent = '';
  protected selectedFiles: File[] = [];
  protected previews: string[] = [];
  protected loading = false;

  constructor(
    private toasterService: ToastrService,
    private cdr: ChangeDetectorRef
  ) {}

  public ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'].currentValue && this.openDialog) {
      this.fileInput.nativeElement.click();
    }
  }

  protected removeImage(index: number): void {
    this.selectedFiles.splice(index, 1);
    this.previews.splice(index, 1);
  }

  protected closeModal(): void {
    this.resetForm();
    this.closeTask.emit();
  }

  protected handleToggleModal(visible: boolean): void {
    if (!visible) this.closeModal();
  }

  protected toggleWarningButtom(): void {
    this.isWarning = !this.isWarning;
  }

  protected onFilesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files) return;

    const incoming = Array.from(input.files);
    const remaining = this.maxImages - this.selectedFiles.length;
    const toAdd = incoming.slice(0, remaining);

    toAdd.forEach(file => {
      this.selectedFiles.push(file);
      const reader = new FileReader();
      reader.onload = (e) => {
        this.previews.push(e.target?.result as string);
        this.cdr.markForCheck();
      }
      reader.readAsDataURL(file);
    });

    input.value = '';
  }

  private resetForm(): void {
    this.loading = false;
    this.isWarning = false;
    this.selectedFiles = [];
    this.previews = [];
    this.textContent = '';
  }

  public stopLoad(): void {
    this.loading = false;
    this.cdr.detectChanges();
  }

  protected onPublish(): void {
    if (this.textContent == "" && this.selectedFiles.length == 0) {
      this.toasterService.error("É necessário um comentário ou imagem para continuar!");
      return;
    }

    this.loading = true;
    const formData = new FormData();

    formData.append('text', this.textContent);
    formData.append('isWarning', String(this.isWarning));

    this.selectedFiles.forEach(file => formData.append('images', file));
    this.publishTask.emit(formData);
  }
}
