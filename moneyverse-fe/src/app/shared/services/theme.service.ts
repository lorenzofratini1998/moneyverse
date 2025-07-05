import {inject, Injectable, signal, effect} from '@angular/core';
import {DOCUMENT} from '@angular/common';

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
      this.setTheme('dark');
    } else {
      this.setTheme('light');
    }
  }

  setTheme(theme: Theme) {
    this.currentTheme.set(theme);
    this.saveThemePreference(theme);
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

  getCurrentTheme(): Theme {
    return this.currentTheme();
  }

  initializeTheme() {
    const savedTheme = localStorage.getItem('theme') as Theme;
    if (savedTheme && (savedTheme === 'light' || savedTheme === 'dark')) {
      this.setTheme(savedTheme);
    } else {
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      this.setTheme(prefersDark ? 'dark' : 'light');
    }
  }

  private saveThemePreference(theme: Theme) {
    localStorage.setItem('theme', theme);
  }
}
