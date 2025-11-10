import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {firstValueFrom, map, Observable} from 'rxjs';
import {Language, Preference, UserPreference, UserPreferenceRequest} from '../models/preference.model';
import {environment} from '../../../environments/environment';
import {StorageService} from './storage.service';

@Injectable({
  providedIn: 'root'
})
export class PreferenceService {
  private readonly httpClient = inject(HttpClient);

  public getUserPreferences(userId: string): Observable<UserPreference[]> {
    return this.httpClient.get<UserPreference[]>(`/users/${userId}/preferences`);
  }

  public getUserPreference(userId: string, preferenceName: string): Observable<UserPreference> {
    return this.httpClient.get<UserPreference>(`/users/${userId}/preferences/${preferenceName}`);
  }

  public getPreferences(mandatory: boolean = false) {
    let params = new HttpParams();
    if (mandatory) {
      params = params.set('mandatory', 'true');
    }
    return this.httpClient.get<Preference[]>('/preferences', { params });
  }

  public async checkMissingPreferences(userId: string): Promise<boolean> {
    const userPreferences = (await firstValueFrom(this.getUserPreferences(userId))) ?? [];

    const missingPreferences = (await firstValueFrom(
      this.getPreferences(true).pipe(
        map(mandatoryPreferences =>
          mandatoryPreferences.filter(
            mandatoryPreference =>
              !userPreferences.some(
                userPreference => userPreference.preference.name === mandatoryPreference.name
              )
          )
        )
      )
    )) ?? [];
    return missingPreferences.length > 0;
  }


  public getLanguages(): Observable<Language[]> {
    return this.httpClient.get<Language[]>(`/languages`);
  }

  public saveUserPreferences(userId: string, userPreferences: UserPreferenceRequest[]): Observable<UserPreference[]> {
    return this.httpClient.post<UserPreference[]>(`/users/${userId}/preferences`, userPreferences);
  }

  public updateUserPreferences(userId: string, request: UserPreferenceRequest[]): Observable<UserPreference[]> {
    return this.httpClient.put<UserPreference[]>(`/users/${userId}/preferences`, request);
  }


}
