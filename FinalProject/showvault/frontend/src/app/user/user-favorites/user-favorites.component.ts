import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ShowService } from '../../services/show.service';
import { Show, Venue } from '../../models/show.model';

@Component({
  selector: 'app-user-favorites',
  templateUrl: './user-favorites.component.html',
  styleUrls: ['./user-favorites.component.css']
})
export class UserFavoritesComponent implements OnInit {
  favorites: Show[] = [];
  isLoading = true;
  error = '';
  successMessage = '';
  
  // For filtering and sorting
  searchQuery = '';
  sortOrder = 'date-asc';
  categoryFilter = 'all';
  
  // For pagination
  currentPage = 1;
  itemsPerPage = 6;
  totalItems = 0;

  constructor(
    private showService: ShowService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadFavorites();
  }

  loadFavorites(): void {
    this.isLoading = true;
    this.showService.getUserFavorites().subscribe({
      next: (shows) => {
        this.favorites = shows;
        this.totalItems = shows.length;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to load favorites. Please try again.';
        this.isLoading = false;
        console.error('Error loading favorites:', error);
      }
    });
  }

  removeFromFavorites(showId: number): void {
    if (!showId) return;
    
    this.showService.removeFromFavorites(showId).subscribe({
      next: () => {
        this.favorites = this.favorites.filter(show => show.id !== showId);
        this.totalItems = this.favorites.length;
        this.successMessage = 'Show removed from favorites.';
        
        // Clear success message after 3 seconds
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      error: (error) => {
        this.error = 'Failed to remove from favorites. Please try again.';
        console.error('Error removing from favorites:', error);
        
        // Clear error message after 3 seconds
        setTimeout(() => {
          this.error = '';
        }, 3000);
      }
    });
  }

  viewShowDetails(showId: number): void {
    this.router.navigate(['/shows', showId]);
  }

  bookShow(showId: number): void {
    this.router.navigate(['/booking', showId]);
  }

  applyFilters(): void {
    // Reset to first page when filters change
    this.currentPage = 1;
  }

  get filteredFavorites(): Show[] {
    let filtered = [...this.favorites];
    
    // Apply category filter
    if (this.categoryFilter !== 'all') {
      filtered = filtered.filter(show => show.type === this.categoryFilter);
    }
    
    // Apply search filter
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(show => 
        show.title.toLowerCase().includes(query) ||
        (show.venue || '').toLowerCase().includes(query) ||
        (show.description || '').toLowerCase().includes(query)
      );
    }
    
    // Apply sorting
    if (this.sortOrder === 'date-asc') {
      filtered.sort((a, b) => new Date(a.date || 0).getTime() - new Date(b.date || 0).getTime());
    } else if (this.sortOrder === 'date-desc') {
      filtered.sort((a, b) => new Date(b.date || 0).getTime() - new Date(a.date || 0).getTime());
    } else if (this.sortOrder === 'title-asc') {
      filtered.sort((a, b) => a.title.localeCompare(b.title));
    } else if (this.sortOrder === 'title-desc') {
      filtered.sort((a, b) => b.title.localeCompare(a.title));
    } else if (this.sortOrder === 'price-asc') {
      filtered.sort((a, b) => (a.price || 0) - (b.price || 0));
    } else if (this.sortOrder === 'price-desc') {
      filtered.sort((a, b) => (b.price || 0) - (a.price || 0));
    }
    
    return filtered;
  }

  get paginatedFavorites(): Show[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredFavorites.slice(startIndex, startIndex + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredFavorites.length / this.itemsPerPage);
  }

  changePage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  getFormattedDate(dateString: string | Date): string {
    if (!dateString) return 'TBA';
    
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  isShowInPast(dateString: string | Date): boolean {
    if (!dateString) return false;
    
    const showDate = new Date(dateString);
    const now = new Date();
    
    return showDate < now;
  }
}