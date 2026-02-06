import { Component, EventEmitter, Input, OnChanges, Output } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonCloseDirective, ButtonDirective, FormControlDirective, FormLabelDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective } from '@coreui/angular';
import { cilPencil } from '@coreui/icons';
import { IconDirective } from '@coreui/icons-angular';

@Component({
  selector: 'app-new-ci-modal',
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
  templateUrl: './new-ci-modal.component.html',
  styleUrl: './new-ci-modal.component.scss',
})
export class NewCiModalComponent implements OnChanges {
  @Input() visible!: boolean;
  @Output() closeModal = new EventEmitter<any>();
  @Output() createNewCI = new EventEmitter<number>();

  protected newCIForm: FormGroup;
  protected showErrors = false;
  protected icons = { cilPencil };

  constructor(private formBuilder: FormBuilder) {
    this.newCIForm = this.formBuilder.group({
      orderNumber: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
    });
  }

  public ngOnChanges(): void {
    if (!this.visible) {
      this.resetForm();
    }
  }

  protected closeCIModal(): void {
    this.closeModal.emit();
    this.resetForm()
  }

  protected onSubmit(): void {
    if (!this.newCIForm.valid) {
      this.showErrors = true;
      return;
    }

    this.createNewCI.emit(this.newCIForm.get('orderNumber')?.value);
  }

  private resetForm(): void {
    this.newCIForm.reset({ orderNumber: '' });
  }
}
