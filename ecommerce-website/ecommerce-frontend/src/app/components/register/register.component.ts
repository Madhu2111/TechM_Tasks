import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  loading = false;
  submitted = false;

  constructor(
    private formBuilder: FormBuilder,
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
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(50)]],
      role: ['USER', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(40)]],
      confirmPassword: ['', Validators.required]
    }, {
      validator: this.mustMatch('password', 'confirmPassword')
    });
  }

  // Custom validator to check if password and confirm password match
  mustMatch(controlName: string, matchingControlName: string) {
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[controlName];
      const matchingControl = formGroup.controls[matchingControlName];

      if (matchingControl.errors && !matchingControl.errors['mustMatch']) {
        // Return if another validator has already found an error
        return;
      }

      // Set error on matchingControl if validation fails
      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({ mustMatch: true });
      } else {
        matchingControl.setErrors(null);
      }
    };
  }

  // Convenience getter for easy access to form fields
  get f() { return this.registerForm.controls; }

  onSubmit(): void {
    this.submitted = true;

    // Stop here if form is invalid
    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;
    const registerData = {
      username: this.f['username'].value,
      email: this.f['email'].value,
      role: this.f['role'].value,
      password: this.f['password'].value
    };
    
    console.log('Sending registration data:', registerData);
    
    this.authService.register(registerData).subscribe({
      next: (response) => {
        console.log('Registration response:', response);
        this.notificationService.showSuccess(response.message || 'Registration successful');
        this.router.navigate(['/login']);
      },
      error: error => {
        console.error('Registration error:', error);
        
        let errorMessage = 'Registration failed';
        
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        } else if (error.error && typeof error.error === 'string') {
          errorMessage = error.error;
        } else if (error.error && error.error.errors) {
          // Handle validation errors
          const validationErrors = error.error.errors;
          errorMessage = Object.keys(validationErrors)
            .map(key => `${key}: ${validationErrors[key]}`)
            .join(', ');
        } else if (error.status === 400) {
          errorMessage = 'Bad request. Please check your input.';
        } else if (error.status === 200) {
          // This is a success response that couldn't be parsed as JSON
          this.notificationService.showSuccess('Registration successful');
          this.router.navigate(['/login']);
          return; // Don't show error message
        }
        
        this.notificationService.showError(errorMessage);
        this.loading = false;
      }
    });
  }
}
