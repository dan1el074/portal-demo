import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Me, UserMinData } from './../interface/user.interface';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private api = environment.apiUrl + '/api/user';

  constructor(private http: HttpClient) {}

  getUserData(): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Me>(this.api + '/me', { headers });
  }

  findAll(): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<UserMinData>>(this.api, { headers });
  }
}
