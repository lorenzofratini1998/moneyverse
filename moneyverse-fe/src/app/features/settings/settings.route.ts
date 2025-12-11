import {Routes} from '@angular/router';
import {ProfileComponent} from './pages/profile/profile.component';
import {SettingsManagementComponent} from './pages/settings-management/settings-management.component';
import {PreferenceComponent} from './pages/preferences/preference.component';

export default [
  {path: '', redirectTo: 'profile', pathMatch: 'full'},
  {
    path: '', component: SettingsManagementComponent, children: [
      {path: 'profile', component: ProfileComponent},
      {path: 'preferences', component: PreferenceComponent}
    ]
  }
] as Routes;
