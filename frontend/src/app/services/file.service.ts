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

export interface ProjectSearchResult {
  fileName: string;
  source: 'NEW' | 'OLD';
}

@Injectable({
  providedIn: 'root',
})
export class FileService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public searchProject(projectNumber: string): Observable<ProjectSearchResult[]> {
    return this.http.get<ProjectSearchResult[]>(this.api + '/api/pdf/search', { params: { term: projectNumber } });
  }

  public openProject(projectName: string, source: ProjectSearchResult['source'] = 'OLD'): void {
    const url = this.api + '/api/pdf/' + encodeURIComponent(projectName) + '?source=' + source;

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
