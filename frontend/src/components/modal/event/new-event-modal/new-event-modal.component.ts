import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, ElementRef, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonCloseDirective, ButtonDirective, FormControlDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective } from '@coreui/angular';
import { CalendarComponent, LoadingButtonComponent } from '@coreui/angular-pro';
import { ToastrService } from 'ngx-toastr';
import { EventCard } from '../../../../app/interface/event.interface';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-new-event-modal',
  imports: [CommonModule, FormsModule, ModalComponent, ModalHeaderComponent, ModalTitleDirective, ModalBodyComponent, ModalFooterComponent, ButtonDirective, ButtonCloseDirective, FormControlDirective, CalendarComponent, LoadingButtonComponent],
  templateUrl: './new-event-modal.component.html',
  styleUrl: './new-event-modal.component.scss'
})
export class NewEventModalComponent implements OnChanges {
  @Input() visible = false;
  @Input() editingEvent: EventCard | null = null;
  @Output() closeTask = new EventEmitter<void>();
  @Output() publishTask = new EventEmitter<{ id?: number; data: FormData }>();
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  protected readonly today = new Date();
  protected apiUrl = environment.apiUrl;
  protected title = '';
  protected eventDate: Date | null = null;
  protected eventHour = new Date().getHours();
  protected eventMinute = new Date().getMinutes();
  protected imageFile: File | null = null;
  protected imagePreview: string | null = null;
  protected loading = false;

  constructor(private toast: ToastrService, private cdr: ChangeDetectorRef) {}

  ngOnChanges(changes: SimpleChanges): void { if (changes['visible']?.currentValue) this.load(); }
  protected close(): void { this.loading = false; this.closeTask.emit(); }
  protected visibleChange(visible: boolean): void { if (!visible) this.close(); }
  protected selectDate(date: Date | null): void { this.eventDate = date; }
  protected selectImage(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    this.imageFile = file;
    const reader = new FileReader();
    reader.onload = e => { this.imagePreview = e.target?.result as string; this.cdr.markForCheck(); };
    reader.readAsDataURL(file);
  }
  protected submit(): void {
    if (!this.title.trim() || !this.eventDate) { this.toast.error('Informe o título, a data e a hora do evento!'); return; }
    if (!Number.isInteger(this.eventHour) || this.eventHour < 0 || this.eventHour > 23 || !Number.isInteger(this.eventMinute) || this.eventMinute < 0 || this.eventMinute > 59) {
      this.toast.error('Informe um horário válido entre 00:00 e 23:59!');
      return;
    }
    if (!this.editingEvent && !this.imageFile) { this.toast.error('Adicione uma imagem para o evento!'); return; }
    const date = new Date(this.eventDate);
    date.setHours(this.eventHour, this.eventMinute, 0, 0);
    const data = new FormData();
    data.append('title', this.title.trim());
    data.append('eventDate', date.toISOString());
    if (this.imageFile) data.append('image', this.imageFile);
    this.loading = true;
    this.publishTask.emit({ id: this.editingEvent?.id, data });
  }
  public stopLoad(): void { this.loading = false; this.cdr.detectChanges(); }
  public finishSubmit(): void { this.loading = false; this.closeTask.emit(); }
  private load(): void {
    const now = new Date();
    this.loading = false; this.title = ''; this.eventDate = null; this.eventHour = now.getHours(); this.eventMinute = now.getMinutes(); this.imageFile = null; this.imagePreview = null;
    if (!this.editingEvent) return;
    const date = new Date(this.editingEvent.eventDate);
    this.title = this.editingEvent.title; this.eventDate = date; this.eventHour = date.getHours(); this.eventMinute = date.getMinutes();
    this.imagePreview = this.apiUrl + '/images/' + this.editingEvent.picture.id + '?v=' + this.editingEvent.updatedAt;
  }
}
