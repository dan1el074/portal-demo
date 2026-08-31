import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ProbusConfig, ProbusConfigUpdate } from '../interface/probus.interface';

@Injectable({ providedIn: 'root' })
export class ProbusService {
  private readonly api = environment.apiUrl + '/api/integrations/probus/config';

  constructor(private http: HttpClient) {}

  public getConfig(): Observable<ProbusConfig> {
    return this.http.get<ProbusConfig>(this.api);
  }

  public updateConfig(config: ProbusConfigUpdate): Observable<ProbusConfig> {
    return this.http.put<ProbusConfig>(this.api, config);
  }
}
