import { UserSummary } from "./user.interface";

export interface Notification {
  id: number;
  message: string;
  actionUrl?: string;
  viewed: boolean;
  autoDelete: boolean;
  type: string;
  referenceId: number;
  createdBy: UserSummary;
  createdAt: string;
}

export interface NotificationSocketMessage {
  type: 'NEW_NOTIFICATION'| 'UNREAD_COUNT_UPDATED'| 'NOTIFICATION_VIEWED'| 'NOTIFICATION_REMOVED'| 'NOTIFICATION_REFERENCE_REMOVED';
  notification?: Notification;
  unreadCount: number;
  notificationId?: number;
  referenceId?: number;
}

