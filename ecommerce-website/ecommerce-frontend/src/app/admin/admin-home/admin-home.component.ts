import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-admin-home',
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.css']
})
export class AdminHomeComponent implements OnInit {
  dashboardStats: any = {};
  isLoading = true;
  error: string | null = null;
  
  constructor(
    private adminService: AdminService,
    private router: Router,
    private authService: AuthService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    // Verify admin status before loading data
    if (!this.authService.isLoggedIn()) {
      console.log('AdminHomeComponent - User is not logged in');
      this.notificationService.showError('Please log in to access the admin area');
      this.router.navigate(['/login']);
      return;
    }
    
    if (!this.authService.isAdmin()) {
      console.log('AdminHomeComponent - User is not admin');
      this.notificationService.showError('You need admin privileges to access this page');
      this.router.navigate(['/']);
      return;
    }
    
    console.log('AdminHomeComponent - User is admin, loading dashboard stats');
    this.loadDashboardStats();
  }

  loadDashboardStats(): void {
    this.isLoading = true;
    this.error = null;

    this.adminService.getDashboardStats().subscribe({
      next: (stats) => {
        console.log('AdminHomeComponent - Dashboard stats loaded successfully:', stats);
        this.dashboardStats = stats;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('AdminHomeComponent - Error loading dashboard stats', err);
        
        if (err.status === 403) {
          this.error = 'You do not have permission to access admin resources. Please contact the administrator.';
          // Force logout if server rejects admin access
          this.authService.logout();
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 3000);
        } else {
          this.error = 'Failed to load dashboard statistics. Please try again later.';
        }
        
        this.isLoading = false;
      }
    });
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }
  
  retryLoading(): void {
    this.loadDashboardStats();
  }
}