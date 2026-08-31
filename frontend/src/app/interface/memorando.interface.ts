import { Position } from './position.interface';
import { UserSummary } from './user.interface';
import { ErpSource } from './erp.interface';

export interface Memorando {
  id: number;
  number: number;
  request: number;
  client: string;
  erpSource: ErpSource;
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
}

export interface MemorandoList {
  id: number;
  number: number;
  request: number;
  client: string;
  status: string;
  signatureSummary: Array<UserSummary>;
  createAt: string;
}

export type MemorandoGroup = 'PUBLISHED' | 'DRAFT';
export type MemorandoStatus = 'CREATED' | 'PUBLISH' | 'APPROVED' | 'CANCELED';

export interface MemorandoPage {
  content: Array<MemorandoList>;
  totalElements: number;
  totalPages: number;
}

export interface MemorandoSummary {
  total: number;
  active: number;
  approved: number;
  canceled: number;
  draft: number;
}

export interface MemorandoNavigation {
  previousId: number | null;
  nextId: number | null;
}

export interface NewMemorando {
  request: number;
  erpSource: ErpSource;
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
  signedAt: string | null;
}

export interface SignatureList {
  check: boolean;
  position: string;
  signedBy: string | null;
  signedAt: string | null;
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
