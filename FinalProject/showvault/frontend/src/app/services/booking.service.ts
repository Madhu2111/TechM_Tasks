import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, delay, map, switchMap, tap } from 'rxjs/operators';
import { Show, ShowSchedule } from '../models/show.model';

import { 
  Booking, 
  BookingRequest, 
  BookingResponse, 
  BookingSeat, 
  BookingStatus, 
  PaymentIntent, 
  PaymentMethod, 
  PaymentStatus, 
  SeatInfo, 
  SeatSelectionMap,
  SeatRow
} from '../models/booking.model';

export interface PaymentDetails {
  cardNumber: string;
  expiryDate: string;
  cvv: string;
  nameOnCard: string;
}

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private apiUrl = 'http://localhost:8080/api/bookings';
  private httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient) { }

  getBookingById(id: number): Observable<Booking> {
    return this.http.get<Booking>(`${this.apiUrl}/${id}`).pipe(
      catchError(error => {
        console.error(`Error fetching booking ${id}:`, error);
        return throwError(() => new Error(error.message || 'Booking not found'));
      })
    );
  }

  getUserBookings(userId?: number): Observable<Booking[]> {
    console.log('Fetching user bookings');
    return this.http.get<Booking[]>(`${this.apiUrl}/my-bookings`).pipe(
      tap(bookings => {
        console.log('Received user bookings:', bookings);
        if (bookings.length === 0) {
          console.log('No bookings found for user');
        } else {
          console.log(`Found ${bookings.length} bookings for user`);
        }
      }),
      catchError(error => {
        console.error('Error fetching user bookings:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch user bookings'));
      })
    );
  }

  createBooking(bookingRequest: BookingRequest): Observable<any> {
    console.log('Creating booking with request:', bookingRequest);
    
    // Extract just the seat IDs for the backend API
    const seatIds = bookingRequest.seats.map(seat => seat.seatId);
    console.log('Sending seat IDs to backend:', seatIds);
    
    // First create the booking with seats
    return this.http.post<any>(`${this.apiUrl}/schedule/${bookingRequest.scheduleId}/seats`, 
      seatIds, 
      this.httpOptions
    ).pipe(
      tap(booking => {
        console.log('Booking created successfully, refreshing user bookings');
        // Refresh user bookings to ensure the new booking is included
        this.getUserBookings().subscribe(
          bookings => console.log('User bookings after creation:', bookings),
          error => console.error('Error refreshing user bookings:', error)
        );
      }),
      switchMap(booking => {
        if (!booking || !booking.id) {
          throw new Error('Failed to create booking');
        }
        
        // Then process the payment
        return this.processPayment(booking.id, bookingRequest.paymentMethodId).pipe(
          map(paymentIntent => {
            // Construct and return the booking response
            const response: BookingResponse = {
              booking: {
                ...booking,
                paymentStatus: paymentIntent.status,
                customerName: bookingRequest.customerName,
                customerEmail: bookingRequest.customerEmail,
                customerPhone: bookingRequest.customerPhone
              },
              success: true,
              confirmationCode: booking.bookingNumber || `BK-${Math.floor(Math.random() * 1000000)}`,
              paymentIntent: paymentIntent
            };
            return response;
          })
        );
      }),
      catchError(error => {
        console.error('Error creating booking:', error);
        return throwError(() => new Error(error.message || 'Failed to create booking'));
      })
    );
  }



  cancelBooking(id: number): Observable<BookingResponse> {
    return this.http.post<BookingResponse>(`${this.apiUrl}/${id}/cancel`, {}, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error cancelling booking ${id}:`, error);
        return throwError(() => new Error(error.message || 'Failed to cancel booking'));
      })
    );
  }
  
  requestRefund(id: number, reason: string): Observable<BookingResponse> {
    return this.http.post<BookingResponse>(
      `${this.apiUrl}/${id}/refund-request`, 
      { reason }, 
      this.httpOptions
    ).pipe(
      catchError(error => {
        console.error(`Error requesting refund for booking ${id}:`, error);
        return throwError(() => new Error(error.message || 'Failed to request refund'));
      })
    );
  }

  getAvailableSeats(scheduleId: number): Observable<SeatSelectionMap> {
    return this.http.get<SeatSelectionMap>(`${this.apiUrl}/schedules/${scheduleId}/seats`).pipe(
      catchError(error => {
        console.error(`Error fetching seats for schedule ${scheduleId}:`, error);
        
        // For development/testing, return a sample seat map
        console.log('Returning sample seat map for development');
        return of(this.createSampleSeatMap());
      })
    );
  }
  
  private createSampleSeatMap(): SeatSelectionMap {
    // Create a sample seat map for testing
    const seatMap: SeatSelectionMap = {
      rows: [],
      screen: 'SCREEN',
      legend: {
        available: 'Available',
        reserved: 'Reserved',
        sold: 'Sold',
        selected: 'Selected',
        standard: 'Standard',
        premium: 'Premium',
        vip: 'VIP'
      }
    };
    
    // Create rows A-J with 15 seats each
    const rowLabels = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'];
    let seatId = 1;
    
    rowLabels.forEach((rowLabel, rowIndex) => {
      const row: SeatRow = {
        rowLabel,
        seats: []
      };
      
      // Create 15 seats per row
      for (let i = 1; i <= 15; i++) {
        // Determine seat category based on row
        let category: 'STANDARD' | 'PREMIUM' | 'VIP' = 'STANDARD';
        let price = 10.99;
        
        if (rowLabel === 'A' || rowLabel === 'B') {
          category = 'VIP';
          price = 19.99;
        } else if (rowLabel === 'C' || rowLabel === 'D' || rowLabel === 'E') {
          category = 'PREMIUM';
          price = 14.99;
        }
        
        // Randomly mark some seats as sold or reserved
        let status: 'AVAILABLE' | 'RESERVED' | 'SOLD' = 'AVAILABLE';
        if (Math.random() < 0.2) {
          status = 'SOLD';
        } else if (Math.random() < 0.1) {
          status = 'RESERVED';
        }
        
        row.seats.push({
          id: seatId++,
          seatNumber: i,
          status,
          price,
          category
        });
      }
      
      seatMap.rows.push(row);
    });
    
    return seatMap;
  }

  getSavedPaymentMethods(): Observable<PaymentMethod[]> {
    return this.http.get<PaymentMethod[]>(`${this.apiUrl}/payment-methods`).pipe(
      catchError(error => {
        console.error('Error fetching payment methods:', error);
        
        // For development/testing, return mock payment methods if the API fails
        console.log('Returning mock payment methods for development');
        return of(this.createMockPaymentMethods());
      })
    );
  }
  
  createMockPaymentMethods(): PaymentMethod[] {
    return [
      {
        id: 'pm_' + Math.random().toString(36).substring(2, 10),
        type: 'CREDIT_CARD',
        name: 'Visa ending in 4242',
        icon: 'bi-credit-card',
        lastFour: '4242',
        expiryDate: '12/25'
      },
      {
        id: 'pm_' + Math.random().toString(36).substring(2, 10),
        type: 'PAYPAL',
        name: 'PayPal Account',
        icon: 'bi-paypal'
      }
    ];
  }

  processPayment(bookingId: number, paymentMethodId: string): Observable<PaymentIntent> {
    return this.http.post<PaymentIntent>(
      `${this.apiUrl}/${bookingId}/payment`,
      { paymentMethodId },
      this.httpOptions
    ).pipe(
      catchError(error => {
        console.error(`Error processing payment for booking ${bookingId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to process payment'));
      })
    );
  }

  addPaymentMethod(paymentMethod: Partial<PaymentMethod>): Observable<PaymentMethod> {
    return this.http.post<PaymentMethod>(
      `${this.apiUrl}/payment-methods`,
      paymentMethod,
      this.httpOptions
    ).pipe(
      catchError(error => {
        console.error('Error adding payment method:', error);
        
        // For development/testing, return a mock payment method if the API fails
        console.log('Returning mock payment method for development');
        const mockPaymentMethod: PaymentMethod = {
          id: 'pm_' + Math.random().toString(36).substring(2, 10),
          type: paymentMethod.type || 'CREDIT_CARD',
          name: paymentMethod.name || 'New Card',
          icon: paymentMethod.icon || 'bi-credit-card',
          lastFour: paymentMethod.lastFour,
          expiryDate: paymentMethod.expiryDate
        };
        return of(mockPaymentMethod);
      })
    );
  }
  
  downloadTicket(bookingId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${bookingId}/ticket`, {
      responseType: 'blob'
    }).pipe(
      catchError(error => {
        console.error(`Error downloading ticket for booking ${bookingId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to download ticket'));
      })
    );
  }
  
  sendTicketByEmail(bookingId: number, email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${bookingId}/send-ticket`, { email }, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error sending ticket for booking ${bookingId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to send ticket by email'));
      })
    );
  }
  
  getBookingNotifications(userId?: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/notifications`).pipe(
      catchError(error => {
        console.error('Error fetching booking notifications:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch booking notifications'));
      })
    );
  }
  
  markNotificationAsRead(notificationId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/notifications/${notificationId}/read`, {}).pipe(
      catchError(error => {
        console.error(`Error marking notification ${notificationId} as read:`, error);
        return throwError(() => new Error(error.message || 'Failed to mark notification as read'));
      })
    );
  }
}