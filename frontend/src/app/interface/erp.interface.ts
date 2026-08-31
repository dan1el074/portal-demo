export interface OrderInfo {
  client: string;
  item: string;
  number: number;
  source: ErpSource;
}

export type ErpSource = 'FOCCO' | 'PROBUS';

export interface ErpOrderSearch {
  orderNumber: number;
  source: ErpSource;
}
