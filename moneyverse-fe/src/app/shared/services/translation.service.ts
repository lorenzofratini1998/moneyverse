import {inject, Injectable} from '@angular/core';
import {InterpolationParameters, TranslateService} from '@ngx-translate/core';
import {toSignal} from '@angular/core/rxjs-interop';

@Injectable({
  providedIn: 'root'
})
export class TranslationService {

  private readonly translateService = inject(TranslateService);

  readonly lang = toSignal(this.translateService.onLangChange, {
    initialValue: null
  });

  translate(key: string | string[], interpolateParams?: InterpolationParameters): string {
    return this.translateService.instant(key, interpolateParams);
  }
}
