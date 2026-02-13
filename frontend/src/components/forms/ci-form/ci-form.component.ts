import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonDirective, FormControlDirective, FormFloatingDirective, FormLabelDirective } from '@coreui/angular';
import { OrderInfo } from './../../../app/interface/erp.interface';
import { NewInternalCommunication } from './../../../app/interface/internal-communication.interface';

@Component({
  selector: 'app-ci-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormFloatingDirective,
    FormLabelDirective,
    FormControlDirective,
    ButtonDirective
  ],
  templateUrl: './ci-form.component.html',
  styleUrl: './ci-form.component.scss',
})
export class CiFormComponent implements OnChanges {
  @Input() items!: Array<OrderInfo>;
  @Output() createTask = new EventEmitter<NewInternalCommunication>();
  @Output() exitTask = new EventEmitter<void>();

  protected valid: boolean | undefined;
  protected createForm: FormGroup;
  protected showErrors = false;

  constructor(
    private formBuilder: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.createForm = this.formBuilder.group({
      request: ['', [Validators.required]],
      client: ['', [Validators.required, Validators.minLength(5)]],
      item: ['', [Validators.required]],
      title: ['', [Validators.required]],
      description: [ '', [Validators.required, Validators.minLength(5)]],
      reason: ['', [Validators.required, Validators.minLength(5)]],
      departments: ['', [Validators.required]],
      status: ['']
    })
  }

  public ngOnChanges(): void {
    if (this.items == null) return;
    if (this.items.length <= 0) return;

    this.createForm.reset({
      request: this.items[0].number,
      client: this.items[0].client,
      item: this.items[0].item,
      title: '',
      description: '',
      reason: '',
      departments: ''
    });
    this.cdr.detectChanges();
  }

  protected onExit(): void {
    this.createForm.reset({
      request: '',
      client: '',
      item: '',
      title: '',
      description: '',
      reason: '',
      departments: ''
    });
    this.exitTask.emit();
  }

  protected onSave(): void {
    this.createForm.patchValue({
      status: 'CREATED'
    });
    this.onSubmit();
  }

  protected onPublish(): void {
    this.createForm.patchValue({
      status: 'PUBLISH'
    });
    this.onSubmit();
  }

  protected onSubmit(): void {
    if (!this.createForm.valid) {
      this.showErrors = true;
      return;
    }

    this.createForm.patchValue({
      description: this.createForm.get('description')?.value.replace(/\r?\n/g, '<br>')
    });
    this.createTask.emit(this.createForm.value);
    this.onExit();
  }
}
