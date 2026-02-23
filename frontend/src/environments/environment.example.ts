import packageJson from '../../package.json';

export const environment = {
  production: true,
  appName: 'appName',
  appVersion: packageJson.version,
  apiUrl: 'http://base.url:1234',
  clientId: 'clientId',
  clientSecret: 'clientSecret',
};
