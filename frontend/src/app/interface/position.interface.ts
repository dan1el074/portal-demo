import { UserSummary } from "./user.interface";

export interface Position {
  id: number;
  name: string;
}

export interface PositionTable {
  id: number;
  name: string;
  mananger: UserSummary | null;
  updatedAt: string;
  createdAt: string;
}
