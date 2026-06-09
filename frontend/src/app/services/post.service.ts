import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { NewPost, PostCard } from '../interface/post.interface';

@Injectable({
  providedIn: 'root',
})
export class PostService {
  private api = environment.apiUrl + '/api/post';

  constructor(private http: HttpClient) {}

  public getFeedFromId(lastId: number, limit = 4): Observable<PostCard[]> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    const params = new HttpParams()
      .set('lastId', lastId.toString())
      .set('limit', limit.toString());

    return this.http.get<Array<PostCard>>(this.api, { headers, params });
  }

  public insert(data: FormData): Observable<any> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.post(this.api, data, { headers });
  }

  public delete(id: number): Observable<any> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.delete<void>(this.api + `/${id}`, { headers });
  }

  public update(id: number, data: NewPost): Observable<any> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.put<void>(this.api + `/${id}`, data, { headers });
  }
}
