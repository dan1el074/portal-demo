import { Injectable } from '@angular/core';
import { Observable, delay, of } from 'rxjs';
import { calculateRawMaterialUnitWeight, getRawMaterialStockStatus, RawMaterialCategory, RawMaterialFilters, RawMaterialPagedResult, RawMaterialSummary, RawMaterialsTable, RawMaterialUserAccess } from '../interface/raw-materials.interface';

const createItem = (
  id: number,
  code: string,
  name: string,
  currentStorage: number,
  currentStorageKg: number,
  minStorage: number,
  minStorageKg: number,
  maxStorage: number,
  maxStorageKg: number,
  type: string,
  active = true
): RawMaterialsTable => {
  const dimensions = name.match(/(\d{3,4})x(\d{3,4})/i);
  const length = dimensions?.[1] ?? '1000';
  const width = dimensions?.[2] ?? '1000';
  const unitWeight = currentStorage ? currentStorageKg / currentStorage : 0;

  return {
    id,
    code,
    name,
    description: 'Chapas de proteção lateral de NDE e mesas',
    currentStorage,
    currentStorageKg,
    minStorage,
    minStorageKg,
    maxStorage,
    maxStorageKg,
    type,
    active,
    updateAt: `2026-07-${String(29 - (id % 8)).padStart(2, '0')}T${String(8 + (id % 9)).padStart(2, '0')}:30:00-03:00`,
    user: id % 3 === 0 ? 'Mariana Costa' : 'Daniel Vargas',
    length,
    width,
    weightPerMillimeter: String(unitWeight / (Number(length) * Number(width))),
  };
};

@Injectable({ providedIn: 'root' })
export class RawMaterialsService {
  /**
   * Mock temporário. A assinatura de query já reproduz a consulta paginada que
   * será enviada ao backend (page, size, busca, categoria, status e ordenação).
   */
  private items: RawMaterialsTable[] = [
    createItem(1, '21805', 'CH A36 #1,55 1200x3000mm GALV. Z275 MP', 73, 3208.79, 30, 1319.68, 45, 1978.02, 'Chapas Finas'),
    createItem(2, '18912', 'CH A36 #1,50 1200x3000mm MP', 18, 791.21, 7, 307.69, 11, 461.54, 'Chapas Finas'),
    createItem(3, '32641', 'CH A36 #2,00 1200x1390mm MP', 31, 827.33, 25, 667.2, 38, 1000.8, 'Chapas Finas'),
    createItem(4, '32614', 'CH A36 #2,00 1200x1690mm MP', 114, 3699.07, 120, 3893.76, 180, 5840.64, 'Chapas Finas'),
    createItem(5, '18751', 'CH A36 #2,00 1200x1855mm MP', 66, 2350.66, 80, 2849.28, 120, 4273.92, 'Chapas Finas'),
    createItem(6, '28691', 'CH A36 #2,00 1200x1855mm MP', 63, 2727.65, 40, 1731.84, 60, 2597.76, 'Chapas Finas'),
    createItem(7, '21311', 'CH A36 #2,00 1200x3000mm MP', 20, 1152, 10, 565.56, 15, 848.34, 'Chapas Finas'),
    createItem(8, '32615', 'CH A36 #3,00 1200x1690mm MP', 24, 1168.13, 40, 1946.88, 60, 2920.32, 'Chapas Finas'),
    createItem(9, '32641-A', 'BARRA CHATA #2,00 1200x1390mm MP', 31, 827.33, 25, 667.2, 38, 1000.8, 'Aços Longos'),
    createItem(10, '32614-A', 'BARRA CHATA #2,00 1200x1690mm MP', 114, 3699.07, 120, 3893.76, 180, 5840.64, 'Aços Longos'),
    createItem(11, '18751-A', 'BARRA CHATA #2,00 1200x1855mm MP', 66, 2350.66, 80, 2849.28, 120, 4273.92, 'Aços Longos'),
    createItem(12, '28691-A', 'BARRA CHATA #2,00 1200x1855mm MP', 63, 2727.65, 40, 1731.84, 60, 2597.76, 'Aços Longos'),
    createItem(13, '21311-A', 'BARRA CHATA #2,00 1200x3000mm MP', 20, 1152, 10, 565.56, 15, 848.34, 'Aços Longos'),
    createItem(14, '32615-A', 'BARRA CHATA #3,00 1200x1690mm MP', 24, 1168.13, 40, 1946.88, 60, 2920.32, 'Aços Longos'),
    createItem(15, 'T-1040', 'TUBO REDONDO Ø 38,10 x 2,00mm', 42, 512.4, 25, 305, 60, 732, 'Tubos'),
    createItem(16, 'P-2098', 'PERFIL U ENRIJECIDO 100x50x17', 0, 0, 12, 220.8, 30, 552, 'Perfis', false),
  ];

  private categories: RawMaterialCategory[] = [
    { id: 1, name: 'Chapas Finas', color: 'blue', updatedAt: '2026-07-12T09:15:00-03:00' },
    { id: 2, name: 'Aços Longos', color: 'violet', updatedAt: '2026-07-15T14:20:00-03:00' },
    { id: 3, name: 'Tubos', color: 'cyan', updatedAt: '2026-07-22T10:40:00-03:00' },
    { id: 4, name: 'Perfis', color: 'amber', updatedAt: '2026-07-25T16:05:00-03:00' },
  ];

  private users: RawMaterialUserAccess[] = [
    { id: 1, name: 'Daniel Vargas', initials: 'DV', categoryIds: [1, 2, 3, 4] },
    { id: 2, name: 'Mariana Costa', initials: 'MC', categoryIds: [1, 3] },
    { id: 3, name: 'Carlos Henrique', initials: 'CH', categoryIds: [2, 4] },
    { id: 4, name: 'Fernanda Almeida', initials: 'FA', categoryIds: [1, 2] },
  ];

  query(filters: RawMaterialFilters): Observable<RawMaterialPagedResult> {
    const search = this.normalize(filters.search ?? '');
    let result = this.items.filter(item => {
      if (item.active === Boolean(filters.inactive)) return false;
      if (filters.category && item.type !== filters.category) return false;
      if (filters.status && filters.status !== 'all' && getRawMaterialStockStatus(item) !== filters.status) return false;
      return !search || this.normalize(`${item.code} ${item.name} ${item.description ?? ''}`).includes(search);
    });

    if (filters.sortColumn) {
      const direction = filters.sortDirection === 'desc' ? -1 : 1;
      result = [...result].sort((a, b) => {
        const left = a[filters.sortColumn as keyof RawMaterialsTable] ?? '';
        const right = b[filters.sortColumn as keyof RawMaterialsTable] ?? '';
        return String(left).localeCompare(String(right), 'pt-BR', { numeric: true }) * direction;
      });
    }

    const start = filters.page * filters.size;
    return of({
      content: result.slice(start, start + filters.size).map(item => ({ ...item })),
      totalElements: result.length,
    }).pipe(delay(180));
  }

  getSummary(): Observable<RawMaterialSummary> {
    const active = this.items.filter(item => item.active);
    return of({
      low: active.filter(item => getRawMaterialStockStatus(item) === 'low').length,
      ok: active.filter(item => getRawMaterialStockStatus(item) === 'ok').length,
      high: active.filter(item => getRawMaterialStockStatus(item) === 'high').length,
    }).pipe(delay(100));
  }

  getCategories(): Observable<RawMaterialCategory[]> {
    return of(this.categories.map(category => ({ ...category }))).pipe(delay(80));
  }

  getUsers(): Observable<RawMaterialUserAccess[]> {
    return of(this.users.map(user => ({ ...user, categoryIds: [...user.categoryIds] }))).pipe(delay(80));
  }

  saveItem(item: RawMaterialsTable): Observable<RawMaterialsTable> {
    const unitWeight = calculateRawMaterialUnitWeight(item);
    const saved = {
      ...item,
      length: item.length ?? '',
      width: item.width ?? '',
      weightPerMillimeter: item.weightPerMillimeter ?? '',
      currentStorageKg: this.calculateWeight(item.currentStorage, unitWeight),
      minStorageKg: this.calculateWeight(item.minStorage, unitWeight),
      maxStorageKg: this.calculateWeight(item.maxStorage, unitWeight),
      id: item.id || Math.max(...this.items.map(current => current.id), 0) + 1,
      updateAt: new Date().toISOString(),
      active: item.id ? item.active : true,
    };
    const index = this.items.findIndex(current => current.id === saved.id);
    if (index >= 0) this.items[index] = saved;
    else this.items.unshift(saved);
    return of({ ...saved }).pipe(delay(120));
  }

  updateStock(id: number, currentStorage: number, currentStorageKg: number, user: string): Observable<void> {
    const item = this.items.find(current => current.id === id);
    if (item) Object.assign(item, { currentStorage, currentStorageKg, user, updateAt: new Date().toISOString() });
    return of(void 0).pipe(delay(120));
  }

  toggleActive(id: number): Observable<void> {
    const item = this.items.find(current => current.id === id);
    if (item) Object.assign(item, { active: !item.active, updateAt: new Date().toISOString() });
    return of(void 0).pipe(delay(120));
  }

  addCategory(name: string): Observable<RawMaterialCategory> {
    const colors: RawMaterialCategory['color'][] = ['blue', 'violet', 'cyan', 'amber', 'rose', 'emerald', 'slate', 'orange'];
    const category: RawMaterialCategory = {
      id: Math.max(...this.categories.map(current => current.id), 0) + 1,
      name,
      color: colors[this.categories.length % colors.length],
      updatedAt: new Date().toISOString(),
    };
    this.categories.push(category);
    return of({ ...category }).pipe(delay(80));
  }

  updateCategory(id: number, name: string): Observable<RawMaterialCategory | null> {
    const category = this.categories.find(current => current.id === id);
    const duplicated = this.categories.some(current => current.id !== id && this.normalize(current.name) === this.normalize(name));
    if (!category || duplicated) return of(null).pipe(delay(80));

    const previousName = category.name;
    Object.assign(category, { name, updatedAt: new Date().toISOString() });
    this.items = this.items.map(item => item.type === previousName ? { ...item, type: name } : item);
    return of({ ...category }).pipe(delay(80));
  }

  deleteCategory(id: number): Observable<boolean> {
    const category = this.categories.find(current => current.id === id);
    if (!category || this.items.some(item => item.type === category.name)) {
      return of(false).pipe(delay(80));
    }

    this.categories = this.categories.filter(current => current.id !== id);
    this.users = this.users.map(user => ({
      ...user,
      categoryIds: user.categoryIds.filter(categoryId => categoryId !== id),
    }));
    return of(true).pipe(delay(80));
  }

  updateUserAccess(users: RawMaterialUserAccess[]): Observable<void> {
    this.users = users.map(user => ({ ...user, categoryIds: [...user.categoryIds] }));
    return of(void 0).pipe(delay(100));
  }

  private normalize(value: string): string {
    return value.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('pt-BR').trim();
  }

  private calculateWeight(quantity: number, unitWeight: number): number {
    return Number((quantity * unitWeight).toFixed(2));
  }
}
