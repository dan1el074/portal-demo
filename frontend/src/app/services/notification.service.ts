import { HttpClient } from '@angular/common/http';
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
    return this.http.get<Array<Notification>>(this.api);
  }

  public getUnreadCount(): Observable<{ unreadCount: number }> {
    return this.http.get<{ unreadCount: number }>(this.api + "/unread-count");
  }

  public markAsViewed(id: number): Observable<void> {
    return this.http.patch<void>(this.api + '/' + id + '/view', {});
  }

  public markAllAsViewed(): Observable<void> {
    return this.http.put<void>(this.api + '/all/view', {});
  }

  public delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
