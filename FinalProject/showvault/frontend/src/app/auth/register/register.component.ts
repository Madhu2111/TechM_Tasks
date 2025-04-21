import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../models/user.model';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  isSubmitted = false;
  registrationError = '';
  
  // Valid roles for type checking and validation
  private validRoles: ('ROLE_USER' | 'ROLE_ORGANIZER' | 'ROLE_ADMIN')[] = ['ROLE_USER', 'ROLE_ORGANIZER', 'ROLE_ADMIN'];
  
  // User roles as specified in the backend ERole enum
  userRoles: { id: 'ROLE_USER' | 'ROLE_ORGANIZER' | 'ROLE_ADMIN', name: string }[] = [
    { id: 'ROLE_USER', name: 'Regular User' },
    { id: 'ROLE_ORGANIZER', name: 'Show Organizer' },
    { id: 'ROLE_ADMIN', name: 'Administrator' },
    { id: 'ROLE_ADMIN', name: 'Administrator' }
  ];

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) { 
    this.registerForm = this.formBuilder.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.pattern('^[0-9]{10}$')]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      role: ['ROLE_USER', [Validators.required]],
      termsAccepted: [false, [Validators.requiredTrue]]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  ngOnInit(): void {
  }

  // Custom validator to check if password and confirm password match
  passwordMatchValidator(formGroup: FormGroup) {
    const password = formGroup.get('password')?.value;
    const confirmPassword = formGroup.get('confirmPassword')?.value;
    
    if (password !== confirmPassword) {
      formGroup.get('confirmPassword')?.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    } else {
      return null;
    }
  }

  // Getter for easy access to form fields
  get formControls() { return this.registerForm.controls; }

  onSubmit(): void {
    this.isSubmitted = true;
    
    // Stop here if form is invalid
    if (this.registerForm.invalid) {
      return;
    }

    // Validate and ensure role is in correct format
    const selectedRole = this.registerForm.value.role as 'ROLE_USER' | 'ROLE_ORGANIZER' | 'ROLE_ADMIN';
    if (!this.validRoles.includes(selectedRole)) {
      this.registrationError = 'Invalid role selected. Please select a valid role.';
      return;
    }
    
    // Convert role format to match backend expectations (remove 'ROLE_' prefix)
    const roleValue = selectedRole.replace('ROLE_', '').toLowerCase();
    
    const registerRequest: RegisterRequest = {
      username: this.registerForm.value.email,
      email: this.registerForm.value.email,
      password: this.registerForm.value.password,
      firstName: this.registerForm.value.firstName,
      lastName: this.registerForm.value.lastName,
      phoneNumber: this.registerForm.value.phoneNumber,
      roles: [roleValue], // Send as array of roles without 'ROLE_' prefix
      termsAccepted: this.registerForm.value.termsAccepted
    };

    this.authService.register(registerRequest).subscribe({
      next: () => {
        // Registration successful
        this.router.navigate(['/login']);
      },
      error: (error) => {
        this.registrationError = error.error?.message || 'Registration failed. Please try again.';
      }
    });
  }
}