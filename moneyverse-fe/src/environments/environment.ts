export const environment = {
  keycloak: {
    url: 'http://localhost:7080',
    realm: 'moneyverse-fe',
    clientId: 'web-client',
    redirectUri: window.location.origin + '/onboarding'
  },
  cacheExpirationTime: 15 * 60 * 1000 //15 minutes
}
