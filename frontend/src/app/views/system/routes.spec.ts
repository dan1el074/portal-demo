import { routes } from './routes';

describe('System routes', () => {
  it('protects the parameters page with the global admin and system parameters roles', () => {
    const paramsRoute = routes[0].children?.find(route => route.path === 'params');

    expect(paramsRoute).toBeDefined();
    expect(paramsRoute?.data?.['roles']).toEqual(['ROLE_ADMIN', 'ROLE_SYSTEM_PARAMS']);
  });
});
