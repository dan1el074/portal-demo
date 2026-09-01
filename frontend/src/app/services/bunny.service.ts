import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { BunnyConfig, BunnyConfigUpdate } from '../interface/bunny.interface';

@Injectable({ providedIn: 'root' })
export class BunnyService {
  private readonly api = environment.apiUrl + '/api/integrations/bunny/config';

  constructor(private http: HttpClient) {}

  getConfig(): Observable<BunnyConfig> {
    return this.http.get<BunnyConfig>(this.api);
  }

  updateConfig(config: BunnyConfigUpdate): Observable<BunnyConfig> {
    return this.http.put<BunnyConfig>(this.api, config);
  }
}
