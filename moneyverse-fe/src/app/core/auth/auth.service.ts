import {inject, Injectable, signal} from '@angular/core';
import Keycloak from 'keycloak-js';
import {Observable, of, throwError} from 'rxjs';
import {UserModel} from './models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  readonly keycloak = inject(Keycloak);

  user = signal<UserModel>({
    userId: '',
    firstName: '',
    lastName: '',
    fullName: '',
    email: ''
  });

  constructor() {
    this.updateUserFromToken();
  }

  private updateUserFromToken() {
    const token = this.keycloak.tokenParsed;

    this.user.set({
      userId: token?.sub ?? '',
      firstName: token?.['given_name'] ?? '',
      lastName: token?.['family_name'] ?? '',
      fullName: token?.['name'] ?? '',
      email: token?.['email'] ?? ''
    });
  }

  public async refreshToken(): Promise<void> {
    try {
      await this.keycloak.updateToken(-1);
      this.updateUserFromToken();
    } catch (error) {
      console.error('Failed to refresh token', error);
      throw error;
    }
  }

  get token() {
    const token = this.keycloak.token;
    if (!token) {
      throw new Error('Token is not available');
    }
    return token;
  }

  public async logout(): Promise<void> {
    await this.keycloak.logout();
  }

  public async login(): Promise<void> {
    await this.keycloak.login();
  }
}
