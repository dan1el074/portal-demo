import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormControlDirective, FormFloatingDirective } from '@coreui/angular';

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
  protected valid: boolean | undefined;
  protected signupForm: FormGroup;

  constructor(private formBuilder: FormBuilder) {
    this.signupForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.minLength(6)]],
      email: ['', [Validators.required, Validators.email]],
    });
  }

  onSignUp(): void {
    if (!this.signupForm.valid) {
      this.showError();
      return;
    }
    this.resetValidation();
    this.signupForm.setValue({ name: "", email: ""});
  }

  resetValidation(): void {
    this.valid = undefined;
  }

  showError(): void {
    this.valid = false;
  }

}
