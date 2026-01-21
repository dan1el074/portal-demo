import { UserSummary } from "./user.interface";

export interface Notification {
  id: number;
  title: string;
  isNew: boolean;
  createdAt: string;
  createdBy: UserSummary;
}
