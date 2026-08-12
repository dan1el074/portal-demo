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
  categoryId?: number;
  active: boolean;
  updateAt: string;
  user: string;
  length?: string;
  width?: string;
  thickness?: string;
  weightPerSquareMeter?: string;
}

export interface RawMaterialHistory {
  id: number;
  action: 'CREATED' | 'UPDATED' | 'STOCK_UPDATED' | 'STOCK_AND_ITEM_UPDATED';
  previousStorage: number | null;
  newStorage: number | null;
  changedFields: string[];
  createdAt: string;
  user: string;
}

export interface RawMaterialCategory {
  id: number;
  name: string;
  conversionFactor?: string | null;
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
  if (item.minStorage <= 0 || item.maxStorage <= 0) return 'ok';
  if (item.currentStorage < item.minStorage) return 'low';
  if (item.currentStorage > item.maxStorage) return 'high';
  return 'ok';
}

export function calculateRawMaterialUnitWeight(item: RawMaterialsTable, formula?: string | null): number {
  if (!formula?.trim()) return 0;
  try {
    const result = new ConversionFormulaParser(formula, {
      c: parseRawMaterialDecimal(item.length),
      l: parseRawMaterialDecimal(item.width),
      e: parseRawMaterialDecimal(item.thickness),
      p: parseRawMaterialDecimal(item.weightPerSquareMeter),
    }).parse();
    return Number.isFinite(result) && result >= 0 ? result : 0;
  } catch {
    return 0;
  }
}

class ConversionFormulaParser {
  private index = 0;

  constructor(private expression: string, private variables: Record<'c' | 'l' | 'e' | 'p', number>) {}

  parse(): number {
    const result = this.parseExpression();
    this.skipWhitespace();
    if (this.index !== this.expression.length) throw new Error('Expressão inválida');
    return result;
  }

  private parseExpression(): number {
    let result = this.parseTerm();
    while (true) {
      this.skipWhitespace();
      if (this.consume('+')) result += this.parseTerm();
      else if (this.consume('-')) result -= this.parseTerm();
      else return result;
    }
  }

  private parseTerm(): number {
    let result = this.parseFactor();
    while (true) {
      this.skipWhitespace();
      if (this.consume('*')) result *= this.parseFactor();
      else if (this.consume('/')) {
        const divisor = this.parseFactor();
        if (divisor === 0) throw new Error('Divisão por zero');
        result /= divisor;
      } else return result;
    }
  }

  private parseFactor(): number {
    this.skipWhitespace();
    if (this.consume('+')) return this.parseFactor();
    if (this.consume('-')) return -this.parseFactor();
    if (this.consume('(')) {
      const result = this.parseExpression();
      this.skipWhitespace();
      if (!this.consume(')')) throw new Error('Parêntese não fechado');
      return result;
    }
    if (this.consume('%')) {
      const variable = this.expression[this.index++]?.toLowerCase() as 'c' | 'l' | 'e' | 'p';
      if (!(variable in this.variables)) throw new Error('Variável inválida');
      return this.variables[variable];
    }
    return this.parseNumber();
  }

  private parseNumber(): number {
    const start = this.index;
    let separator = false;
    while (this.index < this.expression.length) {
      const character = this.expression[this.index];
      if (/\d/.test(character)) this.index++;
      else if ((character === '.' || character === ',') && !separator) {
        separator = true;
        this.index++;
      } else break;
    }
    if (start === this.index) throw new Error('Número esperado');
    const result = Number(this.expression.slice(start, this.index).replace(',', '.'));
    if (!Number.isFinite(result)) throw new Error('Número inválido');
    return result;
  }

  private skipWhitespace(): void {
    while (/\s/.test(this.expression[this.index] ?? '')) this.index++;
  }

  private consume(character: string): boolean {
    if (this.expression[this.index] !== character) return false;
    this.index++;
    return true;
  }
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
