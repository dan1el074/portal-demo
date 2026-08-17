import { ChangeDetectorRef, Component, ElementRef, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild, ChangeDetectionStrategy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AvatarComponent, ButtonCloseDirective, ButtonDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, TooltipDirective } from '@coreui/angular';
import { LoadingButtonComponent } from '@coreui/angular-pro';
import { ToastrService } from '../../../../app/services/toast.service';
import { Me } from '../../../../app/interface/user.interface';
import { PostCard } from '../../../../app/interface/post.interface';
import { environment } from '../../../../environments/environment';
import { ModalBackNavigationDirective } from '../../../../app/directive/modal-back-navigation.directive';

interface PreviewItem { url: string; existingId?: number; file?: File; }

@Component({
  selector: 'app-new-post-modal',
  imports: [FormsModule, ModalComponent, ModalBackNavigationDirective, ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent, ModalFooterComponent, ButtonDirective, ButtonCloseDirective, TooltipDirective, AvatarComponent, LoadingButtonComponent],
  templateUrl: './new-post-modal.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './new-post-modal.component.scss',
})
export class NewPostModalComponent implements OnChanges {
  @Input() visible = false;
  @Input() openDialog = false;
  @Input() user!: Me;
  @Input() editingPost: PostCard | null = null;
  @Output() closeTask = new EventEmitter<void>();
  @Output() publishTask = new EventEmitter<FormData>();
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  protected apiUrl = environment.apiUrl;
  protected readonly maxImages = 5;
  protected textContent = '';
  protected previews: PreviewItem[] = [];
  protected loading = false;

  constructor(private toasterService: ToastrService, private cdr: ChangeDetectorRef) {}

  public ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible']?.currentValue) {
      this.loadEditingData();
      if (this.openDialog) setTimeout(() => this.fileInput?.nativeElement.click());
    }
  }

  protected removeImage(index: number): void { this.previews.splice(index, 1); }
  protected closeModal(): void { this.resetForm(); this.closeTask.emit(); }
  protected handleToggleModal(visible: boolean): void { if (!visible) this.closeModal(); }

  protected onFilesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files) return;
    Array.from(input.files).slice(0, this.maxImages - this.previews.length).forEach(file => {
      const reader = new FileReader();
      reader.onload = e => {
        this.previews.push({ url: e.target?.result as string, file });
        this.cdr.markForCheck();
      };
      reader.readAsDataURL(file);
    });
    input.value = '';
  }

  private resetForm(): void { this.loading = false; this.previews = []; this.textContent = ''; }
  public stopLoad(): void { this.loading = false; this.cdr.detectChanges(); }

  protected onPublish(): void {
    if (!this.textContent.trim() && !this.previews.length) {
      this.toasterService.error('É necessário um comentário ou imagem para continuar!');
      return;
    }
    this.loading = true;
    const data = new FormData();
    data.append('text', this.textContent);
    data.append('isWarning', 'false');
    this.previews.filter(item => item.existingId).forEach(item => data.append('retainedImageIds', String(item.existingId)));
    this.previews.filter(item => item.file).forEach(item => data.append('images', item.file!));
    this.publishTask.emit(data);
  }

  private loadEditingData(): void {
    this.resetForm();
    if (!this.editingPost) return;
    this.textContent = (this.editingPost.content ?? '').replace(/<br\s*\/?>/gi, '\n').replace(/<strong>(.*?)<\/strong>/gi, '*$1*').replace(/<[^>]+>/g, '');
    this.previews = this.editingPost.pictures.map(picture => ({ url: this.apiUrl + '/images/' + picture.id, existingId: picture.id }));
  }
}
