import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ShowService } from '../../services/show.service';
import { Show } from '../../models/show.model';

interface ShowSchedule {
  id?: number;
  showDate: string;
  showTime?: string;
  venue?: { name: string };
}

interface TestShow {
  id: number;
  title: string;
  type: 'Movie' | 'Theater' | 'Concert' | 'Event' | 'Other';
  schedules: ShowSchedule[];
}

@Component({
  selector: 'app-booking-test',
  templateUrl: './booking-test.component.html',
  styleUrls: ['./booking-test.component.css']
})
export class BookingTestComponent implements OnInit {
  showId = 1;
  scheduleId = 1;
  quantity = 2;
  
  availableShows: TestShow[] = [];
  selectedShow: TestShow | null = null;
  loading = false;
  error = '';

  constructor(
    private router: Router,
    private showService: ShowService
  ) { }

  ngOnInit(): void {
    this.loadAvailableShows();
  }

  loadAvailableShows(): void {
    this.loading = true;
    this.error = '';
    
    this.showService.getAllShows().subscribe({
      next: (shows) => {
        this.availableShows = shows.map(show => ({
          id: show.id || 0,
          title: show.title,
          type: show.type || 'Other',
          schedules: (show.schedules || []).map(schedule => ({
            id: schedule.id || 0,
            showDate: schedule.showDate || '',
            showTime: schedule.showTime || '',
            venue: { name: schedule.venue?.name || 'Unknown Venue' }
          }))
        }));
        
        if (this.availableShows.length > 0) {
          this.selectedShow = this.availableShows[0];
          this.showId = this.selectedShow.id;
          
          if (this.selectedShow.schedules && this.selectedShow.schedules.length > 0 && this.selectedShow.schedules[0].id) {
            this.scheduleId = this.selectedShow.schedules[0].id || 0;
          } else {
            this.scheduleId = 0;
          }
        }
        
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading shows:', err);
        this.error = 'Failed to load available shows. Please try again.';
        this.loading = false;
      }
    });
  }

  onShowChange(): void {
    this.selectedShow = this.availableShows.find(show => show.id === this.showId) || null;
    
    if (this.selectedShow && this.selectedShow.schedules && this.selectedShow.schedules.length > 0) {
      this.scheduleId = this.selectedShow.schedules[0].id || 0;
    } else {
      this.scheduleId = 0;
    }
  }

  startBookingFlow(): void {
    if (!this.showId || !this.scheduleId) {
      this.error = 'Please select a valid show and schedule';
      return;
    }
    
    if (this.quantity < 1 || this.quantity > 10) {
      this.error = 'Please select a quantity between 1 and 10';
      return;
    }
    
    this.error = '';
    this.router.navigate(['/booking/select-seats'], { 
      queryParams: { 
        showId: this.showId,
        scheduleId: this.scheduleId,
        quantity: this.quantity
      }
    });
  }
  
  getScheduleDisplay(schedule: ShowSchedule): string {
    return `${schedule.showDate} at ${schedule.showTime} - ${schedule.venue?.name || 'Unknown Venue'}`;
  }
}