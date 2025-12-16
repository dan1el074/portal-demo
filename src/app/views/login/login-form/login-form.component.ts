import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

export interface Credential {
  username: string;
  password: string;
}

@Component({
  selector: 'app-login-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.scss',
})
export class LoginFormComponent {
  @Output() loginTask = new EventEmitter<Credential>();
  protected badRequest = false;
  protected loginForm: FormGroup;

  constructor(private formBuilder: FormBuilder) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  onSubmit(): void {
    if (!this.loginForm.valid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    const { username, password } = this.loginForm.value;
    const obj = {
      username: username,
      password: password,
    };

    this.loginTask.emit(obj);
    this.badRequest = false;
  }

  resetValidation(): void {
    setTimeout(() => {
      this.loginForm.markAsUntouched();
      this.badRequest = false;
    }, 300);
  }

  showError() {
    this.badRequest = true;
  }
}
