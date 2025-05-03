import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-admin-orders',
  templateUrl: './admin-orders.component.html',
  styleUrls: ['./admin-orders.component.css']
})
export class AdminOrdersComponent implements OnInit {
  orders: any[] = [];
  isLoading = true;
  error: string | null = null;
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  
  // Sorting
  sortBy = 'createdAt';
  sortDirection = 'desc';
  
  // Filtering
  statusFilter: string | null = null;
  orderStatuses: string[] = [];

  constructor(
    private adminService: AdminService,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.loadOrders();
    this.loadOrderStatuses();
  }

  loadOrders(): void {
    this.isLoading = true;
    this.error = null;

    if (this.statusFilter) {
      this.adminService.getOrdersByStatus(this.statusFilter, this.currentPage, this.pageSize)
        .subscribe({
          next: (response) => {
            this.orders = response.content;
            this.totalElements = response.totalElements;
            this.isLoading = false;
          },
          error: (err) => {
            console.error('Error loading orders', err);
            this.error = 'Failed to load orders. Please try again later.';
            this.isLoading = false;
          }
        });
    } else {
      this.adminService.getAllOrders(this.currentPage, this.pageSize, this.sortBy, this.sortDirection)
        .subscribe({
          next: (response) => {
            this.orders = response.content;
            this.totalElements = response.totalElements;
            this.isLoading = false;
          },
          error: (err) => {
            console.error('Error loading orders', err);
            this.error = 'Failed to load orders. Please try again later.';
            this.isLoading = false;
          }
        });
    }
  }

  loadOrderStatuses(): void {
    // This would typically come from an API call
    this.orderStatuses = ['PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadOrders();
  }

  onSortChange(sortBy: string): void {
    if (this.sortBy === sortBy) {
      // Toggle direction if clicking the same column
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = sortBy;
      this.sortDirection = 'asc';
    }
    this.loadOrders();
  }

  onStatusFilterChange(status: string | null): void {
    this.statusFilter = status;
    this.currentPage = 0; // Reset to first page
    this.loadOrders();
  }

  updateOrderStatus(orderId: number, status: string): void {
    this.adminService.updateOrderStatus(orderId, status).subscribe({
      next: () => {
        this.notificationService.showSuccess(`Order status updated to ${status}`);
        this.loadOrders();
      },
      error: (err) => {
        console.error('Error updating order status', err);
        this.notificationService.showError('Failed to update order status');
      }
    });
  }

  getTotalPages(): number {
    return Math.ceil(this.totalElements / this.pageSize);
  }
}