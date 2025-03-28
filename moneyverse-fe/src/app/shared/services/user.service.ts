import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {UserUpdateRequestDto} from '../../core/auth/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly httpClient = inject(HttpClient);
  private readonly baseUrl = environment.services.userManagementUrl;

  public updateUser(userId: string, request: Partial<UserUpdateRequestDto>) {
    return this.httpClient.put(`${this.baseUrl}/users/${userId}`, request);
  }

  public deleteUser(userId: string) {
    return this.httpClient.delete(`${this.baseUrl}/users/${userId}`);
  }
}
