export const environment = {
  keycloak: {
    url: 'http://localhost:30080',
    realm: 'moneyverse-fe',
    clientId: 'web-client',
    redirectUri: window.location.origin
  },
  storage: {
    expirationTime: 24 * 60 * 60 * 1000, // 1 day
  }
}
