import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ShowService } from '../../services/show.service';

interface Rating {
  id: number;
  showId: number;
  showTitle: string;
  showImageUrl?: string;
  rating: number;
  review?: string;
  date: Date;
  showDate: Date;
  venue: string;
}

@Component({
  selector: 'app-user-ratings',
  templateUrl: './user-ratings.component.html',
  styleUrls: ['./user-ratings.component.css']
})
export class UserRatingsComponent implements OnInit {
  ratings: Rating[] = [];
  isLoading = true;
  error = '';
  successMessage = '';
  
  // For editing ratings
  editRatingForm: FormGroup;
  selectedRatingId: number | null = null;
  showEditModal = false;
  
  // For filtering and sorting
  sortOrder = 'newest';
  ratingFilter = 'all';
  
  // For pagination
  currentPage = 1;
  itemsPerPage = 5;
  totalItems = 0;

  constructor(
    private showService: ShowService,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.editRatingForm = this.fb.group({
      rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      review: ['', [Validators.maxLength(500)]]
    });
  }

  ngOnInit(): void {
    this.loadRatings();
  }

  loadRatings(): void {
    this.isLoading = true;
    this.showService.getUserRatings().subscribe({
      next: (ratings) => {
        this.ratings = ratings;
        this.totalItems = ratings.length;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to load ratings. Please try again.';
        this.isLoading = false;
        console.error('Error loading ratings:', error);
      }
    });
  }

  deleteRating(ratingId: number): void {
    if (confirm('Are you sure you want to delete this rating? This action cannot be undone.')) {
      this.showService.deleteRating(ratingId).subscribe({
        next: () => {
          this.ratings = this.ratings.filter(rating => rating.id !== ratingId);
          this.totalItems = this.ratings.length;
          this.successMessage = 'Rating deleted successfully.';
          
          // Clear success message after 3 seconds
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (error) => {
          this.error = 'Failed to delete rating. Please try again.';
          console.error('Error deleting rating:', error);
          
          // Clear error message after 3 seconds
          setTimeout(() => {
            this.error = '';
          }, 3000);
        }
      });
    }
  }

  openEditModal(rating: Rating): void {
    this.selectedRatingId = rating.id;
    this.editRatingForm.patchValue({
      rating: rating.rating,
      review: rating.review || ''
    });
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.selectedRatingId = null;
  }

  updateRating(): void {
    if (this.editRatingForm.valid && this.selectedRatingId) {
      const updatedRating = {
        rating: this.editRatingForm.value.rating,
        review: this.editRatingForm.value.review
      };
      
      this.showService.updateRating(this.selectedRatingId, updatedRating).subscribe({
        next: (response) => {
          // Update the rating in the local array
          const index = this.ratings.findIndex(r => r.id === this.selectedRatingId);
          if (index !== -1) {
            this.ratings[index] = {
              ...this.ratings[index],
              rating: updatedRating.rating,
              review: updatedRating.review
            };
          }
          
          this.successMessage = 'Rating updated successfully.';
          this.closeEditModal();
          
          // Clear success message after 3 seconds
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (error) => {
          this.error = 'Failed to update rating. Please try again.';
          console.error('Error updating rating:', error);
          
          // Clear error message after 3 seconds
          setTimeout(() => {
            this.error = '';
          }, 3000);
        }
      });
    }
  }

  viewShow(showId: number): void {
    this.router.navigate(['/shows', showId]);
  }

  applyFilters(): void {
    // Reset to first page when filters change
    this.currentPage = 1;
  }

  get filteredRatings(): Rating[] {
    let filtered = [...this.ratings];
    
    // Apply rating filter
    if (this.ratingFilter !== 'all') {
      const ratingValue = parseInt(this.ratingFilter);
      filtered = filtered.filter(rating => rating.rating === ratingValue);
    }
    
    // Apply sorting
    if (this.sortOrder === 'newest') {
      filtered.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
    } else if (this.sortOrder === 'oldest') {
      filtered.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
    } else if (this.sortOrder === 'highest') {
      filtered.sort((a, b) => b.rating - a.rating);
    } else if (this.sortOrder === 'lowest') {
      filtered.sort((a, b) => a.rating - b.rating);
    }
    
    return filtered;
  }

  get paginatedRatings(): Rating[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredRatings.slice(startIndex, startIndex + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredRatings.length / this.itemsPerPage);
  }

  changePage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  getFormattedDate(dateString: string | Date): string {
    if (!dateString) return 'N/A';
    
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  getStarArray(rating: number): number[] {
    return Array(rating).fill(0);
  }

  getEmptyStarArray(rating: number): number[] {
    return Array(5 - rating).fill(0);
  }
}