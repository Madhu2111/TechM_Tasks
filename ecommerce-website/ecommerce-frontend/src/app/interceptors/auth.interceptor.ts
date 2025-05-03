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
import { Router } from '@angular/router';
import { NotificationService } from '../services/notification.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Check if the request is to an API endpoint that requires authentication
    const isApiRequest = request.url.includes('/api/');
    const isAuthRequest = request.url.includes('/api/auth/');
    const isPublicRequest = request.url.includes('/api/v1/products');
    
    // Only add the token for API requests that aren't auth requests
    if (isApiRequest && !isAuthRequest && !isPublicRequest) {
      // Double check if the user is logged in with a valid token
      if (this.authService.isLoggedIn()) {
        const token = this.authService.getToken();
        
        if (token) {
          console.log(`Adding auth token to request: ${request.url}`);
          request = request.clone({
            setHeaders: {
              Authorization: `Bearer ${token}`
            }
          });
        } else {
          console.log(`No token available for request: ${request.url}`);
        }
      } else {
        console.log(`User not logged in for request: ${request.url}`);
      }
    }
    
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Auto logout if 401 response returned from API
          this.authService.logout();
          
          // Don't show notification for cart sync operations to avoid spamming the user
          if (!request.url.includes('/cart/sync')) {
            this.notificationService.showError('Your session has expired. Please login again.');
            
            // Only redirect to login for non-cart operations
            if (!request.url.includes('/cart')) {
              this.router.navigate(['/login']);
            }
          } else {
            console.log('Suppressed auth error notification for cart sync');
          }
        } else if (error.status === 403) {
          this.notificationService.showError('You do not have permission to perform this action');
        }
        
        return throwError(() => error);
      })
    );
  }
}