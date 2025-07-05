import {patchState, signalStore, withHooks, withMethods, withState} from '@ngrx/signals';
import {CurrencyDto} from '../models/currencyDto';
import {inject} from '@angular/core';
import {CurrencyService} from '../services/currency.service';

interface CurrencyState {
  currencies: CurrencyDto[];
}

export const CurrencyStore = signalStore(
  {providedIn: 'root'},
  withState<CurrencyState>(() => ({
    currencies: [],
  })),

  withMethods((store) => ({
    loadCurrencies() {
      const currencyService = inject(CurrencyService);
      if (store.currencies().length > 0) {
        return;
      }
      currencyService
        .getCurrencies()
        .subscribe({
          next: (data) => {
            patchState(store, {currencies: data});
          },
          error: (err) => {
            console.error('Currency load failed', err);
          }
        });
    }
  })),

  withHooks({
    onInit: (store) => {
      store.loadCurrencies();
    },
  })
)
