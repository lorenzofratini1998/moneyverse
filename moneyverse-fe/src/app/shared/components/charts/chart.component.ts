import {computed, Directive, inject, Injectable, signal} from '@angular/core';
import {LayoutService} from '../../../core/layout/layout.service';
import {CurrencyPipe} from '@angular/common';
import {PercentagePipe} from '../../pipes/percentage.pipe';
import {TranslationService} from '../../services/translation.service';

@Injectable({
  providedIn: 'root'
})
class CurrencyPipeInjectable extends CurrencyPipe {
}

@Injectable({
  providedIn: 'root'
})
class PercentagePipeInjectable extends PercentagePipe {
}

@Directive()
export abstract class ChartComponent {
  protected readonly layoutService = inject(LayoutService);
  protected readonly currencyPipe = inject(CurrencyPipeInjectable);
  protected readonly percentagePipe = inject(PercentagePipeInjectable);
  protected readonly translateService = inject(TranslationService);

  private readonly _textColor = signal(this.getTextColor());
  private readonly _surfaceCard = signal(this.getSurfaceCard());
  private readonly _colorPalette = signal(this.getPaletteColor());

  protected textColor = computed(() => this._textColor());
  protected surfaceCard = computed(() => this._surfaceCard());
  protected colorPalette = computed(() => this._colorPalette());

  protected constructor() {
    const observer = new MutationObserver(() => {
      this._textColor.set(this.getTextColor());
      this._surfaceCard.set(this.getSurfaceCard());
      this._colorPalette.set(this.getPaletteColor());
    });

    observer.observe(document.documentElement, {
      attributes: true,
      attributeFilter: ['class']
    });
  }

  private getTextColor(): string {
    return this.getCSSVariable('--text-color');
  }

  private getSurfaceCard(): string {
    return this.getCSSVariable('--surface-card');
  }

  private getPaletteColor(): string[] {
    const palette = [];
    for (let i = 0; i < 11; i++) {
      palette.push(this.getCSSVariable(`--chart-color-${i + 1}`));
    }
    return palette;
  }

  protected getCSSVariable(variableName: string): string {
    return getComputedStyle(document.documentElement)
      .getPropertyValue(variableName)
      .trim();
  }
}
