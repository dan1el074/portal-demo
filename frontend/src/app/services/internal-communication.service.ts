import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Position } from '../interface/position.interface';
import { environment } from '../../environments/environment';
import { NewInternalCommunication } from '../interface/internal-communication.interface';

@Injectable({
  providedIn: 'root',
})
export class InternalCommunicationService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public findAll(): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<Position>>(this.api + '/api/ci', { headers });
  }

  public findById(id: number): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<Position>>(this.api + '/api/ci/' + id, { headers });
  }

  public searchOrder(order: number): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<Position>>(this.api + '/api/erp/order/' + order, { headers });
  }

  public insert(data: NewInternalCommunication): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.post<Array<Position>>(this.api + '/api/ci', data, { headers });
  }

}
