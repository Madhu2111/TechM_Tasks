import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ShowService } from '../../services/show.service';
import { AuthService } from '../../services/auth.service';
import { ImageService } from '../../services/image.service';
import { Show, ShowSchedule, ShowReview } from '../../models/show.model';
import { finalize } from 'rxjs/operators';
import { format, parseISO } from 'date-fns';
import { Subscription } from 'rxjs';
declare var bootstrap: any;

@Component({
  selector: 'app-show-details',
  templateUrl: './show-details.component.html',
  styleUrls: ['./show-details.component.css']
})
export class ShowDetailsComponent implements OnInit, OnDestroy {
  show: Show | null = null;
  loading = false;
  error = '';
  ticketQuantity = 1;
  totalPrice = 0;
  schedules: ShowSchedule[] = [];
  groupedSchedules: Map<string, ShowSchedule[]> = new Map();
  selectedSchedule: ShowSchedule | null = null;
  reviews: ShowReview[] = [];
  averageRating = 0;
  similarShows: Show[] = [];
  isLoggedIn = false;
  reviewForm: FormGroup;
  isSubmittingReview = false;
  reviewModal: any;
  linkCopiedToast: any;
  private subscriptions: Subscription[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private showService: ShowService,
    private authService: AuthService,
    private imageService: ImageService,
    private fb: FormBuilder
  ) {
    this.reviewForm = this.fb.group({
      rating: [0, [Validators.required, Validators.min(1), Validators.max(5)]],
      comment: [''],
    });
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

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isLoggedIn();
    this.loadShowDetails();
    
    // Initialize Bootstrap components
    setTimeout(() => {
      this.initializeModals();
    }, 500);
    
    console.log('ShowDetailsComponent initialized, isLoggedIn:', this.isLoggedIn);
  }
  
  ngOnDestroy(): void {
    // Clean up subscriptions
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }
  
  retryLoading(): void {
    this.error = '';
    this.loadShowDetails();
  }
  
  private initializeModals(): void {
    // Initialize review modal
    const reviewModalElement = document.getElementById('reviewModal');
    if (reviewModalElement) {
      this.reviewModal = new bootstrap.Modal(reviewModalElement);
    }
    
    // Initialize toast
    const toastElement = document.getElementById('linkCopiedToast');
    if (toastElement) {
      this.linkCopiedToast = new bootstrap.Toast(toastElement);
    }
  }

  loadShowDetails(): void {
    this.loading = true;
    this.error = '';
    const showId = Number(this.route.snapshot.paramMap.get('id'));
    
    console.log('Loading show details for ID:', showId);
    
    if (isNaN(showId)) {
      this.error = 'Invalid show ID';
      this.loading = false;
      console.error('Invalid show ID');
      return;
    }

    // Load show details
    const showSubscription = this.showService.getShowById(showId).subscribe({
      next: (show) => {
        this.show = show;
        
        // Extract schedules if available
        if (show.schedules && show.schedules.length > 0) {
          this.schedules = show.schedules;
          this.groupSchedulesByDate();
          this.selectedSchedule = this.schedules[0]; // Select first schedule by default
        }
        
        // Enhance show image if needed
        this.enhanceShowImage();
        
        this.calculateTotal();
        this.loading = false;
        
        // Load reviews
        this.loadReviews(showId);
        
        // Load similar shows
        this.loadSimilarShows(showId);
      },
      error: (error) => {
        console.error('Error loading show details:', error);
        this.error = 'Failed to load show details. Please try again later.';
        this.loading = false;
      }
    });
    
    this.subscriptions.push(showSubscription);
  }
  
  /**
   * Enhance show images by trying to get specific movie posters
   */
  enhanceShowImage(): void {
    if (this.show && (!this.show.posterUrl && !this.show.imageUrl)) {
      // For movies, try to get a specific movie poster
      if (this.show.type === 'Movie' || this.show.genre?.toLowerCase() === 'movie') {
        this.imageService.getSpecificMovieImage(this.show.title, this.show.type, this.show.genre).subscribe(imageUrl => {
          if (this.show) {
            this.show.imageUrl = imageUrl;
          }
        });
      } else {
        // For non-movies, use the existing image service
        if (this.show) {
          this.show.imageUrl = this.getImageUrl(null, this.show.type || this.show.genre || 'Show');
        }
      }
    }
  }
  
  loadReviews(showId: number): void {
    const reviewSubscription = this.showService.getShowReviews(showId).subscribe({
      next: (reviews) => {
        this.reviews = reviews;
        this.calculateAverageRating();
      },
      error: (error) => {
        console.error('Error loading reviews:', error);
        // Silently fail - reviews are not critical
        this.reviews = [];
      }
    });
    
    this.subscriptions.push(reviewSubscription);
  }
  
  loadSimilarShows(showId: number): void {
    const similarShowsSubscription = this.showService.getRecommendedShows(showId).subscribe({
      next: (shows) => {
        this.similarShows = shows;
        
        // Enhance images for similar shows
        this.similarShows.forEach(show => {
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
      },
      error: (error) => {
        console.error('Error loading similar shows:', error);
        // Silently fail - similar shows are not critical
        this.similarShows = [];
      }
    });
    
    this.subscriptions.push(similarShowsSubscription);
  }
  
  groupSchedulesByDate(): void {
    this.groupedSchedules = new Map<string, ShowSchedule[]>();
    
    this.schedules.forEach(schedule => {
      const date = schedule.showDate;
      if (!this.groupedSchedules.has(date)) {
        this.groupedSchedules.set(date, []);
      }
      this.groupedSchedules.get(date)?.push(schedule);
    });
    
    // Sort times within each date
    this.groupedSchedules.forEach((schedules, date) => {
      schedules.sort((a, b) => {
        return a.showTime && b.showTime ? 
          a.showTime.localeCompare(b.showTime) : 0;
      });
    });
  }
  
  calculateAverageRating(): void {
    if (this.reviews.length === 0) {
      this.averageRating = 0;
      return;
    }
    
    const sum = this.reviews.reduce((total, review) => total + review.rating, 0);
    this.averageRating = sum / this.reviews.length;
  }

  selectSchedule(schedule: ShowSchedule): void {
    this.selectedSchedule = schedule;
    this.calculateTotal();
  }

  updateQuantity(change: number): void {
    const newQuantity = this.ticketQuantity + change;
    if (newQuantity >= 1 && newQuantity <= this.getAvailableSeats()) {
      this.ticketQuantity = newQuantity;
      this.calculateTotal();
    }
  }

  calculateTotal(): void {
    if (this.selectedSchedule) {
      this.totalPrice = this.selectedSchedule.basePrice * this.ticketQuantity;
    } else if (this.show) {
      this.totalPrice = this.show.price * this.ticketQuantity;
    } else {
      this.totalPrice = 0;
    }
  }

  proceedToBooking(): void {
    if (!this.isLoggedIn) {
      // Redirect to login with return URL
      this.router.navigate(['/login'], { 
        queryParams: { returnUrl: `/shows/${this.show?.id}` }
      });
      return;
    }
    
    // Redirect to theater selection page
    this.router.navigate(['/booking/theater-selection', this.show?.id]);
    console.log('Navigating to theater selection with show ID:', this.show?.id);
  }

  getAvailableSeats(): number {
    return this.selectedSchedule ? 
      (this.selectedSchedule.availableSeats || 0) : 
      (this.show?.availableSeats || 0);
  }
  
  canBookMoreTickets(): boolean {
    return this.ticketQuantity < this.getAvailableSeats();
  }
  
  canBook(): boolean {
    return this.getAvailableSeats() > 0;
  }
  
  getBookButtonText(): string {
    if (!this.canBook()) {
      return 'Sold Out';
    }
    
    if (!this.isLoggedIn) {
      return 'Login to Book';
    }
    
    return 'Book Now';
  }
  
  getAvailabilityStatus(): string {
    const availableSeats = this.getAvailableSeats();
    const totalSeats = this.selectedSchedule ? 
      (this.selectedSchedule.totalSeats || 0) : 
      (this.show?.totalSeats || 0);
    
    if (availableSeats === 0) {
      return 'Sold Out';
    }
    
    if (availableSeats <= totalSeats * 0.2) {
      return 'Almost Sold Out';
    }
    
    if (availableSeats <= totalSeats * 0.5) {
      return 'Selling Fast';
    }
    
    return 'Available';
  }
  
  getAvailabilityBadgeClass(): string {
    const status = this.getAvailabilityStatus();
    
    switch (status) {
      case 'Sold Out':
        return 'bg-danger';
      case 'Almost Sold Out':
        return 'bg-warning text-dark';
      case 'Selling Fast':
        return 'bg-info text-dark';
      default:
        return 'bg-success';
    }
  }
  
  formatDate(dateString: string | Date | undefined): string {
    if (!dateString) return '';
    
    try {
      const date = typeof dateString === 'string' ? parseISO(dateString) : dateString;
      return format(date, 'MMMM d, yyyy');
    } catch (error) {
      return String(dateString);
    }
  }
  
  getStarsArray(rating: number): number[] {
    const stars: number[] = [];
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 >= 0.5;
    
    // Add full stars
    for (let i = 0; i < fullStars; i++) {
      stars.push(1);
    }
    
    // Add half star if needed
    if (hasHalfStar) {
      stars.push(0.5);
    }
    
    // Add empty stars
    while (stars.length < 5) {
      stars.push(0);
    }
    
    return stars;
  }
  
  getInitials(name: string): string {
    return name
      .split(' ')
      .map(part => part.charAt(0))
      .join('')
      .toUpperCase()
      .substring(0, 2);
  }
  
  openReviewModal(): void {
    this.reviewForm.reset({ rating: 0, comment: '' });
    if (this.reviewModal) {
      this.reviewModal.show();
    }
  }
  
  setRating(rating: number): void {
    this.reviewForm.patchValue({ rating });
  }
  
  submitReview(): void {
    if (this.reviewForm.invalid || !this.show?.id) {
      return;
    }
    
    this.isSubmittingReview = true;
    
    const review: ShowReview = {
      showId: this.show.id,
      userId: 0, // Will be set by backend
      rating: this.reviewForm.value.rating,
      comment: this.reviewForm.value.comment
    };
    
    this.showService.addReview(this.show.id, review)
      .pipe(finalize(() => this.isSubmittingReview = false))
      .subscribe({
        next: (newReview) => {
          // Add the new review to the list
          this.reviews.unshift(newReview);
          this.calculateAverageRating();
          
          // Close the modal
          if (this.reviewModal) {
            this.reviewModal.hide();
          }
        },
        error: (error) => {
          console.error('Error submitting review:', error);
          alert('Failed to submit review. Please try again later.');
        }
      });
  }
  
  shareShow(platform: string): void {
    const url = window.location.href;
    const title = this.show?.title || 'Check out this show';
    
    switch (platform) {
      case 'facebook':
        window.open(`https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(url)}`, '_blank');
        break;
      case 'twitter':
        window.open(`https://twitter.com/intent/tweet?url=${encodeURIComponent(url)}&text=${encodeURIComponent(title)}`, '_blank');
        break;
      case 'whatsapp':
        window.open(`https://api.whatsapp.com/send?text=${encodeURIComponent(title + ' ' + url)}`, '_blank');
        break;
      case 'email':
        window.location.href = `mailto:?subject=${encodeURIComponent(title)}&body=${encodeURIComponent('Check out this show: ' + url)}`;
        break;
    }
  }
  
  copyLink(): void {
    const url = window.location.href;
    navigator.clipboard.writeText(url).then(() => {
      if (this.linkCopiedToast) {
        this.linkCopiedToast.show();
      }
    });
  }
  
  goBack(): void {
    this.router.navigate(['/shows']);
  }
}