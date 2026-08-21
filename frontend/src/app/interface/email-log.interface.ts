export type EmailDeliveryStatus = 'SENT' | 'ERROR';

export interface EmailLog {
  id: number;
  subject: string;
  recipient: string;
  module: string;
  status: EmailDeliveryStatus;
  errorMessage: string | null;
  createdAt: string;
}

export interface EmailLogPage {
  content: EmailLog[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}
