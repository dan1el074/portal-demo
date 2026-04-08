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
  fromDepartments: Array<Position>;
  signatures: Array<Signature>;
  status: string;
  logs: Array<CILog>
  signatureSummary: Array<UserSummary>;
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
  user: UserSummary;
  isSign: boolean;
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

export interface UpdateDepartmentMemorando {
  userId: number;
  departmentId: number;
}
