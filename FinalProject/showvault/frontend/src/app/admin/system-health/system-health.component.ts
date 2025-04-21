import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-system-health',
  templateUrl: './system-health.component.html',
  styleUrls: ['./system-health.component.css']
})
export class SystemHealthComponent implements OnInit {
  systemHealth: any = null;
  isLoading = true;
  error = '';
  refreshInterval: any = null;
  autoRefresh = true;
  refreshRate = 30; // seconds

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadSystemHealth();
    this.setupAutoRefresh();
  }

  ngOnDestroy(): void {
    this.clearRefreshInterval();
  }

  loadSystemHealth(): void {
    this.isLoading = true;
    this.error = '';
    
    this.adminService.getSystemHealth().subscribe({
      next: (health) => {
        this.systemHealth = health;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to load system health data. Please try again.';
        this.isLoading = false;
        console.error('Error loading system health:', error);
      }
    });
  }

  setupAutoRefresh(): void {
    if (this.autoRefresh) {
      this.clearRefreshInterval();
      this.refreshInterval = setInterval(() => {
        this.loadSystemHealth();
      }, this.refreshRate * 1000);
    }
  }

  clearRefreshInterval(): void {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
      this.refreshInterval = null;
    }
  }

  toggleAutoRefresh(): void {
    this.autoRefresh = !this.autoRefresh;
    if (this.autoRefresh) {
      this.setupAutoRefresh();
    } else {
      this.clearRefreshInterval();
    }
  }

  changeRefreshRate(rate: number): void {
    this.refreshRate = rate;
    if (this.autoRefresh) {
      this.setupAutoRefresh();
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'healthy':
      case 'up':
        return 'text-success';
      case 'warning':
      case 'degraded':
        return 'text-warning';
      case 'critical':
      case 'down':
        return 'text-danger';
      default:
        return 'text-secondary';
    }
  }

  getStatusIcon(status: string): string {
    switch (status) {
      case 'healthy':
      case 'up':
        return 'bi-check-circle-fill';
      case 'warning':
      case 'degraded':
        return 'bi-exclamation-triangle-fill';
      case 'critical':
      case 'down':
        return 'bi-x-circle-fill';
      default:
        return 'bi-question-circle-fill';
    }
  }

  getProgressBarClass(value: number): string {
    if (value < 50) {
      return 'bg-success';
    } else if (value < 75) {
      return 'bg-warning';
    } else {
      return 'bg-danger';
    }
  }

  formatDate(date: Date | string): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleString();
  }
}