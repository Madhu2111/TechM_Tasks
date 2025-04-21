import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-sidebar',
  templateUrl: './admin-sidebar.component.html',
  styleUrls: ['./admin-sidebar.component.css']
})
export class AdminSidebarComponent implements OnInit {
  menuItems = [
    { 
      title: 'Dashboard', 
      icon: 'bi-speedometer2', 
      route: '/admin/dashboard',
      active: false
    },
    { 
      title: 'User Management', 
      icon: 'bi-people', 
      route: '/admin/users',
      active: false
    },
    { 
      title: 'Booking Management', 
      icon: 'bi-ticket-perforated', 
      route: '/admin/bookings',
      active: false
    },
    { 
      title: 'Reports', 
      icon: 'bi-bar-chart', 
      route: '/admin/reports',
      active: false
    },
    { 
      title: 'System Health', 
      icon: 'bi-heart-pulse', 
      route: '/admin/system-health',
      active: false
    },
    { 
      title: 'Platform Settings', 
      icon: 'bi-gear', 
      route: '/admin/settings',
      active: false
    },
    { 
      title: 'Audit Logs', 
      icon: 'bi-journal-text', 
      route: '/admin/audit-logs',
      active: false
    }
  ];

  constructor(private router: Router) { }

  ngOnInit(): void {
    this.setActiveMenuItem();
  }

  setActiveMenuItem(): void {
    const currentUrl = this.router.url;
    this.menuItems.forEach(item => {
      item.active = currentUrl === item.route;
    });
  }
}