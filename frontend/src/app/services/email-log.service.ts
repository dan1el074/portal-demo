import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { EmailLogPage } from '../interface/email-log.interface';

@Injectable({ providedIn: 'root' })
export class EmailLogService {
  private readonly api = environment.apiUrl + '/api/email-logs';

  constructor(private http: HttpClient) {}

  list(page: number, size: number): Observable<EmailLogPage> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<EmailLogPage>(this.api, { params });
  }
}
