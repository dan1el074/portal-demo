import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { RoleGroup } from '../interface/role.interface';

@Injectable({
  providedIn: 'root',
})
export class RoleService {
  private api = environment.apiUrl + '/api/role';

  constructor(private http: HttpClient) {}

  public findAll(): Observable<any> {
    return this.http.get<Array<RoleGroup>>(this.api);
  }
}
