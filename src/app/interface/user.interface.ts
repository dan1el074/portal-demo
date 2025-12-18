import { Role } from './role.interface';
import { Notification } from './notification.interface';

export interface UserTable {
  id: number;
  picture: string | null;
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
  picture: string | null;
  activated: boolean;
  username: string;
  roles: Array<Role>;
  notifications: Array<Notification>;
}

export interface UserMinData {
  id: number;
  picture: string | null;
  name: string;
  username: string;
  position: string;
  email: string;
  activated: boolean;
  updateAt: string;
}
