import {Component, inject} from '@angular/core';
import {RouterModule} from '@angular/router';
import {PreferenceService} from './shared/services/preference.service';
import {LoadingService} from './shared/services/loading.service';
import {ProgressSpinner} from 'primeng/progressspinner';
import {SystemService} from './core/services/system.service';

@Component({
  selector: 'app-root',
  imports: [RouterModule, ProgressSpinner],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [PreferenceService]
})
export class AppComponent {

  private readonly systemService = inject(SystemService);
  protected readonly loadingService = inject(LoadingService);

  constructor() {
    this.systemService.setupApplication();
  }
}
