import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ShowService } from '../services/show.service';
import { AuthService } from '../services/auth.service';
import { ImageService } from '../services/image.service';
import { Show } from '../models/show.model';
import { finalize, map } from 'rxjs/operators';
import { forkJoin, of } from 'rxjs';
declare var bootstrap: any;

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  featuredShows: any[] = [];
  upcomingShows: any[] = [];
  isLoading = true;
  isLoggedIn = false;
  loginModal: any;

  categories = [
    { name: 'Movies', route: '/shows', params: { category: 'movies' }, icon: 'bi bi-film' },
    { name: 'Concerts', route: '/shows', params: { category: 'concerts' }, icon: 'bi bi-music-note-beamed' },
    { name: 'Theater', route: '/shows', params: { category: 'theater' }, icon: 'bi bi-mask' },
    { name: 'Events', route: '/shows', params: { category: 'events' }, icon: 'bi bi-calendar-event' }
  ];

  constructor(
    private router: Router,
    private showService: ShowService,
    private authService: AuthService,
    private imageService: ImageService
  ) { }
  
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

  ngOnInit(): void {
    // Check if user is logged in
    this.isLoggedIn = this.authService.isLoggedIn();
    
    // Load shows from the backend
    this.loadShows();
    
    // Initialize carousel and modal when component loads
    setTimeout(() => {
      this.initializeCarousel();
      this.initializeLoginModal();
    }, 500);
  }

  loadShows(): void {
    this.isLoading = true;
    
    // Get all shows from the database
    this.showService.getAllShows()
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (shows: Show[]) => {
          if (shows && shows.length > 0) {
            // Process each show to get specific images based on title and type
            const processShows = shows.map(show => {
              // For movies, try to get a specific movie poster
              if (show.type === 'Movie' || show.genre?.toLowerCase() === 'movie') {
                return this.imageService.getSpecificMovieImage(show.title, show.type, show.genre).pipe(
                  map(imageUrl => ({
                    id: show.id,
                    title: show.title,
                    type: show.type || show.genre || 'Show',
                    image: show.posterUrl || imageUrl,
                    date: this.getShowDate(show),
                    venue: this.getShowVenue(show),
                    price: this.getShowPrice(show),
                    description: show.description
                  }))
                );
              } else {
                // For non-movies, use the existing image service
                return of({
                  id: show.id,
                  title: show.title,
                  type: show.type || show.genre || 'Show',
                  image: show.posterUrl || this.getImageUrl(null, show.type || show.genre || 'Show'),
                  date: this.getShowDate(show),
                  venue: this.getShowVenue(show),
                  price: this.getShowPrice(show),
                  description: show.description
                });
              }
            });
            
            // Wait for all image lookups to complete
            forkJoin(processShows).subscribe(mappedShows => {
              // Filter for featured (ONGOING) and upcoming (UPCOMING) shows
              this.featuredShows = mappedShows.filter(show => 
                shows.find(s => s.id === show.id)?.status === 'ONGOING'
              ).slice(0, 6);
              
              this.upcomingShows = mappedShows.filter(show => 
                shows.find(s => s.id === show.id)?.status === 'UPCOMING'
              ).slice(0, 6);
              
              console.log('Loaded shows from database:', {
                featured: this.featuredShows.length,
                upcoming: this.upcomingShows.length
              });
            });
          } else {
            console.warn('No shows returned from the database');
            this.featuredShows = [];
            this.upcomingShows = [];
          }
        },
        error: (error) => {
          console.error('Error fetching shows:', error);
          this.featuredShows = [];
          this.upcomingShows = [];
        }
      });
  }

  // Helper methods to extract show information
  private getShowDate(show: Show): string {
    if (show.schedules && show.schedules.length > 0) {
      return show.schedules[0].showDate;
    }
    return new Date().toISOString().split('T')[0]; // Today's date as fallback
  }

  private getShowVenue(show: Show): string {
    if (show.schedules && show.schedules.length > 0 && show.schedules[0].venue) {
      return show.schedules[0].venue.name;
    }
    return 'Various Venues';
  }

  private getShowPrice(show: Show): number {
    if (show.schedules && show.schedules.length > 0) {
      return show.schedules[0].basePrice;
    }
    return 19.99; // Default price
  }

  // Helper method to get a better image for a show based on its title and type
  private getEnhancedImage(show: Show): string {
    if (show.posterUrl) {
      return show.posterUrl;
    }
    
    // For movies, try to get a specific movie poster
    if (show.type === 'Movie' || show.genre?.toLowerCase() === 'movie') {
      // We'll use the image service to get a better image in the loadShows method
      return this.getImageUrl(null, 'movie', show.genre || '');
    }
    
    // For other types, use the appropriate default image
    return this.getImageUrl(null, show.type || show.genre || 'show');
  }

  navigateToCategory(category: string): void {
    // Allow navigation to shows list without login
    this.router.navigate(['/shows'], { queryParams: { category } });
  }

  checkLoginBeforeNavigate(event: Event, url: string): void {
    // Allow navigation to shows list and show details without login
    if (url === '/shows' || url.startsWith('/shows/')) {
      return;
    }
    
    // For other protected routes, require login
    if (!this.isLoggedIn) {
      event.preventDefault();
      this.showLoginRequiredModal();
    }
  }

  private showLoginRequiredModal(): void {
    if (this.loginModal) {
      this.loginModal.show();
    }
  }

  private initializeCarousel(): void {
    // Bootstrap carousel initialization
    const carouselElement = document.getElementById('showCarousel');
    if (carouselElement) {
      new bootstrap.Carousel(carouselElement, {
        interval: 5000,
        wrap: true
      });
    }
  }

  private initializeLoginModal(): void {
    const modalElement = document.getElementById('loginRequiredModal');
    if (modalElement) {
      this.loginModal = new bootstrap.Modal(modalElement);
    }
  }
}