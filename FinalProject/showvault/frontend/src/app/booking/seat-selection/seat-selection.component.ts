import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ShowService } from '../../services/show.service';
import { BookingService } from '../../services/booking.service';
import { AuthService } from '../../services/auth.service';
import { Show, ShowSchedule } from '../../models/show.model';
import { 
  BookingSeat, 
  BookingSummary, 
  SeatInfo, 
  SeatSelectionMap,
  SeatRow
} from '../../models/booking.model';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-seat-selection',
  templateUrl: './seat-selection.component.html',
  styleUrls: ['./seat-selection.component.css']
})
export class SeatSelectionComponent implements OnInit {
  show: Show | null = null;
  schedule: ShowSchedule | null = null;
  seatMap: SeatSelectionMap | null = null;
  selectedSeats: SeatInfo[] = [];
  maxSeats = 10;
  loading = false;
  error = '';
  bookingSummary: BookingSummary | null = null;
  processingBooking = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private showService: ShowService,
    private bookingService: BookingService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.loading = true;
    
    // Get query parameters
    const showId = Number(this.route.snapshot.queryParamMap.get('showId'));
    const scheduleId = Number(this.route.snapshot.queryParamMap.get('scheduleId'));
    
    console.log('Seat Selection Component initialized with showId:', showId, 'scheduleId:', scheduleId);
    
    if (isNaN(showId) || isNaN(scheduleId)) {
      this.error = 'Invalid show or schedule ID';
      this.loading = false;
      return;
    }
    
    // Load show details
    this.showService.getShowById(showId).subscribe({
      next: (show) => {
        this.show = show;
        
        // If show has schedules, find the selected one
        if (show.schedules && show.schedules.length > 0) {
          this.schedule = show.schedules.find(s => s.id === scheduleId) || null;
        }
        
        // Load seat map
        this.loadSeatMap(scheduleId);
      },
      error: (error) => {
        console.error('Error loading show:', error);
        this.error = 'Failed to load show details';
        this.loading = false;
        
        // Load sample data in development environment
        if (true) { // Change to environment.development check in real app
          this.loadSampleData(showId, scheduleId);
          this.error = '';
        }
      }
    });
  }
  
  loadSampleData(showId: number, scheduleId: number): void {
    // Sample show data
    this.show = {
      id: showId,
      title: 'Sample Show',
      type: 'Movie',
      image: 'assets/images/movie1.jpg',
      date: '2023-12-15',
      time: '19:00',
      venue: 'Cinema City',
      price: 12.99,
      description: 'Sample show description',
      duration: 120
    };
    
    // Sample schedule data
    this.schedule = {
      id: scheduleId,
      showId: showId,
      showDate: '2023-12-15',
      showTime: '19:00',
      basePrice: 12.99,
      venue: { 
        id: 1, 
        name: 'Cinema City',
        address: '123 Main St',
        city: 'New York',
        state: 'NY',
        capacity: 200
      },
      availableSeats: 150,
      totalSeats: 200
    };
    
    // Create sample seat map
    this.createSampleSeatMap();
  }
  
  createSampleSeatMap(): void {
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
    
    this.seatMap = seatMap;
    this.loading = false;
  }

  loadSeatMap(scheduleId: number): void {
    this.bookingService.getAvailableSeats(scheduleId).subscribe({
      next: (seatMap) => {
        this.seatMap = seatMap;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading seats:', error);
        this.error = 'Failed to load seat map';
        this.loading = false;
      }
    });
  }

  toggleSeatSelection(seat: SeatInfo): void {
    if (seat.status !== 'AVAILABLE') {
      return; // Can't select unavailable seats
    }
    
    const index = this.selectedSeats.findIndex(s => s.id === seat.id);
    
    if (index >= 0) {
      // Deselect the seat
      this.selectedSeats.splice(index, 1);
      seat.isSelected = false;
    } else {
      // Check if max seats reached
      if (this.selectedSeats.length >= this.maxSeats) {
        alert(`You can select a maximum of ${this.maxSeats} seats.`);
        return;
      }
      
      // Select the seat
      this.selectedSeats.push(seat);
      seat.isSelected = true;
    }
    
    // Update booking summary
    this.updateBookingSummary();
  }

  getSeatStatusClass(seat: SeatInfo): string {
    if (seat.isSelected) {
      return 'selected';
    }
    
    return seat.status.toLowerCase();
  }

  getSeatCategoryClass(seat: SeatInfo): string {
    return seat.category ? seat.category.toLowerCase() : '';
  }

  updateBookingSummary(): void {
    if (!this.show || !this.schedule) {
      return;
    }
    
    // Calculate pricing
    const subtotal = this.selectedSeats.reduce((sum, seat) => sum + seat.price, 0);
    const fees = subtotal * 0.05; // 5% booking fee
    const taxes = subtotal * 0.08; // 8% tax
    const total = subtotal + fees + taxes;
    
    // Create booking summary
    this.bookingSummary = {
      show: {
        id: this.show.id || 0,
        title: this.show.title,
        type: this.show.type,
        image: this.show.image
      },
      schedule: {
        id: this.schedule.id || 0,
        date: this.schedule.showDate,
        time: this.schedule.showTime || '',
        venue: this.schedule.venue?.name || this.show.venue
      },
      seats: {
        count: this.selectedSeats.length,
        details: this.selectedSeats.map(seat => ({
          row: this.getSeatRow(seat),
          seatNumber: seat.seatNumber,
          price: seat.price,
          category: seat.category
        }))
      },
      pricing: {
        subtotal,
        fees,
        taxes,
        total
      }
    };
  }

  getSeatRow(seat: SeatInfo): string {
    if (!this.seatMap) return '';
    
    // Find the row that contains this seat
    const row = this.seatMap.rows.find(r => 
      r.seats.some(s => s.id === seat.id)
    );
    
    return row ? row.rowLabel : '';
  }

  proceedToCheckout(): void {
    if (this.selectedSeats.length === 0) {
      alert('Please select at least one seat.');
      return;
    }
    
    this.processingBooking = true;
    
    // Get user info
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        // Update booking summary with customer info
        if (this.bookingSummary) {
          this.bookingSummary.customer = {
            name: user.name || `${user.firstName} ${user.lastName}`,
            email: user.email
          };
        }
        
        // Navigate to payment page with booking summary
        this.navigateToPayment();
      },
      error: (error) => {
        console.error('Error getting user:', error);
        this.processingBooking = false;
        this.router.navigate(['/login'], { 
          queryParams: { returnUrl: this.router.url } 
        });
      }
    });
  }
  
  private navigateToPayment(): void {
    this.router.navigate(['/booking/payment'], { 
      state: { 
        bookingSummary: this.bookingSummary,
        showId: this.show?.id,
        scheduleId: this.schedule?.id,
        selectedSeats: this.selectedSeats.map(seat => ({
          seatId: seat.id,
          row: this.getSeatRow(seat),
          seatNumber: seat.seatNumber,
          price: seat.price,
          category: seat.category
        }))
      } 
    });
  }

  goBack(): void {
    if (this.show) {
      this.router.navigate(['/booking/theater-selection', this.show.id]);
    } else {
      this.router.navigate(['/shows']);
    }
  }
}