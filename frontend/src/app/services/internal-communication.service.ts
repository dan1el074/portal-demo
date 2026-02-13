import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Position } from '../interface/position.interface';
import { environment } from '../../environments/environment';
import { InternalCommunication, NewInternalCommunication } from '../interface/internal-communication.interface';
import { OrderInfo } from '../interface/erp.interface';

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

    return this.http.get<Array<InternalCommunication>>(this.api + '/api/ci', { headers });
  }

  public findById(id: number): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<InternalCommunication>(this.api + '/api/ci/' + id, { headers });
  }

  public searchOrder(order: number): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<OrderInfo>>(this.api + '/api/erp/order/' + order, { headers });
  }

  public insert(data: NewInternalCommunication): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.post<InternalCommunication>(this.api + '/api/ci', data, { headers });
  }

  public update(id: number, data: NewInternalCommunication): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<InternalCommunication>(this.api + '/api/ci/' + id, data, { headers });
  }

  public sign(id: number): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<InternalCommunication>(this.api + '/api/ci/sign/' + id, null, { headers });
  }

  public disable(id: number): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<InternalCommunication>(this.api + '/api/ci/disable/' + id, null, { headers });
  }

  public delete(id: number): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.delete<void>(this.api + '/api/ci/' + id, { headers });
  }

}
