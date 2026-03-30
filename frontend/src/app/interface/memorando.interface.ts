import { Position } from './position.interface';
import { UserSummary } from './user.interface';

export interface Memorando {
  id: number;
  number: number;
  request: number;
  client: string;
  items: Array<string>;
  title: string;
  description: string;
  reason: string;
  createAt: string;
  user: UserSummary;
  signature: Array<Signature>;
  signatureSummary: Array<UserSummary>;
  fromDepartments: Array<Position>;
  status: string;
  logs: Array<CILog>
}

export interface NewMemorando {
  request: number;
  client: string;
  items: Array<string>;
  title: string;
  description: string;
  reason: string;
  departments: Array<number>;
  status: string;
}

export interface Signature {
  departmentSigned: Position;
  user: UserSummary
}

export interface SignatureList {
  check: boolean;
  position: string;
  signedBy: string | null;
}

export interface CILog {
  id: number;
  content: string;
  user: UserSummary | null;
  createdAt: string;
}
