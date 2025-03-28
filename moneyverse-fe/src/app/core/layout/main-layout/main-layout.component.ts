import {Component, inject} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {HeaderComponent} from '../header/header.component';
import {MenuComponent} from '../menu/menu.component';
import {FooterComponent} from '../footer/footer.component';
import {LoadingService} from '../../../shared/services/loading.service';

@Component({
  selector: 'app-main-layout',
  imports: [
    MenuComponent,
    RouterOutlet,
    HeaderComponent,
    FooterComponent
  ],
  templateUrl: './main-layout.component.html'
})
export class MainLayoutComponent {

  protected readonly loadingService = inject(LoadingService);

  menuVisible: boolean = true;

  toggleUserMenu() {
    this.menuVisible = !this.menuVisible;
  }

}
