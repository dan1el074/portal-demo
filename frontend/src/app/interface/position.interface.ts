import { UserSummary } from "./user.interface";

export interface Position {
  id: number;
  name: string;
  manangers: Array<UserSummary>;
  activated: boolean;
  updatedAt: string;
  createdAt: string;
}

export interface PositionMin {
  id: number;
  name: string;
}

export interface PositionFormImput {
  name: string;
  manangers: Array<number>;
  activated: boolean;
}
