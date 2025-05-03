import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { environment } from '../../environments/environment';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Add auth header with jwt if user is logged in and request is to the api url
    const token = this.authService.getToken();
    const isApiUrl = request.url.startsWith(environment.apiUrl);
    
    if (token && isApiUrl) {
      console.log(`JwtInterceptor - Adding token to request: ${request.url}`);
      
      // Debug token information
      try {
        const parts = token.split('.');
        if (parts.length === 3) {
          const base64Url = parts[1];
          const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
          const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
          }).join(''));
          
          const payload = JSON.parse(jsonPayload);
          console.log('JwtInterceptor - Token payload:', payload);
          console.log('JwtInterceptor - Token auth field:', payload.auth);
          console.log('JwtInterceptor - Token expiration:', new Date(payload.exp * 1000).toISOString());
          console.log('JwtInterceptor - Current time:', new Date().toISOString());
          console.log('JwtInterceptor - Is admin?', this.authService.isAdmin());
        }
      } catch (error) {
        console.error('JwtInterceptor - Error parsing token:', error);
      }
      
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    } else if (isApiUrl) {
      console.log(`JwtInterceptor - No token available for request: ${request.url}`);
    }
    
    return next.handle(request);
  }
}