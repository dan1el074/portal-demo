export interface KanbamItem {
  lotNumber: number;
  orderId: number;
  orderNumber: number;
  itemCode: string;
  itemDescription: string;
  quantidade: number;
}

export interface KanbamDeleteResult {
  orderId: number;
  success: boolean;
  message?: string;
}
