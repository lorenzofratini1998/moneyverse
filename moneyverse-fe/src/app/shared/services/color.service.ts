import {inject, Injectable} from '@angular/core';
import {Color, COLORS} from '../models/color.model';
import {ThemeService} from './theme.service';

@Injectable({
  providedIn: 'root'
})
export class ColorService {
  private readonly colors = COLORS;
  private readonly themeService = inject(ThemeService);

  default(): Color {
    const defaultColor = this.colors.find(c => c.selected) ?? this.colors[0];
    return {
      name: defaultColor.name,
      selected: defaultColor.selected,
      background: this.themeService.theme === 'light' ? defaultColor.light.background : defaultColor.dark?.background ?? defaultColor.light.background,
      text: this.themeService.theme === 'light' ? defaultColor.light.text : defaultColor.dark?.text ?? defaultColor.light.text
    };
  }

  color(name: string, excludeTheme: boolean = false): Color {
    const match = this.colors.find(c => c.name === name);
    if (match) {
      return {
        name: match.name,
        selected: match.selected,
        background: excludeTheme ? match.light.background : (this.themeService.theme === 'light' ? match.light.background : match.dark?.background ?? match.light.background),
        text: excludeTheme ? match.light.text : (this.themeService.theme === 'light' ? match.light.text : match.dark?.text ?? match.light.text)
      }
    }
    return this.default();
  }

  getStyle(colorName: string) {
    const color = this.color(colorName);
    return {
      'background-color': color.background,
      'color': color.text,
    };
  }

}
