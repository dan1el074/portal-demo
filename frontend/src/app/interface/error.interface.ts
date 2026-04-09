export interface Error {
  error: string;
  path: string;
  status: number;
  timestamp: string;
}

export interface FieldMessage {
  label: string;
  message: string;
}

export interface CustomError {
  errors: Array<FieldMessage>;
  path: string;
  status: number;
  timestamp: string;
}
