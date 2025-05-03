import { Component, OnInit, HostListener } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-admin-layout',
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.css']
})
export class AdminLayoutComponent implements OnInit {
  navItems = [
    { path: './', label: 'Home', icon: 'bi-house' },
    { path: './products', label: 'Products', icon: 'bi-box' },
    { path: './orders', label: 'Orders', icon: 'bi-cart3' },
    { path: './users', label: 'Users', icon: 'bi-people' }
  ];
  
  currentRoute: string = '';
  isMobileView: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService
  ) {
    // Track current route for active link highlighting
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.currentRoute = event.url;
    });
  }

  ngOnInit(): void {
    // Verify admin status
    if (!this.authService.isLoggedIn()) {
      console.log('AdminLayoutComponent - User is not logged in');
      this.notificationService.showError('Please log in to access the admin area');
      this.router.navigate(['/login']);
      return;
    }
    
    if (!this.authService.isAdmin()) {
      console.log('AdminLayoutComponent - User is not admin');
      this.notificationService.showError('You need admin privileges to access this page');
      this.router.navigate(['/']);
      return;
    }
    
    console.log('AdminLayoutComponent - User is admin, rendering admin layout');
    
    // Check initial screen size
    this.checkScreenSize();
  }

  @HostListener('window:resize')
  checkScreenSize() {
    this.isMobileView = window.innerWidth < 768;
  }

  logout(): void {
    this.authService.logout();
    this.notificationService.showSuccess('You have been logged out successfully');
    this.router.navigate(['/login']);
  }
  
  isActive(path: string): boolean {
    if (path === './') {
      return this.currentRoute === '/admin' || this.currentRoute === '/admin/';
    }
    return this.currentRoute.includes(path.replace('./', '/admin/'));
  }
}