import {effect, inject, Injectable, signal} from '@angular/core';
import {DOCUMENT} from '@angular/common';
import {PreferenceKey} from '../models/preference.model';

export type Theme = 'light' | 'dark';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly document = inject(DOCUMENT);
  private readonly currentTheme = signal<Theme>('light');

  constructor() {
    effect(() => {
      this.updateThemes(this.currentTheme());
    });
  }

  toggleTheme() {
    if (this.currentTheme() == 'light') {
      this.theme = 'dark';
    } else {
      this.theme = 'light';
    }
  }

  set theme(theme: Theme) {
    this.currentTheme.set(theme);
    this.store(theme);
  }

  get theme(): Theme {
    return this.currentTheme();
  }

  private updateThemes(theme: Theme) {
    // Aggiorna DaisyUI
    this.document.documentElement.setAttribute('data-theme', theme);

    // Aggiorna PrimeNG
    if (theme === 'dark') {
      this.document.documentElement.classList.add('p-dark');
    } else {
      this.document.documentElement.classList.remove('p-dark');
    }
  }

  initializeTheme() {
    const savedTheme = localStorage.getItem(PreferenceKey.THEME) as Theme;
    if (savedTheme && (savedTheme === 'light' || savedTheme === 'dark')) {
      this.theme = savedTheme;
    } else {
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      this.theme = prefersDark ? 'dark' : 'light';
    }
  }

  private store(theme: Theme) {
    localStorage.setItem(PreferenceKey.THEME, theme);
  }
}
