import { Component, Input, Output, EventEmitter } from '@angular/core';
import { BookingSummary } from '../../models/booking.model';

@Component({
  selector: 'app-booking-summary',
  templateUrl: './booking-summary.component.html',
  styleUrls: ['./booking-summary.component.css']
})
export class BookingSummaryComponent {
  @Input() summary: BookingSummary | null = null;
  @Input() showEditButton = false;
  @Input() showPaymentButton = false;
  @Input() isProcessing = false;
  
  @Output() editSeats = new EventEmitter<void>();
  @Output() proceedToPayment = new EventEmitter<void>();
  
  constructor() { }
  
  onEditSeats(): void {
    this.editSeats.emit();
  }
  
  onProceedToPayment(): void {
    console.log('Payment button clicked');
    this.proceedToPayment.emit();
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
}