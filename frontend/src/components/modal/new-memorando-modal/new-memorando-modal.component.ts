import { Component, EventEmitter, Input, OnChanges, Output } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonCloseDirective, ButtonDirective, FormControlDirective, FormLabelDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective } from '@coreui/angular';
import { cilPencil } from '@coreui/icons';
import { IconDirective } from '@coreui/icons-angular';

@Component({
  selector: 'app-new-memorando-modal',
  imports: [
    ButtonDirective,
    ModalComponent,
    ModalHeaderComponent,
    ModalTitleDirective,
    ButtonCloseDirective,
    ModalBodyComponent,
    ModalFooterComponent,
    IconDirective,
    ReactiveFormsModule,
    FormsModule,
    FormLabelDirective,
    FormControlDirective
  ],
  templateUrl: './new-memorando-modal.component.html',
  styleUrl: './new-memorando-modal.component.scss',
})
export class NewMemorandoModalComponent implements OnChanges {
  @Input() visible!: boolean;
  @Output() closeModal = new EventEmitter<any>();
  @Output() createNewMemorando = new EventEmitter<number>();

  protected newMemorandoForm: FormGroup;
  protected showErrors = false;
  protected icons = { cilPencil };

  constructor(private formBuilder: FormBuilder) {
    this.newMemorandoForm = this.formBuilder.group({
      orderNumber: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
    });
  }

  public ngOnChanges(): void {
    if (!this.visible) {
      this.resetForm();
    }
  }

  protected closeMemorandoModal(): void {
    this.closeModal.emit();
    this.resetForm()
  }

  protected onSubmit(): void {
    if (!this.newMemorandoForm.valid) {
      this.showErrors = true;
      return;
    }

    this.createNewMemorando.emit(this.newMemorandoForm.get('orderNumber')?.value);
  }

  private resetForm(): void {
    this.newMemorandoForm.reset({ orderNumber: '' });
  }
}
