import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { Me, UserData, UserGroup, UserMinData } from './../interface/user.interface';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private api = environment.apiUrl + '/api/user';
  private userSubject = new BehaviorSubject<Me | null>(null);
  public user$ = this.userSubject.asObservable();

  constructor(private http: HttpClient) {
    const storageUser = localStorage.getItem('user');
    if (storageUser) this.userSubject.next(JSON.parse(storageUser));
  }

  private getAuthHeaders(): HttpHeaders {
    const token = sessionStorage.getItem('auth-token');
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }

  /*
  *  Controle de usuário
  */

  public setUser(user: Me): void {
    this.userSubject.next(user);
    localStorage.setItem('user', JSON.stringify(user));
  }

  public getCurrentUser(): Me | null {
    return this.userSubject.value;
  }

  public clearUser(): void {
    this.userSubject.next(null);
    localStorage.removeItem('user');
  }

  public refreshUser(): Observable<Me> {
    return this.getUserData().pipe(
      tap((user) => this.setUser(user))
    );
  }

  /*
  *  Requisições da API
  */

  public getUserData(): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Me>(this.api + '/me', { headers });
  }

  public getUserConfig(): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<void>(this.api + '/config', { headers });
  }

  public findAll(): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<UserMinData>>(this.api, { headers });
  }

  public listByPositionName(): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<UserGroup>>(this.api + '/group', { headers });
  }

  public findById(id: number): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<UserData>(this.api + '/' + id, { headers });
  }

  public insert(data: FormData): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.post<Array<UserMinData>>(this.api, data, { headers });
  }

  public update(id: number, data: FormData): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<Array<UserMinData>>(this.api + '/' + id, data, { headers });
  }

  public updateConfig(data: FormData): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<void>(this.api + '/config', data, { headers });
  }

  public deactivateUser(id: number): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<Array<UserMinData>>(this.api + '/deactivate-user/' + id, null, { headers });
  }
}
