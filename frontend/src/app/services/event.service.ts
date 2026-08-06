import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { EventCard } from '../interface/event.interface';

@Injectable({ providedIn: 'root' })
export class EventService {
  private api = environment.apiUrl + '/api/event';

  constructor(private http: HttpClient) {}

  public insert(data: FormData): Observable<EventCard> {
    return this.http.post<EventCard>(this.api, data);
  }

  public update(id: number, data: FormData): Observable<EventCard> {
    return this.http.put<EventCard>(`${this.api}/${id}`, data);
  }

  public delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
