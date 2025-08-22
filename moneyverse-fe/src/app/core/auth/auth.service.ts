import {inject, Injectable} from '@angular/core';
import Keycloak from 'keycloak-js';
import {Observable, of, throwError} from 'rxjs';
import {UserModel} from './models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  readonly keycloak = inject(Keycloak);

  public getUserId(): Observable<string> {
    const userId = this.keycloak.tokenParsed?.sub;
    return userId ? of(userId) : throwError(() => new Error("User ID is missing"));
  }

  public getAuthenticatedUser(): UserModel {
    return {
      userId: this.keycloak.tokenParsed?.sub ?? '',
      firstName: this.keycloak.tokenParsed?.['given_name'] ?? '',
      lastName: this.keycloak.tokenParsed?.['family_name'] ?? '',
      fullName: this.keycloak.tokenParsed?.['name'] ?? '',
      email: this.keycloak.tokenParsed?.['email'] ?? ''
    };
  }

  get token() {
    const token = this.keycloak.token;
    if (!token) {
      throw new Error('Token is not available');
    }
    return token;
  }

  get authenticatedUser(): UserModel {
    const tokenParsed = this.keycloak.tokenParsed;
    if (!tokenParsed) {
      throw new Error('Token is not available');
    }
    return {
      userId: tokenParsed.sub ?? '',
      firstName: tokenParsed['given_name'] ?? '',
      lastName: tokenParsed['family_name'] ?? '',
      fullName: tokenParsed['name'] ?? '',
      email: tokenParsed['email'] ?? ''
    }
  }

  public async logout(): Promise<void> {
    await this.keycloak.logout();
  }

  public async login(): Promise<void> {
    await this.keycloak.login();
  }
}
