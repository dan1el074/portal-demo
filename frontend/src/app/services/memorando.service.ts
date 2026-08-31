import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { UpdateDepartmentMemorando, Memorando, NewMemorando, MemorandoGroup, MemorandoNavigation, MemorandoPage, MemorandoStatus, MemorandoSummary } from '../interface/memorando.interface';
import { OrderInfo } from '../interface/erp.interface';

@Injectable({
  providedIn: 'root',
})
export class MemorandoService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public findAll(
    page: number,
    size: number,
    group: MemorandoGroup,
    sortColumn?: string,
    sortDirection?: 'asc' | 'desc',
    search?: string,
    status?: MemorandoStatus,
    fullText = false
  ): Observable<MemorandoPage> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('group', group)
      .set('fullText', fullText);
    const trimmedSearch = search?.trim();

    if (sortColumn) params = params.set('sort', `${sortColumn},${sortDirection ?? 'asc'}`);
    if (trimmedSearch) params = params.set('search', trimmedSearch);
    if (status) params = params.set('status', status);

    return this.http.get<MemorandoPage>(this.api + '/api/memorando', { params });
  }

  public getSummary(): Observable<MemorandoSummary> {
    return this.http.get<MemorandoSummary>(this.api + '/api/memorando/summary');
  }

  public findById(id: number): Observable<Memorando> {
    return this.http.get<Memorando>(this.api + '/api/memorando/' + id);
  }

  public getNavigation(id: number): Observable<MemorandoNavigation> {
    return this.http.get<MemorandoNavigation>(`${this.api}/api/memorando/${id}/navigation`);
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
