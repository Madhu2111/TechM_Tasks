import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { User, PasswordChangeRequest, UserPreferences } from '../../models/user.model';
import { AuthService } from '../../services/auth.service';
import { ImageService } from '../../services/image.service';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {
  @ViewChild('fileInput') fileInput!: ElementRef;
  
  profileForm: FormGroup;
  passwordForm: FormGroup;
  preferencesForm: FormGroup;
  currentUser: User | null = null;
  isEditing = false;
  showPasswordForm = false;
  showPreferencesForm = false;
  updateSuccess = false;
  updateError = '';
  passwordUpdateSuccess = false;
  passwordUpdateError = '';
  preferencesUpdateSuccess = false;
  preferencesUpdateError = '';
  activeTab = 'profile';
  
  // For profile picture upload
  selectedFile: File | null = null;
  uploadProgress = 0;
  uploadError = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private imageService: ImageService
  ) {
    this.profileForm = this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.pattern('^[0-9]{10}$')]],
      address: [''],
    });
    
    this.passwordForm = this.fb.group({
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    }, { validator: this.passwordMatchValidator });
    
    this.preferencesForm = this.fb.group({
      emailNotifications: [true],
      smsNotifications: [false],
      favoriteCategories: [[]],
      language: ['en'],
      currency: ['USD']
    });
  }
  
  // Custom validator to check if passwords match
  passwordMatchValidator(form: FormGroup) {
    const newPassword = form.get('newPassword')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    
    if (newPassword !== confirmPassword) {
      form.get('confirmPassword')?.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    
    return null;
  }

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUser = user;
        this.profileForm.patchValue({
          firstName: user.firstName,
          lastName: user.lastName,
          email: user.email,
          phone: user.phone || '',
          address: user.address || ''
        });
        this.profileForm.get('email')?.disable();
        
        // Load user preferences if available
        if (user.preferences) {
          this.preferencesForm.patchValue({
            emailNotifications: user.preferences.emailNotifications,
            smsNotifications: user.preferences.smsNotifications,
            favoriteCategories: user.preferences.favoriteCategories || [],
            language: user.preferences.language || 'en',
            currency: user.preferences.currency || 'USD'
          });
        }
      },
      error: (error) => {
        console.error('Error loading user profile:', error);
      }
    });
  }
  
  setActiveTab(tab: string): void {
    this.activeTab = tab;
    // Reset forms and states when switching tabs
    this.updateSuccess = false;
    this.updateError = '';
    this.passwordUpdateSuccess = false;
    this.passwordUpdateError = '';
    this.preferencesUpdateSuccess = false;
    this.preferencesUpdateError = '';
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
    this.updateSuccess = false;
    this.updateError = '';
  }

  onSubmit(): void {
    if (this.profileForm.valid && this.currentUser) {
      const updatedUser = {
        ...this.currentUser,
        firstName: this.profileForm.value.firstName,
        lastName: this.profileForm.value.lastName,
        phone: this.profileForm.value.phone,
        address: this.profileForm.value.address
      };

      this.authService.updateUserProfile(updatedUser).subscribe({
        next: (response) => {
          this.updateSuccess = true;
          this.isEditing = false;
          this.currentUser = response;
          setTimeout(() => {
            this.updateSuccess = false;
          }, 3000);
        },
        error: (error) => {
          this.updateError = 'Failed to update profile. Please try again.';
          console.error('Error updating profile:', error);
        }
      });
    }
  }
  
  onChangePassword(): void {
    if (this.passwordForm.valid) {
      const passwordData: PasswordChangeRequest = {
        currentPassword: this.passwordForm.value.currentPassword,
        newPassword: this.passwordForm.value.newPassword,
        confirmPassword: this.passwordForm.value.confirmPassword
      };
      
      this.authService.changePassword(passwordData).subscribe({
        next: () => {
          this.passwordUpdateSuccess = true;
          this.passwordForm.reset();
          setTimeout(() => {
            this.passwordUpdateSuccess = false;
          }, 3000);
        },
        error: (error) => {
          this.passwordUpdateError = error.message || 'Failed to change password. Please try again.';
          console.error('Error changing password:', error);
        }
      });
    }
  }
  
  onUpdatePreferences(): void {
    if (this.preferencesForm.valid && this.currentUser) {
      const preferences: UserPreferences = {
        emailNotifications: this.preferencesForm.value.emailNotifications,
        smsNotifications: this.preferencesForm.value.smsNotifications,
        favoriteCategories: this.preferencesForm.value.favoriteCategories,
        language: this.preferencesForm.value.language,
        currency: this.preferencesForm.value.currency
      };
      
      this.authService.updateUserPreferences(preferences).subscribe({
        next: (response) => {
          this.preferencesUpdateSuccess = true;
          this.currentUser = response;
          setTimeout(() => {
            this.preferencesUpdateSuccess = false;
          }, 3000);
        },
        error: (error) => {
          this.preferencesUpdateError = 'Failed to update preferences. Please try again.';
          console.error('Error updating preferences:', error);
        }
      });
    }
  }
  
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.uploadProfilePicture();
    }
  }
  
  triggerFileInput(): void {
    this.fileInput.nativeElement.click();
  }
  
  uploadProfilePicture(): void {
    if (!this.selectedFile) return;
    
    this.uploadProgress = 0;
    this.uploadError = '';
    
    // Simulate upload progress (in a real app, this would use HttpClient's progress events)
    const interval = setInterval(() => {
      this.uploadProgress += 10;
      if (this.uploadProgress >= 100) {
        clearInterval(interval);
      }
    }, 200);
    
    this.authService.uploadProfilePicture(this.selectedFile).subscribe({
      next: (response) => {
        this.currentUser = response;
        this.selectedFile = null;
        this.uploadProgress = 100;
        setTimeout(() => {
          this.uploadProgress = 0;
        }, 1000);
      },
      error: (error) => {
        this.uploadError = error.message || 'Failed to upload profile picture. Please try again.';
        this.uploadProgress = 0;
        clearInterval(interval);
        console.error('Error uploading profile picture:', error);
      }
    });
  }

  viewBookingHistory(): void {
    this.router.navigate(['/user/bookings']);
  }
  
  /**
   * Get image URL with fallback to appropriate default image
   * Public wrapper for imageService.getImageUrl
   * @param imageUrl The original image URL
   * @param type The type of content (user, profile, etc.)
   * @returns A valid image URL
   */
  getImageUrl(imageUrl: string | undefined | null, type: string = 'user'): string {
    return this.imageService.getImageUrl(imageUrl, type);
  }
}