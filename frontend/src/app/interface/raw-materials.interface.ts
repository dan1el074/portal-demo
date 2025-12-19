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
}
