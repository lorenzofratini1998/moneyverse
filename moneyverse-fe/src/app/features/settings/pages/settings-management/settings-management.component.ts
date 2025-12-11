import {Component, computed, inject} from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {Tab, TabList, Tabs} from 'primeng/tabs';
import {TranslationService} from '../../../../shared/services/translation.service';

@Component({
  selector: 'app-settings-management',
  imports: [
    RouterOutlet,
    Tabs,
    TabList,
    Tab,
    RouterLink
  ],
  template: `
    <p-tabs value="profile" [scrollable]="true">
      <p-tablist>
        @for (tab of tabs(); track tab.route) {
          <p-tab [value]="tab.route" [routerLink]="tab.route" class="flex items-center !gap-2 text-inherit">
            <i [class]="tab.icon"></i>
            <span>{{ tab.label }}</span>
          </p-tab>
        }
      </p-tablist>
    </p-tabs>

    <div class="mt-4">
      <router-outlet></router-outlet>
    </div>
  `,
  styles: [
    `:host ::ng-deep {
      .p-tablist-tab-list {
        border-radius: var(--content-border-radius);
      }
    }
    `
  ]
})
export class SettingsManagementComponent {
  private readonly translateService = inject(TranslationService);

  tabs = computed(() => {
    this.translateService.lang();
    return [
      {route: 'profile', label: this.translateService.translate('app.profile'), icon: 'pi pi-user-edit'},
      {route: 'preferences', label: this.translateService.translate('app.preferences'), icon: 'pi pi-cog'}
    ]
  })

}
