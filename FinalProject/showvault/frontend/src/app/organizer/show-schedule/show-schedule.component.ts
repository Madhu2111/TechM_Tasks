import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ShowService } from '../../services/show.service';
import { Show, ShowSchedule, Venue } from '../../models/show.model';
import { CommonModule, DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-show-schedule',
  templateUrl: './show-schedule.component.html',
  styleUrls: ['./show-schedule.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, DecimalPipe]
})
export class ShowScheduleComponent implements OnInit {
  showId!: number;
  show: Show | null = null;
  schedules: ShowSchedule[] = [];
  venues: Venue[] = [];
  
  scheduleForm: FormGroup;
  selectedSchedule: ShowSchedule | null = null;
  isLoading = true;
  isSubmitting = false;
  error = '';
  success = '';
  showForm = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private showService: ShowService,
    private fb: FormBuilder
  ) {
    this.scheduleForm = this.fb.group({
      showDate: ['', Validators.required],
      showTime: ['', Validators.required],
      basePrice: ['', [Validators.required, Validators.min(0)]],
      venue: ['', Validators.required],
      totalSeats: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.showId = +id;
        this.loadShow();
        this.loadSchedules();
        this.loadVenues();
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

  loadSchedules(): void {
    this.isLoading = true;
    this.showService.getSchedules(this.showId).subscribe({
      next: (schedules) => {
        this.schedules = schedules;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = `Failed to load schedules: ${error.message}`;
        console.error('Error loading schedules:', error);
        this.isLoading = false;
      }
    });
  }

  loadVenues(): void {
    this.showService.getVenues().subscribe({
      next: (venues) => {
        this.venues = venues;
      },
      error: (error) => {
        this.error = `Failed to load venues: ${error.message}`;
        console.error('Error loading venues:', error);
      }
    });
  }

  openScheduleForm(schedule?: ShowSchedule): void {
    this.selectedSchedule = schedule || null;
    
    if (schedule) {
      this.scheduleForm.patchValue({
        showDate: schedule.showDate,
        showTime: schedule.showTime,
        basePrice: schedule.basePrice,
        venue: schedule.venue?.id,
        totalSeats: schedule.totalSeats
      });
    } else {
      this.scheduleForm.reset();
    }
    
    this.showForm = true;
  }

  closeScheduleForm(): void {
    this.showForm = false;
    this.selectedSchedule = null;
    this.scheduleForm.reset();
  }

  onSubmit(): void {
    if (this.scheduleForm.invalid) {
      return;
    }
    
    this.isSubmitting = true;
    this.error = '';
    this.success = '';
    
    const formValue = this.scheduleForm.value;
    const selectedVenue = this.venues.find(v => v.id === +formValue.venue);
    
    const scheduleData: ShowSchedule = {
      showId: this.showId,
      showDate: formValue.showDate,
      showTime: formValue.showTime,
      basePrice: +formValue.basePrice,
      venue: selectedVenue,
      totalSeats: +formValue.totalSeats,
      availableSeats: +formValue.totalSeats
    };
    
    if (this.selectedSchedule) {
      // Update existing schedule
      this.showService.updateSchedule(this.showId, this.selectedSchedule.id!, scheduleData).subscribe({
        next: (updatedSchedule) => {
          const index = this.schedules.findIndex(s => s.id === this.selectedSchedule!.id);
          if (index !== -1) {
            this.schedules[index] = updatedSchedule;
          }
          this.success = 'Schedule updated successfully!';
          this.isSubmitting = false;
          this.closeScheduleForm();
        },
        error: (error) => {
          this.error = `Failed to update schedule: ${error.message}`;
          console.error('Error updating schedule:', error);
          this.isSubmitting = false;
        }
      });
    } else {
      // Create new schedule
      this.showService.addSchedule(this.showId, scheduleData).subscribe({
        next: (newSchedule) => {
          this.schedules.push(newSchedule);
          this.success = 'Schedule created successfully!';
          this.isSubmitting = false;
          this.closeScheduleForm();
        },
        error: (error) => {
          this.error = `Failed to create schedule: ${error.message}`;
          console.error('Error creating schedule:', error);
          this.isSubmitting = false;
        }
      });
    }
  }

  deleteSchedule(schedule: ShowSchedule): void {
    if (confirm(`Are you sure you want to delete this schedule?`)) {
      this.showService.deleteSchedule(this.showId, schedule.id!).subscribe({
        next: () => {
          this.schedules = this.schedules.filter(s => s.id !== schedule.id);
          this.success = 'Schedule deleted successfully!';
        },
        error: (error) => {
          this.error = `Failed to delete schedule: ${error.message}`;
          console.error('Error deleting schedule:', error);
        }
      });
    }
  }

  configureSeating(schedule: ShowSchedule): void {
    this.router.navigate(['/organizer/shows', this.showId, 'schedules', schedule.id, 'venue-mapping']);
  }

  formatDateTime(date: string, time?: string): string {
    if (!date) return 'N/A';
    
    const dateObj = new Date(date);
    const formattedDate = dateObj.toLocaleDateString('en-US', {
      weekday: 'short',
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
    
    if (time) {
      return `${formattedDate} at ${time}`;
    }
    
    return formattedDate;
  }

  getStatusClass(status?: string): string {
    switch (status) {
      case 'SCHEDULED':
        return 'badge bg-success';
      case 'CANCELLED':
        return 'badge bg-danger';
      case 'COMPLETED':
        return 'badge bg-secondary';
      default:
        return 'badge bg-primary';
    }
  }
}