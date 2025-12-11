import {computed, DestroyRef, effect, inject, Injectable, signal} from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {debounceTime, fromEvent, map, startWith, Subject} from 'rxjs';
import {PreferenceKey} from '../../shared/models/preference.model';
import {Router} from '@angular/router';

export interface LayoutConfig {
  preset?: string;
  primary?: string;
  surface?: string | undefined | null;
  darkTheme?: boolean;
  menuMode?: string;
}

interface LayoutState {
  staticMenuDesktopInactive?: boolean;
  overlayMenuActive?: boolean;
  configSidebarVisible?: boolean;
  staticMenuMobileActive?: boolean;
  menuHoverActive?: boolean;
}

interface MenuChangeEvent {
  key: string;
  routeEvent?: boolean;
}

type Theme = 'light' | 'dark';

@Injectable({
  providedIn: 'root'
})
export class LayoutService {
  private readonly destroyRef = inject(DestroyRef);

  _config: LayoutConfig = {
    preset: 'Aura',
    primary: 'noir',
    surface: null,
    darkTheme: this.initializeTheme() === 'dark',
    menuMode: 'overlay'
  };

  _state: LayoutState = {
    staticMenuDesktopInactive: false,
    overlayMenuActive: false,
    configSidebarVisible: false,
    staticMenuMobileActive: false,
    menuHoverActive: false
  };

  readonly layoutConfig = signal<LayoutConfig>(this._config);
  readonly layoutState = signal<LayoutState>(this._state);

  readonly transitionComplete = signal<boolean>(false);

  readonly isDesktop = signal<boolean>(window.innerWidth > 991);

  private readonly configUpdateSubject = new Subject<LayoutConfig>();
  private readonly overlayOpenSubject = new Subject<void>();
  private readonly menuSourceSubject = new Subject<MenuChangeEvent>();
  private readonly resetSubject = new Subject<void>();
  private readonly router = inject(Router);

  readonly configUpdate$ = this.configUpdateSubject.asObservable();
  readonly overlayOpen$ = this.overlayOpenSubject.asObservable();
  readonly menuSource$ = this.menuSourceSubject.asObservable();
  readonly resetSource$ = this.resetSubject.asObservable();

  readonly theme = computed(() =>
    this.layoutConfig().darkTheme ? 'dark' : 'light'
  );

  readonly isSidebarActive = computed(() => {
    const state = this.layoutState();
    return state.overlayMenuActive || state.staticMenuMobileActive;
  });

  readonly isDarkTheme = computed(() =>
    this.layoutConfig().darkTheme
  );

  readonly getPrimary = computed(() =>
    this.layoutConfig().primary
  );

  readonly getSurface = computed(() =>
    this.layoutConfig().surface
  );

  readonly isOverlay = computed(() =>
    this.layoutConfig().menuMode === 'overlay'
  );

  readonly isMobile = computed(() =>
    !this.isDesktop()
  );

  private initialized = false;

  constructor() {
    this.setupResizeListener();
    this.setupConfigEffects();
  }

  private setupResizeListener(): void {
    fromEvent(window, 'resize')
      .pipe(
        debounceTime(150),
        map(() => window.innerWidth > 991),
        startWith(window.innerWidth > 991),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(isDesktop => {
        this.isDesktop.set(isDesktop);
      });
  }

  private setupConfigEffects(): void {
    effect(() => {
      const config = this.layoutConfig();
      if (config && this.initialized) {
        this.onConfigUpdate();
      }
    });

    effect(() => {
      const config = this.layoutConfig();

      if (!this.initialized) {
        this.initialized = true;
        this.toggleDarkMode(config);
        return;
      }

      if (config) {
        this.handleDarkModeTransition(config);
      }
    });

    effect(() => {
      const isDesktop = this.isDesktop();
      const state = this.layoutState();

      if (isDesktop && state.staticMenuMobileActive) {
        this.layoutState.update(prev => ({
          ...prev,
          staticMenuMobileActive: false,
          overlayMenuActive: false
        }));
      }
    });
  }

  private initializeTheme() {
    const savedTheme = localStorage.getItem(PreferenceKey.THEME) as Theme;
    if (savedTheme && (savedTheme === 'light' || savedTheme === 'dark')) {
      return savedTheme;
    } else {
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      return prefersDark ? 'dark' : 'light';
    }
  }

  private handleDarkModeTransition(config: LayoutConfig): void {
    if ((document as any).startViewTransition) {
      this.startViewTransition(config);
    } else {
      this.toggleDarkMode(config);
      this.onTransitionEnd();
    }
  }

  private startViewTransition(config: LayoutConfig): void {
    const transition = (document as any).startViewTransition(() => {
      this.toggleDarkMode(config);
    });

    transition.ready
      .then(() => this.onTransitionEnd())
      .catch(() => {
        this.onTransitionEnd();
      });
  }

  toggleTheme(): void {
    const _config = this.layoutConfig();
    const darkTheme = !_config.darkTheme;

    this.layoutConfig.update(prev => ({
      ...prev,
      darkTheme: darkTheme
    }));

    this.saveThemePreference(darkTheme);
  }

  private saveThemePreference(isDark: boolean): void {
    try {
      localStorage.setItem(PreferenceKey.THEME, isDark ? 'dark' : 'light');
    } catch (err) {
      console.warn('Unable to save theme preference to localStorage:', err);
    }
  }

  private toggleDarkMode(config: LayoutConfig): void {
    const _config = config || this.layoutConfig();
    if (_config.darkTheme) {
      document.documentElement.classList.add('app-dark');
    } else {
      document.documentElement.classList.remove('app-dark');
    }
  }

  private onTransitionEnd(): void {
    this.transitionComplete.set(true);
    requestAnimationFrame(() => {
      this.transitionComplete.set(false);
    });
  }

  onMenuToggle() {
    if (this.isOverlay()) {
      this.toggleOverlayMenu();
    }

    if (this.isDesktop()) {
      this.toggleDesktopStaticMenu();
    } else {
      this.toggleMobileStaticMenu();
    }
  }

  private toggleOverlayMenu(): void {
    this.layoutState.update((prev) => ({...prev, overlayMenuActive: !this.layoutState().overlayMenuActive}));

    if (this.layoutState().overlayMenuActive) {
      this.overlayOpenSubject.next();
    }
  }

  private toggleDesktopStaticMenu(): void {
    this.layoutState.update((prev) => ({
      ...prev,
      staticMenuDesktopInactive: !this.layoutState().staticMenuDesktopInactive
    }));
  }

  private toggleMobileStaticMenu(): void {
    this.layoutState.update((prev) => ({...prev, staticMenuMobileActive: !this.layoutState().staticMenuMobileActive}));

    if (this.layoutState().staticMenuMobileActive) {
      this.overlayOpenSubject.next();
    }
  }

  onConfigUpdate() {
    this.configUpdateSubject.next(this.layoutConfig());
  }

  onMenuStateChange(event: MenuChangeEvent) {
    this.menuSourceSubject.next(event);
  }

  reset() {
    this.layoutConfig.set(this._config);
    this.layoutState.set(this._state);
    this.resetSubject.next();
  }

  isOnboarding(): boolean {
    return this.router.url.includes('/onboarding');
  }

  isAnalyticsPage(): boolean {
    return this.router.url.includes('/dashboard') || this.router.url.includes('/analytics');
  }
}
