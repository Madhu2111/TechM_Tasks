import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';
import { Router } from '@angular/router';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'An unknown error occurred';
        let redirectToLogin = false;
        
        if (error.error instanceof ErrorEvent) {
          // Client-side error
          errorMessage = `Error: ${error.error.message}`;
        } else {
          // Server-side error
          console.log(`ErrorInterceptor - HTTP error: ${error.status} for ${request.url}`, error);
          
          if (error.status === 401) {
            // Auto logout if 401 response returned from api
            console.log('ErrorInterceptor - 401 Unauthorized error detected');
            
            // Check if this is a login request
            if (request.url.includes('/api/auth/login')) {
              errorMessage = 'Invalid username or password';
            } else {
              // For other requests, it means the token is invalid or expired
              this.authService.logout();
              redirectToLogin = true;
              
              // Check if this is a cart sync error
              if (request.url.includes('/cart/sync')) {
                errorMessage = 'Unable to sync cart. Please log in again.';
                // Don't reload the page for cart sync errors
              } else {
                errorMessage = 'Your session has expired. Please log in again.';
              }
            }
          } else if (error.status === 403) {
            console.log('ErrorInterceptor - 403 Forbidden error detected for URL:', request.url);
            
            // If trying to access admin resources
            if (request.url.includes('/api/admin')) {
              console.log('ErrorInterceptor - Admin resource access forbidden');
              errorMessage = 'You do not have permission to access admin resources';
              
              // Check if user is logged in but not admin
              if (this.authService.isLoggedIn() && !this.authService.isAdmin()) {
                console.log('ErrorInterceptor - User is logged in but not admin');
                this.notificationService.showError('You need admin privileges to access this page');
                this.router.navigate(['/']);
              } else if (this.authService.isLoggedIn() && this.authService.isAdmin()) {
                console.log('ErrorInterceptor - User claims to be admin but server rejected');
                // This is unusual - user thinks they're admin but server disagrees
                // Force logout and re-login to refresh token
                this.notificationService.showError('Your admin session has expired. Please log in again.');
                this.authService.logout();
                this.router.navigate(['/login']);
              }
            } else {
              errorMessage = 'You do not have permission to perform this action';
            }
          } else if (error.status === 404) {
            errorMessage = 'The requested resource was not found';
          } else if (error.error && typeof error.error === 'object' && error.error.errors) {
            // Handle validation errors
            const validationErrors = error.error.errors;
            const errorMessages = Object.keys(validationErrors).map(key => `${key}: ${validationErrors[key]}`);
            errorMessage = errorMessages.join(', ');
          } else if (error.error && error.error.message) {
            errorMessage = error.error.message;
          } else if (typeof error.error === 'string') {
            errorMessage = error.error;
          } else if (error.message && error.message.includes("userDetails is null")) {
            // Handle the specific error we're seeing
            this.authService.logout();
            redirectToLogin = true;
            errorMessage = 'Your session has expired. Please log in again.';
          } else {
            errorMessage = `Error Code: ${error.status}, Message: ${error.message}`;
          }
        }
        
        // Don't show error notifications for cart sync issues to avoid spamming the user
        if (!request.url.includes('/cart/sync') || error.status !== 401) {
          this.notificationService.showError(errorMessage);
        } else {
          console.log('Suppressed cart sync error notification:', errorMessage);
        }
        
        // Redirect to login page if needed
        if (redirectToLogin) {
          this.router.navigate(['/login'], { 
            queryParams: { returnUrl: this.router.url } 
          });
        }
        
        return throwError(() => new Error(errorMessage));
      })
    );
  }
}