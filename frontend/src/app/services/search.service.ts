import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public searchProject(projectNumber: string): Observable<any> {
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<string>>(this.api + '/api/pdf/search?term=' + projectNumber, { headers });
  }

  public openProject(projectName: string): void {
    const newTab = window.open('', '_blank');
    const token = sessionStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    this.http.get(this.api + '/api/pdf/' + encodeURIComponent(projectName), {
      headers,
      responseType: 'blob'
    }).subscribe((blob: Blob) => {

      const file = new File([blob], projectName, {
        type: 'application/pdf'
      });

      const fileURL = URL.createObjectURL(file);
      newTab!.location.href = fileURL;
    });
  }
}
