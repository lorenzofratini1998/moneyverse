import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Currency} from '../models/currency';

@Injectable({
  providedIn: 'root'
})
export class CurrencyService {
  private readonly httpClient = inject(HttpClient);

  private readonly baseUrl = "http://localhost:8080/currencyManagement/api/v1";

  public getCurrencies(): Observable<Currency[]> {
    return this.httpClient.get<Currency[]>(`${this.baseUrl}/currencies`);
  }
}
