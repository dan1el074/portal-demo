import { HttpClient, HttpHeaders } from '@angular/common/http';
import { StepFlowVideoUploadInfo } from '../interface/step-flow.interface';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class StepFlowVideosService {
  private api = environment.apiUrl + '/api/step-flow';

  constructor(private http: HttpClient) {}

  public create(orderId: number, name: string): Observable<StepFlowVideoUploadInfo> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.post<StepFlowVideoUploadInfo>(`${this.api}/${orderId}/video`, { name }, { headers });
  }

  public complete(id: number): Observable<void> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<void>(`${this.api}/video/${id}/complete`, null, { headers });
  }

  public deleteById(id: number): Observable<void> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.delete<void>(`${this.api}/video/${id}`, { headers });
  }
}
