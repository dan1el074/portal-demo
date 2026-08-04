import { Injectable } from '@angular/core';
import { Observable, delay, of } from 'rxjs';
import { calculateRawMaterialUnitWeight, formatRawMaterialDecimal, getRawMaterialStockStatus, RawMaterialCategory, RawMaterialFilters, RawMaterialPagedResult, RawMaterialSummary, RawMaterialsTable, RawMaterialUserAccess } from '../interface/raw-materials.interface';

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
  const dimensions = name.match(/(\d{3,4})\s*x\s*(\d{3,4})(?:\s*x\s*(\d{2,3}))?/i);
  const length = dimensions ? Number(dimensions[1]) / 1000 : 0;
  const width = dimensions ? Number(dimensions[2]) / 1000 : 0;
  const sheetThickness = name.match(/#\s*(\d+(?:[,.]\d+)?)/)?.[1];
  const thickness = sheetThickness
    ? Number(sheetThickness.replace(',', '.'))
    : Number(dimensions?.[3] ?? 0);
  const area = length * width;
  const suppliedUnitWeight = currentStorage > 0 && currentStorageKg > 0
    ? currentStorageKg / currentStorage
    : minStorage > 0 && minStorageKg > 0
      ? minStorageKg / minStorage
      : maxStorage > 0 && maxStorageKg > 0
        ? maxStorageKg / maxStorage
        : 0;
  const weightPerSquareMeter = area > 0 && suppliedUnitWeight > 0
    ? suppliedUnitWeight / area
    : type === 'Chapas Finas' && thickness > 0
      ? thickness * 7.85
      : 0;
  const calculatedUnitWeight = area * weightPerSquareMeter;
  const calculateStoredWeight = (quantity: number, suppliedWeight: number): number => suppliedWeight > 0
    ? suppliedWeight
    : Number((quantity * calculatedUnitWeight).toFixed(3));

  return {
    id,
    code,
    name,
    description: '',
    currentStorage,
    currentStorageKg: calculateStoredWeight(currentStorage, currentStorageKg),
    minStorage,
    minStorageKg: calculateStoredWeight(minStorage, minStorageKg),
    maxStorage,
    maxStorageKg: calculateStoredWeight(maxStorage, maxStorageKg),
    type,
    active,
    updateAt: `2026-07-${String(29 - (id % 8)).padStart(2, '0')}T${String(8 + (id % 9)).padStart(2, '0')}:30:00-03:00`,
    user: id % 3 === 0 ? 'Mariana Costa' : 'Daniel Vargas',
    length: formatRawMaterialDecimal(length),
    width: formatRawMaterialDecimal(width),
    thickness: formatRawMaterialDecimal(thickness),
    weightPerSquareMeter: formatRawMaterialDecimal(weightPerSquareMeter),
  };
};

@Injectable({ providedIn: 'root' })
export class RawMaterialsService {
  /**
   * Mock temporário. A assinatura de query já reproduz a consulta paginada que
   * será enviada ao backend (page, size, busca, categoria, status e ordenação).
   */
  private items: RawMaterialsTable[] = [
    createItem(1, '21805', 'CH A36 #1,55 1200x3000mm GALV. Z275 MP', 29, 1274.724, 30, 1318.68, 45, 1978.02, 'Chapas Finas'),
    createItem(2, '18912', 'CH A36 #1,50 1200x3000mm MP', 26, 1142.856, 7, 307.692, 11, 461.538, 'Chapas Finas'),
    createItem(3, '32641', 'CH A36 #2,00 1200x1390mm MP', 0, 0, 25, 667.2, 38, 1000.8, 'Chapas Finas'),
    createItem(4, '32614', 'CH A36 #2,00 1200x1690mm MP', 82, 2660.736, 120, 3893.76, 180, 5840.64, 'Chapas Finas'),
    createItem(5, '18751', 'CH A36 #2,00 1200x1855mm MP', 65, 2315.04, 80, 2849.28, 120, 4273.92, 'Chapas Finas'),
    createItem(6, '28691', 'CH A36 #2,00 1200x2255mm MP', 64, 2770.944, 40, 1731.84, 60, 2597.76, 'Chapas Finas'),
    createItem(7, '21311', 'CH A36 #2,00 1200x3000mm MP', 20, 1152, 10, 565.56, 15, 848.34, 'Chapas Finas'),
    createItem(8, '32615', 'CH A36 #3,00 1200x1690mm MP', 33, 1606.176, 40, 1946.88, 60, 2920.32, 'Chapas Finas'),
    createItem(9, '25205', 'CH A36 #3,00 1200x1855mm MP', 0, 0, 50, 2709.51, 75, 4064.265, 'Chapas Finas'),
    createItem(10, '47811', 'CH A36 #3,00 1200x1970mm MP', 40, 2308.2096, 27, 1558.04148, 41, 2337.06222, 'Chapas Finas'),
    createItem(11, '28692', 'CH A36 #3,00 1200x2255mm MP', 36, 2337.984, 40, 2597.76, 60, 3896.64, 'Chapas Finas'),
    createItem(12, '20946', 'CH A36 #3,00 1200x3000mm MP', 76, 6678.576, 30, 2636.28, 45, 3954.42, 'Chapas Finas'),
    createItem(13, '18995', 'CH A36 #4,75 1500x3000mm MP', 21, 3529.575, 30, 5042.25, 45, 7563.375, 'Chapas Finas'),
    createItem(14, '38745', 'CH A36 #6,35 1500x3000mm MP', 22, 4930.2, 20, 4482, 30, 6723, 'Chapas Finas'),
    createItem(15, '18997', 'CH A36 #8,00 1500x3000mm MP', 3, 840.375, 3, 840.375, 5, 1260.5625, 'Chapas Finas'),
    createItem(16, '18947', 'CH A36 #9,53 1500x3000mm MP', 23, 7731.45, 20, 6723, 30, 10084.5, 'Chapas Finas'),
    createItem(17, '20954', 'CH A36 #12,70 1200x3000mm MP', 6, 2151.36, 6, 2151.36, 9, 3227.04, 'Chapas Finas'),
    createItem(18, '20689', 'CH A36 #15,88 1200x3000mm MP', 2, 1120.41, 1, 560.205, 2, 840.3075, 'Chapas Finas'),
    createItem(19, '20690', 'CH A36 #19,05 1200x2000mm MP', 2, 717.072, 1, 358.536, 2, 537.804, 'Chapas Finas'),
    createItem(20, '37977', 'CH A36 #3,00 XADREZ 1500x2000mm MP', 4, 324, 10, 810, 15, 1215, 'Chapas Finas'),
    createItem(21, '15888', 'CH A36 #4,75 XADREZ 1500x2000mm MP', 295, 36285, 200, 24600, 300, 36900, 'Chapas Finas'),
    createItem(22, '17322', 'CH A36 #4,75 XADREZ 1500x3000mm MP', 17, 3136.5, 10, 1845, 15, 2767.5, 'Chapas Finas'),
    createItem(23, '18748', 'CH A36 #6,35 XADREZ 1500x2000mm MP', 129, 20898, 200, 32400, 300, 48600, 'Chapas Finas'),
    createItem(24, '39208', 'CH A36 #9,53 XADREZ 1500x2000mm MP', 20, 4860, 40, 9720, 60, 14580, 'Chapas Finas'),
    createItem(25, '29842', 'CH A36 #12,7 XADREZ 1200x2000mm MP', 21, 5019.84, 10, 2390.4, 15, 3585.6, 'Chapas Finas'),
    createItem(26, '25743', 'CH A36 #3,00 XADREZ 1200x2000mm MP', 13, 842.4, 0, 0, 0, 0, 'Chapas Finas'),
    createItem(27, '10474', 'CH A36 #9,53 XADREZ 1200x2000mm MP', 21, 4082.4, 0, 0, 0, 0, 'Chapas Finas'),
    createItem(28, '46924', 'CH EXP #4.75 M50X100 1200x2000mm MP', 27, 907.2, 18, 252, 27, 378, 'Chapas Finas'),
    createItem(29, '2478', 'CH A36 #1,95 1200x3000mm GALV. Z275 MP', 0, 0, 0, 0, 0, 0, 'Chapas Finas', false),
    createItem(30, '32616', 'CH A36 #2,00 1200x900mm MP', 0, 0, 0, 0, 0, 0, 'Chapas Finas', false),
    createItem(31, '18749', 'CH A36 #2,00 1200x1310mm MP', 0, 0, 0, 0, 0, 0, 'Chapas Finas', false),
    createItem(32, '33069', 'CH A36 #3,00 1200x1090mm MP', 0, 0, 0, 0, 0, 0, 'Chapas Finas', false),
    createItem(33, '41764', 'CH A36 #3,00 1200x2270mm MP', 0, 0, 0, 0, 0, 0, 'Chapas Finas', false),
    createItem(34, '45066', 'CH A36 #3,00 1200x2455mm MP', 0, 0, 0, 0, 0, 0, 'Chapas Finas', false),
    createItem(35, '32643', 'CH A36 #3,00 1200x1390mm MP', 0, 0, 0, 0, 0, 0, 'Chapas Finas', false),
    createItem(36, '20952', 'CH A36 #4,75 1200x3000mm MP', 0, 0, 0, 0, 0, 0, 'Chapas Finas', false),
    createItem(37, '20953', 'CH A36 #6,35 1200x3000mm MP', 42, 0, 0, 0, 0, 0, 'Chapas Finas', false),
    createItem(38, '18999', 'CH A36 #12,70 1500x3000mm MP', 0, 0, 0, 0, 0, 0, 'Chapas Finas', false),
    createItem(39, '2269', 'CH A36 #4,75 XADREZ 1200X3000mm MP', 0, 0, 0, 0, 0, 0, 'Chapas Finas', false),
    createItem(40, '11830', 'CH A36 #6,35 XADREZ 1200x2000mm MP', 1, 0, 0, 0, 0, 0, 'Chapas Finas', false),
    createItem(41, '19809', 'CH A36 #6,35 XADREZ 1500x2200mm MP', 14, 0, 0, 0, 0, 0, 'Chapas Finas'),
    createItem(42, '41775', 'CH A36 #6,35 XADREZ 1200x3000mm MP', 7, 0, 0, 0, 0, 0, 'Chapas Finas'),
    createItem(43, '20000', 'CH A36 # 9,53 XADREZ 1200x2200mm MP', 0, 0, 0, 0, 0, 0, 'Chapas Finas', false),
    createItem(44, '47843', 'CH A36 #4,75 XADREZ 1500X2200mm MP', 3, 0, 0, 0, 0, 0, 'Chapas Finas'),
    createItem(45, '23471', 'CH A36 #25,40 1500x3000mm MP', 1, 0, 0, 0, 0, 0, 'Chapas Finas'),
    createItem(46, '50324', 'CH A36 #3,00 1200x2280mm MP', 33, 0, 0, 0, 0, 0, 'Chapas Finas'),
    createItem(47, '5713', 'CH A36 #4,75 XADREZ 1200X2000mm MP', 72, 0, 0, 0, 0, 0, 'Chapas Finas'),
    createItem(48, '42236', 'PALET PALETEIRA IMPORTADA', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(49, '4601', 'PALETE DE MADEIRA 1000X1000X100', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(50, '20928', 'PALETE DE MADEIRA 1000x2300x130', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(51, '20929', 'PALETE DE MADEIRA 1000x2300x70', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(52, '22202', 'PALETE DE MADEIRA 1200X1400X130 REFORÇO X', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(53, '26133', 'PALETE DE MADEIRA 1200X1500X130 REFORÇADO', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(54, '2573', 'PALETE DE MADEIRA 1200X2000X130', 10, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(55, '22201', 'PALETE DE MADEIRA 1200X2000X130 REFORÇO X', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(56, '33061', 'PALETE DE MADEIRA 1200x2200x130 CERTIFICADO', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(57, '41207', 'PALETE DE MADEIRA 1200x2200x130 CONTRAPESO', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(58, '20913-01', 'PALETE DE MADEIRA 1200x2200x130 NDE', 42, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(59, '25357', 'PALETE DE MADEIRA 1200X2300X130', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(60, '20945', 'PALETE DE MADEIRA 1200x2500x130', 17, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(61, '28970', 'PALETE DE MADEIRA 1200X2600X130', 2, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(62, '20914', 'PALETE DE MADEIRA 1200x3000x130', 4, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(63, '43663', 'PALETE DE MADEIRA 1200x3000x130 NDE ESP HYUNDAI', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(64, '22492', 'PALETE DE MADEIRA 1200x3200x130', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(65, '20937', 'PALETE DE MADEIRA 1200x3800x130', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(66, '41516', 'PALETE DE MADEIRA 1200x4700x130', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(67, '28690', 'PALETE DE MADEIRA 1500x2000x130', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(68, '28554', 'PALETE DE MADEIRA 1500x2800x130', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(69, '33714', 'PALETE DE MADEIRA 1600x2200x150mm', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(70, '30437', 'PALETE DE MADEIRA 1700X1700X130', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(71, '14052-01', 'PALETE DE MADEIRA 2000X2000 DM V03', 10, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(72, '37949', 'PALETE DE MADEIRA 2000X3000 DM 3METROS', 2, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(73, '27100', 'PALETE DE MADEIRA 2200x5300x130', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(74, '19021-01', 'PALETE DE MADEIRA 2400X2000 DM L1800 V03', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(75, '28331', 'PALETE DE MADEIRA 2800X2200 DM ESP', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(76, '29356', 'PALETE DE MADEIRA 3000X2200 DM', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(77, '43046', 'PALETE DE MADEIRA 3000X2300 ME', 1, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(78, '39422', 'PALETE DE MADEIRA 3500X1800 ME', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(79, '29650', 'PALETE DE MADEIRA 3500X1800 TC', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(80, '40390', 'PALETE DE MADEIRA 4000x1500 ME', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(81, '14052', 'PALETE ESTRUTURADO DOCAS MÓVEIS', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(82, '14053', 'PALETE ESTRUTURADO 2400 X 4000', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(83, '18536', 'PALLET DE MADEIRA PARA DOCA MOVEL 2300X2000 mm', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(84, '32086', 'PALLET SMART 1000 X 1200 X 150 VAZADO 3 RUMMERS PRETO', 0, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(85, '30125', 'PALETE DE MADEIRA 1200X1200X130', 23, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(86, '2574', 'PALETE DE MADEIRA 1200X1400X130', 22, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(87, '21852', 'PALETE DE MADEIRA 1200X1700X130', 40, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(88, '41917', 'PALETE DE MADEIRA 1500X2800X130', 9, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(89, '2265', 'PALETE DE MADEIRA 1200X2700X130', 30, 0, 0, 0, 0, 0, 'Pallet'),
    createItem(90, '33903', 'PALETE DE MADEIRA 3700X1200 NDE', 2, 0, 0, 0, 0, 0, 'Pallet'),
  ];

  private categories: RawMaterialCategory[] = [
    { id: 1, name: 'Chapas Finas', color: 'blue', updatedAt: '2026-07-12T09:15:00-03:00' },
    { id: 2, name: 'Aços Longos', color: 'violet', updatedAt: '2026-07-15T14:20:00-03:00' },
    { id: 3, name: 'Pallet', color: 'cyan', updatedAt: '2026-08-04T08:00:00-03:00' },
  ];

  private users: RawMaterialUserAccess[] = [
    { id: 3, name: 'Aline Moterle', pictureId: 3, categoryIds: [1, 2, 3] },
    { id: 4, name: 'Carlos Fronza', pictureId: 4, categoryIds: [1, 2, 3] },
    { id: 2, name: 'Enzo Bazzi', pictureId: 2, categoryIds: [1, 2] },
    { id: 5, name: 'Juliano Bortoletti', pictureId: 5, categoryIds: [1, 3] },
  ];

  query(filters: RawMaterialFilters): Observable<RawMaterialPagedResult> {
    const search = this.normalize(filters.search ?? '');
    let result = this.items.filter(item => {
      if (item.active === Boolean(filters.inactive)) return false;
      if (filters.allowedCategories && !filters.allowedCategories.includes(item.type)) return false;
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
      length: formatRawMaterialDecimal(item.length),
      width: formatRawMaterialDecimal(item.width),
      thickness: formatRawMaterialDecimal(item.thickness),
      weightPerSquareMeter: formatRawMaterialDecimal(item.weightPerSquareMeter),
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
    return Number((quantity * unitWeight).toFixed(3));
  }
}
