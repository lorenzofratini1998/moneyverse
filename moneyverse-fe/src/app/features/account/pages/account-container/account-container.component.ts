import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-account-container',
  imports: [
    RouterOutlet
  ],
  template: `
    <div class="container mx-auto p-6">
      <router-outlet></router-outlet>
    </div>
  `
})
export class AccountContainerComponent {

}
