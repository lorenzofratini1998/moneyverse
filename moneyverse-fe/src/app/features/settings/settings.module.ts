import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SettingsComponent} from './pages/settings/settings.component';
import {ProfileComponent} from './pages/profile/profile.component';
import {PreferencesComponent} from './pages/preferences/preferences.component';
import {SettingsRoutingModule} from './settings-routing.module';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    ProfileComponent,
    SettingsComponent,
    PreferencesComponent,
    SettingsRoutingModule
  ]
})
export class SettingsModule {
}
