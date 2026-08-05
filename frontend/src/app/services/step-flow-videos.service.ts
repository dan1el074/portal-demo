import { HttpClient } from '@angular/common/http';
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
    return this.http.post<StepFlowVideoUploadInfo>(`${this.api}/${orderId}/video`, { name });
  }

  public complete(id: number): Observable<void> {
    return this.http.put<void>(`${this.api}/video/${id}/complete`, null);
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/video/${id}`);
  }
}
