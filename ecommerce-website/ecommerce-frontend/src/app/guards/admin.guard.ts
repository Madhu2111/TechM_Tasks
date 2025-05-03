import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {
  
  constructor(
    private authService: AuthService, 
    private router: Router,
    private notificationService: NotificationService
  ) {}
  
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    console.log('AdminGuard - Checking if user is admin');
    console.log('AdminGuard - Route:', route.url);
    console.log('AdminGuard - State URL:', state.url);
    
    // First check if user is logged in
    if (!this.authService.isLoggedIn()) {
      console.log('AdminGuard - User is not logged in, redirecting to login');
      this.notificationService.showError('Please log in to access the admin area');
      this.router.navigate(['/login'], { 
        queryParams: { returnUrl: state.url } 
      });
      return false;
    }
    
    // Then check if user is admin
    const isAdmin = this.authService.isAdmin();
    const currentUser = this.authService.currentUserValue;
    console.log('AdminGuard - isAdmin result:', isAdmin);
    console.log('AdminGuard - Current user:', currentUser);
    
    // Debug token information
    const token = this.authService.getToken();
    if (token) {
      try {
        const parts = token.split('.');
        if (parts.length === 3) {
          const base64Url = parts[1];
          const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
          const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
          }).join(''));
          
          const payload = JSON.parse(jsonPayload);
          console.log('AdminGuard - Token payload:', payload);
          console.log('AdminGuard - Token auth field:', payload.auth);
          console.log('AdminGuard - Token expiration:', new Date(payload.exp * 1000).toISOString());
          console.log('AdminGuard - Current time:', new Date().toISOString());
        }
      } catch (error) {
        console.error('AdminGuard - Error parsing token:', error);
      }
    }
    
    if (!isAdmin) {
      console.log('AdminGuard - User is not admin, redirecting to home');
      this.notificationService.showError('You need admin privileges to access this page');
      this.router.navigate(['/']);
      return false;
    }
    
    console.log('AdminGuard - User is admin, allowing access to:', state.url);
    return true;
  }
}