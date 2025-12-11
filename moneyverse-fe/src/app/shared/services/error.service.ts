import {inject, Injectable} from '@angular/core';
import {TranslationService} from './translation.service';
import {AbstractControl, ValidationErrors} from '@angular/forms';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {
  private readonly translateService = inject(TranslationService);

  getErrorMessage(control: AbstractControl | null, label: string, maxLength?: number): string {
    if (!control || !control.errors) return '';
    const translatedLabel = this.translateService.translate(label);

    const errors: ValidationErrors = control.errors;

    if (errors['required']) {
      return this.translateService.translate('app.form.errors.required', { field: translatedLabel });
    }

    if (errors['maxlength']) {
      const requiredLen = errors['maxlength'].requiredLength ?? maxLength;
      return this.translateService.translate('app.form.errors.maxlength', {
        field: translatedLabel,
        max: requiredLen
      });
    }

    const firstErrorKey = Object.keys(errors)[0];
    return this.translateService.translate(`app.form.errors.${firstErrorKey}`, {
      field: translatedLabel,
      ...errors[firstErrorKey]
    });
  }
}
