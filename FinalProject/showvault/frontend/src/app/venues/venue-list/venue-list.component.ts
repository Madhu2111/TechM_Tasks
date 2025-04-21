import { Component, OnInit } from '@angular/core';
import { VenueService } from '../venue.service';
import { Venue } from '../venue.model';

@Component({
  selector: 'app-venue-list',
  templateUrl: './venue-list.component.html',
  styleUrls: ['./venue-list.component.css']
})
export class VenueListComponent implements OnInit {
  venues: Venue[] = [];
  currentVenueId: number = 0;
  
  constructor(private venueService: VenueService) {}

  ngOnInit() {
    this.loadVenues();
  }

  loadVenues() {
    this.venueService.getAllVenues().subscribe({
      next: (data) => this.venues = data,
      error: (err) => console.error('Error loading venues:', err)
    });
  }

  deleteVenue(id: number) {
    this.venueService.deleteVenue(id).subscribe({
      next: () => this.venues = this.venues.filter(v => v.id !== id),
      error: (err) => console.error('Error deleting venue:', err)
    });
  }
}