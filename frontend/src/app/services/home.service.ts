import { HomeInfo } from './../interface/home.interface';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from './../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class HomeService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public getHomeInfo(): Observable<any> {
    return this.http.get<Array<HomeInfo>>(this.api + '/api/info/home');
  }

  public clearAllCache(): Observable<any> {
    return this.http.put<void>(this.api + '/api/info/clear-all', null);
  }
}
