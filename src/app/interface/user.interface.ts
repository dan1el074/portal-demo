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
