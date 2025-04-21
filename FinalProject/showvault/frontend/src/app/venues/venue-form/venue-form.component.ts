import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { VenueService } from '../venue.service';
import { Venue } from '../venue.model';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-venue-form',
  templateUrl: './venue-form.component.html',
  styleUrls: ['./venue-form.component.css']
})
export class VenueFormComponent implements OnInit {
  venueForm: FormGroup;
  isEditMode = false;
  currentVenueId: number;
  seatCategories: any[] = [];

  constructor(
    private fb: FormBuilder,
    private venueService: VenueService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.venueForm = this.fb.group({
      name: ['', Validators.required],
      location: ['', Validators.required],
      capacity: ['', [Validators.required, Validators.min(1)]],
      seatCategories: this.fb.array([])
    });
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.currentVenueId = params['id'];
        this.loadVenueDetails();
      }
    });
  }

  loadVenueDetails() {
    this.venueService.getVenue(this.currentVenueId).subscribe(venue => {
      this.venueForm.patchValue(venue);
      this.seatCategories = venue.seatCategories;
    });
  }

  onSubmit() {
    if (this.venueForm.valid) {
      const venueData: Venue = {
        ...this.venueForm.value,
        seatCategories: this.seatCategories
      };

      if (this.isEditMode) {
        this.venueService.updateVenue(this.currentVenueId, venueData)
          .subscribe(() => this.router.navigate(['/venues']));
      } else {
        this.venueService.createVenue(venueData)
          .subscribe(() => this.router.navigate(['/venues']));
      }
    }
  }

  addSeatCategory() {
    this.seatCategories.push({ type: '', capacity: 0 });
  }

  removeSeatCategory(index: number) {
    this.seatCategories.splice(index, 1);
  }
}