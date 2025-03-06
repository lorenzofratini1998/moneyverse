import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, of, switchMap, tap} from 'rxjs';
import {UserPreferenceDto} from './shared/dto/user-preference.dto';
import {CacheService} from '../../shared/services/cache.service';
import {AuthService} from '../../core/auth/auth.service';
import {PreferenceDto} from './shared/dto/preference.dto';

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {
  private readonly httpClient = inject(HttpClient);
  private readonly cacheService = inject(CacheService);
  private readonly authService = inject(AuthService);
  private readonly baseUrl = "http://localhost:8081/usersManagement/api/v1";

  public getAuthenticatedUserPreferences(): Observable<UserPreferenceDto[]> {
    return this.authService.getUserId().pipe(
      switchMap(userId => {
        if (!userId) {
          console.error("User ID not found");
          return of([]);
        }
        return this.getUserPreferences(userId);
      })
    )
  }

  private getUserPreferences(userId: string): Observable<UserPreferenceDto[]> {
    const cacheKey = this.getCacheKey(userId);
    const cachedData = this.cacheService.get<UserPreferenceDto[]>(cacheKey);
    if (cachedData) {
      console.log(`Returning cached data from localStorage for user ${userId}`);
      return of(cachedData);
    }
    console.log(`Fetching preferences from API for user ${userId}`);
    return this.httpClient.get<UserPreferenceDto[]>(`${this.baseUrl}/users/${userId}/preferences`)
      .pipe(
        tap(data => this.cacheService.save(cacheKey, data)),
      );
  }

  public getMandatoryPreferences(): Observable<PreferenceDto[]> {
    return this.httpClient.get<PreferenceDto[]>(`${this.baseUrl}/preferences?mandatory=true`);
  }


  private getCacheKey(userId: string) {
    return `userPreferences-${userId}`;
  }
}
