import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../models/user.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  isSubmitted = false;
  loginError = '';

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) { 
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {
    // Auto-redirect if already logged in
    if (this.authService.isLoggedIn()) {
      // Let the auth service handle navigation based on user role
      const currentUser = this.authService.getCurrentUserSync();
      if (currentUser) {
        if (currentUser.role === 'ROLE_ADMIN') {
          this.router.navigate(['/admin']);
        } else if (currentUser.role === 'ROLE_ORGANIZER') {
          this.router.navigate(['/organizer']);
        } else {
          this.router.navigate(['/user/profile']);
        }
      }
    }
  }

  // Getter for easy access to form fields
  get formControls() { return this.loginForm.controls; }

  onSubmit(): void {
    this.isSubmitted = true;
    
    // Stop here if form is invalid
    if (this.loginForm.invalid) {
      return;
    }

    const loginRequest: LoginRequest = {
      username: this.loginForm.value.email, // Backend expects username
      password: this.loginForm.value.password,
      email: this.loginForm.value.email // Using email field for both username and email
    };

    this.authService.login(loginRequest).subscribe({
       next: (response) => {
        // Navigation is now handled by the auth service based on user role
        console.log('Login successful, redirecting...');
        // The auth service will handle navigation based on user role
      },
      error: (error) => {
        this.loginError = error.error?.message || 'Login failed. Please check your credentials.';
      }
    });
  }
  }
