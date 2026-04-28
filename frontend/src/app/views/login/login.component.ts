import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { LoginService } from '../../services/login.service';
import { LoginFormComponent } from './login-form/login-form.component';
import { SignupFormComponent } from './signup-form/signup-form.component';
import { Credential, RequestAccess } from './../../interface/user.interface';

@Component({
  selector: 'app-login',
  imports: [LoginFormComponent, SignupFormComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginComponent implements OnInit {
  @ViewChild('loginFormComp') loginFormComp!: LoginFormComponent;
  @ViewChild('signFormComp') signFormComp!: SignupFormComponent;
  togglePage = false;

  constructor(
    private router: Router,
    private loginService: LoginService,
    private toasterService: ToastrService,
    private spinner: NgxSpinnerService,
    private cdr: ChangeDetectorRef,
  ) {
  }

  public ngOnInit(): void {
    document.documentElement.setAttribute('data-coreui-theme', 'light');
    this.loginService.logout();
    setTimeout(() => console.clear(), 200);
  }

  protected async onLogin(credentials: Credential): Promise<void> {
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

  protected onRequestAccess(data: RequestAccess): void {
    this.loginService.requestAccess(data).subscribe({
      next: () => {
        this.signFormComp.clearForm();
        this.signFormComp.liberate();
        this.toasterService.success("Requisição enviada com sucesso!");
      },
      error: () => {
        this.signFormComp.liberate();
        this.toasterService.error("Erro ao enviar requisição!");
      },
    });
  }

  protected changePage(): void {
    this.togglePage = !this.togglePage;
    this.loginFormComp.resetValidation();
    this.signFormComp.resetValidation();
  }

  private showError(status: number): void {
    if (status == 422) {
      this.loginFormComp.showError();
      this.toasterService.error("Usuário desativado!");
      return;
    }
    if (status == 400) {
      this.loginFormComp.showError();
      this.toasterService.error("Usuário ou senha inválido!");
      return;
    }
    this.toasterService.error("Erro ao comunicar com o servidor!");
  }
}
