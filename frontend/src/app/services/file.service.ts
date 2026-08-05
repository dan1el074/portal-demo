import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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
    return this.http.get<Array<string>>(this.api + '/api/pdf/search?term=' + projectNumber);
  }

  public openProject(projectName: string): void {
    const url = this.api + '/api/pdf/' + encodeURIComponent(projectName);

    if (window.PortalMetaroAndroid) {
      window.PortalMetaroAndroid.openPdf(url, '', projectName);
      return;
    }

    const newTab = window.open('', '_blank');
    this.http.get(url, { responseType: 'blob' }).subscribe((blob: Blob) => {
      const file = new File([blob], projectName, { type: 'application/pdf' });
      newTab!.location.href = URL.createObjectURL(file);
    });
  }

  public openFile(fileName: string): void {
    const url = this.api + '/api/file/' + encodeURIComponent(fileName);

    if (window.PortalMetaroAndroid) {
      window.PortalMetaroAndroid.openFile(url, '', fileName);
      return;
    }

    const newTab = window.open('', '_blank');
    this.http.get(url, { responseType: 'blob' }).subscribe(blob => {
      newTab!.location.href = URL.createObjectURL(blob);
    });
  }
}
