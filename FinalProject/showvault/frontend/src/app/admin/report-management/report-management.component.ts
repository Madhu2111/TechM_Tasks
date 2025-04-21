import { Component, OnInit } from '@angular/core';
import { AdminService, UserReport, SalesReport } from '../../services/admin.service';

@Component({
  selector: 'app-report-management',
  templateUrl: './report-management.component.html',
  styleUrls: ['./report-management.component.css']
})
export class ReportManagementComponent implements OnInit {
  userReport: UserReport | null = null;
  salesReport: SalesReport | null = null;
  activeTab = 'users';
  reportPeriod = 'month';
  isLoading = false;
  error = '';

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadUserReport();
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
    
    if (tab === 'users' && !this.userReport) {
      this.loadUserReport();
    } else if (tab === 'sales' && !this.salesReport) {
      this.loadSalesReport();
    }
  }

  changeReportPeriod(period: string): void {
    this.reportPeriod = period;
    // In a real implementation, we would reload the data with the new period
    // For now, we'll just simulate a reload
    if (this.activeTab === 'users') {
      this.loadUserReport();
    } else {
      this.loadSalesReport();
    }
  }

  loadUserReport(): void {
    this.isLoading = true;
    this.error = '';
    
    this.adminService.getUserReport().subscribe({
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

  exportReportCSV(): void {
    // In a real implementation, this would generate and download a CSV file
    alert('Export to CSV functionality would be implemented here.');
  }

  printReport(): void {
    // In a real implementation, this would format and print the report
    window.print();
  }

  // Helper methods for calculating percentages
  getPercentage(value: number, total: number): string {
    if (!total) return '0%';
    return Math.round((value / total) * 100) + '%';
  }

  getPercentageValue(value: number, total: number): number {
    if (!total) return 0;
    return Math.round((value / total) * 100);
  }
}