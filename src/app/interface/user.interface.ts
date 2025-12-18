import { Role } from './role.interface';
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
  username: string;
  email: string;
  birthDate: string;
  theme: string;
  activated: boolean;
  notifications: number;
  roles: Array<Role>;
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
