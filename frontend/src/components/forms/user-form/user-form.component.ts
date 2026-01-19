import { IconDirective } from '@coreui/icons-angular';
import { CommonModule } from '@angular/common';
import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonCloseDirective, ButtonDirective, ColComponent, FormCheckComponent, FormCheckInputDirective, FormCheckLabelDirective, FormControlDirective, FormFloatingDirective, FormLabelDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, RowComponent } from '@coreui/angular';
import { passwordMatchValidator } from '../../../app/config/validators';
import { ImageCropperComponent, ImageCroppedEvent } from 'ngx-image-cropper';
import { cilPencil, cilX } from '@coreui/icons';

@Component({
  selector: 'app-user-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ImageCropperComponent,
    FormFloatingDirective,
    FormLabelDirective,
    FormControlDirective,
    RowComponent,
    ColComponent,
    FormCheckComponent,
    FormCheckInputDirective,
    FormCheckLabelDirective,
    IconDirective,
    ButtonDirective,
    ModalComponent,
    ModalHeaderComponent,
    ModalTitleDirective,
    ButtonCloseDirective,
    ModalBodyComponent,
    ModalFooterComponent
  ],
  templateUrl: './user-form.component.html',
  styleUrl: './user-form.component.scss',
})
export class UserFormComponent {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  @Output() createTask = new EventEmitter<FormData>();
  @Output() exitTask = new EventEmitter<void>();

  protected icons = { cilPencil, cilX };
  protected valid: boolean | undefined;
  protected createForm: FormGroup;
  protected imageChangedEvent: Event | null = null;
  protected croppedImage: Blob | null = null;
  protected croppedImageUrl?: string;
  protected showErrors = false;
  protected file: string = '';
  protected modalReady = false;

  constructor(private formBuilder: FormBuilder) {
    this.createForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.minLength(6)]],
      position: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      birthDate: ['', [Validators.required]],
      username: [ '', [Validators.required, Validators.minLength(7), Validators.pattern(/^[a-zA-Z0-9.-]+$/)]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.pattern(/^(?=.*[A-Za-z])(?=.*\d).+$/)]],
      repeatPassword: ['', [Validators.required]],
      roles: [[1,2]],
      picture: [null as Blob | null],
      disabled: [false]
    },
    {
      validators: [passwordMatchValidator],
    });
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) {
      this.resetImageCropper();
      input.value = '';
      return;
    }

    if (input.files[0].size > 2 * 1024 * 1024) {
      this.createForm.get('picture')?.setErrors({ imageTooLarge: true });
      this.resetImageCropper();
      input.value = '';
      return;
    }

    this.createForm.get('picture')?.setErrors({ imageTooLarge: false });
    this.imageChangedEvent = event;
    this.toggleModal();
  }

  imageCropped(event: ImageCroppedEvent): void {
    if (!event.blob) return;
    const photoControl = this.createForm.get('picture');

    photoControl?.setValue(event.blob);
    photoControl?.markAsTouched();
    photoControl?.updateValueAndValidity();

    if (event.objectUrl) {
      this.croppedImageUrl = event.objectUrl;
      this.croppedImage = event.blob;
    }
  }

  resetImageCropper(): void {
    this.imageChangedEvent = null;
    this.croppedImage = null;
  }

  processImage(): void {
    if (!this.croppedImageUrl) return;
    this.file = this.croppedImageUrl;
    this.toggleModal(true);
  }

  toggleModal(resetInput: boolean = false) {
    this.modalReady = !this.modalReady;

    if (resetInput) {
      const input = this.imageChangedEvent?.target as HTMLInputElement
      input.value = "";
    }
  }

  handleModalChange(event: any) {
    this.modalReady = event;
  }

  removePicture(): void {
    this.file = '';
    this.createForm.get('picture')?.setValue(null);
  }

  onExit(): void {
    this.createForm.reset({
      name: '',
      position: '',
      email: '',
      birthDate: '',
      username: '',
      password: '',
      repeatPassword: '',
      roles: '',
      picture: null,
      disabled: false
    });

    if (this.fileInput?.nativeElement) {
      this.fileInput.nativeElement.value = '';
    }

    this.file = "";
    this.exitTask.emit();
  }

  onSubmit(): void {
    if (!this.createForm.valid) {
      this.showErrors = true;
      return;
    }

    const formData = new FormData();

    Object.entries(this.createForm.value).forEach(([key, value]) => {
      if (key === 'repeatPassword') return;
      if (key === 'picture') return;
      if (key === 'disabled') return;
      if (typeof value === 'string') formData.append(key, value);
    });

    if (this.croppedImage) formData.append('picture', this.croppedImage, 'profile.png');
    formData.append('activated', this.createForm.get('disabled') ? 'true' : 'false');

    this.createTask.emit(formData);
    this.onExit();
  }
}
