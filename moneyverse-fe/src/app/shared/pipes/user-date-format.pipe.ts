import {inject, Pipe, PipeTransform} from '@angular/core';
import {PreferenceStore} from '../stores/preference.store';

@Pipe({
  name: 'userDateFormat',
  standalone: true,
  pure: false
})
export class UserDateFormatPipe implements PipeTransform {
  private readonly preferenceStore = inject(PreferenceStore);

  transform(date: Date | string, fallbackFormat?: string, mode: "full" | "year" | "month" | "year-month" = "full"): string {
    if (!date) return '';

    const dateObj = date instanceof Date ? date : new Date(date);
    const userFormat = this.preferenceStore.userDateFormat();

    let format = userFormat || fallbackFormat || 'yyyy-MM-dd';

    switch (mode) {
      case "year":
        format = "yyyy";
        break;
      case "month":
        format = "MM";
        break;
      case "year-month":
        format = "MM-yyyy";
        break;
    }

    return this.formatDate(dateObj, format);
  }

  private formatDate(date: Date, format: string): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return format
      .replace(/yyyy/g, year.toString())
      .replace(/MM/g, month)
      .replace(/dd/g, day)
      .replace(/yy/g, year.toString().slice(-2));
  }

}
