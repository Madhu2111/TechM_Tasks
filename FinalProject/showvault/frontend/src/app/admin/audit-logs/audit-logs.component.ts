import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-audit-logs',
  templateUrl: './audit-logs.component.html',
  styleUrls: ['./audit-logs.component.css']
})
export class AuditLogsComponent implements OnInit {
  Math = Math; // Add Math as a class property
  logs: any[] = [];
  totalLogs = 0;
  currentPage = 1;
  pageSize = 10;
  isLoading = true;
  error = '';
  
  filterForm: FormGroup;
  
  // For pagination
  totalPages = 1;
  
  // For sorting
  sortField = 'timestamp';
  sortOrder: 'asc' | 'desc' = 'desc';
  
  // Action types for filter
  actionTypes = [
    'All Actions',
    'LOGIN',
    'LOGOUT',
    'USER_CREATE',
    'USER_UPDATE',
    'USER_DELETE',
    'USER_ROLE_UPDATE',
    'USER_SUSPEND',
    'BOOKING_CREATE',
    'BOOKING_UPDATE',
    'BOOKING_CANCEL',
    'BOOKING_REFUND',
    'SHOW_CREATE',
    'SHOW_UPDATE',
    'SHOW_DELETE',
    'VENUE_CREATE',
    'VENUE_UPDATE',
    'VENUE_DELETE',
    'SETTINGS_UPDATE',
    'SYSTEM_BACKUP'
  ];

  constructor(
    private adminService: AdminService,
    private fb: FormBuilder
  ) {
    this.filterForm = this.fb.group({
      userId: [''],
      action: ['All Actions'],
      dateFrom: [''],
      dateTo: ['']
    });
  }

  ngOnInit(): void {
    this.loadAuditLogs();
  }

  loadAuditLogs(): void {
    this.isLoading = true;
    this.error = '';
    
    const filters: any = {
      sortBy: this.sortField,
      sortOrder: this.sortOrder
    };
    
    if (this.filterForm.value.userId) {
      filters.userId = this.filterForm.value.userId;
    }
    
    if (this.filterForm.value.action && this.filterForm.value.action !== 'All Actions') {
      filters.action = this.filterForm.value.action;
    }
    
    if (this.filterForm.value.dateFrom) {
      filters.dateFrom = this.filterForm.value.dateFrom;
    }
    
    if (this.filterForm.value.dateTo) {
      filters.dateTo = this.filterForm.value.dateTo;
    }
    
    this.adminService.getAuditLogs(this.currentPage, this.pageSize, filters).subscribe({
      next: (response) => {
        this.logs = response.logs;
        this.totalLogs = response.total;
        this.totalPages = Math.ceil(this.totalLogs / this.pageSize);
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to load audit logs. Please try again.';
        this.isLoading = false;
        console.error('Error loading audit logs:', error);
      }
    });
  }

  applyFilters(): void {
    this.currentPage = 1; // Reset to first page when applying filters
    this.loadAuditLogs();
  }

  resetFilters(): void {
    this.filterForm.reset({
      userId: '',
      action: 'All Actions',
      dateFrom: '',
      dateTo: ''
    });
    this.applyFilters();
  }

  changePage(page: number): void {
    if (page < 1 || page > this.totalPages) return;
    this.currentPage = page;
    this.loadAuditLogs();
  }

  sortBy(field: string): void {
    if (this.sortField === field) {
      // Toggle sort order if clicking the same field
      this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
    } else {
      // Default to descending order for a new sort field
      this.sortField = field;
      this.sortOrder = 'desc';
    }
    
    this.loadAuditLogs();
  }

  getSortIcon(field: string): string {
    if (this.sortField !== field) return 'bi-arrow-down-up';
    return this.sortOrder === 'asc' ? 'bi-sort-down' : 'bi-sort-up';
  }

  formatDate(date: Date | string): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleString();
  }

  getActionClass(action: string): string {
    if (action.includes('CREATE') || action.includes('LOGIN')) {
      return 'bg-success';
    } else if (action.includes('UPDATE')) {
      return 'bg-primary';
    } else if (action.includes('DELETE') || action.includes('CANCEL') || action.includes('LOGOUT')) {
      return 'bg-danger';
    } else if (action.includes('SUSPEND')) {
      return 'bg-warning';
    } else {
      return 'bg-secondary';
    }
  }
}