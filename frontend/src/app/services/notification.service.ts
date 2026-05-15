import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { Notification } from './../interface/notification.interface';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private api = environment.apiUrl + '/api/notifications';

  constructor(private http: HttpClient) {}

  public getMyNotifications(): Observable<Notification[]> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<Notification>>(this.api, { headers });
  }

  public getUnreadCount(): Observable<{ unreadCount: number }> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<{ unreadCount: number }>(this.api + "/unread-count", { headers });
  }

  public markAsViewed(id: number): Observable<void> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.patch<void>(this.api + '/' + id + '/view', {}, { headers });
  }

  public delete(id: number): Observable<void> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.delete<void>(`${this.api}/${id}`, { headers });
  }
}
