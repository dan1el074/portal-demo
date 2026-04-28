import { Component, EventEmitter, Output, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormControlDirective, FormFloatingDirective } from '@coreui/angular';
import { RequestAccess } from '../../../interface/user.interface';

@Component({
  selector: 'app-signup-form',
  imports: [
    FormFloatingDirective,
    FormControlDirective,
    ReactiveFormsModule
  ],
  templateUrl: './signup-form.component.html',
  styleUrl: './signup-form.component.scss',
})
export class SignupFormComponent {
  @Output() requestAccess = new EventEmitter<RequestAccess>();
  protected valid: boolean | undefined;
  protected signupForm: FormGroup;
  protected loading: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.signupForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.minLength(6)]],
      email: ['', [Validators.required, Validators.email]],
    });
  }

  public onSignUp(): void {
    if (!this.signupForm.valid) {
      this.showError();
      return;
    }

    this.loading = true;
    this.resetValidation();
    this.requestAccess.emit(this.signupForm.value);
  }

  public clearForm(): void {
    this.signupForm.setValue({ name: "", email: ""});
  }

  public liberate(): void {
    this.loading = false;
    this.cdr.detectChanges();
  }

  public resetValidation(): void {
    this.valid = undefined;
  }

  public showError(): void {
    this.valid = false;
  }
}
