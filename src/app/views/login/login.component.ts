import { Component, OnInit, ViewChild } from '@angular/core';
import { LoginFormComponent } from './login-form/login-form.component';
import { SignupFormComponent } from './signup-form/signup-form.component';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login.service';

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

  constructor(
    private router: Router,
    private loginService: LoginService
  ) {}

  ngOnInit(): void {
    sessionStorage.clear();
  }

  async onLogin(credentials: Credential) {
    // this.spinner.show("loginSpinner");
    // await new Promise(resolve => setTimeout(resolve, 500));

    this.loginService.login(credentials.username, credentials.password).subscribe({
      next: () => {
        this.router.navigate(['home']);
      },
      error: (error) => {
        // this.spinner.hide("loginSpinner");

        this.loginFormComp.showError();
        console.log(error.error.error);
        const statusCode = error.error.error == "user_disabled" ? 422 : error.status;
        this.showError(statusCode);
      }
    });
  }

  showError(status: number) {
    if (status == 422) {
      // this.toastr.error("Usuário desativado!")
      return
    }
    if (status == 400) {
      // this.toastr.error("Usuário ou senha inválido!")
      return
    }
    // this.toastr.error("Erro ao comunicar com o servidor!")
  }

  changePage() {
    this.togglePage = !this.togglePage;
    this.loginFormComp.resetValidation();
  }
}
