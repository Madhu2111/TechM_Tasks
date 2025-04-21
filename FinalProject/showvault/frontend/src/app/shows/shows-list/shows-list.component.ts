import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ShowService } from '../../services/show.service';
import { ImageService } from '../../services/image.service';
import { DataTransformerService } from '../../services/data-transformer.service';
import { Show, ShowFilter } from '../../models/show.model';
import { debounceTime, distinctUntilChanged, finalize } from 'rxjs/operators';
import { format, parseISO } from 'date-fns';
declare var bootstrap: any;

@Component({
  selector: 'app-shows-list',
  templateUrl: './shows-list.component.html',
  styleUrls: ['./shows-list.component.css']
})
export class ShowsListComponent implements OnInit {
  shows: Show[] = [];
  loading = false;
  error = '';
  totalShows = 0;
  totalPages = 1;
  currentPage = 1;
  pageSize = 12;
  
  // Filter controls
  searchQuery = '';
  selectedCategory = 'all';
  dateFromControl = new FormControl('');
  dateToControl = new FormControl('');
  priceMinControl = new FormControl('');
  priceMaxControl = new FormControl('');
  sortOption = 'relevance';
  
  // Category options
  categories = [
    { label: 'All Shows', value: 'all', icon: 'bi bi-grid-3x3-gap' },
    { label: 'Movies', value: 'Movie', icon: 'bi bi-film' },
    { label: 'Theater', value: 'Theater', icon: 'bi bi-mask' },
    { label: 'Concerts', value: 'Concert', icon: 'bi bi-music-note-beamed' },
    { label: 'Events', value: 'Event', icon: 'bi bi-calendar-event' }
  ];

  constructor(
    private showService: ShowService,
    private route: ActivatedRoute,
    private router: Router,
    private imageService: ImageService,
    private dataTransformerService: DataTransformerService
  ) {}

  ngOnInit(): void {
    // Initialize dropdowns
    setTimeout(() => {
      const dropdownElementList = document.querySelectorAll('.dropdown-toggle');
      dropdownElementList.forEach(dropdownToggleEl => {
        new bootstrap.Dropdown(dropdownToggleEl);
      });
    }, 500);
    
    // Get query parameters
    this.route.queryParams.subscribe(params => {
      // Reset filters first
      this.resetFiltersWithoutReload();
      
      // Apply filters from URL parameters
      if (params['category']) {
        this.selectedCategory = params['category'];
      }
      
      if (params['search']) {
        this.searchQuery = params['search'];
      }
      
      if (params['dateFrom']) {
        this.dateFromControl.setValue(params['dateFrom']);
      }
      
      if (params['dateTo']) {
        this.dateToControl.setValue(params['dateTo']);
      }
      
      if (params['priceMin']) {
        this.priceMinControl.setValue(params['priceMin']);
      }
      
      if (params['priceMax']) {
        this.priceMaxControl.setValue(params['priceMax']);
      }
      
      if (params['sort']) {
        this.sortOption = params['sort'];
      }
      
      if (params['page']) {
        this.currentPage = parseInt(params['page'], 10) || 1;
      }
      
      // Load shows with applied filters
      this.loadShows();
    });
  }

  loadShows(): void {
    this.loading = true;
    this.error = '';
    
    // Build filter object
    const filter: ShowFilter = {
      page: this.currentPage,
      pageSize: this.pageSize,
      sort: this.sortOption
    };
    
    // Add category filter
    if (this.selectedCategory !== 'all') {
      filter.type = this.selectedCategory;
    }
    
    // Add search filter
    if (this.searchQuery) {
      filter.search = this.searchQuery;
    }
    
    // Add date filters
    if (this.dateFromControl.value) {
      filter.dateFrom = this.dateFromControl.value;
    }
    
    if (this.dateToControl.value) {
      filter.dateTo = this.dateToControl.value;
    }
    
    // Add price filters
    if (this.priceMinControl.value) {
      filter.priceMin = parseFloat(this.priceMinControl.value);
    }
    
    if (this.priceMaxControl.value) {
      filter.priceMax = parseFloat(this.priceMaxControl.value);
    }
    
    console.log('Filtering shows with:', filter);
    
    // Call API with filters
    this.showService.filterShows(filter)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (response) => {
          console.log('Received show response:', response);
          
          if (Array.isArray(response)) {
            // Handle case where API returns just an array
            this.shows = response;
            this.totalShows = response.length;
            this.totalPages = 1;
            
            // Log the shows for debugging
            console.log(`Loaded ${this.shows.length} shows:`, this.shows);
            
            // Enhance show images if needed
            this.enhanceShowImages(this.shows);
          } else if (response && typeof response === 'object') {
            // Handle case where API returns pagination object
            this.shows = response.content || [];
            this.totalShows = response.totalElements || this.shows.length;
            this.totalPages = response.totalPages || 1;
            
            // Log the shows for debugging
            console.log(`Loaded ${this.shows.length} shows from paginated response:`, this.shows);
            
            // Enhance show images if needed
            this.enhanceShowImages(this.shows);
          } else {
            console.warn('Received empty or invalid response');
            this.shows = [];
            this.totalShows = 0;
            this.totalPages = 1;
          }
        },
        error: (error) => {
          console.error('Error loading shows:', error);
          this.error = 'Failed to load shows. Please try again later.';
          this.shows = [];
        }
      });
  }
  
  // Enhanced image handling for shows
  enhanceShowImages(shows: Show[]): void {
    shows.forEach(show => {
      if (!show.posterUrl && !show.imageUrl) {
        // For movies, try to get a specific movie poster
        if (show.type === 'Movie' || show.genre?.toLowerCase() === 'movie') {
          this.imageService.getSpecificMovieImage(show.title, show.type, show.genre).subscribe(imageUrl => {
            show.imageUrl = imageUrl;
          });
        } else {
          // For non-movies, use the existing image service
          show.imageUrl = this.getImageUrl(null, show.type || show.genre || 'Show');
        }
      }
    });
  }

  // Filter methods
  selectCategory(category: string): void {
    if (this.selectedCategory !== category) {
      this.selectedCategory = category;
      this.currentPage = 1;
      this.updateUrlAndLoadShows();
    }
  }
  
  applyFilters(): void {
    this.currentPage = 1;
    this.updateUrlAndLoadShows();
  }
  
  applyDateFilter(): void {
    // Close dropdown
    const dropdownElement = document.getElementById('dateFilterDropdown');
    if (dropdownElement) {
      const dropdown = bootstrap.Dropdown.getInstance(dropdownElement);
      if (dropdown) {
        dropdown.hide();
      }
    }
    
    this.currentPage = 1;
    this.updateUrlAndLoadShows();
  }
  
  applyPriceFilter(): void {
    // Close dropdown
    const dropdownElement = document.getElementById('priceFilterDropdown');
    if (dropdownElement) {
      const dropdown = bootstrap.Dropdown.getInstance(dropdownElement);
      if (dropdown) {
        dropdown.hide();
      }
    }
    
    this.currentPage = 1;
    this.updateUrlAndLoadShows();
  }
  
  applySorting(): void {
    this.updateUrlAndLoadShows();
  }
  
  // Clear filter methods
  clearCategoryFilter(): void {
    this.selectedCategory = 'all';
    this.currentPage = 1;
    this.updateUrlAndLoadShows();
  }
  
  clearSearchFilter(): void {
    this.searchQuery = '';
    this.currentPage = 1;
    this.updateUrlAndLoadShows();
  }
  
  clearDateFilter(): void {
    this.dateFromControl.setValue('');
    this.dateToControl.setValue('');
    this.currentPage = 1;
    this.updateUrlAndLoadShows();
  }
  
  clearPriceFilter(): void {
    this.priceMinControl.setValue('');
    this.priceMaxControl.setValue('');
    this.currentPage = 1;
    this.updateUrlAndLoadShows();
  }
  
  resetAllFilters(): void {
    this.resetFiltersWithoutReload();
    this.currentPage = 1;
    this.updateUrlAndLoadShows();
  }
  
  resetFiltersWithoutReload(): void {
    this.selectedCategory = 'all';
    this.searchQuery = '';
    this.dateFromControl.setValue('');
    this.dateToControl.setValue('');
    this.priceMinControl.setValue('');
    this.priceMaxControl.setValue('');
    this.sortOption = 'relevance';
  }
  
  // Helper methods
  hasActiveFilters(): boolean {
    return this.selectedCategory !== 'all' ||
           !!this.searchQuery ||
           !!this.dateFromControl.value ||
           !!this.dateToControl.value ||
           !!this.priceMinControl.value ||
           !!this.priceMaxControl.value;
  }
  
  getDateFilterLabel(): string {
    if (this.dateFromControl.value && this.dateToControl.value) {
      return `${this.formatDateShort(this.dateFromControl.value)} - ${this.formatDateShort(this.dateToControl.value)}`;
    } else if (this.dateFromControl.value) {
      return `From ${this.formatDateShort(this.dateFromControl.value)}`;
    } else if (this.dateToControl.value) {
      return `Until ${this.formatDateShort(this.dateToControl.value)}`;
    } else {
      return 'Any Date';
    }
  }
  
  getPriceFilterLabel(): string {
    if (this.priceMinControl.value && this.priceMaxControl.value) {
      return `$${this.priceMinControl.value} - $${this.priceMaxControl.value}`;
    } else if (this.priceMinControl.value) {
      return `From $${this.priceMinControl.value}`;
    } else if (this.priceMaxControl.value) {
      return `Up to $${this.priceMaxControl.value}`;
    } else {
      return 'Any Price';
    }
  }
  
  getCategoryLabel(value: string): string {
    const category = this.categories.find(c => c.value === value);
    return category ? category.label : 'All Shows';
  }
  
  formatDate(dateString: string | undefined | null): string {
    if (!dateString) {
      return 'TBA';
    }
    
    try {
      const date = parseISO(dateString);
      return format(date, 'MMM d, yyyy');
    } catch (error) {
      return dateString.toString();
    }
  }
  
  getPrice(show: Show): string {
    if (show && typeof show.price === 'number') {
      return show.price.toFixed(2);
    }
    return '0.00';
  }
  
  getTruncatedDescription(description: string): string {
    if (!description) {
      return '';
    }
    
    if (description.length <= 100) {
      return description;
    }
    
    return description.substring(0, 100) + '...';
  }
  
  formatDateShort(dateString: string): string {
    try {
      const date = parseISO(dateString);
      return format(date, 'MM/dd/yyyy');
    } catch (error) {
      return dateString;
    }
  }
  
  // Pagination methods
  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages && page !== this.currentPage) {
      this.currentPage = page;
      this.updateUrlAndLoadShows();
      // Scroll to top
      window.scrollTo(0, 0);
    }
  }
  
  getPaginationArray(): number[] {
    const pages: number[] = [];
    const maxVisiblePages = 5;
    
    if (this.totalPages <= maxVisiblePages) {
      // Show all pages if total pages is less than max visible
      for (let i = 1; i <= this.totalPages; i++) {
        pages.push(i);
      }
    } else {
      // Always show first page
      pages.push(1);
      
      // Calculate start and end of visible pages
      let start = Math.max(2, this.currentPage - 1);
      let end = Math.min(this.totalPages - 1, start + 2);
      
      // Adjust start if end is at max
      if (end === this.totalPages - 1) {
        start = Math.max(2, end - 2);
      }
      
      // Add ellipsis if needed
      if (start > 2) {
        pages.push(-1); // -1 represents ellipsis
      }
      
      // Add visible pages
      for (let i = start; i <= end; i++) {
        pages.push(i);
      }
      
      // Add ellipsis if needed
      if (end < this.totalPages - 1) {
        pages.push(-1); // -1 represents ellipsis
      }
      
      // Always show last page
      pages.push(this.totalPages);
    }
    
    return pages;
  }
  
  // URL update
  updateUrlAndLoadShows(): void {
    // Build query params
    const queryParams: any = {};
    
    if (this.selectedCategory !== 'all') {
      queryParams.category = this.selectedCategory;
    }
    
    if (this.searchQuery) {
      queryParams.search = this.searchQuery;
    }
    
    if (this.dateFromControl.value) {
      queryParams.dateFrom = this.dateFromControl.value;
    }
    
    if (this.dateToControl.value) {
      queryParams.dateTo = this.dateToControl.value;
    }
    
    if (this.priceMinControl.value) {
      queryParams.priceMin = this.priceMinControl.value;
    }
    
    if (this.priceMaxControl.value) {
      queryParams.priceMax = this.priceMaxControl.value;
    }
    
    if (this.sortOption !== 'relevance') {
      queryParams.sort = this.sortOption;
    }
    
    if (this.currentPage > 1) {
      queryParams.page = this.currentPage;
    }
    
    // Update URL without reloading
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams,
      queryParamsHandling: 'merge',
      replaceUrl: true
    });
    
    // Load shows with new filters
    this.loadShows();
  }
  
  retryLoading(): void {
    this.loadShows();
  }
  
  /**
   * Get image URL with fallback to appropriate default image
   * Public wrapper for imageService.getImageUrl
   * @param imageUrl The original image URL
   * @param type The type of content (show, movie, concert, etc.)
   * @param subType Optional subtype for more specific images (action, rock, ballet, etc.)
   * @param title Optional title for searching specific content images
   * @returns A valid image URL
   */
  getImageUrl(imageUrl: string | undefined | null, type: string = 'show', subType: string = '', title: string = ''): string {
    return this.imageService.getImageUrl(imageUrl, type, subType, title);
  }
}