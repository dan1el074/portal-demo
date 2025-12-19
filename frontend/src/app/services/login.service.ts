import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

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

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<LoginResponse> {
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
}
