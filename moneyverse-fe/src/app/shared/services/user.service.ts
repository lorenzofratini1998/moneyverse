import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {UserUpdateRequestDto} from '../../core/auth/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly httpClient = inject(HttpClient);

  public updateUser(userId: string, request: Partial<UserUpdateRequestDto>) {
    return this.httpClient.put(`/users/${userId}`, request);
  }

  public deleteUser(userId: string) {
    return this.httpClient.delete(`/users/${userId}`);
  }
}
