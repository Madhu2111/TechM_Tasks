import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from '../../models/user.model';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css', '../../admin/admin-layout/admin-layout.component.css']
})
export class ProfileComponent implements OnInit {
  currentUser: User | null = null;
  editMode: boolean = false;
  profileForm: FormGroup;
  isSubmitting: boolean = false;
  isAdmin: boolean = false;

  constructor(
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private notificationService: NotificationService,
    private http: HttpClient,
    private router: Router
  ) {
    this.profileForm = this.formBuilder.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: [''],
      confirmPassword: ['']
    });
  }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (user) {
        // Check if user is admin using the auth service
        this.isAdmin = this.authService.isAdmin();
        console.log('User role:', user.role);
        console.log('isAdmin set to:', this.isAdmin);
        
        // Try to get the full user profile from the API
        this.fetchUserProfile();
        
        // Initialize form with current user data
        this.profileForm.patchValue({
          username: user.username,
          email: user.email
        });
      }
    });
  }
  
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
    this.notificationService.showSuccess('You have been logged out successfully');
  }
  
  fetchUserProfile(): void {
    this.http.get(`${environment.apiUrl}/api/users/me`)
      .subscribe({
        next: (userData: any) => {
          // Update the current user with the full profile data
          if (userData) {
            this.currentUser = {
              ...this.currentUser,
              ...userData,
              // Make sure we keep the role from the token
              role: this.currentUser?.role || userData.role
            };
            
            // Update the form with the latest data
            this.profileForm.patchValue({
              username: userData.username,
              email: userData.email
            });
            
            // Re-check admin status with the updated data
            if (this.currentUser) {
              // Also update the auth service
              this.authService.refreshUserData(this.currentUser);
              
              // Use the auth service to check admin status
              this.isAdmin = this.authService.isAdmin();
              console.log('Updated user role:', this.currentUser.role);
              console.log('isAdmin updated to:', this.isAdmin);
            }
          }
        },
        error: (error) => {
          console.error('Error fetching user profile:', error);
          // Continue with the data from the token
        }
      });
  }

  toggleEditMode(): void {
    this.editMode = !this.editMode;
    if (!this.editMode) {
      // Reset form when canceling edit
      this.resetForm();
    }
  }

  resetForm(): void {
    if (this.currentUser) {
      this.profileForm.patchValue({
        username: this.currentUser.username,
        email: this.currentUser.email,
        password: '',
        confirmPassword: ''
      });
    }
  }

  updateProfile(): void {
    if (this.profileForm.invalid) {
      return;
    }

    // Check if passwords match
    const password = this.profileForm.get('password')?.value;
    const confirmPassword = this.profileForm.get('confirmPassword')?.value;
    
    if (password && password !== confirmPassword) {
      this.notificationService.showError('Passwords do not match');
      return;
    }

    this.isSubmitting = true;

    // Create update payload
    const updateData: any = {
      username: this.profileForm.get('username')?.value,
      email: this.profileForm.get('email')?.value
    };

    // Only include password if it was provided
    if (password) {
      updateData.password = password;
    }

    // Use the profile update endpoint
    this.http.put(`${environment.apiUrl}/api/users/profile`, updateData)
      .subscribe({
        next: (response: any) => {
          this.isSubmitting = false;
          this.notificationService.showSuccess('Profile updated successfully');
          
          // Update the current user in auth service
          if (this.currentUser) {
            const updatedUser: User = {
              ...this.currentUser,
              username: updateData.username,
              email: updateData.email
            };
            
            // Force refresh the token or user data
            this.authService.refreshUserData(updatedUser);
          }
          
          this.toggleEditMode();
        },
        error: (error) => {
          this.isSubmitting = false;
          this.notificationService.showError('Failed to update profile: ' + (error.error?.message || 'Unknown error'));
        }
      });
  }
}
