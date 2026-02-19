import { Picture } from './image.interface';
import { Role } from './role.interface';
import { Notification } from './notification.interface';
import { Position } from './position.interface';

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
  supportToken: string | null;
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
  positionId: number;
  email: string;
  birthDate: string;
  username: string;
  supportToken: string | null;
  roles: Array<Role>;
  activated: boolean;
}

export interface UserSummary {
  id: number;
  name: string;
  position: Position;
  picture: Picture | null;
}
