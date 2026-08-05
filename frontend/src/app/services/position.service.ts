import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Position, PositionFormImput, PositionMin } from './../interface/position.interface';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PostitionService {
  private api = environment.apiUrl + '/api/position';

  constructor(private http: HttpClient) {}

  public findAll(): Observable<any> {
    return this.http.get<Array<Position>>(this.api);
  }

  public list(): Observable<any> {
    return this.http.get<Array<PositionMin>>(this.api + '/min');
  }

  public findById(id: number): Observable<any> {
    return this.http.get<Array<Position>>(this.api + '/' + id);
  }

  public insert(data: PositionFormImput): Observable<any> {
    return this.http.post<Array<Position>>(this.api, data);
  }

  public update(id: number, data: PositionFormImput): Observable<any> {
    return this.http.put<Array<PositionMin>>(this.api + '/' + id, data);
  }

  public deactive(id: number): Observable<any> {
    return this.http.put<Array<PositionMin>>(this.api + '/deactive/' + id, null);
  }
}
