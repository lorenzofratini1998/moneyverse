import {inject, Injectable} from '@angular/core';
import Keycloak from 'keycloak-js';
import {Observable, of} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly keycloak = inject(Keycloak);

  public getUserId(): Observable<string | null> {
    return of(this.keycloak.tokenParsed?.sub || null);
  }
}
