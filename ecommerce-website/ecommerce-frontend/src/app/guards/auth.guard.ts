import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard {
  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    
    if (this.authService.isLoggedIn()) {
      // Check if route has data.roles and user has one of the required roles
      if (route.data['roles'] && !this.checkRoles(route.data['roles'])) {
        // Role not authorized, redirect to home page
        this.router.navigate(['/']);
        return false;
      }
      
      // Authorized, return true
      return true;
    }

    // Not logged in, redirect to login page with return url
    this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }

  private checkRoles(roles: string[]): boolean {
    const user = this.authService.currentUserValue;
    if (!user) return false;
    
    return roles.includes(user.role);
  }
}