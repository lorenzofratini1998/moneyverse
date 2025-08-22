import {Component, input, OnInit, signal} from '@angular/core';
import {NavigationEnd, Router, RouterModule} from '@angular/router';
import {filter} from 'rxjs/operators';
import {CommonModule} from '@angular/common';
import {RippleModule} from 'primeng/ripple';
import {MenuItem} from 'primeng/api';
import {SvgComponent} from '../../../../shared/components/svg/svg.component';
import {TranslatePipe} from '@ngx-translate/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-menu-item',
  imports: [CommonModule, RouterModule, RippleModule, SvgComponent, TranslatePipe],
  templateUrl: './menu-item.component.html',
})
export class MenuItemComponent implements OnInit {
  item = input.required<MenuItem>();
  isChild = input<boolean>(false);

  isExpanded = signal(false);

  constructor(private router: Router) {
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        takeUntilDestroyed()
      )
      .subscribe(() => {
        if (this.item().items) {
          this.checkChildrenActive();
        }
      });
  }

  ngOnInit() {
    if (this.item().items) {
      this.checkChildrenActive();
    }
  }

  toggleSubmenu() {
    this.isExpanded.update(expanded => !expanded);
  }

  private checkChildrenActive() {
    const currentItem = this.item();
    if (!currentItem.items) return;

    const hasActiveChild = currentItem.items.some(child =>
      this.router.isActive(child.routerLink || '', {
        paths: 'subset',
        queryParams: 'ignored',
        matrixParams: 'ignored',
        fragment: 'ignored'
      })
    );

    if (hasActiveChild) {
      this.isExpanded.set(true);
    }
  }
}
