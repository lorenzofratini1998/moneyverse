import {inject, Injectable, signal} from '@angular/core';
import {DOCUMENT} from '@angular/common';

export type Theme = 'light' | 'dark';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly document = inject(DOCUMENT);
  private readonly currentTheme = signal<Theme>('light');

  toggleTheme() {
    if (this.currentTheme() == 'light') {
      this.setTheme('dark');
    } else {
      this.setTheme('light');
    }
  }

  setTheme(theme: Theme) {
    this.currentTheme.set(theme);
    this.document.documentElement.setAttribute('data-theme', theme);
  }

  getCurrentTheme(): Theme {
    return this.currentTheme();
  }
}
