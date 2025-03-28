import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {StorageItem} from '../models/storage.model';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  private readonly expirationTime = environment.storage.expirationTime;

  public getItem<T>(key: string, isSession = false): T | null {
    const storage = this.getStorage(isSession);
    const item = storage.getItem(key);
    if (!item) {
      return null;
    }
    const data: StorageItem = JSON.parse(item);
    if (new Date().getTime() > data.expiresAt) {
      this.remove(key);
      return null;
    }
    return JSON.parse(data.value);
  }

  public setItem<T>(key: string, value: T, isSession = false, expirationTime: number = this.expirationTime): void {
    const storage = this.getStorage(isSession);
    const item: StorageItem = {
      value: JSON.stringify(value),
      expiresAt: new Date().getTime() + expirationTime
    };
    storage.setItem(key, JSON.stringify(item));
  }

  public remove(key: string, isSession = false): void {
    const storage = this.getStorage(isSession);
    storage.removeItem(key);
  }

  public clear(isSession = false): void {
    const storage = this.getStorage(isSession);
    storage.clear();
  }

  private getStorage(isSession: boolean): Storage {
    return isSession ? sessionStorage : localStorage;
  }
}
