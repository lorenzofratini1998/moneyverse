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
    userManagementUrl: 'http://localhost:8081/usersManagement/api/v1',
    accountManagementUrl: 'http://localhost:8082/accountsManagement/api/v1'
  }
}
