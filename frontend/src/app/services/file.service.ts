import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

declare global {
  interface Window {
    PortalMetaroAndroid?: {
      openPdf(url: string, bearerToken: string, fileName: string): void;
      openFile(url: string, bearerToken: string, fileName: string): void;
    };
  }
}

@Injectable({
  providedIn: 'root',
})
export class FileService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public searchProject(projectNumber: string): Observable<any> {
    const token = localStorage.getItem('auth-token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    return this.http.get<Array<string>>(this.api + '/api/pdf/search?term=' + projectNumber, { headers });
  }

  public openProject(projectName: string): void {
    const token = localStorage.getItem('auth-token') ?? '';
    const url = this.api + '/api/pdf/' + encodeURIComponent(projectName);

    if (window.PortalMetaroAndroid) {
      window.PortalMetaroAndroid.openPdf(url, token, projectName);
      return;
    }

    const newTab = window.open('', '_blank');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
    this.http.get(url, { headers, responseType: 'blob' }).subscribe((blob: Blob) => {
      const file = new File([blob], projectName, { type: 'application/pdf' });
      newTab!.location.href = URL.createObjectURL(file);
    });
  }

  public openFile(fileName: string): void {
    const token = localStorage.getItem('auth-token') ?? '';
    const url = this.api + '/api/file/' + encodeURIComponent(fileName);

    if (window.PortalMetaroAndroid) {
      window.PortalMetaroAndroid.openFile(url, token, fileName);
      return;
    }

    const newTab = window.open('', '_blank');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
    this.http.get(url, { headers, responseType: 'blob' }).subscribe(blob => {
      newTab!.location.href = URL.createObjectURL(blob);
    });
  }
}
