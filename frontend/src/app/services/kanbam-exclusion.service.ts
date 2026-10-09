import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { KanbamItem } from '../interface/kanbam-exclusion.interface';

@Injectable({ providedIn: 'root' })
export class KanbamExclusionService {
  private readonly api = environment.apiUrl + '/api/pcp/kanbam-exclusion';

  constructor(private http: HttpClient) {}

  findByLotNumber(lotNumber: string): Observable<KanbamItem[]> {
    const params = new HttpParams().set('lotNumber', lotNumber);
    return this.http.get<KanbamItemResponse[]>(this.api, { params }).pipe(
      map(items => items.map(item => ({
        lotNumber: item.lotNumber ?? item.num_lote ?? 0,
        orderId: item.orderId ?? item.id_ordem ?? 0,
        orderNumber: item.orderNumber ?? item.num_ordem ?? 0,
        itemCode: item.itemCode ?? item.cod_item ?? '',
        itemDescription: item.itemDescription ?? item.desc_item ?? '',
        quantidade: item.quantidade ?? 0
      })))
    );
  }

  deleteManufacturingOrder(orderId: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${orderId}`);
  }
}

interface KanbamItemResponse extends Partial<KanbamItem> {
  num_lote?: number;
  id_ordem?: number;
  num_ordem?: number;
  cod_item?: string;
  desc_item?: string;
}
