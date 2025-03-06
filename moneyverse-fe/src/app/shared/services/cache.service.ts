import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CacheService {
  private readonly cacheExpirationTime = environment.cacheExpirationTime;

  public get<T>(key: string): T | null {
    const cacheData = localStorage.getItem(key);
    if (!cacheData) {
      return null;
    }
    const data = JSON.parse(cacheData);
    if (new Date().getTime() > data.expDate) {
      this.remove(key);
      return null;
    }
    return data.value;
  }

  public save<T>(key: string, data: T): void {
    this.saveWithExpirationDate(key, data, new Date().getTime() + this.cacheExpirationTime);
  }

  public saveWithExpirationDate<T>(key: string, data: T, expDate: number): void {
    localStorage.setItem(key, JSON.stringify({
      value: data,
      expDate
    }));
  }

  public remove(key: string): void {
    localStorage.removeItem(key);
  }
}
