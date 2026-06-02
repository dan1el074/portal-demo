import { HomeInfo } from './../interface/home.interface';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from './../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class HomeService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public getHomeInfo(): Observable<any> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<HomeInfo>>(this.api + '/api/info/home', { headers });
  }
}
