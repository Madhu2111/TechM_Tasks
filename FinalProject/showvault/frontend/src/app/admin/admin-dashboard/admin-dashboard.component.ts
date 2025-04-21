import { Component, OnInit } from '@angular/core';
import { AdminService, DashboardStats, UserReport, SalesReport } from '../../services/admin.service';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  dashboardStats: DashboardStats | null = null;
  userReport: UserReport | null = null;
  salesReport: SalesReport | null = null;
  activeTab = 'overview';
  isLoading = true;
  error = '';

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(startDate?: string, endDate?: string): void {
    this.isLoading = true;
    this.error = '';
    
    // Load dashboard statistics
    this.adminService.getDashboardStats(startDate, endDate).subscribe({
      next: (stats) => {
        this.dashboardStats = stats;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to load dashboard data. Please try again.';
        this.isLoading = false;
        console.error('Error loading dashboard stats:', error);
      }
    });
  }

  loadUserReport(startDate?: string, endDate?: string): void {
    if (this.userReport && !startDate && !endDate) return; // Don't reload if already loaded and no date filters
    
    this.isLoading = true;
    this.error = '';
    
    this.adminService.getUserReport(startDate, endDate).subscribe({
      next: (report) => {
        this.userReport = report;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to load user report. Please try again.';
        this.isLoading = false;
        console.error('Error loading user report:', error);
      }
    });
  }

  loadSalesReport(): void {
    if (this.salesReport) return; // Don't reload if already loaded
    
    this.isLoading = true;
    this.error = '';
    
    this.adminService.getSalesReport().subscribe({
      next: (report) => {
        this.salesReport = report;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to load sales report. Please try again.';
        this.isLoading = false;
        console.error('Error loading sales report:', error);
      }
    });
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
    
    // Load data for the selected tab if not already loaded
    if (tab === 'users') {
      this.loadUserReport();
    } else if (tab === 'sales') {
      this.loadSalesReport();
    }
  }

  // Helper method to format date
  formatDate(date: string): string {
    return new Date(date).toLocaleDateString();
  }
}