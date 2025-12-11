import {Component, computed, DestroyRef, effect, inject, Renderer2} from '@angular/core';
import {NavigationEnd, Router, RouterOutlet} from '@angular/router';
import {filter} from 'rxjs';
import {TopbarComponent} from './topbar/topbar.component';
import {FooterComponent} from './footer.component';
import {AppSidebar} from './sidebar.component';
import {LayoutService} from '../layout.service';
import {NgClass} from '@angular/common';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {Toast} from 'primeng/toast';
import {ConfirmDialogComponent} from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-layout',
  imports: [
    TopbarComponent,
    AppSidebar,
    RouterOutlet,
    FooterComponent,
    NgClass,
    Toast,
    ConfirmDialogComponent
  ],
  template: `
    <div class="layout-wrapper" [ngClass]="containerClass()">
      <app-topbar/>
      @if (!layoutService.isOnboarding()) {
        <app-sidebar/>
      }
      <div class="layout-main-container">
        <div class="layout-main">
          <router-outlet></router-outlet>
        </div>
        <app-footer/>
        <p-toast/>
        <app-confirm-dialog/>
      </div>
      <div class="layout-mask animate-fadein"></div>
    </div>
  `
})
export class LayoutComponent {
  protected readonly layoutService = inject(LayoutService);
  private readonly renderer = inject(Renderer2);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  readonly containerClass = computed(() => {
    const config = this.layoutService.layoutConfig();
    const state = this.layoutService.layoutState();

    return {
      'layout-overlay': config.menuMode === 'overlay',
      'layout-static': config.menuMode === 'static',
      'layout-static-inactive': state.staticMenuDesktopInactive && config.menuMode === 'static',
      'layout-overlay-active': state.overlayMenuActive,
      'layout-mobile-active': state.staticMenuMobileActive
    };
  });

  private menuOutsideClickListener: (() => void) | null = null;

  constructor() {
    // Subscription for opening the overlay menu
    this.layoutService.overlayOpen$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        if (!this.layoutService.isOnboarding()) {
          this.handleOverlayMenuOpen();
        }
      });

    // Subscription for navigation events
    this.router.events
      .pipe(
        filter((event): event is NavigationEnd => event instanceof NavigationEnd),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => {
        if (!this.layoutService.isOnboarding()) {
          this.hideMenu();
        }
      });

    effect(() => {
      const state = this.layoutService.layoutState();
      if (state.staticMenuMobileActive) {
        this.blockBodyScroll();
      }
    });
  }

  private handleOverlayMenuOpen(): void {
    if (!this.menuOutsideClickListener) {
      this.menuOutsideClickListener = this.renderer.listen(
        'document',
        'click',
        (event: MouseEvent) => {
          if (this.isOutsideClicked(event)) {
            this.hideMenu();
          }
        }
      );
    }
  }

  private isOutsideClicked(event: MouseEvent): boolean {
    const sidebarEl = document.querySelector('.layout-sidebar');
    const topbarEl = document.querySelector('.layout-menu-button');
    const eventTarget = event.target as Node;

    return !(
      sidebarEl?.isSameNode(eventTarget) ||
      sidebarEl?.contains(eventTarget) ||
      topbarEl?.isSameNode(eventTarget) ||
      topbarEl?.contains(eventTarget)
    );
  }

  hideMenu() {
    this.layoutService.layoutState.update((prev) => ({
      ...prev,
      overlayMenuActive: false,
      staticMenuMobileActive: false,
      menuHoverActive: false
    }));
    if (this.menuOutsideClickListener) {
      this.menuOutsideClickListener();
      this.menuOutsideClickListener = null;
    }
    this.unblockBodyScroll();
  }

  private blockBodyScroll(): void {
    document.body.classList.add('blocked-scroll');
  }

  private unblockBodyScroll(): void {
    document.body.classList.remove('blocked-scroll');
  }
}
