export type RawMaterialView = 'admin' | 'operator' | 'consultation';
export type RawMaterialStockStatus = 'all' | 'low' | 'ok' | 'high';

export interface RawMaterialsTable {
  id: number;
  code: string;
  name: string;
  description?: string;
  currentStorage: number;
  currentStorageKg: number;
  minStorage: number;
  minStorageKg: number;
  maxStorage: number;
  maxStorageKg: number;
  type: string;
  active: boolean;
  updateAt: string;
  user: string;
  length?: string;
  width?: string;
  thickness?: string;
  weightPerSquareMeter?: string;
}

export interface RawMaterialCategory {
  id: number;
  name: string;
  color: 'blue' | 'violet' | 'cyan' | 'amber' | 'rose' | 'emerald' | 'slate' | 'orange';
  updatedAt: string;
}

export interface RawMaterialUserAccess {
  id: number;
  name: string;
  pictureId: number | null;
  categoryIds: number[];
}

export interface RawMaterialFilters {
  page: number;
  size: number;
  search?: string;
  category?: string;
  allowedCategories?: string[];
  status?: RawMaterialStockStatus;
  inactive?: boolean;
  sortColumn?: string;
  sortDirection?: 'asc' | 'desc';
}

export interface RawMaterialPagedResult {
  content: RawMaterialsTable[];
  totalElements: number;
}

export interface RawMaterialSummary {
  low: number;
  ok: number;
  high: number;
}

export function getRawMaterialStockStatus(item: RawMaterialsTable): Exclude<RawMaterialStockStatus, 'all'> {
  if (item.minStorage <= 0 && item.maxStorage <= 0) return 'ok';
  if (item.currentStorage < item.minStorage) return 'low';
  if (item.currentStorage > item.maxStorage) return 'high';
  return 'ok';
}

export function calculateRawMaterialUnitWeight(item: RawMaterialsTable): number {
  return parseRawMaterialDecimal(item.length)
    * parseRawMaterialDecimal(item.width)
    * parseRawMaterialDecimal(item.weightPerSquareMeter);
}

export function parseRawMaterialDecimal(value?: string | number): number {
  return Number(String(value ?? '').trim().replace(',', '.')) || 0;
}

export function formatRawMaterialDecimal(value?: string | number): string {
  return parseRawMaterialDecimal(value).toLocaleString('pt-BR', {
    minimumFractionDigits: 3,
    maximumFractionDigits: 3,
    useGrouping: false,
  });
}
