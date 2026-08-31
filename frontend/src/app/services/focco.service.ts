import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { FoccoConfig, FoccoConfigUpdate } from '../interface/focco.interface';

@Injectable({ providedIn: 'root' })
export class FoccoService {
  private readonly api = environment.apiUrl + '/api/integrations/focco/config';

  constructor(private http: HttpClient) {}

  getConfig(): Observable<FoccoConfig> {
    return this.http.get<FoccoConfig>(this.api);
  }

  updateConfig(config: FoccoConfigUpdate): Observable<FoccoConfig> {
    return this.http.put<FoccoConfig>(this.api, config);
  }
}
