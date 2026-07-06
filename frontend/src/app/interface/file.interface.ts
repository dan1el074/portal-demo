export interface FileCard {
  id: number;
  title: string;
  fileName: string;
}

export interface UploadedFile {
  id: string;
  file: File;
  name: string;
  size: string;
}
