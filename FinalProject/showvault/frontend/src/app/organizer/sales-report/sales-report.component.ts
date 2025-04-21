import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { OrganizerService, SalesReport } from '../../services/organizer.service';
import { ShowService } from '../../services/show.service';
import { Show } from '../../models/show.model';
import { CommonModule, DecimalPipe } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-sales-report',
  templateUrl: './sales-report.component.html',
  styleUrls: ['./sales-report.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  providers: [DecimalPipe]
})
export class SalesReportComponent implements OnInit {
  salesReport: SalesReport | null = null;
  shows: Show[] = [];
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
    private organizerService: OrganizerService,
    private showService: ShowService,
    private fb: FormBuilder
  ) {
    this.filterForm = this.fb.group({
      dateRange: ['30days'],
      startDate: [''],
      endDate: [''],
      showId: ['all']
    });
  }

  ngOnInit(): void {
    this.loadShows();
    this.loadSalesReport();
    
    // Listen for date range changes
    this.filterForm.get('dateRange')?.valueChanges.subscribe(value => {
      this.showCustomRange = value === 'custom';
      if (value !== 'custom') {
        this.applyFilters();
      }
    });
  }

  loadShows(): void {
    this.showService.getAllShows().subscribe({
      next: (shows) => {
        this.shows = shows;
      },
      error: (error) => {
        console.error('Error loading shows:', error);
      }
    });
  }

  loadSalesReport(): void {
    this.isLoading = true;
    
    const formValues = this.filterForm.value;
    let dateFrom: string | undefined;
    let dateTo: string | undefined;
    
    // Calculate date range based on selection
    if (formValues.dateRange !== 'custom') {
      const { from, to } = this.calculateDateRange(formValues.dateRange);
      dateFrom = from;
      dateTo = to;
    } else {
      dateFrom = formValues.startDate;
      dateTo = formValues.endDate;
    }
    
    const showId = formValues.showId !== 'all' ? +formValues.showId : undefined;
    
    this.organizerService.getSalesReport(dateFrom, dateTo, showId).subscribe({
      next: (report) => {
        this.salesReport = report;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = `Failed to load sales report: ${error.message}`;
        console.error('Error loading sales report:', error);
        this.isLoading = false;
      }
    });
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

  applyFilters(): void {
    this.loadSalesReport();
  }

  exportReport(format: 'pdf' | 'csv' | 'excel'): void {
    // In a real application, this would call a backend API to generate the report
    alert(`Exporting report as ${format.toUpperCase()}... (Not implemented in this demo)`);
  }

  printReport(): void {
    window.print();
  }
}