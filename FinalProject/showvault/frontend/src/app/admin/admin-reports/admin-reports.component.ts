import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { AdminService } from '../../services/admin.service';
import { UserReport } from '../../models/user-report.model';
import { SalesReport } from '../../models/sales-report.model';

@Component({
  selector: 'app-admin-reports',
  templateUrl: './admin-reports.component.html',
  styleUrls: ['./admin-reports.component.css']
})
export class AdminReportsComponent implements OnInit {
  activeTab = 'sales';
  salesReport: SalesReport | null = null;
  
  userReport: UserReport | null = null;
  isLoading = true;
  error = '';
  
  filterForm: FormGroup;
  
  // Date range options
  dateRanges = [
    { label: 'Last 7 Days', value: '7days' },
    { label: 'Last 30 Days', value: '30days' },
    { label: 'This Month', value: 'thisMonth' },
    { label: 'Last Month', value: 'lastMonth' },
    { label: 'This Year', value: 'thisYear' },
    { label: 'Custom Range', value: 'custom' }
  ];
  
  showCustomRange = false;

  constructor(
    private adminService: AdminService,
    private fb: FormBuilder
  ) {
    this.filterForm = this.fb.group({
      dateRange: ['30days'],
      startDate: [''],
      endDate: ['']
    });
  }

  ngOnInit(): void {
    this.loadReports();
    
    // Listen for date range changes
    this.filterForm.get('dateRange')?.valueChanges.subscribe(value => {
      this.showCustomRange = value === 'custom';
      if (value !== 'custom') {
        this.applyFilters();
      }
    });
  }

  loadReports(): void {
    this.isLoading = true;
    this.error = '';
    
    if (this.activeTab === 'sales') {
      this.loadSalesReport();
    } else if (this.activeTab === 'users') {
      this.loadUserReport();
    }
  }

  loadSalesReport(): void {
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

  loadUserReport(): void {
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

  setActiveTab(tab: string): void {
    this.activeTab = tab;
    this.loadReports();
  }

  applyFilters(): void {
    this.loadReports();
  }

  resetFilters(): void {
    this.filterForm.reset({
      dateRange: '30days',
      startDate: '',
      endDate: ''
    });
    this.applyFilters();
  }

  calculateDateRange(range: string): { from: string, to: string } {
    const today = new Date();
    let fromDate = new Date();
    let toDate = new Date();
    
    switch (range) {
      case '7days':
        fromDate.setDate(today.getDate() - 7);
        break;
      case '30days':
        fromDate.setDate(today.getDate() - 30);
        break;
      case 'thisMonth':
        fromDate = new Date(today.getFullYear(), today.getMonth(), 1);
        toDate = new Date(today.getFullYear(), today.getMonth() + 1, 0);
        break;
      case 'lastMonth':
        fromDate = new Date(today.getFullYear(), today.getMonth() - 1, 1);
        toDate = new Date(today.getFullYear(), today.getMonth(), 0);
        break;
      case 'thisYear':
        fromDate = new Date(today.getFullYear(), 0, 1);
        break;
      default:
        fromDate.setDate(today.getDate() - 30);
    }
    
    return {
      from: fromDate.toISOString().split('T')[0],
      to: toDate.toISOString().split('T')[0]
    };
  }

  exportReport(format: 'pdf' | 'csv' | 'excel'): void {
    // In a real application, this would call a backend API to generate the report
    alert(`Exporting ${this.activeTab} report as ${format.toUpperCase()}... (Not implemented in this demo)`);
  }

  printReport(): void {
    window.print();
  }
}