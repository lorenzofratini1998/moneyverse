export const environment = {
  keycloak: {
    url: 'http://localhost:7080',
    realm: 'moneyverse-fe',
    clientId: 'web-client',
    redirectUri: window.location.origin
  },
  storage: {
    expirationTime: 24 * 60 * 60 * 1000, // 1 day
  },
  services: {
    krakendUrl: 'http://localhost:8080',
    nginxUrl: 'http://localhost:80'
  }
}
