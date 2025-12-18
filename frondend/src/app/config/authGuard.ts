import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { catchError, map, Observable, of, tap } from 'rxjs';
import { UserService } from '../services/user.service';
import { ToastrService } from 'ngx-toastr';
import { Me } from '../interface/user.interface';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard {
    private user!: Me | null;

    constructor(
      private userService: UserService,
      private router: Router,
      private toaster: ToastrService
    ) {}

    canActivate(route: ActivatedRouteSnapshot): Observable<boolean> | boolean {
      if (!this.isAuthenticated()) {
        this.clearUser();
        this.router.navigate(['/login']);
        return false;
      }

      const requiredRoles: string[] = route.data?.['roles'];
      if (!requiredRoles || requiredRoles.length == 0) {
        return true;
      }

      return this.getUser().pipe(
        map((user) => {
          const userRoles = user.roles.map(
            (role) => role.authority
          );

          const hasRole = requiredRoles.some(role =>
            userRoles.includes(role)
          );

          if (!hasRole) {
            this.showError(403);
          }

          return hasRole;
        }),
        catchError(() => {
          this.showError(401);
          return of(false);
        })
      );
    }

    isAuthenticated(): boolean {
      return sessionStorage.getItem('auth-token') ? true : false;
    }

    getUser(): Observable<Me> {
      if (this.user) {
        return of(this.user);
      }

      const storageUser = localStorage.getItem('user');
      if (storageUser) {
        this.user = JSON.parse(storageUser);
        return of(this.user as Me);
      }

      return this.userService.getUserData().pipe(
        tap((data) => {
          this.setUser(data);
        }),
        catchError((err) => {
          this.showError(err.status);
          throw new Error('');
        })
      );
    }

    setUser(data: Me) {
      this.user = data;
      localStorage.setItem('user', JSON.stringify(data));
    }

    showError(status: number) {
      switch(status) {
        case 401:
          this.toaster.error('Sessão expirada!');
          this.router.navigate(['/login']);
          break;
        case 403:
          this.toaster.error('Acesso negado!');
          this.router.navigate(['/home']);
          break;
        default:
          this.toaster.error('Erro ao se conectar ao servidor!');
          this.router.navigate(['/login']);
          break;
      }
    }

    clearUser() {
      this.user = null;
      sessionStorage.clear();
      localStorage.removeItem('user');
    }
}
