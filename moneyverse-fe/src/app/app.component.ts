import {Component, effect, inject, Signal} from '@angular/core';
import {RouterModule} from '@angular/router';
import {UserManagementService} from './features/user-management/user-management.service';
import {UserPreferenceDto} from './features/user-management/shared/dto/user-preference.dto';
import {toSignal} from '@angular/core/rxjs-interop';
import {PreferenceDto} from './features/user-management/shared/dto/preference.dto';

@Component({
  selector: 'app-root',
  imports: [RouterModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [UserManagementService]
})
export class AppComponent {
  private readonly userManagementService = inject(UserManagementService);

  title = 'moneyverse-fe';
  public preferences$: Signal<PreferenceDto[]>;
  public userPreferences$: Signal<UserPreferenceDto[]>;

  public constructor() {
    this.preferences$ = toSignal(this.userManagementService.getMandatoryPreferences(), {initialValue: []});
    effect(() => {
      const preferences = this.preferences$();
      if (preferences.length === 0) {
        console.log("No mandatory preferences found");
      } else {
        console.log("Mandatory preferences found:", preferences);
      }
    });
    this.userPreferences$ = toSignal(
      this.userManagementService.getAuthenticatedUserPreferences(),
      {initialValue: []}
    );
    effect(() => {
      const userPreferences = this.userPreferences$();
      if (userPreferences.length === 0) {
        console.log("No user preferences found for user");
      } else {
        console.log("User preferences found:", userPreferences);
      }
    });
  }

}
