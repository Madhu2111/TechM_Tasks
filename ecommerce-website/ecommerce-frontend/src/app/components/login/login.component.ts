import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  loading = false;
  submitted = false;
  returnUrl: string = '/';

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private notificationService: NotificationService
  ) {
    // Redirect to home if already logged in
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      usernameOrEmail: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    // Get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  // Convenience getter for easy access to form fields
  get f() { return this.loginForm.controls; }

  onSubmit(): void {
    this.submitted = true;

    // Stop here if form is invalid
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.authService.login({
      usernameOrEmail: this.f['usernameOrEmail'].value,
      password: this.f['password'].value
    }).subscribe({
      next: () => {
        // Wait for the user data to be loaded before checking admin status
        setTimeout(() => {
          console.log('Checking admin status after login');
          if (this.authService.isAdmin()) {
            console.log('User is admin, redirecting to admin dashboard');
            this.notificationService.showSuccess('Welcome to Admin Portal');
            this.router.navigate(['/admin']);
          } else {
            console.log('User is not admin, redirecting to return URL:', this.returnUrl);
            this.notificationService.showSuccess('Login successful');
            
            // If the return URL is an admin route but the user is not an admin,
            // redirect to home instead
            if (this.returnUrl.includes('/admin')) {
              this.router.navigate(['/']);
            } else {
              this.router.navigate([this.returnUrl]);
            }
          }
        }, 500); // Small delay to ensure user data is loaded
      },
      error: error => {
        let errorMessage = 'Login failed';
        
        if (typeof error.error === 'string') {
          errorMessage = error.error;
        } else if (error.error && error.error.message) {
          errorMessage = error.error.message;
        } else if (error.status === 401) {
          errorMessage = 'Invalid username or password';
        } else if (error.status === 403) {
          errorMessage = 'Account is locked or disabled';
        }
        
        this.notificationService.showError(errorMessage);
        this.loading = false;
      }
    });
  }
}
