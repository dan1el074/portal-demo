import { StepFlowImage } from './image.interface';
import { SafeHtml } from "@angular/platform-browser";

export interface Resume {
  id: number;
  title: string;
  description: string;
  color: string;
  value: number;
  icon: SafeHtml;
}

export interface Step {
  id: number;
  title: string;
  description: string;
  icon: SafeHtml;
  count: number;
}

// retornos da API
export interface AdminDashboard {
  totalCount: number,
  progressCount: number,
  completeCount: number,
  lateCount: number,
  stepsCount: Array<number>;
}

export interface StepFlowData {
  id: number;
  number: number;
  quantity: string;
  client: string;
  dueDate: string;
  currentStep: string;
  status: string;
  progress: Array<number>;
}

export interface StepFlowStepMessage {
  id: number;
  created: string;
  userName: string;
  message: string;
}

export interface StepFlowOrderStep {
  id: number;
  step: string;
  status: string;
  userName: string;
  startedAt: string;
  finishedAt: string;
  messages: Array<StepFlowStepMessage>;
}

export interface StepFlowOrderItem {
  id: number;
  code: number;
  description: string;
  unit: string;
  unitPrice: number;
  quantity: number;
  producedQuantity: number;
  invoicedQuantity: number;
  total: number;
}

export interface StepFlowOrder {
  id: number;
  number: number;
  status: string;
  client: string;
  cnpj: string;
  phone: string;
  seller: string;
  startDate: string;
  dueDate: string;
  address: string;
  steps: Array<StepFlowOrderStep>;
  observation: string;
  items: Array<StepFlowOrderItem>;
  subtotal: number;
  discount: number;
  shipment: number;
  carrier: string;
  total: number;
  pictures: Array<StepFlowImage>;
  currentStep: string;
  nextStep: string | null;
}

export interface PagedResult<T> {
  content: T[];
  totalElements: number;
}

export interface StepFlowOrderInfo {
  number: number;
  client: string;
  cnpj: string;
  phone: string;
  salesperson: string;
  startDate: string;
  dueDate: string;
  address: string;
  subtotal: number;
  discount: number;
  total: number;
  items: Array<StepFlowOrderItem>;
}
