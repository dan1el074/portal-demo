import { HttpClient } from '@angular/common/http';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from '../../services/toast.service';
import { EMPTY, Subscription, catchError, interval, startWith, switchMap } from 'rxjs';
import { LoginService } from '../../services/login.service';
import { BUILD_VERSION } from '../../generated/build-version';
import { LoginFormComponent } from './login-form/login-form.component';
import { SignupFormComponent } from './signup-form/signup-form.component';
import { Credential, RequestAccess } from './../../interface/user.interface';

interface PublishedAppVersion {
  version: string;
}

@Component({
  selector: 'app-login',
  imports: [LoginFormComponent, SignupFormComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginComponent implements OnInit, OnDestroy {
  @ViewChild('loginFormComp') loginFormComp!: LoginFormComponent;
  @ViewChild('signFormComp') signFormComp!: SignupFormComponent;
  togglePage = false;
  protected updateAvailable = false;
  private publishedVersion?: string;
  private versionCheckSubscription?: Subscription;

  constructor(
    private router: Router,
    private loginService: LoginService,
    private http: HttpClient,
    private toasterService: ToastrService,
    private spinner: NgxSpinnerService,
    private cdr: ChangeDetectorRef,
  ) {
  }

  public ngOnInit(): void {
    document.documentElement.setAttribute('data-coreui-theme', 'light');
    this.loginService.logout();
    sessionStorage.setItem('first-access', 'true');
    this.watchApplicationVersion();
    setTimeout(() => console.clear(), 200);
  }

  public ngOnDestroy(): void {
    this.versionCheckSubscription?.unsubscribe();
  }

  private watchApplicationVersion(): void {
    this.versionCheckSubscription = interval(5 * 60 * 1000).pipe(
      startWith(0),
      switchMap(() => this.http.get<PublishedAppVersion>(
        `/assets/app-version.json?v=${Date.now()}`
      ).pipe(catchError(() => EMPTY)))
    ).subscribe(({ version }) => {
      this.publishedVersion = version;
      this.updateAvailable = version !== BUILD_VERSION;
      this.cdr.detectChanges();
    });
  }

  protected async updateApplication(): Promise<void> {
    if ('caches' in window) {
      const cacheNames = await window.caches.keys();
      await Promise.all(cacheNames.map(cacheName => window.caches.delete(cacheName)));
    }

    const url = new URL(window.location.href);
    url.searchParams.set('_appVersion', this.publishedVersion ?? Date.now().toString());
    window.location.replace(url.toString());
  }

  protected async onLogin(credentials: Credential): Promise<void> {
    this.spinner.show("loginSpinner");
    await new Promise(resolve => setTimeout(resolve, 500));

    this.loginService.login(credentials.username.trim().toLowerCase(), credentials.password.trim()).subscribe({
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
