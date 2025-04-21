import { Component, OnInit } from '@angular/core';
import { ShowService } from '../../services/show.service';
import { OrganizerService, SalesReport } from '../../services/organizer.service';
import { Show } from '../../models/show.model';

import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-organizer-dashboard',
  templateUrl: './organizer-dashboard.component.html',
  styleUrls: ['./organizer-dashboard.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, DecimalPipe]
})
export class OrganizerDashboardComponent implements OnInit {
  shows: Show[] = [];
  salesReport: SalesReport | null = null;
  selectedShow: Show | null = null;
  showForm = false;

  constructor(
    private showService: ShowService,
    private organizerService: OrganizerService
  ) {}

  ngOnInit(): void {
    this.loadShows();
    this.loadSalesReport();
  }

  loadShows(): void {
    this.showService.getAllShows().subscribe({
      next: (shows) => this.shows = shows,
      error: (error) => console.error('Error loading shows:', error)
    });
  }

  loadSalesReport(): void {
    this.organizerService.getSalesReport().subscribe({
      next: (report) => this.salesReport = report,
      error: (error) => console.error('Error loading sales report:', error)
    });
  }

  onCreateShow(): void {
    this.selectedShow = null;
    this.showForm = true;
  }

  onEditShow(show: Show): void {
    this.selectedShow = show;
    this.showForm = true;
  }

  onDeleteShow(show: Show): void {
    if (!show.id) return;
    
    if (confirm(`Are you sure you want to delete ${show.title}?`)) {
      this.showService.deleteShow(show.id).subscribe({
        next: () => {
          this.shows = this.shows.filter(s => s.id !== show.id);
          this.loadSalesReport();
        },
        error: (error) => console.error('Error deleting show:', error)
      });
    }
  }

  onShowSaved(show: Show): void {
    if (this.selectedShow) {
      // Update existing show
      if (!show.id) return;
      this.showService.updateShow(show.id, show).subscribe({
        next: (updatedShow) => {
          const index = this.shows.findIndex(s => s.id === show.id);
          if (index !== -1) {
            this.shows[index] = updatedShow;
          }
          this.showForm = false;
          this.selectedShow = null;
          this.loadSalesReport();
        },
        error: (error) => console.error('Error updating show:', error)
      });
    } else {
      // Create new show
      this.showService.createShow(show).subscribe({
        next: (newShow) => {
          this.shows.push(newShow);
          this.showForm = false;
          this.loadSalesReport();
        },
        error: (error) => console.error('Error creating show:', error)
      });
    }
  }

  onCancelForm(): void {
    this.showForm = false;
    this.selectedShow = null;
  }
}