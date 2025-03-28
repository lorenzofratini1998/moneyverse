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

  public async logout(): Promise<void> {
    await this.keycloak.logout();
  }

  public async login(): Promise<void> {
    await this.keycloak.login();
  }
}
