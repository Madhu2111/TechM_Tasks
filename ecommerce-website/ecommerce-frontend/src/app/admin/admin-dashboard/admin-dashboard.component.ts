import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  dashboardStats: any = {};
  isLoading = true;
  error: string | null = null;

  // Chart data
  monthlyRevenueLabels: string[] = [];
  monthlyRevenueData: number[] = [];
  
  // Make Math available in the template
  Math = Math;

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadDashboardStats();
  }

  loadDashboardStats(): void {
    this.isLoading = true;
    this.error = null;

    console.log('AdminDashboardComponent - Loading dashboard stats');
    this.adminService.getDashboardStats().subscribe({
      next: (stats) => {
        console.log('AdminDashboardComponent - Dashboard stats loaded successfully:', stats);
        this.dashboardStats = stats;
        this.prepareChartData();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('AdminDashboardComponent - Error loading dashboard stats', err);
        this.error = 'Failed to load dashboard statistics. Please try again later.';
        this.isLoading = false;
      }
    });
  }

  prepareChartData(): void {
    if (this.dashboardStats.monthlyRevenue) {
      const monthlyRevenue = this.dashboardStats.monthlyRevenue;
      
      // Sort months chronologically
      const months = ['JANUARY', 'FEBRUARY', 'MARCH', 'APRIL', 'MAY', 'JUNE', 
                     'JULY', 'AUGUST', 'SEPTEMBER', 'OCTOBER', 'NOVEMBER', 'DECEMBER'];
      
      // Get the last 6 months in chronological order
      const currentMonth = new Date().getMonth(); // 0-11
      const last6Months = [];
      
      for (let i = 5; i >= 0; i--) {
        const monthIndex = (currentMonth - i + 12) % 12; // Ensure positive index
        last6Months.push(months[monthIndex]);
      }
      
      // Create data arrays based on the sorted months
      this.monthlyRevenueLabels = last6Months.map(month => month.substring(0, 3)); // First 3 letters
      this.monthlyRevenueData = last6Months.map(month => monthlyRevenue[month] || 0);
    }
  }

  // Add new method to calculate max revenue
  getMaxRevenue(): number {
    return Math.max(...this.monthlyRevenueData);
  }
}