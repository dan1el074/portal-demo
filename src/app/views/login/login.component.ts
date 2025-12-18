import { AuthGuard } from './../../config/authGuard';
import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { LoginFormComponent } from './login-form/login-form.component';
import { SignupFormComponent } from './signup-form/signup-form.component';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login.service';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';

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
  @ViewChild('signFormComp') signFormComp!: LoginFormComponent;
  togglePage = false;

  constructor(
    private router: Router,
    private loginService: LoginService,
    private authGuardService: AuthGuard,
    private toasterService: ToastrService,
    private spinner: NgxSpinnerService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    document.documentElement.setAttribute('data-coreui-theme', 'light');
    this.authGuardService.clearUser();
  }

  async onLogin(credentials: Credential): Promise<void> {
    this.spinner.show("loginSpinner");
    await new Promise(resolve => setTimeout(resolve, 500));

    this.loginService.login(credentials.username, credentials.password).subscribe({
      next: () => {
        this.router.navigate(['home']);
      },
      error: (error) => {
        this.spinner.hide("loginSpinner");
        this.showError(error.error.error == "user_disabled" ? 422 : error.status);
        this.cdr.detectChanges();
      }
    });
  }

  showError(status: number): void {
    if (status == 422) {
      this.loginFormComp.showError();
      this.toasterService.error("Usuário desativado!")
      return
    }
    if (status == 400) {
      this.loginFormComp.showError();
      this.toasterService.error("Usuário ou senha inválido!")
      return
    }
    this.toasterService.error("Erro ao comunicar com o servidor!")
  }

  changePage(): void {
    this.togglePage = !this.togglePage;
    this.loginFormComp.resetValidation();
    this.signFormComp.resetValidation();
  }
}
