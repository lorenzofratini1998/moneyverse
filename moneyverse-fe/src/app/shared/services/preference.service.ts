import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {firstValueFrom, map, Observable, of, tap} from 'rxjs';
import {LanguageDto, PreferenceDto, UserPreferenceDto, UserPreferenceRequestDto} from '../models/preference.model';
import {environment} from '../../../environments/environment';
import {StorageService} from './storage.service';
import {STORAGE_KEY_PREFERENCES_LANGS} from '../models/constants.model';

@Injectable({
  providedIn: 'root'
})
export class PreferenceService {
  private readonly httpClient = inject(HttpClient);
  private readonly storageService = inject(StorageService);
  private readonly baseUrl = environment.services.userManagementUrl;

  public getUserPreferences(userId: string): Observable<UserPreferenceDto[]> {
    return this.httpClient.get<UserPreferenceDto[]>(`${this.baseUrl}/users/${userId}/preferences`);
  }

  public getUserPreference(userId: string, preferenceName: string): Observable<UserPreferenceDto> {
    const userPreference = this.storageService.getItem<UserPreferenceDto>(preferenceName);
    if (userPreference) {
      return of(userPreference)
    }
    return this.httpClient.get<UserPreferenceDto>(`${this.baseUrl}/users/${userId}/preferences/${preferenceName}`).pipe(
      tap(userPreference => this.storageService.setItem(preferenceName, userPreference))
    );
  }

  public getPreferences(mandatory: boolean = false) {
    if (mandatory) {
      return this.httpClient.get<PreferenceDto[]>(`${this.baseUrl}/preferences?mandatory=true`);
    }
    return this.httpClient.get<PreferenceDto[]>(`${this.baseUrl}/preferences`);
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


  public getLanguages(): Observable<LanguageDto[]> {
    const languages = this.storageService.getItem<LanguageDto[]>(STORAGE_KEY_PREFERENCES_LANGS);
    if (languages) {
      return of(languages);
    }
    return this.httpClient.get<LanguageDto[]>(`${this.baseUrl}/languages`).pipe(tap(langs => this.storageService.setItem(STORAGE_KEY_PREFERENCES_LANGS, langs)));
  }

  public saveUserPreferences(userId: string, userPreferences: UserPreferenceRequestDto[]): Observable<UserPreferenceDto[]> {
    return this.httpClient.post<UserPreferenceDto[]>(`${this.baseUrl}/users/${userId}/preferences`, userPreferences);
  }


}
