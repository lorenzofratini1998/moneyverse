import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {AuthGuardData, createAuthGuard} from 'keycloak-angular';
import {inject} from '@angular/core';
import {PreferenceService} from '../../shared/services/preference.service';
import {AuthService} from './auth.service';
import {firstValueFrom} from 'rxjs';
import {StorageService} from '../../shared/services/storage.service';

import {STORAGE_MISSING_MANDATORY_PREFERENCES} from '../../shared/models/preference.model';

const isAccessAllowed = async (
  route: ActivatedRouteSnapshot,
  _: RouterStateSnapshot,
  authData: AuthGuardData
): Promise<boolean | UrlTree> => {
  const {authenticated, grantedRoles} = authData;
  const authService = inject(AuthService);

  if (!authenticated) {
    await authService.login();
  }

  const requiredRole = route.data['role'];
  if (!requiredRole) {
    return false;
  }

  const hasRequiredRole = (role: string): boolean =>
    Object.values(grantedRoles.resourceRoles).some((roles) => roles.includes(role));

  if (authenticated && hasRequiredRole(requiredRole)) {
    return true;
  }

  const router = inject(Router);
  return router.parseUrl('/forbidden');
};


const areMandatoryPreferencesMissing = async (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
  authData: AuthGuardData,
): Promise<boolean | UrlTree> => {

  const authService = inject(AuthService);
  const userService = inject(PreferenceService);
  const storageService = inject(StorageService);
  const router = inject(Router);

  if (state.url === '/onboarding') {
    return true;
  }

  try {
    const userId = await firstValueFrom(authService.getUserId());
    let missingPreferences: boolean;
    const storageMissingPreferences = storageService.getItem(STORAGE_MISSING_MANDATORY_PREFERENCES);

    if (storageMissingPreferences !== null) {
      missingPreferences = storageMissingPreferences === true;
    } else {
      missingPreferences = await userService.checkMissingPreferences(userId);
      storageService.setItem(STORAGE_MISSING_MANDATORY_PREFERENCES, missingPreferences);
    }

    if (!missingPreferences) {
      return true;
    }

    console.log('Missing mandatory preferences, redirecting to onboarding');
    return router.parseUrl('/onboarding');
  } catch (error) {
    console.error('Error checking mandatory preferences:', error);
    return true;
  }
}

export const canActivateAuthRole = createAuthGuard<CanActivateFn>(isAccessAllowed);
export const canUseApplication = createAuthGuard<CanActivateFn>(areMandatoryPreferencesMissing);
