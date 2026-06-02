import { UserSummary } from "./user.interface";

export interface Birthday {
  user: UserSummary
  day: number;
  month: string;
}
