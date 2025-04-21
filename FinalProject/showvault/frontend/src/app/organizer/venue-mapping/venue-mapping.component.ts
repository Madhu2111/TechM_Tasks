import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ShowService } from '../../services/show.service';
import { Show, Seat } from '../../models/show.model';
import { ShowSchedule, SeatMap, SeatMapSection, SeatMapRow } from '../../models/show-interfaces.model';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-venue-mapping',
  templateUrl: './venue-mapping.component.html',
  styleUrls: ['./venue-mapping.component.css'],
  providers: [DecimalPipe],
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule]
})
export class VenueMappingComponent implements OnInit {
  showId!: number;
  scheduleId!: number;
  show: Show | null = null;
  schedule: ShowSchedule | null = null;
  seatMap: SeatMap | null = null;
  
  isLoading = true;
  isSaving = false;
  error = '';
  success = '';
  
  // Seat mapping configuration
  selectedSection: SeatMapSection | null = null;
  selectedSeatCategory: 'STANDARD' | 'PREMIUM' | 'VIP' = 'STANDARD';
  selectedPriceMultiplier = 1.0;
  
  // Editing mode
  editMode = false;
  
  // Seat selection
  selectedSeats: Seat[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private showService: ShowService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const showId = params.get('id');
      const scheduleId = params.get('scheduleId');
      
      if (showId && scheduleId) {
        this.showId = +showId;
        this.scheduleId = +scheduleId;
        this.loadShow();
        this.loadSchedule();
        this.loadSeatMap();
      }
    });
  }

  loadShow(): void {
    this.showService.getShowById(this.showId).subscribe({
      next: (show) => {
        this.show = show;
      },
      error: (error) => {
        this.error = `Failed to load show: ${error.message}`;
        console.error('Error loading show:', error);
      }
    });
  }

  loadSchedule(): void {
    this.showService.getScheduleById(this.showId, this.scheduleId).subscribe({
      next: (schedule) => {
        this.schedule = schedule;
      },
      error: (error) => {
        this.error = `Failed to load schedule: ${error.message}`;
        console.error('Error loading schedule:', error);
      }
    });
  }

  loadSeatMap(): void {
    this.isLoading = true;
    this.showService.getSeatMap(this.showId, this.scheduleId).subscribe({
      next: (seatMap) => {
        this.seatMap = seatMap;
        this.isLoading = false;
      },
      error: (error) => {
        // If seat map doesn't exist yet, create a default one
        console.log('No existing seat map found, creating default');
        this.createDefaultSeatMap();
        this.isLoading = false;
      }
    });
  }

  createDefaultSeatMap(): void {
    // Create a default seat map with standard configuration
    const defaultSeatMap: SeatMap = {
      scheduleId: this.scheduleId,
      sections: [
        {
          name: 'Main Floor',
          rows: this.generateDefaultRows(10, 15, 'STANDARD', 1.0),
          priceCategory: 'STANDARD',
          priceMultiplier: 1.0
        },
        {
          name: 'Premium',
          rows: this.generateDefaultRows(5, 10, 'PREMIUM', 1.5),
          priceCategory: 'PREMIUM',
          priceMultiplier: 1.5
        },
        {
          name: 'VIP',
          rows: this.generateDefaultRows(3, 8, 'VIP', 2.0),
          priceCategory: 'VIP',
          priceMultiplier: 2.0
        }
      ],
      rows: 18, // Total rows across all sections
      columns: 15, // Maximum number of seats in a row
      layout: JSON.stringify({
        // Default layout configuration
        orientation: 'landscape',
        stagePosition: 'top',
        aisleSeats: [5, 10]
      })
    };
    
    this.seatMap = defaultSeatMap;
  }

  generateDefaultRows(numRows: number, seatsPerRow: number, category: 'STANDARD' | 'PREMIUM' | 'VIP', priceMultiplier: number): SeatMapRow[] {
    const rows: SeatMapRow[] = [];
    
    for (let i = 0; i < numRows; i++) {
      const rowLabel = String.fromCharCode(65 + i); // A, B, C, ...
      const seats: Seat[] = [];
      
      for (let j = 1; j <= seatsPerRow; j++) {
        seats.push({
          seatNumber: j,
          status: 'AVAILABLE',
          price: this.schedule ? this.schedule.basePrice * priceMultiplier : 10 * priceMultiplier,
          category: category
        });
      }
      
      rows.push({
        rowNumber: i + 1,
        rowLabel: rowLabel,
        seats: seats
      });
    }
    
    return rows;
  }

  toggleEditMode(): void {
    this.editMode = !this.editMode;
    this.selectedSeats = [];
  }

  selectSection(section: SeatMapSection): void {
    this.selectedSection = section;
  }

  selectSeat(seat: Seat): void {
    if (!this.editMode) return;
    
    const index = this.selectedSeats.findIndex(s => s === seat);
    if (index === -1) {
      this.selectedSeats.push(seat);
    } else {
      this.selectedSeats.splice(index, 1);
    }
  }

  isSeatSelected(seat: Seat): boolean {
    return this.selectedSeats.includes(seat);
  }

  getSeatClass(seat: Seat): string {
    if (this.isSeatSelected(seat)) {
      return 'seat selected';
    }
    
    switch (seat.status) {
      case 'AVAILABLE':
        return `seat available ${seat.category.toLowerCase()}`;
      case 'RESERVED':
        return 'seat reserved';
      case 'SOLD':
        return 'seat sold';
      case 'DISABLED':
        return 'seat disabled';
      default:
        return 'seat';
    }
  }

  updateSelectedSeats(): void {
    if (this.selectedSeats.length === 0) return;
    
    // Update the category and price for all selected seats
    this.selectedSeats.forEach(seat => {
      seat.category = this.selectedSeatCategory;
      seat.price = this.schedule ? this.schedule.basePrice * this.selectedPriceMultiplier : 10 * this.selectedPriceMultiplier;
    });
    
    this.selectedSeats = [];
    this.success = 'Seats updated successfully!';
    
    // Clear success message after 3 seconds
    setTimeout(() => {
      this.success = '';
    }, 3000);
  }

  disableSelectedSeats(): void {
    if (this.selectedSeats.length === 0) return;
    
    this.selectedSeats.forEach(seat => {
      seat.status = seat.status === 'DISABLED' ? 'AVAILABLE' : 'DISABLED';
    });
    
    this.selectedSeats = [];
    this.success = 'Seats updated successfully!';
    
    // Clear success message after 3 seconds
    setTimeout(() => {
      this.success = '';
    }, 3000);
  }

  saveSeatMap(): void {
    if (!this.seatMap) return;
    
    this.isSaving = true;
    this.error = '';
    this.success = '';
    
    this.showService.updateSeatMap(this.showId, this.scheduleId, this.seatMap).subscribe({
      next: (updatedSeatMap) => {
        this.seatMap = updatedSeatMap;
        this.success = 'Seat map saved successfully!';
        this.isSaving = false;
        this.editMode = false;
      },
      error: (error) => {
        this.error = `Failed to save seat map: ${error.message}`;
        console.error('Error saving seat map:', error);
        this.isSaving = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/organizer/shows', this.showId, 'schedules']);
  }
}