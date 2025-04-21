import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { BookingService } from '../../services/booking.service';
import { BookingResponse, BookingSummary } from '../../models/booking.model';

@Component({
  selector: 'app-booking-confirmation',
  templateUrl: './booking-confirmation.component.html',
  styleUrls: ['./booking-confirmation.component.css']
})
export class BookingConfirmationComponent implements OnInit, OnDestroy {
  bookingResponse: BookingResponse | null = null;
  bookingSummary: BookingSummary | null = null;
  downloadingTicket = false;
  error = '';

  constructor(
    private router: Router,
    private bookingService: BookingService
  ) {
    // Refresh user bookings when the confirmation page is loaded
    this.bookingService.getUserBookings().subscribe(bookings => {
      console.log('User bookings from confirmation page:', bookings);
    });
  }

  ngOnInit(): void {
    // Get data from router state
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state as {
      bookingResponse: BookingResponse;
      bookingSummary: BookingSummary;
    };
    
    if (state && state.bookingResponse && state.bookingSummary) {
      console.log('Booking confirmation received state:', state);
      this.bookingResponse = state.bookingResponse;
      this.bookingSummary = state.bookingSummary;
      
      // Store in session storage for page refreshes
      try {
        sessionStorage.setItem('bookingResponse', JSON.stringify(this.bookingResponse));
        sessionStorage.setItem('bookingSummary', JSON.stringify(this.bookingSummary));
        console.log('Stored booking data in session storage');
      } catch (error) {
        console.error('Error storing booking data in session storage:', error);
      }
    } else {
      // Try to get from session storage (for page refreshes)
      const storedResponse = sessionStorage.getItem('bookingResponse');
      const storedSummary = sessionStorage.getItem('bookingSummary');
      
      if (storedResponse && storedSummary) {
        this.bookingResponse = JSON.parse(storedResponse);
        this.bookingSummary = JSON.parse(storedSummary);
      } else {
        // Try to get from history state
        const historyState = window.history.state;
        if (historyState && historyState.bookingResponse && historyState.bookingSummary) {
          this.bookingResponse = historyState.bookingResponse;
          this.bookingSummary = historyState.bookingSummary;
          
          // Store in session storage
          try {
            sessionStorage.setItem('bookingResponse', JSON.stringify(this.bookingResponse));
            sessionStorage.setItem('bookingSummary', JSON.stringify(this.bookingSummary));
          } catch (error) {
            console.error('Error storing booking data in session storage:', error);
          }
        } else {
          // No state data, redirect back to shows
          this.error = 'Booking information not found. Redirecting to shows...';
          setTimeout(() => {
            this.router.navigate(['/shows']);
          }, 3000);
        }
      }
    }
  }

  downloadTicket(): void {
    if (!this.bookingResponse?.booking) {
      this.error = 'Booking information not available';
      return;
    }
    
    this.downloadingTicket = true;
    this.error = '';
    
    this.bookingService.downloadTicket(this.bookingResponse.booking.id!).subscribe({
      next: (blob) => {
        // Create a download link
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `ticket-${this.bookingResponse?.booking.id}.pdf`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
        this.downloadingTicket = false;
      },
      error: (error) => {
        console.error('Error downloading ticket:', error);
        this.error = 'Failed to download ticket. Please try again.';
        this.downloadingTicket = false;
      }
    });
  }

  viewBookings(): void {
    this.clearBookingData();
    this.router.navigate(['/user/bookings']);
  }

  browseShows(): void {
    this.clearBookingData();
    this.router.navigate(['/shows']);
  }

  formatDate(dateString: string): string {
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    } catch (error) {
      return dateString;
    }
  }

  formatTime(timeString: string): string {
    return timeString;
  }
  
  ngOnDestroy(): void {
    // Clear session storage when navigating away
    this.clearBookingData();
  }
  
  // Helper method to clear session storage
  clearBookingData(): void {
    try {
      sessionStorage.removeItem('bookingResponse');
      sessionStorage.removeItem('bookingSummary');
    } catch (error) {
      console.error('Error clearing booking data from session storage:', error);
    }
  }
}