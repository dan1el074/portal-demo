import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormControlDirective, FormFloatingDirective } from '@coreui/angular';

export interface Credential {
  username: string;
  password: string;
}

@Component({
  selector: 'app-login-form',
  imports: [
    FormFloatingDirective,
    FormControlDirective,
    ReactiveFormsModule
  ],
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.scss',
})
export class LoginFormComponent {
  @Output() loginTask = new EventEmitter<Credential>();
  protected valid: boolean | undefined;
  protected loginForm: FormGroup;

  constructor(private formBuilder: FormBuilder) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]],
    });
  }

  onSubmit(): void {
    if (!this.loginForm.valid) {
      this.showError();
      return;
    }
    this.resetValidation();
    this.loginTask.emit(this.loginForm.value);
  }

  resetValidation(): void {
    this.valid = undefined;
  }

  showError(): void {
    this.valid = false;
  }
}
