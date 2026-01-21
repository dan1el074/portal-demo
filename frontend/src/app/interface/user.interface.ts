import { Role } from './role.interface';
import { Notification } from './notification.interface';

export interface UserTable {
  id: number;
  pictureId: string | null;
  name: string;
  username: string;
  position: string;
  email: string;
  activated: boolean;
  updateAt: string;
}

export interface Me {
  id: number;
  name: string;
  email: string;
  position: string | null;
  birthDate: string;
  pictureId: number | null;
  activated: boolean;
  username: string;
  roles: Array<Role>;
  notifications: Array<Notification>;
}

export interface UserMinData {
  id: number;
  pictureId: number | null;
  name: string;
  username: string;
  position: string;
  email: string;
  activated: boolean;
  updateAt: string;
}

export interface UserData {
  id: number;
  pictureId: number | null;
  name: string;
  position: string;
  email: string;
  birthDate: string;
  username: string;
  roles: Array<Role>;
  activated: boolean;
}

export interface UserSummary {
  id: number;
  name: string;
  pictureId: number;
}
