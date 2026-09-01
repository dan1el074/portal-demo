export interface FoccoConfig {
  baseUrl: string;
  key: string;
  tokenConfigured: boolean;
}

export interface FoccoConfigUpdate {
  baseUrl: string;
  key: string;
  token: string;
}
