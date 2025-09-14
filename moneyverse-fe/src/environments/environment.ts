export const environment = {
  keycloak: {
    url: 'http://moneyverse-keycloak:7080',
    realm: 'moneyverse-fe',
    clientId: 'web-client',
    redirectUri: window.location.origin
  },
  storage: {
    expirationTime: 24 * 60 * 60 * 1000, // 1 day
  },
  services: {
    krakendUrl: 'http://moneyverse-krakend:8080',
    nginxUrl: 'http://moneyverse-nginx:80'
  }
}
