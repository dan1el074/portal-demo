import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Me, UserData, UserMinData } from './../interface/user.interface';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private api = environment.apiUrl + '/api/user';

  constructor(private http: HttpClient) {}

  public getUserData(): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Me>(this.api + '/me', { headers });
  }

  public findAll(): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<UserMinData>>(this.api, { headers });
  }

  public findById(id: number): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<UserData>(this.api + '/' + id, { headers });
  }

  public insert(data: FormData): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.post<Array<UserMinData>>(this.api, data, { headers });
  }

  public update(id: number, data: FormData): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<Array<UserMinData>>(this.api + '/' + id, data, { headers });
  }

  public deactivateUser(id: number): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<Array<UserMinData>>(this.api + '/deactivate-user/' + id, null, { headers });
  }
}
