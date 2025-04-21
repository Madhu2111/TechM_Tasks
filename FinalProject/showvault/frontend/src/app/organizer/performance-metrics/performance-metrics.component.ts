import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ShowService } from '../../services/show.service';
import { ShowAnalytics } from '../../models/show.model';

@Component({
  selector: 'app-performance-metrics',
  templateUrl: './performance-metrics.component.html',
  styleUrls: ['./performance-metrics.component.css']
})
export class PerformanceMetricsComponent implements OnInit {
  showId: number;
  analytics: ShowAnalytics | null = null;
  isLoading = true;
  error = '';
  
  // Chart options
  salesChartOptions: any;
  demographicsChartOptions: any;
  categoryChartOptions: any;

  constructor(
    private route: ActivatedRoute,
    private showService: ShowService
  ) {
    this.showId = this.route.snapshot.params['id'];
  }

  ngOnInit(): void {
    this.loadPerformanceData();
  }

  loadPerformanceData(): void {
    this.isLoading = true;
    this.error = '';
    
    this.showService.getShowAnalytics(this.showId).subscribe({
      next: (data) => {
        this.analytics = data;
        this.initCharts();
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to load performance metrics. Please try again.';
        this.isLoading = false;
        console.error('Error loading performance metrics:', error);
      }
    });
  }

  initCharts(): void {
    if (!this.analytics) return;
    
    // Initialize sales chart
    this.initSalesChart();
    
    // Initialize demographics chart
    this.initDemographicsChart();
    
    // Initialize category chart
    this.initCategoryChart();
  }

  initSalesChart(): void {
    if (!this.analytics || !this.analytics.salesByDay) return;
    
    const dates = Object.keys(this.analytics.salesByDay);
    const sales = Object.values(this.analytics.salesByDay);
    
    this.salesChartOptions = {
      series: [{
        name: 'Sales',
        data: sales
      }],
      chart: {
        type: 'line',
        height: 350
      },
      xaxis: {
        categories: dates
      },
      title: {
        text: 'Daily Sales'
      }
    };
  }

  initDemographicsChart(): void {
    if (!this.analytics || !this.analytics.audienceDemographics || !this.analytics.audienceDemographics.ageGroups) return;
    
    const ageGroups = Object.keys(this.analytics.audienceDemographics.ageGroups);
    const values = Object.values(this.analytics.audienceDemographics.ageGroups);
    
    this.demographicsChartOptions = {
      series: values,
      chart: {
        type: 'pie',
        height: 350
      },
      labels: ageGroups,
      title: {
        text: 'Audience Age Distribution'
      }
    };
  }

  initCategoryChart(): void {
    if (!this.analytics || !this.analytics.salesByCategory) return;
    
    const categories = Object.keys(this.analytics.salesByCategory);
    const values = Object.values(this.analytics.salesByCategory);
    
    this.categoryChartOptions = {
      series: [{
        name: 'Sales',
        data: values
      }],
      chart: {
        type: 'bar',
        height: 350
      },
      xaxis: {
        categories: categories
      },
      title: {
        text: 'Sales by Category'
      }
    };
  }

  getOccupancyRateClass(): string {
    if (!this.analytics) return '';
    
    const rate = this.analytics.occupancyRate;
    
    if (rate >= 80) {
      return 'text-success';
    } else if (rate >= 50) {
      return 'text-warning';
    } else {
      return 'text-danger';
    }
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(value);
  }

  formatPercentage(value: number): string {
    return `${value.toFixed(1)}%`;
  }
}