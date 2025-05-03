import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { JwtAuthResponse, LoginRequest, SignUpRequest, User } from '../models/user.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/api/auth`;
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser$: Observable<User | null>;

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  login(loginRequest: LoginRequest): Observable<JwtAuthResponse> {
    return this.http.post<JwtAuthResponse>(`${this.apiUrl}/login`, loginRequest)
      .pipe(
        tap(response => {
          this.storeToken(response.accessToken);
          this.loadCurrentUser();
        })
      );
  }

  register(signUpRequest: SignUpRequest): Observable<any> {
    console.log('Auth service sending registration request to:', `${this.apiUrl}/register`);
    console.log('Registration data:', signUpRequest);
    return this.http.post(`${this.apiUrl}/register`, signUpRequest);
  }

  logout(): void {
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  private storeToken(token: string): void {
    localStorage.setItem('token', token);
  }

  private loadCurrentUser(): void {
    // Decode JWT token to get user info
    const token = this.getToken();
    if (token) {
      try {
        const base64Url = token.split('.')[1];
        if (!base64Url) {
          console.error('Invalid token format - missing payload');
          this.logout();
          return;
        }
        
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        const payload = JSON.parse(jsonPayload);
        
        // Debug the token payload
        console.log('Token payload:', payload);
        
        if (!payload.sub) {
          console.error('Invalid token - missing subject');
          this.logout();
          return;
        }
        
        if (!payload.auth) {
          console.error('Invalid token - missing authorities');
          this.logout();
          return;
        }
        
        // Check if token is expired
        if (payload.exp && payload.exp * 1000 < Date.now()) {
          console.error('Token expired');
          this.logout();
          return;
        }
        
        console.log('Raw auth from token:', payload.auth);
        
        // Handle different formats of auth field
        let role = 'USER';
        console.log('Parsing role from payload:', {
          auth: payload.auth,
          roles: payload.roles,
          role: payload.role
        });

        const isAdmin = (value: string): boolean => {
          if (!value) return false;
          const upperValue = value.trim().toUpperCase();
          console.log('Checking if value is admin role:', upperValue);
          return upperValue === 'ROLE_ADMIN' || upperValue === 'ADMIN' || upperValue.includes('ROLE_ADMIN');
        };

        if (typeof payload.auth === 'string') {
          // Handle comma-separated string format: "ROLE_ADMIN,ROLE_USER"
          const roles = payload.auth.split(',');
          console.log('Parsed roles from string:', roles);
          if (roles.some(isAdmin)) {
            role = 'ADMIN';
          }
        } else if (Array.isArray(payload.auth)) {
          // Handle array format: ["ROLE_ADMIN", "ROLE_USER"]
          console.log('Auth is array:', payload.auth);
          if (payload.auth.some(isAdmin)) {
            role = 'ADMIN';
          }
        } else if (payload.roles && Array.isArray(payload.roles)) {
          // Handle roles array format: ["ROLE_ADMIN", "ROLE_USER"]
          console.log('Using roles array:', payload.roles);
          if (payload.roles.some(isAdmin)) {
            role = 'ADMIN';
          }
        } else if (payload.role) {
          // Handle direct role property
          console.log('Using direct role property:', payload.role);
          if (isAdmin(payload.role)) {
            role = 'ADMIN';
          }
        } else if (typeof payload.auth === 'object' && payload.auth !== null) {
          // Handle object format with authority field
          console.log('Auth is object:', payload.auth);
          const authorities = payload.auth.authority || payload.auth.authorities || [];
          if (Array.isArray(authorities) && authorities.some(isAdmin)) {
            role = 'ADMIN';
          }
        }
        
        console.log('Parsed role:', role);
        
        const user: User = {
          username: payload.sub,
          email: payload.email || '', // Don't use a fallback email
          role: role
        };
        
        console.log('User loaded from token:', user);
        this.currentUserSubject.next(user);
      } catch (error) {
        console.error('Error parsing token:', error);
        this.logout();
      }
    }
  }

  private getUserFromStorage(): User | null {
    const token = this.getToken();
    if (!token) {
      return null;
    }
    
    try {
      // Don't call loadCurrentUser() here as it creates a circular reference
      // Instead, decode the token directly
      const parts = token.split('.');
      if (parts.length !== 3) {
        console.error('Invalid token format');
        return null;
      }
      
      const base64Url = parts[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));

      const payload = JSON.parse(jsonPayload);
      
      // Validate token data
      if (!payload.sub) {
        console.error('Invalid token - missing subject');
        return null;
      }
      
      if (!payload.auth) {
        console.error('Invalid token - missing authorities');
        return null;
      }
      
      // Check if token is expired
      if (payload.exp && payload.exp * 1000 < Date.now()) {
        console.error('Token expired');
        return null;
      }
      
      console.log('getUserFromStorage - Raw auth from token:', payload.auth);
      let role = 'USER';
      const isAdmin = (value: string): boolean => {
        if (!value) return false;
        const upperValue = value.trim().toUpperCase();
        console.log('getUserFromStorage - Checking if value is admin role:', upperValue);
        return upperValue === 'ROLE_ADMIN' || upperValue === 'ADMIN' || upperValue.includes('ROLE_ADMIN');
      };

      if (typeof payload.auth === 'string') {
        const roles = payload.auth.split(',');
        console.log('getUserFromStorage - Roles from string:', roles);
        if (roles.some(isAdmin)) {
          role = 'ADMIN';
        }
      } else if (Array.isArray(payload.auth)) {
        console.log('getUserFromStorage - Auth is array:', payload.auth);
        if (payload.auth.some(isAdmin)) {
          role = 'ADMIN';
        }
      } else if (payload.roles && Array.isArray(payload.roles)) {
        console.log('getUserFromStorage - Using roles array:', payload.roles);
        if (payload.roles.some(isAdmin)) {
          role = 'ADMIN';
        }
      } else if (payload.role) {
        console.log('getUserFromStorage - Using direct role property:', payload.role);
        if (isAdmin(payload.role)) {
          role = 'ADMIN';
        }
      }
      console.log('getUserFromStorage - Parsed role:', role);
      
      const user = {
        username: payload.sub,
        email: payload.email || '', // Don't use a fallback email
        role: role
      };
      
      console.log('getUserFromStorage - User:', user);
      return user;
    } catch (error) {
      console.error('Error parsing user from token', error);
      return null;
    }
  }

  get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }
    
    try {
      // Check if token is valid JWT format
      const parts = token.split('.');
      if (parts.length !== 3) {
        console.error('Invalid token format');
        this.logout();
        return false;
      }
      
      // Check if token is expired
      const base64Url = parts[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      
      const payload = JSON.parse(jsonPayload);
      
      // Check if token has expiration
      if (!payload.exp) {
        console.error('Token has no expiration');
        this.logout();
        return false;
      }
      
      // Check if token is expired
      const currentTime = Date.now() / 1000;
      if (payload.exp < currentTime) {
        console.log('Token is expired', {
          expiration: new Date(payload.exp * 1000).toISOString(),
          current: new Date(currentTime * 1000).toISOString()
        });
        this.logout();
        return false;
      }
      
      // Check if token has subject (username)
      if (!payload.sub) {
        console.error('Token has no subject');
        this.logout();
        return false;
      }
      
      // Check if token has authorities
      if (!payload.auth) {
        console.error('Token has no authorities');
        this.logout();
        return false;
      }
      
      return true;
    } catch (error) {
      console.error('Error checking token validity', error);
      this.logout();
      return false;
    }
  }

  hasRole(role: string): boolean {
    const user = this.currentUserValue;
    console.log('Checking role:', role, 'Current user:', user);
    
    if (!user) {
      return false;
    }
    
    // Check if the role matches exactly
    if (user.role === role) {
      return true;
    }
    
    // Also check for case-insensitive match
    if (user.role && user.role.toUpperCase() === role.toUpperCase()) {
      return true;
    }
    
    return false;
  }
  
  isAdmin(): boolean {
    const isAdmin = this.hasRole('ADMIN');
    console.log('isAdmin check result:', isAdmin);
    return isAdmin;
  }

  refreshUserData(user: User): void {
    // Update the current user in the BehaviorSubject
    this.currentUserSubject.next(user);
    
    // In a real application, this would typically involve getting a new token
    // or updating the user's session on the server
    console.log('User data refreshed:', user);
  }
}