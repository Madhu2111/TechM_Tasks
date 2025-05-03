import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Order } from '../../models/order.model';
import { OrderService } from '../../services/order.service';
import { NotificationService } from '../../services/notification.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-order-details',
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.css']
})
export class OrderDetailsComponent implements OnInit {
  order: Order | null = null;
  isLoading = true;
  isAdmin = false;
  availableStatuses: string[] = [];
  selectedStatus: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
    private notificationService: NotificationService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.isAdmin = this.authService.hasRole('ADMIN');
    this.loadOrderStatuses();
    
    const orderId = this.route.snapshot.paramMap.get('id');
    if (orderId) {
      this.loadOrderDetails(+orderId);
    } else {
      this.router.navigate(['/orders']);
    }
  }

  loadOrderDetails(orderId: number): void {
    this.isLoading = true;
    this.orderService.getOrderById(orderId).subscribe({
      next: (order) => {
        this.order = order;
        this.selectedStatus = order.status;
        this.isLoading = false;
      },
      error: (error) => {
        this.notificationService.showError('Failed to load order details. Please try again.');
        console.error('Error loading order details:', error);
        this.isLoading = false;
        this.router.navigate(['/orders']);
      }
    });
  }

  loadOrderStatuses(): void {
    this.orderService.getOrderStatuses().subscribe({
      next: (statuses) => {
        this.availableStatuses = statuses;
      },
      error: (error) => {
        console.error('Error loading order statuses:', error);
      }
    });
  }

  updateOrderStatus(): void {
    if (!this.order || !this.selectedStatus) return;
    
    this.isLoading = true;
    this.orderService.updateOrderStatus(this.order.id!, this.selectedStatus).subscribe({
      next: (updatedOrder) => {
        this.order = updatedOrder;
        this.notificationService.showSuccess('Order status updated successfully!');
        this.isLoading = false;
      },
      error: (error) => {
        this.notificationService.showError('Failed to update order status. Please try again.');
        console.error('Error updating order status:', error);
        this.isLoading = false;
      }
    });
  }

  canCancelOrder(): boolean {
    if (!this.order) return false;
    return this.order.status === 'PENDING' || this.order.status === 'PROCESSING';
  }

  cancelOrder(): void {
    if (!this.order) return;
    
    this.isLoading = true;
    this.orderService.updateOrderStatus(this.order.id!, 'CANCELLED').subscribe({
      next: (updatedOrder) => {
        this.order = updatedOrder;
        this.selectedStatus = updatedOrder.status;
        this.notificationService.showSuccess('Order cancelled successfully!');
        this.isLoading = false;
      },
      error: (error) => {
        this.notificationService.showError('Failed to cancel order. Please try again.');
        console.error('Error cancelling order:', error);
        this.isLoading = false;
      }
    });
  }
}