import { UserSummary } from "./user.interface";

export interface Position {
  id: number;
  name: string;
}

export interface PositionTable {
  id: number;
  name: string;
  managers: Array<UserSummary> | null;
  updatedAt: string;
  createdAt: string;
}
