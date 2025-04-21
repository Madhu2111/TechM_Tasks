import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError, BehaviorSubject } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Router } from '@angular/router';

import { 
  User, 
  AuthResponse, 
  LoginRequest, 
  RegisterRequest, 
  PasswordChangeRequest,
  UserPreferences 
} from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private userApiUrl = 'http://localhost:8080/api/users';
  private currentUserKey = 'currentUser';
  private tokenKey = 'auth_token';
  private authStateSubject = new BehaviorSubject<boolean>(false);

  constructor(
    private http: HttpClient,
    private router: Router
  ) { 
    // Initialize auth state
    this.authStateSubject.next(this.isLoggedIn());
  }

  login(loginData: LoginRequest): Observable<AuthResponse> {
    return this.http.post<any>(`${this.apiUrl}/signin`, loginData).pipe(
      map(response => {
        if (!response) {
          throw new Error('Empty response received from server');
        }

        // Check if response has the expected structure
        if (typeof response !== 'object') {
          throw new Error('Invalid response format from server');
        }

        console.log('Raw backend response:', response);

        // Validate token existence and format
        // Check for token in either 'token' or 'accessToken' field
        const token = response.token || response.accessToken;
        if (!token || typeof token !== 'string') {
          console.error('Invalid token in response:', response);
          throw new Error('Invalid or missing token in authentication response');
        }

        // Decode and validate token structure
        const decodedToken = this.decodeToken(token);
        if (!decodedToken || !decodedToken.sub) {
          throw new Error('Invalid token format or missing required claims');
        }

        // Extract roles from response or token
        const roles = response.roles || decodedToken.roles || [];
        const role = roles.length > 0 ? this.normalizeRole(roles[0]) : 'ROLE_USER';

        // Construct a proper AuthResponse object
        const authResponse: AuthResponse = {
          token: token,
          user: {
            id: response.id || decodedToken.id,
            username: response.username || decodedToken.sub,
            email: response.email || decodedToken.email || loginData.email || loginData.username,
            firstName: response.firstName || decodedToken.firstName || '',
            lastName: response.lastName || decodedToken.lastName || '',
            role: role,
            status: 'active'
          }
        };

        return authResponse;
      }),
      tap(response => {
        try {
          this.setSession(response);
        } catch (error) {
          console.error('Error setting session:', error);
          throw new Error('Failed to initialize user session');
        }
      }),
      catchError(error => {
        console.error('Login error:', error);
        const errorMessage = error.error?.message || error.message || 'Authentication failed';
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  private normalizeRole(role: string): User['role'] {
    const normalizedRole = role.toUpperCase();
    return normalizedRole.startsWith('ROLE_') ? normalizedRole as User['role'] : `ROLE_${normalizedRole}` as User['role'];
  }

  private decodeToken(token: string): any {
    try {
      if (!token || token.split('.').length !== 3) {
        throw new Error('Invalid token format');
      }
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const paddedBase64 = base64.padEnd(base64.length + (4 - (base64.length % 4)) % 4, '=');
      const decodedToken = JSON.parse(window.atob(paddedBase64));
      
      if (!decodedToken || typeof decodedToken !== 'object') {
        throw new Error('Invalid token payload');
      }
      
      return decodedToken;
    } catch (error) {
      console.error('Token decode error:', error);
      return null;
    }
  }

  private setSession(authResult: AuthResponse): void {
    if ((!authResult?.token && !authResult?.accessToken) || !authResult?.user) {
      throw new Error('Invalid authentication result');
    }
    try {
      // Use token from either field
      const token = authResult.token || authResult.accessToken;
      if (!token) {
        throw new Error('No valid token found in authentication result');
      }
      
      localStorage.setItem(this.tokenKey, token);
      localStorage.setItem(this.currentUserKey, JSON.stringify(authResult.user));
      console.log('Session data saved successfully');
      
      // Notify subscribers that auth state has changed
      this.authStateSubject.next(true);
    } catch (error) {
      console.error('Error setting session:', error);
      this.logout();
      throw new Error('Failed to save authentication data');
    }
  }

  register(registerData: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<any>(`${this.apiUrl}/signup`, registerData).pipe(
      map(response => {
        if (!response) {
          throw new Error('Empty response received from server');
        }

        // Check if response has the expected structure
        if (typeof response !== 'object') {
          throw new Error('Invalid response format from server');
        }

        console.log('Raw backend registration response:', response);

        // Validate token existence and format
        const token = response.token || response.accessToken;
        if (!token || typeof token !== 'string') {
          console.error('Invalid token in response:', response);
          throw new Error('Invalid or missing token in authentication response');
        }

        // Extract roles from response
        const roles = response.roles || [];
        const role = roles.length > 0 ? this.normalizeRole(roles[0]) : 'ROLE_USER';

        // Construct a proper AuthResponse object
        const authResponse: AuthResponse = {
          token: token,
          user: {
            id: response.id,
            username: response.username,
            email: response.email || registerData.email,
            firstName: registerData.firstName || '',
            lastName: registerData.lastName || '',
            role: role,
            status: 'active'
          }
        };

        return authResponse;
      }),
      tap(response => {
        const roles = registerData.roles || [];
        if (roles.length > 0) {
          const role = roles[0].toUpperCase();
          localStorage.setItem('user_role', `ROLE_${role}`);
        }
        this.setSession(response);
        // Note: Navigation after registration is handled by the component
      }),
      catchError(error => {
        console.error('Registration error:', error);
        let errorMessage = 'Registration failed';
        
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        } else if (error.message) {
          errorMessage = error.message;
        }
        
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.currentUserKey);
    localStorage.removeItem(this.tokenKey);
    this.authStateSubject.next(false);
    this.router.navigate(['/login']);
  }
  
  // Observable to track authentication state changes
  authStateChanged(): Observable<boolean> {
    return this.authStateSubject.asObservable();
  }

  isLoggedIn(): boolean {
    const token = localStorage.getItem(this.tokenKey);
    if (!token) return false;
    
    try {
      const parts = token.split('.');
      if (parts.length !== 3) {
        console.error('Invalid token format');
        return false;
      }

      const base64Url = parts[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const paddedBase64 = base64.padEnd(base64.length + (4 - (base64.length % 4)) % 4, '=');
      
      const tokenData = JSON.parse(atob(paddedBase64));
      if (!tokenData.exp) {
        console.error('Token missing expiration');
        return false;
      }
      
      const expirationTime = tokenData.exp * 1000;
      return Date.now() < expirationTime;
    } catch (e) {
      console.error('Token validation error:', e);
      return false;
    }
  }

  getCurrentUser(): Observable<User> {
    const storedUser = localStorage.getItem(this.currentUserKey);
    if (storedUser) {
      try {
        const user = JSON.parse(storedUser) as User;
        return of(user);
      } catch (e) {
        console.error('Error parsing stored user:', e);
      }
    }

    return this.http.get<User>(`${this.userApiUrl}/profile`).pipe(
      map((user: User) => {
        if (!user.name && user.firstName && user.lastName) {
          user.name = `${user.firstName} ${user.lastName}`;
        }
        return user;
      }),
      tap(user => {
        localStorage.setItem(this.currentUserKey, JSON.stringify(user));
      }),
      catchError(error => {
        return throwError(() => new Error(error.error?.message || 'Failed to fetch user profile'));
      })
    );
  }

  getCurrentUserSync(): User | null {
    if (!this.isLoggedIn()) {
      return null;
    }

    const storedUser = localStorage.getItem(this.currentUserKey);
    if (!storedUser) {
      return null;
    }
    
    try {
      const user = JSON.parse(storedUser) as User;
      if (user.role && !user.role.startsWith('ROLE_')) {
        const roleUpperCase = user.role.toUpperCase();
        switch (roleUpperCase) {
          case 'ADMIN':
            user.role = 'ROLE_ADMIN';
            break;
          case 'ORGANIZER':
            user.role = 'ROLE_ORGANIZER';
            break;
          default:
            user.role = 'ROLE_USER';
        }
      }
      return user;
    } catch (e) {
      console.error('Error parsing stored user:', e);
      return null;
    }
  }

  getUserProfile(): Observable<User> {
    return this.http.get<User>(`${this.userApiUrl}/profile`).pipe(
      map(user => {
        if (!user.name && user.firstName && user.lastName) {
          user.name = `${user.firstName} ${user.lastName}`;
        }
        return user;
      }),
      tap(user => {
        localStorage.setItem(this.currentUserKey, JSON.stringify(user));
      }),
      catchError(error => {
        return throwError(() => new Error(error.error?.message || 'Failed to fetch user profile'));
      })
    );
  }

  updateUserProfile(user: User): Observable<User> {
    if (user.firstName && user.lastName) {
      user.name = `${user.firstName} ${user.lastName}`;
    }
    
    return this.http.put<User>(`${this.userApiUrl}/profile`, user).pipe(
      tap(updatedUser => {
        localStorage.setItem(this.currentUserKey, JSON.stringify(updatedUser));
      }),
      catchError(error => {
        return throwError(() => new Error(error.error?.message || 'Failed to update profile'));
      })
    );
  }

  changePassword(passwordData: PasswordChangeRequest): Observable<void> {
    return this.http.patch<void>(`${this.userApiUrl}/password`, null, {
      params: {
        currentPassword: passwordData.currentPassword,
        newPassword: passwordData.newPassword
      }
    }).pipe(
      catchError(error => {
        let errorMessage = 'Failed to change password';
        if (error.error?.message) {
          errorMessage = error.error.message;
        } else if (error.status === 400) {
          errorMessage = 'Current password is incorrect or new password does not meet requirements';
        }
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  uploadProfilePicture(file: File): Observable<User> {
    const formData = new FormData();
    formData.append('profilePicture', file);
    
    return this.http.post<User>(`${this.userApiUrl}/profile-picture`, formData).pipe(
      tap(updatedUser => {
        const storedUser = localStorage.getItem(this.currentUserKey);
        if (storedUser) {
          const currentUser = JSON.parse(storedUser);
          currentUser.profilePicture = updatedUser.profilePicture;
          localStorage.setItem(this.currentUserKey, JSON.stringify(currentUser));
        }
      }),
      catchError(error => {
        const errorMessage = error.error?.message || 'Failed to upload profile picture';
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  updateUserPreferences(preferences: UserPreferences): Observable<User> {
    return this.http.put<User>(`${this.userApiUrl}/preferences`, preferences).pipe(
      tap(updatedUser => {
        const storedUser = localStorage.getItem(this.currentUserKey);
        if (storedUser) {
          const currentUser = JSON.parse(storedUser);
          currentUser.preferences = updatedUser.preferences;
          localStorage.setItem(this.currentUserKey, JSON.stringify(currentUser));
        }
      }),
      catchError(error => {
        return throwError(() => new Error(error.error?.message || 'Failed to update preferences'));
      })
    );
  }

  getToken(): string | null {
    const token = localStorage.getItem(this.tokenKey);
    if (!token) return null;

    try {
      const parts = token.split('.');
      if (parts.length !== 3) {
        console.error('Invalid token structure');
        this.logout();
        return null;
      }

      const base64Url = parts[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const paddedBase64 = base64.padEnd(base64.length + (4 - (base64.length % 4)) % 4, '=');
      
      const payload = JSON.parse(atob(paddedBase64));
      if (!payload.sub || !payload.exp) {
        console.error('Invalid token payload');
        this.logout();
        return null;
      }

      if (Date.now() >= payload.exp * 1000) {
        console.error('Token expired');
        this.logout();
        return null;
      }

      return token;
    } catch (error) {
      console.error('Token validation error:', error);
      this.logout();
      return null;
    }
  }
}