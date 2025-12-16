import { Component, OnInit, ViewChild } from '@angular/core';
import { LoginFormComponent } from './login-form/login-form.component';
import { SignupFormComponent } from './signup-form/signup-form.component';
import { Router } from '@angular/router';

export interface Credential {
  username: string;
  password: string;
}

@Component({
  selector: 'app-login',
  imports: [LoginFormComponent, SignupFormComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent implements OnInit {
  @ViewChild('loginFormComp') loginFormComp!: LoginFormComponent;
  togglePage = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    sessionStorage.clear();
  }

  async onLogin(credentials: Credential) {}

  changePage() {
    this.togglePage = !this.togglePage;
    this.loginFormComp.resetValidation();
  }
}
