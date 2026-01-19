import { UserData } from './../../../app/interface/user.interface';
import { IconDirective } from '@coreui/icons-angular';
import { CommonModule } from '@angular/common';
import { Component, ElementRef, EventEmitter, Input, OnChanges, Output, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonCloseDirective, ButtonDirective, ColComponent, FormCheckComponent, FormCheckInputDirective, FormCheckLabelDirective, FormControlDirective, FormFloatingDirective, FormLabelDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, RowComponent } from '@coreui/angular';
import { passwordMatchValidator } from '../../../app/config/validators';
import { ImageCropperComponent, ImageCroppedEvent } from 'ngx-image-cropper';
import { cilPencil, cilX } from '@coreui/icons';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-user-edit-form',
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
  templateUrl: './user-edit-form.component.html',
  styleUrl: './user-edit-form.component.scss',
})
export class UserEditFormComponent implements OnChanges {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  @Input() userData!: UserData;
  @Output() editTask = new EventEmitter<{data: FormData, id: number}>();
  @Output() exitTask = new EventEmitter<void>();

  protected icons = { cilPencil, cilX };
  protected valid: boolean | undefined;
  protected editForm: FormGroup;
  protected imageChangedEvent: Event | null = null;
  protected croppedImage: Blob | null = null;
  protected croppedImageUrl?: string;
  protected resetPicture = false;
  protected showErrors = false;
  protected file: string = '';
  protected modalReady = false;

  constructor(private formBuilder: FormBuilder) {
    this.editForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.minLength(6)]],
      position: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      birthDate: ['', [Validators.required]],
      username: [ '', [Validators.required, Validators.minLength(7), Validators.pattern(/^[a-zA-Z0-9.-]+$/)]],
      password: ['', [Validators.minLength(8), Validators.pattern(/^(?=.*[A-Za-z])(?=.*\d).+$/)]],
      repeatPassword: [''],
      roles: [[1,2]],
      picture: [null as Blob | null],
      disabled: [false]
    },
    {
      validators: [passwordMatchValidator],
    });
  }

  public ngOnChanges(): void {
    this.clearUserData();

    if (this.userData.pictureId) this.file = environment.apiUrl + '/images/' + this.userData.pictureId;;

    this.editForm.get('name')?.setValue(this.userData.name);
    this.editForm.get('position')?.setValue(this.userData.position);
    this.editForm.get('email')?.setValue(this.userData.email);
    this.editForm.get('birthDate')?.setValue(this.userData.birthDate);
    this.editForm.get('username')?.setValue(this.userData.username);
    this.editForm.get('roles')?.setValue(this.userData.roles);
    this.editForm.get('disabled')?.setValue(!this.userData.activated);
  }

  protected onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) {
      this.resetImageCropper();
      input.value = '';
      return;
    }

    if (input.files[0].size > 2 * 1024 * 1024) {
      this.editForm.get('picture')?.setErrors({ imageTooLarge: true });
      this.resetImageCropper();
      input.value = '';
      return;
    }

    this.editForm.get('picture')?.setErrors({ imageTooLarge: false });
    this.imageChangedEvent = event;
    this.toggleModal();
  }

  protected imageCropped(event: ImageCroppedEvent): void {
    if (!event.blob) return;
    const photoControl = this.editForm.get('picture');

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
    this.editForm.get('picture')?.setValue(null);
    this.resetPicture = true;
  }

  protected clearUserData(): void {
    this.editForm.reset({
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

    this.resetImageCropper();
    this.file = '';
    this.editForm.get('picture')?.setValue(null);
    this.resetPicture = false;
  }

  protected onExit(): void {
    this.clearUserData();

    if (this.fileInput?.nativeElement) {
      this.fileInput.nativeElement.value = '';
    }

    this.file = "";
    this.exitTask.emit();
  }

  protected onSubmit(): void {
    if (!this.editForm.valid) {
      this.showErrors = true;
      return;
    }

    const formData = new FormData();
    Object.entries(this.editForm.value).forEach(([key, value]) => {
      if (key === 'repeatPassword') return;
      if (key === 'picture') return;
      if (key === 'password' && !value) return;
      if (key === 'disabled') return;
      formData.append(key, String(value));
    });

    formData.append('activated', this.editForm.get('disabled') ? 'true' : 'false');

    if (this.resetPicture) {
      formData.append('resetPicture', 'true');
    } else if (this.file && this.croppedImage) {
      formData.append('picture', this.croppedImage, 'profile.png');
    }

    this.editTask.emit({data: formData, id: this.userData.id});
    this.onExit();
  }
}
