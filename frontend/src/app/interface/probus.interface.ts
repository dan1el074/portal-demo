export interface ProbusConfig {
  jdbcUrl: string;
  username: string;
  passwordConfigured: boolean;
}

export interface ProbusConfigUpdate {
  jdbcUrl: string;
  username: string;
  password: string;
}
