import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthGuard } from '../config/authGuard';
import { NotificationWebSocketService } from './websocket.service';
import { RequestAccess } from '../interface/user.interface';

type LoginResponse = {
  access_token: string
}

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private api = environment.apiUrl + '/oauth2/token';
  private clientId = environment.clientId;
  private clientSecret = environment.clientSecret;

  constructor(
    private http: HttpClient,
    private authGuardService: AuthGuard,
    private websocket: NotificationWebSocketService,
    private router: Router,
  ) {}

  public login(username: string, password: string): Observable<LoginResponse> {
    const body = new HttpParams()
      .set('username', username)
      .set('password', password)
      .set('grant_type', 'password');

    const basicAuth = btoa(`${this.clientId}:${this.clientSecret}`);
    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded',
      Authorization: `Basic ${basicAuth}`,
    });

    return this.http
      .post<LoginResponse>(this.api, body.toString(), { headers })
      .pipe(
          tap((value) => {
            sessionStorage.setItem('auth-token', value.access_token);
          })
      );
  }

  public logout(): void {
    sessionStorage.removeItem('auth-token');
    this.authGuardService.clearUser();
    this.websocket.disconnect();
  }

  public logoutAndRedirect(): void {
    this.logout();
    this.router.navigateByUrl('/login');
  }

  public requestAccess(data: RequestAccess): Observable<any> {
    return this.http.post<any>(environment.apiUrl + "/api/request-access", data);
  }
}
