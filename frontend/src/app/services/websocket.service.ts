import { Injectable, NgZone, signal } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { ToastrService } from 'ngx-toastr';
import { Notification, NotificationSocketMessage } from './../interface/notification.interface';

@Injectable({
  providedIn: 'root'
})
export class NotificationWebSocketService {
  private socket: WebSocket | null = null;
  private currentToken: string | null = null;
  private reconnectTimeout: any = null;
  private manualDisconnect = false;

  public notifications = signal<Notification[]>([]);
  public unreadCount = signal<number>(0);

  constructor(
    private toasterService: ToastrService,
    private router: Router,
    private ngZone: NgZone
  ) {}

  public connect(token: string) {
    this.currentToken = token;
    this.manualDisconnect = false;

    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout);
      this.reconnectTimeout = null;
    }

    if (!token || this.isTokenExpired(token)) {
      this.handleUnauthorized();
      return;
    }

    if (this.socket && (
      this.socket.readyState === WebSocket.OPEN ||
      this.socket.readyState === WebSocket.CONNECTING
    )) {
      return;
    }

    const wsBaseUrl = environment.apiUrl.replace(/^http/, 'ws');
    const wsUrl = `${wsBaseUrl}/ws/notifications?token=${token}`;

    this.socket = new WebSocket(wsUrl);

    this.socket.onmessage = (event) => {
      this.ngZone.run(() => {
        const data: NotificationSocketMessage = JSON.parse(event.data);

        if (data.type === 'NEW_NOTIFICATION' && data.notification) {
          this.notifications.update(current => [data.notification!, ...current]);
          this.unreadCount.set(data.unreadCount);
          this.toasterService.info(data.notification.message, 'Nova notificação');
          return;
        }

        if (data.type === 'UNREAD_COUNT_UPDATED') {
          this.unreadCount.set(data.unreadCount);
          return;
        }

        if (data.type === 'NOTIFICATION_VIEWED' && data.notificationId != null) {
          this.markAsViewedLocal(data.notificationId);
          this.unreadCount.set(data.unreadCount);
          return;
        }

        if (data.type === 'NOTIFICATION_REMOVED' && data.notificationId != null) {
          this.removeLocal(data.notificationId);
          this.unreadCount.set(data.unreadCount);
          return;
        }

        if (data.type === 'NOTIFICATION_REFERENCE_REMOVED' && data.referenceId != null) {
          this.removeByReference(data.referenceId);
          this.unreadCount.set(data.unreadCount);
          return;
        }
      });
    };

    this.socket.onclose = () => {
      console.warn('WebSocket fechado');
      this.socket = null;

      if (this.manualDisconnect) {
        return;
      }

      if (!this.currentToken || this.isTokenExpired(this.currentToken)) {
        this.handleUnauthorized();
        return;
      }

      this.reconnectTimeout = setTimeout(() => {
        this.connect(this.currentToken!);
      }, 3000);
    };

    this.socket.onerror = () => {
      console.error('Erro ao conectar WebSocket');
    };
  }

  public disconnect() {
    this.manualDisconnect = true;
    this.currentToken = null;

    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout);
      this.reconnectTimeout = null;
    }

    this.socket?.close();
    this.socket = null;
  }

  public setInitialNotifications(list: Notification[]) {
    this.notifications.set(list);
  }

  public setInitialUnreadCount(count: number) {
    this.unreadCount.set(count);
  }

  public markAsViewedLocal(notificationId: number) {
    this.notifications.update(list =>
      list.map(n => n.id === notificationId ? { ...n, viewed: true } : n)
    );
    this.recalculateNotificationCount();
  }

  public removeLocal(notificationId: number) {
    this.notifications.update(list => list.filter(n => n.id !== notificationId));
    this.recalculateNotificationCount();
  }

  public removeByReference(referenceId: number) {
    this.notifications.update(list => list.filter(n => n.referenceId !== referenceId));
    this.recalculateNotificationCount();
  }

  private recalculateNotificationCount(): void {
    const unread = this.notifications().filter(n => !n.viewed).length;
    this.unreadCount.set(unread);
  }

  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const exp = payload.exp;

      if (!exp) {
        return true;
      }

      const nowInSeconds = Math.floor(Date.now() / 1000);
      return exp <= nowInSeconds;
    } catch {
      return true;
    }
  }

  private handleUnauthorized(): void {
    this.disconnect();
    localStorage.removeItem('auth-token');
    this.router.navigateByUrl('/login');
  }
}
