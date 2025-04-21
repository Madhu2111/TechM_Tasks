import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loginError: string = '';
  isLoading = false;
  showPassword = false;
  returnUrl: string = '/';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rememberMe: [false]
    });
  }

  ngOnInit(): void {
    // Get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    
    // Auto-redirect if already logged in
    if (this.authService.isLoggedIn()) {
      const currentUser = this.authService.getCurrentUserSync();
      console.log('User already logged in with role:', currentUser?.role);
      
      if (currentUser) {
        // Navigate based on user role
        if (currentUser.role === 'ROLE_ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else if (currentUser.role === 'ROLE_ORGANIZER') {
          this.router.navigate(['/organizer/dashboard']);
        } else {
          // Default to user profile for any other role
          this.router.navigate(['/user/profile']);
        }
      } else {
        this.router.navigate([this.returnUrl]);
      }
    }
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      // Mark form as touched to trigger validation styles
      this.loginForm.markAllAsTouched();
      
      // Clear any previous login errors
      this.loginError = '';
      this.isLoading = true;
      
      // Map email to username for backend compatibility
      const loginData = {
        username: this.loginForm.value.email,
        password: this.loginForm.value.password
      };
      
      console.log('Submitting login data:', loginData);
      
      this.authService.login(loginData)
        .pipe(finalize(() => this.isLoading = false))
        .subscribe({
          next: (response) => {
            console.log('Login successful, response:', response);
            
            // Store remember me preference
            if (this.loginForm.value.rememberMe) {
              localStorage.setItem('rememberUser', 'true');
            } else {
              localStorage.removeItem('rememberUser');
            }
            
            // Navigation is now handled by the auth service based on user role
            const currentUser = this.authService.getCurrentUserSync();
            console.log('Current user after login:', currentUser);
            
            // Navigate to shows page after login for all users
            if (currentUser) {
              if (currentUser.role === 'ROLE_ADMIN') {
                this.router.navigate(['/admin/dashboard']);
              } else if (currentUser.role === 'ROLE_ORGANIZER') {
                this.router.navigate(['/organizer/dashboard']);
              } else {
                // Redirect regular users to shows page instead of profile
                this.router.navigate(['/shows']);
              }
            }
          },
          error: (error) => {
            console.error('Login failed:', error);
            
            // Provide user-friendly error messages
            if (error.status === 401) {
              this.loginError = 'Invalid email or password. Please try again.';
            } else if (error.status === 400) {
              this.loginError = 'Invalid login information. Please check your details.';
            } else if (error.status === 0) {
              this.loginError = 'Unable to connect to the server. Please check your internet connection.';
            } else {
              this.loginError = error.message || 'Login failed. Please try again later.';
            }
          }
        });
    } else {
      // Mark all fields as touched to trigger validation display
      this.markFormGroupTouched(this.loginForm);
    }
  }

  // Helper method to mark all controls as touched
  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
}