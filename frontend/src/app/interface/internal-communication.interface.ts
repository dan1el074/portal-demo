import { Position } from './position.interface';
import { UserSummary } from './user.interface';

export interface InternalCommunication {
  id: number;
  number: number;
  request: number;
  client: string;
  item: string;
  title: string;
  description: string;
  reason: string;
  createAt: string;
  user: UserSummary;
  interactions: Array<UserSummary>;
  fromDepartments: Array<Position>;
  status: string;
  logs: Array<CILog>
}

export interface NewInternalCommunication {
  request: number;
  client: string;
  item: string;
  title: string;
  description: string;
  reason: string;
  departments: string;
}

export interface CILog {
  id: number,
  content: string,
  user: UserSummary | null,
  createdAt: string
}

