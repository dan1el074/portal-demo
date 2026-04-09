import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { UpdateDepartmentMemorando, Memorando, NewMemorando, MemorandoList } from '../interface/memorando.interface';
import { OrderInfo } from '../interface/erp.interface';

@Injectable({
  providedIn: 'root',
})
export class MemorandoService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public findAll(): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<MemorandoList>>(this.api + '/api/memorando', { headers });
  }

  public findById(id: number): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Memorando>(this.api + '/api/memorando/' + id, { headers });
  }

  public searchOrder(order: number): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<OrderInfo>>(this.api + '/api/erp/order/' + order, { headers });
  }

  public insert(data: NewMemorando): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.post<Memorando>(this.api + '/api/memorando', data, { headers });
  }

  public update(id: number, data: NewMemorando): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<Memorando>(this.api + '/api/memorando/' + id, data, { headers });
  }

  public sign(id: number): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<Memorando>(this.api + '/api/memorando/sign/' + id, null, { headers });
  }

  public disable(id: number): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<Memorando>(this.api + '/api/memorando/disable/' + id, null, { headers });
  }

  public rollback(id: number): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<Memorando>(this.api + '/api/memorando/rollback/' + id, null, { headers });
  }

  public delete(id: number): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.delete<void>(this.api + '/api/memorando/' + id, { headers });
  }

  public updateSignatures(id: number, data: UpdateDepartmentMemorando): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<Memorando>(this.api + '/api/memorando/updateSignatures/' + id, data, { headers });
  }
}
