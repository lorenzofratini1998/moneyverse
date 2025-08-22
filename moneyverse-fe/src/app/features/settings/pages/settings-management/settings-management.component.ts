import {Component} from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {Tab, TabList, Tabs} from 'primeng/tabs';

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
        @for (tab of tabs; track tab.route) {
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
  tabs = [
    {route: 'profile', label: 'Profile', icon: 'pi pi-user-edit'},
    {route: 'preferences', label: 'Preferences', icon: 'pi pi-cog'}
  ]

}
