import { CommonModule } from '@angular/common';
import { IconDirective } from '@coreui/icons-angular';
import { DatePickerComponent, MultiSelectComponent, MultiSelectOptgroupComponent, MultiSelectOptionComponent } from '@coreui/angular-pro';
import { Component, ElementRef, EventEmitter, Input, OnChanges, Output, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonCloseDirective, ButtonDirective, ColComponent, FormCheckComponent, FormCheckInputDirective, FormCheckLabelDirective, FormControlDirective, FormFloatingDirective, FormLabelDirective, FormSelectDirective, ModalBodyComponent, ModalComponent, ModalFooterComponent, ModalHeaderComponent, ModalTitleDirective, RowComponent } from '@coreui/angular';
import { passwordMatchValidator } from '../../../app/config/validators';
import { ImageCropperComponent, ImageCroppedEvent } from 'ngx-image-cropper';
import { cilPencil, cilX } from '@coreui/icons';
import { environment } from '../../../environments/environment';
import { PositionMin } from '../../../app/interface/position.interface';
import { UserEditData } from './../../../app/interface/user.interface';
import { RoleGroup } from './../../../app/interface/role.interface';

@Component({
  selector: 'app-user-edit-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ImageCropperComponent,
    ColComponent,
    RowComponent,
    FormFloatingDirective,
    FormLabelDirective,
    FormControlDirective,
    FormCheckComponent,
    FormCheckInputDirective,
    FormCheckLabelDirective,
    FormSelectDirective,
    IconDirective,
    ButtonDirective,
    ModalComponent,
    ModalHeaderComponent,
    ModalTitleDirective,
    ButtonCloseDirective,
    ModalBodyComponent,
    ModalFooterComponent,
    MultiSelectComponent,
    MultiSelectOptionComponent,
    MultiSelectOptgroupComponent,
    DatePickerComponent
  ],
  templateUrl: './user-edit-form.component.html',
  styleUrl: './user-edit-form.component.scss',
})
export class UserEditFormComponent implements OnChanges {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  @Input() roles!: Array<RoleGroup>;
  @Input() userData!: UserEditData;
  @Input() positions!: Array<PositionMin>;
  @Output() editTask = new EventEmitter<{data: FormData, id: number}>();
  @Output() exitTask = new EventEmitter<void>();

  protected icons = { cilPencil, cilX };
  protected valid: boolean | undefined;
  protected editForm: FormGroup;

  // profile image
  protected imageChangedEvent: Event | null = null;
  protected croppedImage: Blob | null = null;
  protected croppedImageUrl?: string;
  protected resetPicture = false;
  protected showErrors = false;
  protected file: string = '';
  protected modalReady = false;

  // data picker
  protected birthDate = new Date();

  constructor(private formBuilder: FormBuilder) {
    this.editForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.minLength(6)]],
      position: [0, [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      birthDate: [null, [Validators.required]],
      username: [ '', [Validators.required, Validators.minLength(7), Validators.pattern(/^[a-zA-Z0-9.-]+$/)]],
      password: ['', [Validators.minLength(8), Validators.pattern(/^(?=.*[A-Za-z])(?=.*\d).+$/)]],
      repeatPassword: [''],
      roles: [[]],
      supportToken: [null],
      picture: [null as Blob | null],
      disabled: [false]
    },
    {
      validators: [passwordMatchValidator],
    });
  }

  public ngOnChanges(): void {
    this.clearUserData();
    this.birthDate = this.parseDate(this.userData.birthDate) ?? null;

    this.editForm.get('birthDate')?.setValue(this.birthDate);
    this.editForm.get('name')?.setValue(this.userData.name);
    this.editForm.get('email')?.setValue(this.userData.email);
    this.editForm.get('username')?.setValue(this.userData.username);
    this.editForm.get('supportToken')?.setValue(this.userData.supportToken);
    this.editForm.get('disabled')?.setValue(!this.userData.activated);

    if (this.userData.pictureId) this.file = environment.apiUrl + '/images/' + this.userData.pictureId;
    if (this.userData.positionId > 0) this.editForm.get('position')?.setValue(this.userData.positionId);
    if (this.userData.roles[0] == 2) this.editForm.get('roles')?.disable();
    if (this.userData.roles[0] != 2) this.editForm.get('roles')?.setValue(this.userData.roles);
  }

  private parseDate(value: string | null): Date {
    if (!value) return new Date();

    const [year, month, day] = value.split('-').map(Number);
    return new Date(year, month - 1, day);
  }

  protected getPositionName(id: number): string {
    if (!id || id == 0) return '';

    const position = this.positions?.find(x => x.id == id);
    return position ? position.name : '';
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
      birthDate: null,
      username: '',
      password: '',
      repeatPassword: '',
      roles: [],
      supportToken: null,
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
    this.birthDate = new Date();

    this.editForm.get('roles')?.enable();
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
      if (key === 'password' && !value) return;
      if (key === 'disabled') return;
      if (key === 'picture') return;
      if (key === 'supportToken' && value == null) return;
      if (key === 'birthDate') return;
      formData.append(key, String(value));
    });

    // birthDate -> convert Date to string
    const date: Date | null = this.editForm.value.birthDate;
    const birthDateString = date ? date.toISOString().split('T')[0] : null;
    formData.append('birthDate', String(birthDateString));

    // if user is Admin, add id role
    if (this.userData.roles.find(role => role == 2)) {
      formData.append('roles', '2');
    }

    // if resetPicture = true -> send signal to backend
    if (this.resetPicture) formData.append('resetPicture', 'true');

    // change picture
    if (!this.resetPicture && this.file && this.croppedImage) {
      formData.append('picture', this.croppedImage, 'profile.png');
    }

    // if disabled input = false -> activated = true
    formData.append('activated', this.editForm.get('disabled')?.value ? 'false' : 'true');

    // // send to the father component
    this.editTask.emit({data: formData, id: this.userData.id});
    this.onExit();
  }
}
