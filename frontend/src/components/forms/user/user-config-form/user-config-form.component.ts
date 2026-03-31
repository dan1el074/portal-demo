import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonCloseDirective, ButtonDirective, ColComponent, FormControlDirective, FormFloatingDirective, FormLabelDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, RowComponent } from '@coreui/angular';
import { DatePickerComponent } from '@coreui/angular-pro';
import { IconDirective } from '@coreui/icons-angular';
import { cilPencil, cilX } from '@coreui/icons';
import { ImageCroppedEvent, ImageCropperComponent } from 'ngx-image-cropper';
import { passwordMatchValidator } from '../../../../app/config/validators';
import { UserConfigData } from '../../../../app/interface/user.interface';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-user-config-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ImageCropperComponent,
    ColComponent,
    RowComponent,
    FormFloatingDirective,
    FormLabelDirective,
    FormControlDirective,
    IconDirective,
    ButtonDirective,
    ModalComponent,
    ModalHeaderComponent,
    ModalTitleDirective,
    ButtonCloseDirective,
    ModalBodyComponent,
    ModalFooterComponent,
    DatePickerComponent
  ],
  templateUrl: './user-config-form.component.html',
  styleUrl: './user-config-form.component.scss',
})
export class UserConfigFormComponent {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  @Output() editTask = new EventEmitter<FormData>();
  @Input() userData!: UserConfigData;

  protected icons = { cilPencil, cilX };
  protected valid: boolean | undefined;
  protected configForm: FormGroup;
  protected showErrors = false;

  // profile image
  protected file: string = '';
  protected imageChangedEvent: Event | null = null;
  protected croppedImage: Blob | null = null;
  protected croppedImageUrl?: string;
  protected resetPicture = false;
  protected modalReady = false;

  // data picker
  protected birthDate = new Date();

  constructor(private formBuilder: FormBuilder) {
    this.configForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.minLength(6)]],
      email: ['', [Validators.required, Validators.email]],
      birthDate: [null, [Validators.required]],
      password: ['', [Validators.minLength(8), Validators.pattern(/^(?=.*[A-Za-z])(?=.*\d).+$/)]],
      repeatPassword: [''],
      picture: [null as Blob | null],
    },
    {
      validators: [passwordMatchValidator],
    });
  }

  public ngOnChanges(): void {
    if (!this.userData) return;

    this.birthDate = this.parseDate(this.userData.birthDate) ?? null;
    this.configForm.get('birthDate')?.setValue(this.birthDate);
    this.configForm.get('name')?.setValue(this.userData.name);
    this.configForm.get('email')?.setValue(this.userData.email);

    if (this.userData.pictureId) this.file = environment.apiUrl + '/images/' + this.userData.pictureId;
  }

  private parseDate(value: string | null): Date {
    if (!value) return new Date();

    const [year, month, day] = value.split('-').map(Number);
    return new Date(year, month - 1, day);
  }

  protected onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) {
      this.resetImageCropper();
      input.value = '';
      return;
    }

    if (input.files[0].size > 2 * 1024 * 1024) {
      this.configForm.get('picture')?.setErrors({ imageTooLarge: true });
      this.resetImageCropper();
      input.value = '';
      return;
    }

    this.configForm.get('picture')?.setErrors({ imageTooLarge: false });
    this.imageChangedEvent = event;
    this.toggleModal();
  }

  protected imageCropped(event: ImageCroppedEvent): void {
    if (!event.blob) return;
    const photoControl = this.configForm.get('picture');

    photoControl?.setValue(event.blob);
    photoControl?.markAsTouched();
    photoControl?.updateValueAndValidity();

    if (event.objectUrl) {
      this.croppedImageUrl = event.objectUrl;
      this.croppedImage = event.blob;
    }
  }

  protected resetImageCropper(): void {
    this.imageChangedEvent = null;
    this.croppedImage = null;
  }

  protected processImage(): void {
    if (!this.croppedImageUrl) return;
    this.file = this.croppedImageUrl;
    this.resetPicture = false;
    this.toggleModal(true);
  }

  protected toggleModal(resetInput: boolean = false) {
    this.modalReady = !this.modalReady;

    if (resetInput) {
      const input = this.imageChangedEvent?.target as HTMLInputElement
      input.value = "";
    }
  }

  protected handleModalChange(event: any) {
    this.modalReady = event;
  }

  protected removePicture(): void {
    if (this.fileInput?.nativeElement) {
      this.fileInput.nativeElement.value = '';
    }

    this.file = '';
    this.configForm.get('picture')?.setValue(null);
    this.resetPicture = true;
  }

  protected onSubmit(): void {
    if (!this.configForm.valid) {
      this.showErrors = true;
      return;
    }

    const formData = new FormData();
    Object.entries(this.configForm.value).forEach(([key, value]) => {
      if (key === 'repeatPassword') return;
      if (key === 'password' && !value) return;
      if (key === 'picture') return;
      if (key === 'birthDate') return;
      formData.append(key, String(value));
    });

    // birthDate -> convert Date to string
    const date: Date | null = this.configForm.value.birthDate;
    const birthDateString = date ? date.toISOString().split('T')[0] : null;
    formData.append('birthDate', String(birthDateString));

    // if resetPicture = true -> send signal to backend
    if (this.resetPicture) formData.append('resetPicture', 'true');

    // change picture
    if (!this.resetPicture && this.file && this.croppedImage) {
      formData.append('picture', this.croppedImage, 'profile.png');
    }

    // send to the father component
    this.editTask.emit(formData);
  }
}
