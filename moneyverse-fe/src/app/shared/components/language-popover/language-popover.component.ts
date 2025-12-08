import {Component, inject} from '@angular/core';
import {Popover} from 'primeng/popover';
import {SystemService} from '../../../core/services/system.service';
import {FlagComponent} from '../flag/flag.component';
import {PreferenceStore} from '../../stores/preference.store';
import {PreferenceKey} from '../../models/preference.model';

@Component({
  selector: 'app-language-popover',
  imports: [
    Popover,
    FlagComponent
  ],
  template: `
    @if (systemService.languages().length > 0 && systemService.currentLanguage()) {
      <button #languageButton class="layout-topbar-action" type="button" (click)="languageOp.toggle($event)">
        <app-flag [code]="systemService.currentLanguage()?.isoCode ?? 'en'" class="size-6"/>
      </button>

      <p-popover #languageOp>
        <ul class="list-none p-0 m-0 flex flex-col">
          @for (language of systemService.languages(); track language.isoCode) {
            <li class="gap-2 p-2 hover:bg-emphasis cursor-pointer rounded-border">
              <a
                class="flex items-center gap-2"
                (click)="changeLanguage(language.isoCode)"
              >
                <app-flag [code]="language.isoCode" class="size-5"/>
                <span>{{ language.country }}</span>
              </a>
            </li>
          }
        </ul>
      </p-popover>
    }
  `
})
export class LanguagePopoverComponent {

  protected readonly systemService = inject(SystemService);
  private readonly preferenceStore = inject(PreferenceStore);

  protected changeLanguage(language: string) {
    let userLanguage = this.preferenceStore.preferences()[PreferenceKey.LANGUAGE]!;
    userLanguage.value = language;
    this.preferenceStore.updatePreference(PreferenceKey.LANGUAGE, userLanguage);
    this.systemService.changeLanguage(language);
  }

}
