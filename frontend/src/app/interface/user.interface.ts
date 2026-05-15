import { Picture } from './image.interface';
import { Role } from './role.interface';
import { Notification } from './notification.interface';
import { PositionMin } from './position.interface';

export interface Credential {
  username: string;
  password: string;
}

export interface RequestAccess {
  name: string;
  email: string;
}

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
  pendingIssues: Array<PendingIssues>;
}

export interface PendingIssues {
  id: number,
  title: string;
  action: string;
  urgency: 'urgent' | 'pending';
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

export interface UserConfigData {
  pictureId: number | null;
  position: string;
  name: string;
  email: string;
  birthDate: string;
}

export interface UserEditData {
  id: number;
  pictureId: number | null;
  name: string;
  positionId: number;
  email: string;
  birthDate: string;
  username: string;
  supportToken: string | null;
  roles: Array<number>;
  activated: boolean;
}

export interface UserSummary {
  id: number;
  name: string;
  position: PositionMin;
  picture: Picture | null;
}

export interface UserMinSummary {
  id: number;
  name: string;
}

export interface UserGroup {
  title: string;
  childrens: Array<UserMinSummary>;
}
