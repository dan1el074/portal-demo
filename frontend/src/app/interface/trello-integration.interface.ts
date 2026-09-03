export type TrelloIntegrationStatus = 'SENT' | 'PENDING' | 'ERROR';
export type TrelloIntegrationView = 'operator' | 'admin';

export interface TrelloIntegrationRecord {
  id: number;
  order: string;
  orderType: string;
  client: string;
  code: string;
  description: string;
  quantity: number;
  seller: string;
  releaseDate: string;
  expectedDelivery: string;
  status: TrelloIntegrationStatus;
  statusLabel: string;
  importedAt: string;
  destinationEmail: string;
  sentAt?: string;
  lastResentAt?: string;
  errorMessage?: string;
}

export interface TrelloIntegrationSettings {
  retentionDays: number;
  erpLookbackDays: number;
  destinationEmail: string;
  ccEmail: string;
}

export interface TrelloIntegrationPage {
  content: TrelloIntegrationRecord[];
  totalElements: number;
  totalPages: number;
}

export interface TrelloIntegrationSummary {
  total: number;
  sent: number;
  pending: number;
  errors: number;
}

export interface TrelloIntegrationConsultResult {
  received: number;
  imported: number;
  ignored: number;
  scheduled: number;
  removedByRetention: number;
}
