import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { AdminDashboard, PagedResult, StepFlowData, StepFlowOrder, StepFlowOrderInfo } from './../interface/step-flow.interface';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class StepFlowService {
  private api = environment.apiUrl + '/api/step-flow';

  constructor(private http: HttpClient) {}

  public findById(id: number): Observable<StepFlowOrder> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<StepFlowOrder>(this.api + '/' + id, { headers });
  }

  public findAll( page: number, size: number, sortColumn?: string, sortDirection?: 'asc' | 'desc', search?: string): Observable<PagedResult<StepFlowData>> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
    let params = new HttpParams().set('page', page).set('size', size);

    if (sortColumn) params = params.set('sort', `${sortColumn},${sortDirection ?? 'asc'}`);
    if (search) params = params.set('search', search);

    return this.http.get<PagedResult<StepFlowData>>(this.api, { headers, params });
  }

  public findAllFromCurrentStep(index: number): Observable<Array<StepFlowData>> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<StepFlowData>>(this.api + '/step/' + index, { headers });
  }

  public getDashboard(): Observable<AdminDashboard> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<AdminDashboard>(this.api + '/admin', { headers });
  }

  public findOrderInfoByNumber(orderNumber: number): Observable<StepFlowOrderInfo> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<StepFlowOrderInfo>(this.api + '/erp/' + orderNumber, { headers });
  }

  public create(orderNumber: number): Observable<void> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.post<void>(this.api + '/' + orderNumber, {}, { headers });
  }

  public updateStep(orderId: number, formData: FormData): Observable<StepFlowOrder> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<StepFlowOrder>(this.api + '/' + orderId, formData, { headers });
  }

  public nextStep(orderId: number): Observable<void> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<void>(this.api + '/' + orderId + '/nextStep', null, { headers });
  }
}
