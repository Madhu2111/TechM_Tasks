import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BookingService } from '../../services/booking.service';
import { Booking, BookingStatus } from '../../models/booking.model';

@Component({
  selector: 'app-booking-history',
  templateUrl: './booking-history.component.html',
  styleUrls: ['./booking-history.component.css']
})
export class BookingHistoryComponent implements OnInit {
  bookings: Booking[] = [];
  filteredBookings: Booking[] = [];
  isLoading = true;
  error = '';
  successMessage = '';
  BookingStatus = BookingStatus; // Make enum available in template
  
  // For refund request
  showRefundModal = false;
  selectedBookingId: number | null = null;
  refundForm: FormGroup;

  // Helper methods for template
  getStatusClass(status: string | undefined): string {
    if (!status) return 'badge bg-secondary';
    
    // Convert to uppercase for consistency
    const upperStatus = typeof status === 'string' ? status.toUpperCase() : status;
    
    switch (upperStatus) {
      case 'CONFIRMED': return 'badge bg-success';
      case 'CANCELLED': return 'badge bg-danger';
      case 'PENDING': return 'badge bg-warning text-dark';
      case 'EXPIRED': return 'badge bg-secondary';
      case 'REFUND_REQUESTED': return 'badge bg-info';
      case 'REFUNDED': return 'badge bg-secondary';
      default: return 'badge bg-secondary';
    }
  }

  canCancel(booking: any): boolean {
    if (!booking.status) return false;
    
    // Get show date from either the new model or the old model
    let showDate: Date | null = null;
    if (booking.showDate) {
      showDate = new Date(booking.showDate);
    } else if (booking.showSchedule?.showDate) {
      showDate = new Date(booking.showSchedule.showDate);
    } else if (booking.schedule?.showDate) {
      showDate = new Date(booking.schedule.showDate);
    }
    
    if (!showDate) return false;
    
    const now = new Date();
    now.setHours(now.getHours() + 24);
    
    return showDate > now && 
           (booking.status === BookingStatus.CONFIRMED || 
            booking.status === BookingStatus.PENDING ||
            booking.status === 'CONFIRMED' || 
            booking.status === 'PENDING');
  }
  
  // For email ticket
  showEmailModal = false;
  emailForm: FormGroup;
  
  // For notifications
  notifications: any[] = [];
  unreadNotifications = 0;
  showNotifications = false;
  
  // For filtering
  statusFilter = 'all';
  searchQuery = '';
  sortOrder = 'newest';

  constructor(
    private bookingService: BookingService,
    private fb: FormBuilder
  ) {
    this.refundForm = this.fb.group({
      reason: ['', [Validators.required, Validators.minLength(10)]]
    });
    
    this.emailForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit(): void {
    this.loadBookings();
    this.loadNotifications();
  }

  loadBookings(): void {
    this.isLoading = true;
    this.error = '';
    console.log('Loading bookings...');
    
    this.bookingService.getUserBookings().subscribe({
      next: (bookings) => {
        console.log('Received bookings:', bookings);
        
        if (bookings.length === 0) {
          console.log('No bookings found');
        } else {
          console.log(`Found ${bookings.length} bookings`);
          
          // Log details of each booking for debugging
          bookings.forEach(booking => {
            console.log(`Booking ID: ${booking.id}, Status: ${booking.status}`);
            console.log(`Show: ${booking.showSchedule?.show?.title}`);
            console.log(`Seats: ${booking.seatBookings?.length || 0}`);
          });
        }
        
        this.bookings = bookings;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to load booking history. Please try again.';
        this.isLoading = false;
        console.error('Error loading bookings:', error);
      }
    });
  }
  
  loadNotifications(): void {
    this.bookingService.getBookingNotifications().subscribe({
      next: (notifications) => {
        this.notifications = notifications;
        this.unreadNotifications = notifications.filter(n => !n.read).length;
      },
      error: (error) => {
        console.error('Error loading notifications:', error);
      }
    });
  }
  
  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
    
    // Mark notifications as read when opened
    if (this.showNotifications && this.unreadNotifications > 0) {
      this.notifications.forEach(notification => {
        if (!notification.read) {
          this.bookingService.markNotificationAsRead(notification.id).subscribe();
          notification.read = true;
        }
      });
      this.unreadNotifications = 0;
    }
  }
  
  applyFilters(): void {
    // Start with all bookings
    let filtered = [...this.bookings];
    
    // Apply status filter
    if (this.statusFilter !== 'all') {
      filtered = filtered.filter(booking => {
        // Convert to uppercase for consistency
        const bookingStatus = typeof booking.status === 'string' ? booking.status.toUpperCase() : booking.status;
        const filterStatus = typeof this.statusFilter === 'string' ? this.statusFilter.toUpperCase() : this.statusFilter;
        return bookingStatus === filterStatus;
      });
    }
    
    // Apply search filter
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(booking => 
        (booking.showName?.toLowerCase().includes(query)) ||
        (booking.showSchedule?.show?.title?.toLowerCase().includes(query)) ||
        (booking.bookingNumber?.toLowerCase().includes(query)) ||
        (booking.id?.toString().includes(query)) || false
      );
    }
    
    // Apply sorting
    if (this.sortOrder === 'newest') {
      filtered.sort((a, b) => {
        const dateA = new Date(a.bookingDate || 0).getTime();
        const dateB = new Date(b.bookingDate || 0).getTime();
        return dateB - dateA;
      });
    } else if (this.sortOrder === 'oldest') {
      filtered.sort((a, b) => {
        const dateA = new Date(a.bookingDate || 0).getTime();
        const dateB = new Date(b.bookingDate || 0).getTime();
        return dateA - dateB;
      });
    } else if (this.sortOrder === 'price-high') {
      filtered.sort((a, b) => (b.totalAmount || 0) - (a.totalAmount || 0));
    } else if (this.sortOrder === 'price-low') {
      filtered.sort((a, b) => (a.totalAmount || 0) - (b.totalAmount || 0));
    }
    
    this.filteredBookings = filtered;
  }

  cancelBooking(bookingId: number): void {
    if (!bookingId) return;
    
    if (confirm('Are you sure you want to cancel this booking? This action cannot be undone.')) {
      this.bookingService.cancelBooking(bookingId).subscribe({
        next: () => {
          this.successMessage = 'Booking cancelled successfully!';
          // Update the status in the local array
          const booking = this.bookings.find(b => b.id === bookingId);
          if (booking) {
            booking.status = BookingStatus.CANCELLED;
          }
          
          // Apply filters to update the filtered list
          this.applyFilters();
          
          // Clear success message after 3 seconds
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (error) => {
          this.error = 'Failed to cancel booking. Please try again.';
          console.error('Error cancelling booking:', error);
          
          // Clear error message after 3 seconds
          setTimeout(() => {
            this.error = '';
          }, 3000);
        }
      });
    }
  }

  downloadTicket(bookingId: number): void {
    if (!bookingId) return;
    
    this.bookingService.downloadTicket(bookingId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `ticket-${bookingId}.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      },
      error: (error) => {
        this.error = 'Failed to download ticket. Please try again.';
        console.error('Error downloading ticket:', error);
        
        // Clear error message after 3 seconds
        setTimeout(() => {
          this.error = '';
        }, 3000);
      }
    });
  }
  
  openRefundModal(bookingId: number): void {
    this.selectedBookingId = bookingId;
    this.showRefundModal = true;
    this.refundForm.reset();
  }
  
  closeRefundModal(): void {
    this.showRefundModal = false;
    this.selectedBookingId = null;
  }
  
  submitRefundRequest(): void {
    if (this.refundForm.valid && this.selectedBookingId) {
      const reason = this.refundForm.value.reason;
      
      this.bookingService.requestRefund(this.selectedBookingId, reason).subscribe({
        next: () => {
          this.successMessage = 'Refund request submitted successfully!';
          
          // Update the status in the local array
          const booking = this.bookings.find(b => b.id === this.selectedBookingId);
          if (booking) {
            booking.status = BookingStatus.REFUND_REQUESTED;
            booking.refundReason = reason;
          }
          
          // Apply filters to update the filtered list
          this.applyFilters();
          
          // Close the modal
          this.closeRefundModal();
          
          // Clear success message after 3 seconds
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (error) => {
          this.error = 'Failed to submit refund request. Please try again.';
          console.error('Error requesting refund:', error);
          
          // Clear error message after 3 seconds
          setTimeout(() => {
            this.error = '';
          }, 3000);
        }
      });
    }
  }
  
  openEmailModal(bookingId: number): void {
    this.selectedBookingId = bookingId;
    this.showEmailModal = true;
    
    // Pre-fill with user's email if available
    const booking = this.bookings.find(b => b.id === bookingId);
    if (booking && booking.customerEmail) {
      this.emailForm.patchValue({ email: booking.customerEmail });
    } else {
      this.emailForm.reset();
    }
  }
  
  closeEmailModal(): void {
    this.showEmailModal = false;
    this.selectedBookingId = null;
  }
  
  sendTicketByEmail(): void {
    if (this.emailForm.valid && this.selectedBookingId) {
      const email = this.emailForm.value.email;
      
      this.bookingService.sendTicketByEmail(this.selectedBookingId, email).subscribe({
        next: () => {
          this.successMessage = `Ticket sent to ${email} successfully!`;
          
          // Close the modal
          this.closeEmailModal();
          
          // Clear success message after 3 seconds
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (error) => {
          this.error = 'Failed to send ticket by email. Please try again.';
          console.error('Error sending ticket by email:', error);
          
          // Clear error message after 3 seconds
          setTimeout(() => {
            this.error = '';
          }, 3000);
        }
      });
    }
  }


  
  canRequestRefund(booking: Booking): boolean {
    // A booking can have a refund requested if it's confirmed
    // and the show date is in the past
    if (booking.status !== BookingStatus.CONFIRMED) {
      return false;
    }
    
    if (!booking.showDate) {
      return false;
    }
    
    const showDate = new Date(booking.showDate);
    const now = new Date();
    
    // Can request refund if show date is in the past
    return showDate < now;
  }
  
  getFormattedDate(dateString: string | Date): string {
    if (!dateString) return 'N/A';
    
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
  
  getRelativeTime(dateString: string | Date): string {
    if (!dateString) return '';
    
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffSec = Math.round(diffMs / 1000);
    const diffMin = Math.round(diffSec / 60);
    const diffHour = Math.round(diffMin / 60);
    const diffDay = Math.round(diffHour / 24);
    
    if (diffSec < 60) {
      return 'just now';
    } else if (diffMin < 60) {
      return `${diffMin} minute${diffMin > 1 ? 's' : ''} ago`;
    } else if (diffHour < 24) {
      return `${diffHour} hour${diffHour > 1 ? 's' : ''} ago`;
    } else if (diffDay < 7) {
      return `${diffDay} day${diffDay > 1 ? 's' : ''} ago`;
    } else {
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    }
  }
}