import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EMPTY, Observable, expand, reduce } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  TrelloIntegrationConsultResult,
  TrelloIntegrationPage,
  TrelloIntegrationRecord,
  TrelloIntegrationSettings,
  TrelloIntegrationSummary,
} from '../interface/trello-integration.interface';

@Injectable({ providedIn: 'root' })
export class TrelloIntegrationService {
  private readonly api = environment.apiUrl + '/api/trello-integration';

  constructor(private http: HttpClient) {}

  findAll(
    page: number,
    size: number,
    search?: string,
    sortColumn = 'importedAt',
    sortDirection: 'asc' | 'desc' = 'desc',
  ): Observable<TrelloIntegrationPage> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', `${sortColumn},${sortDirection}`);
    const trimmedSearch = search?.trim();
    if (trimmedSearch) params = params.set('search', trimmedSearch);

    return this.http.get<TrelloIntegrationPage>(this.api, { params });
  }

  findSnapshot(): Observable<TrelloIntegrationRecord[]> {
    const pageSize = 100;
    return this.findAll(0, pageSize).pipe(
      expand((page, index) => index + 1 < page.totalPages
        ? this.findAll(index + 1, pageSize)
        : EMPTY),
      reduce(
        (records, page) => [...records, ...(page.content ?? [])],
        [] as TrelloIntegrationRecord[],
      ),
    );
  }

  getSummary(): Observable<TrelloIntegrationSummary> {
    return this.http.get<TrelloIntegrationSummary>(`${this.api}/summary`);
  }

  findById(id: number): Observable<TrelloIntegrationRecord> {
    return this.http.get<TrelloIntegrationRecord>(`${this.api}/${id}`);
  }

  consultErp(): Observable<TrelloIntegrationConsultResult> {
    return this.http.post<TrelloIntegrationConsultResult>(`${this.api}/consult`, null);
  }

  resend(id: number): Observable<TrelloIntegrationRecord> {
    return this.http.post<TrelloIntegrationRecord>(`${this.api}/${id}/resend`, null);
  }

  getSettings(): Observable<TrelloIntegrationSettings> {
    return this.http.get<TrelloIntegrationSettings>(`${this.api}/settings`);
  }

  updateSettings(settings: TrelloIntegrationSettings): Observable<TrelloIntegrationSettings> {
    return this.http.put<TrelloIntegrationSettings>(`${this.api}/settings`, settings);
  }
}
