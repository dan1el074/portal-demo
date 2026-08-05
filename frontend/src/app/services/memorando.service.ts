import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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
    return this.http.get<Array<MemorandoList>>(this.api + '/api/memorando');
  }

  public findById(id: number): Observable<any> {
    return this.http.get<Memorando>(this.api + '/api/memorando/' + id);
  }

  public searchOrder(order: number): Observable<any> {
    return this.http.get<Array<OrderInfo>>(this.api + '/api/erp/order/' + order);
  }

  public insert(data: NewMemorando): Observable<any> {
    return this.http.post<Memorando>(this.api + '/api/memorando', data);
  }

  public update(id: number, data: NewMemorando): Observable<any> {
    return this.http.put<Memorando>(this.api + '/api/memorando/' + id, data);
  }

  public sign(id: number): Observable<any> {
    return this.http.put<Memorando>(this.api + '/api/memorando/sign/' + id, null);
  }

  public disable(id: number): Observable<any> {
    return this.http.put<Memorando>(this.api + '/api/memorando/disable/' + id, null);
  }

  public rollback(id: number): Observable<any> {
    return this.http.put<Memorando>(this.api + '/api/memorando/rollback/' + id, null);
  }

  public delete(id: number): Observable<any> {
    return this.http.delete<void>(this.api + '/api/memorando/' + id);
  }

  public updateSignatures(id: number, data: UpdateDepartmentMemorando): Observable<any> {
    return this.http.put<Memorando>(this.api + '/api/memorando/updateSignatures/' + id, data);
  }
}
