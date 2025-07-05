import {Directive, ElementRef, forwardRef, HostListener, Input} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';

@Directive({
  selector: '[appDecimalFormat]',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DecimalFormatDirective),
      multi: true
    }
  ]
})
export class DecimalFormatDirective implements ControlValueAccessor {
  /** Numero di cifre decimali (default 2) */
  @Input() decimals = 2;

  /** Funzioni che Angular ci passa per notificare il model */
  private onChange: (value: string) => void = () => {
  };
  private onTouched: () => void = () => {
  };

  /** Manteniamo sempre l’ultimo valore valido */
  private previousValue = '';

  constructor(private el: ElementRef<HTMLInputElement>) {
  }

  /** Scrive sul view quando il model cambia */
  writeValue(value: any): void {
    if (value != null && value !== '') {
      const num = parseFloat(value);
      if (!isNaN(num)) {
        // Inizializziamo con il numero già formattato
        this.previousValue = num.toFixed(this.decimals);
        this.el.nativeElement.value = this.previousValue;
      } else {
        this.clear();
      }
    } else {
      this.clear();
    }
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  /** Al cambio dell’input, limitiamo il numero di decimali */
  @HostListener('input')
  onInput(): void {
    let val = this.el.nativeElement.value;

    if (val === '') {
      this.previousValue = '';
      this.onChange('');
      return;
    }

    // Pattern: cifre, poi optional “.cifre” fino a {decimals}
    const regex = new RegExp(
      `^\\d*(\\.\\d{0,${this.decimals}})?$`
    );

    if (!regex.test(val)) {
      // Se supera i decimali, tronchiamo
      const parts = val.split('.');
      parts[1] = parts[1]?.slice(0, this.decimals) ?? '';
      val = parts.join('.');
      this.el.nativeElement.value = val;
    }

    this.previousValue = val;
    this.onChange(val);
  }

  /** Al blur, “paddiamo” con zeri se necessario */
  @HostListener('blur')
  onBlur(): void {
    let val = this.el.nativeElement.value;

    if (val === '') {
      this.onChange('');
      this.onTouched();
      return;
    }

    let num = parseFloat(val);
    if (isNaN(num)) num = 0;

    const formatted = num.toFixed(this.decimals);
    this.el.nativeElement.value = formatted;
    this.previousValue = formatted;
    this.onChange(formatted);
    this.onTouched();
  }

  private clear() {
    this.el.nativeElement.value = '';
    this.previousValue = '';
  }
}
