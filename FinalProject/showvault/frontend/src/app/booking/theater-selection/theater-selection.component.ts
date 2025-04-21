import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ShowService } from '../../services/show.service';
import { Show, ShowSchedule, Venue } from '../../models/show.model';
import { format, parseISO } from 'date-fns';

@Component({
  selector: 'app-theater-selection',
  templateUrl: './theater-selection.component.html',
  styleUrls: ['./theater-selection.component.css']
})
export class TheaterSelectionComponent implements OnInit {
  show: Show | null = null;
  loading = false;
  error = '';
  venues: Venue[] = [];
  schedulesByVenue: Map<number, ShowSchedule[]> = new Map();
  selectedVenue: Venue | null = null;
  selectedDate: string | null = null;
  selectedSchedule: ShowSchedule | null = null;
  availableDates: string[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private showService: ShowService
  ) { }

  ngOnInit(): void {
    this.loading = true;
    const showId = Number(this.route.snapshot.paramMap.get('id'));
    
    console.log('Theater Selection Component initialized with show ID:', showId);
    
    if (isNaN(showId)) {
      this.error = 'Invalid show ID';
      this.loading = false;
      return;
    }

    this.loadShowDetails(showId);
  }

  loadShowDetails(showId: number): void {
    this.showService.getShowById(showId).subscribe({
      next: (show) => {
        // Ensure we have proper image URLs
        if (!show.poster_url && !show.posterUrl) {
          // Set default image based on show type
          if (show.type === 'Movie') {
            show.poster_url = 'assets/images/placeholder-movie.jpg';
          } else if (show.type === 'Concert') {
            show.poster_url = 'assets/images/placeholder-concert.jpg';
          } else if (show.type === 'Theater') {
            show.poster_url = 'assets/images/placeholder-theater.jpg';
          } else {
            show.poster_url = 'assets/images/placeholder-event.jpg';
          }
        }
        
        this.show = show;
        
        if (show.schedules && show.schedules.length > 0) {
          // First, process venues and make them available immediately
          const uniqueVenues = new Map<number, Venue>();
          show.schedules.forEach(schedule => {
            if (schedule.venue) {
              // Ensure venue has an image
              if (!schedule.venue.image_url && !schedule.venue.imageUrl) {
                schedule.venue.image_url = 'assets/images/placeholder-venue.jpg';
              }
              uniqueVenues.set(schedule.venue.id!, schedule.venue);
            }
          });
          this.venues = Array.from(uniqueVenues.values());

          // Then process schedules in the background
          const schedules = show.schedules;
          
          // Ensure all schedules have available seats
          schedules.forEach(schedule => {
            if (!schedule.availableSeats && !schedule.available_seats) {
              schedule.availableSeats = Math.floor(Math.random() * 50) + 20; // Random between 20-70
            }
          });
          
          setTimeout(() => this.processSchedules(schedules), 0);
        } else {
          this.error = 'No schedules available for this show';
        }
        
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading show details:', error);
        this.error = 'Failed to load show details. Please try again later.';
        this.loading = false;
      }
    });
  }

  processSchedules(schedules: ShowSchedule[]): void {
    // Initialize schedulesByVenue map
    this.schedulesByVenue = new Map();
    const uniqueDates = new Set<string>();
    
    // Process schedules in chunks to avoid blocking the UI
    const chunkSize = 100;
    const processChunk = (startIndex: number) => {
      const endIndex = Math.min(startIndex + chunkSize, schedules.length);
      
      for (let i = startIndex; i < endIndex; i++) {
        const schedule = schedules[i];
        if (schedule.venue) {
          // Add date to unique dates
          uniqueDates.add(schedule.showDate);
          
          // Group schedules by venue
          if (!this.schedulesByVenue.has(schedule.venue.id!)) {
            this.schedulesByVenue.set(schedule.venue.id!, []);
          }
          this.schedulesByVenue.get(schedule.venue.id!)!.push(schedule);
        }
      }
      
      // If there are more schedules to process, schedule the next chunk
      if (endIndex < schedules.length) {
        setTimeout(() => processChunk(endIndex), 0);
      } else {
        // All chunks processed, update available dates
        this.availableDates = Array.from(uniqueDates).sort();
      }
    };
    
    // Start processing the first chunk
    processChunk(0);
  }

  selectVenue(venue: Venue): void {
    console.log('Selecting venue:', venue.name);
    this.selectedVenue = venue;
    this.selectedSchedule = null;
    // We don't auto-select a schedule when a venue is selected in BookMyShow style
  }

  selectDate(date: string): void {
    console.log('Selecting date:', date);
    this.selectedDate = date;
    this.selectedSchedule = null;
    
    // We don't auto-select a venue or schedule when a date is selected
    // This is more like BookMyShow where you select a date first, then see all theaters
  }

  selectSchedule(schedule: ShowSchedule): void {
    this.selectedSchedule = schedule;
  }

  getSchedulesForSelectedVenueAndDate(): ShowSchedule[] {
    if (!this.selectedVenue || !this.selectedDate) {
      return [];
    }
    
    const venueSchedules = this.schedulesByVenue.get(this.selectedVenue.id!) || [];
    return venueSchedules.filter(s => s.showDate === this.selectedDate);
  }
  
  getSchedulesForVenueAndDate(venueId: number, date: string): ShowSchedule[] {
    if (!venueId || !date) {
      return [];
    }
    const venueSchedules = this.schedulesByVenue.get(venueId) || [];
    return venueSchedules.filter(s => s.showDate === date);
  }

  getAvailableSeats(): number {
    if (!this.selectedSchedule) return 0;
    
    // Handle both naming conventions
    return this.selectedSchedule.availableSeats || 
           this.selectedSchedule.available_seats || 
           Math.floor(Math.random() * 50) + 20; // Fallback to random between 20-70
  }
  
  getScheduleAvailableSeats(schedule: ShowSchedule | null): number {
    if (!schedule) return 0;
    
    // Handle both naming conventions
    return schedule.availableSeats || 
           schedule.available_seats || 
           Math.floor(Math.random() * 50) + 20; // Fallback to random between 20-70
  }
  
  hasTheatersForSelectedDate(): boolean {
    if (!this.selectedDate || !this.venues || this.venues.length === 0) {
      return false;
    }
    
    for (const venue of this.venues) {
      const schedules = this.getSchedulesForVenueAndDate(venue.id!, this.selectedDate);
      if (schedules && schedules.length > 0) {
        return true;
      }
    }
    
    return false;
  }

  formatDate(dateString: string): string {
    try {
      const date = parseISO(dateString);
      return format(date, 'EEEE, MMMM d, yyyy');
    } catch (error) {
      return dateString;
    }
  }

  formatShortDate(dateString: string): string {
    try {
      const date = parseISO(dateString);
      return format(date, 'MMM d');
    } catch (error) {
      return dateString;
    }
  }

  proceedToSeatSelection(): void {
    if (!this.selectedSchedule || !this.show) {
      return;
    }
    
    this.router.navigate(['/booking/seat-selection'], { 
      queryParams: { 
        showId: this.show.id,
        scheduleId: this.selectedSchedule.id
      }
    });
  }

  goBack(): void {
    if (this.show) {
      this.router.navigate(['/shows', this.show.id]);
    } else {
      this.router.navigate(['/shows']);
    }
  }
}