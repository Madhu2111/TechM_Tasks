import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { Booking, BookingStatus, PaymentStatus } from '../../models/booking.model';

@Component({
  selector: 'app-booking-management',
  templateUrl: './booking-management.component.html',
  styleUrls: ['./booking-management.component.css']
})
export class BookingManagementComponent implements OnInit {
  bookings: Booking[] = [];
  totalBookings = 0;
  currentPage = 1;
  pageSize = 10;
  isLoading = false;
  error = '';
  selectedBooking: Booking | null = null;
  showBookingDetails = false;
  statusFilter: BookingStatus | 'all' = 'all';
  BookingStatus = BookingStatus; // Make enum available to template
  PaymentStatus = PaymentStatus; // Make enum available to template
  dateFilter: string = '';
  Math = Math; // Make Math available to the template

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadBookings();
  }

  loadBookings(): void {
    this.isLoading = true;
    this.error = '';
    
    const status = this.statusFilter === 'all' ? undefined : this.statusFilter;
    this.adminService.getBookings(this.currentPage, this.pageSize, status, this.dateFilter).subscribe({
      next: (response) => {
        this.bookings = response.bookings;
        this.totalBookings = response.total;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to load bookings. Please try again.';
        this.isLoading = false;
        console.error('Error loading bookings:', error);
      }
    });
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.loadBookings();
  }

  applyFilters(): void {
    this.currentPage = 1; // Reset to first page when filters change
    this.loadBookings();
  }

  resetFilters(): void {
    this.statusFilter = 'all';
    this.dateFilter = '';
    this.applyFilters();
  }

  viewBookingDetails(booking: Booking): void {
    this.selectedBooking = booking;
    this.showBookingDetails = true;
  }

  closeBookingDetails(): void {
    this.showBookingDetails = false;
    this.selectedBooking = null;
  }

  updateBookingStatus(bookingId: number | undefined, status: BookingStatus): void {
    if (!bookingId) return;
    this.isLoading = true;
    this.error = '';
    
    this.adminService.updateBookingStatus(bookingId, status).subscribe({
      next: (updatedBooking) => {
        // Update booking in the list
        const index = this.bookings.findIndex(b => b.id === bookingId);
        if (index !== -1) {
          this.bookings[index] = updatedBooking;
        }
        this.isLoading = false;
        
        if (this.selectedBooking && this.selectedBooking.id === bookingId) {
          this.selectedBooking = updatedBooking;
        }
      },
      error: (error) => {
        this.error = `Failed to update booking status to ${status}. Please try again.`;
        this.isLoading = false;
        console.error('Error updating booking status:', error);
      }
    });
  }

  processRefund(bookingId: number | undefined): void {
    if (!bookingId || !confirm('Are you sure you want to process a refund for this booking?')) {
      return;
    }
    
    this.isLoading = true;
    this.error = '';
    
    this.adminService.processRefund(bookingId).subscribe({
      next: (updatedBooking) => {
        // Update booking in the list
        const index = this.bookings.findIndex(b => b.id === bookingId);
        if (index !== -1) {
          this.bookings[index] = updatedBooking;
        }
        this.isLoading = false;
        
        if (this.selectedBooking && this.selectedBooking.id === bookingId) {
          this.selectedBooking = updatedBooking;
        }
      },
      error: (error) => {
        this.error = 'Failed to process refund. Please try again.';
        this.isLoading = false;
        console.error('Error processing refund:', error);
      }
    });
  }

  // Helper method to format date
  formatDate(date: Date | undefined): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString();
  }

  // Helper method to calculate total amount
  calculateTotal(booking: Booking): number {
    return booking.totalAmount || 0;
  }
}