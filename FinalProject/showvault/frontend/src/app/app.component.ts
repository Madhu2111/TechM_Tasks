import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './services/auth.service';
declare var bootstrap: any;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styles: [`
    .navbar {
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    .nav-link.active {
      font-weight: bold;
    }
    .feature-icon {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 3rem;
      height: 3rem;
      font-size: 1.5rem;
      border-radius: 50%;
    }
  `]
})
export class AppComponent implements OnInit {
  title = 'ShowVault';
  userRole: string | null = null;
  loginModal: any;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Check if user is logged in and get their role
    if (this.isLoggedIn()) {
      this.getUserRole();
    }
    
    // Initialize login modal
    setTimeout(() => {
      this.initializeLoginModal();
    }, 500);
    
    // Subscribe to auth state changes
    this.authService.authStateChanged().subscribe(isLoggedIn => {
      if (isLoggedIn) {
        this.getUserRole();
      } else {
        this.userRole = null;
      }
    });
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  getUserRole(): void {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        // Convert the role to lowercase for easier comparison in the template
        switch (user.role) {
          case 'ROLE_ADMIN':
            this.userRole = 'admin';
            break;
          case 'ROLE_ORGANIZER':
            this.userRole = 'organizer';
            break;
          default:
            this.userRole = 'user';
        }
        console.log('User role set to:', this.userRole);
      },
      error: (error: unknown) => {
        console.error('Error getting user role:', error);
        this.userRole = null;
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.userRole = null;
    this.router.navigate(['/']);
  }
  
  checkLoginBeforeNavigate(event: Event, url: string): void {
    if (!this.isLoggedIn()) {
      event.preventDefault();
      this.showLoginRequiredModal();
    }
  }
  
  private showLoginRequiredModal(): void {
    if (this.loginModal) {
      this.loginModal.show();
    }
  }
  
  private initializeLoginModal(): void {
    const modalElement = document.getElementById('loginRequiredModal');
    if (modalElement) {
      this.loginModal = new bootstrap.Modal(modalElement);
    }
  }
}