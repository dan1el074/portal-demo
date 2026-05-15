import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Position, PositionFormImput, PositionMin } from './../interface/position.interface';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PostitionService {
  private api = environment.apiUrl + '/api/position';

  constructor(private http: HttpClient) {}

  public findAll(): Observable<any> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<Position>>(this.api, { headers });
  }

  public list(): Observable<any> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<PositionMin>>(this.api + '/min', { headers });
  }

  public findById(id: number): Observable<any> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<Position>>(this.api + '/' + id, { headers });
  }

  public insert(data: PositionFormImput): Observable<any> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.post<Array<Position>>(this.api, data, { headers });
  }

  public update(id: number, data: PositionFormImput): Observable<any> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<Array<PositionMin>>(this.api + '/' + id, data, { headers });
  }

  public deactive(id: number): Observable<any> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<Array<PositionMin>>(this.api + '/deactive/' + id, null, { headers });
  }
}
