import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { ActiveSession, Me, UserData, UserGroup, UserMinData } from './../interface/user.interface';

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

  /**
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

  /**
  *  Requisições da API
  */

  public getUserData(): Observable<Me> {
    return this.http.get<Me>(this.api + '/me');
  }

  public getUserConfig(): Observable<any> {
    return this.http.get<void>(this.api + '/config');
  }

  public findAll(): Observable<any> {
    return this.http.get<Array<UserMinData>>(this.api);
  }

  public listByPositionName(): Observable<any> {
    return this.http.get<Array<UserGroup>>(this.api + '/group');
  }

  public findById(id: number): Observable<any> {
    return this.http.get<UserData>(this.api + '/' + id);
  }

  public insert(data: FormData): Observable<any> {
    return this.http.post<Array<UserMinData>>(this.api, data);
  }

  public update(id: number, data: FormData): Observable<any> {
    return this.http.put<Array<UserMinData>>(this.api + '/' + id, data);
  }

  public updateConfig(data: FormData): Observable<any> {
    return this.http.put<void>(this.api + '/config', data);
  }

  public deactivateUser(id: number): Observable<any> {
    return this.http.put<Array<UserMinData>>(this.api + '/deactivate-user/' + id, null);
  }

  public getActiveSessions(): Observable<Array<ActiveSession>> {
    return this.http.get<Array<ActiveSession>>(environment.apiUrl + '/api/auth/active-sessions');
  }

  public disconnectActiveSession(userId: number): Observable<void> {
    return this.http.delete<void>(environment.apiUrl + '/api/auth/active-sessions/user/' + userId);
  }
}
