import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Currency} from '../models/currency';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CurrencyService {
  private readonly httpClient = inject(HttpClient);

  private readonly baseUrl = environment.services.krakendUrl;

  public getCurrencies(): Observable<Currency[]> {
    return this.httpClient.get<Currency[]>(`${this.baseUrl}/currencies`);
  }
}
