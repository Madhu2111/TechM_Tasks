import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ImageService {
  private defaultImages: Record<string, string> = {
    // General show image
    show: 'https://images.unsplash.com/photo-1585699324551-f6c309eedeca?q=80&w=600&auto=format&fit=crop',
    
    // Movie related images
    movie: 'https://images.unsplash.com/photo-1536440136628-849c177e76a1?q=80&w=600&auto=format&fit=crop',
    action: 'https://images.unsplash.com/photo-1598899134739-24c46f58b8c0?q=80&w=600&auto=format&fit=crop',
    comedy: 'https://images.unsplash.com/photo-1543584756-31dc18f1c41d?q=80&w=600&auto=format&fit=crop',
    drama: 'https://images.unsplash.com/photo-1485846234645-a62644f84728?q=80&w=600&auto=format&fit=crop',
    horror: 'https://images.unsplash.com/photo-1509248961158-e54f6934749c?q=80&w=600&auto=format&fit=crop',
    scifi: 'https://images.unsplash.com/photo-1506901437675-cde80ff9c746?q=80&w=600&auto=format&fit=crop',
    
    // Concert related images
    concert: 'https://images.unsplash.com/photo-1501386761578-eac5c94b800a?q=80&w=600&auto=format&fit=crop',
    rock: 'https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?q=80&w=600&auto=format&fit=crop',
    pop: 'https://images.unsplash.com/photo-1429962714451-bb934ecdc4ec?q=80&w=600&auto=format&fit=crop',
    jazz: 'https://images.unsplash.com/photo-1415201364774-f6f0bb35f28f?q=80&w=600&auto=format&fit=crop',
    classical: 'https://images.unsplash.com/photo-1507838153414-b4b713384a76?q=80&w=600&auto=format&fit=crop',
    
    // Theater related images
    theater: 'https://images.unsplash.com/photo-1503095396549-807759245b35?q=80&w=600&auto=format&fit=crop',
    musical: 'https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?q=80&w=600&auto=format&fit=crop',
    play: 'https://images.unsplash.com/photo-1503095396549-807759245b35?q=80&w=600&auto=format&fit=crop',
    ballet: 'https://images.unsplash.com/photo-1518834107812-67b0b7c58434?q=80&w=600&auto=format&fit=crop',
    opera: 'https://images.unsplash.com/photo-1522776851755-3125a4c71c8c?q=80&w=600&auto=format&fit=crop',
    
    // Event related images
    event: 'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?q=80&w=600&auto=format&fit=crop',
    festival: 'https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6a3?q=80&w=600&auto=format&fit=crop',
    conference: 'https://images.unsplash.com/photo-1505373877841-8d25f7d46678?q=80&w=600&auto=format&fit=crop',
    exhibition: 'https://images.unsplash.com/photo-1531058020387-3be344556be6?q=80&w=600&auto=format&fit=crop',
    
    // User related images
    user: 'https://images.unsplash.com/photo-1633332755192-727a05c4013d?q=80&w=600&auto=format&fit=crop',
    profile: 'https://images.unsplash.com/photo-1633332755192-727a05c4013d?q=80&w=600&auto=format&fit=crop',
    
    // Venue related images
    venue: 'https://images.unsplash.com/photo-1578944032637-f09897c5233d?q=80&w=600&auto=format&fit=crop',
    stadium: 'https://images.unsplash.com/photo-1577224682124-d0357ebb1e2e?q=80&w=600&auto=format&fit=crop',
    cinema: 'https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?q=80&w=600&auto=format&fit=crop',
    hall: 'https://images.unsplash.com/photo-1519167758481-83f550bb49b3?q=80&w=600&auto=format&fit=crop'
  };
  
  // Movie API configuration
  private movieApiUrl = 'https://api.themoviedb.org/3';
  private movieApiKey = 'YOUR_API_KEY'; // Replace with your actual API key if available
  private movieImageBaseUrl = 'https://image.tmdb.org/t/p/w500';
  
  constructor(private http: HttpClient) { }

  /**
   * Get image URL with fallback to appropriate default image
   * @param imageUrl The original image URL
   * @param type The type of content (show, movie, concert, etc.)
   * @param subType Optional subtype for more specific images (action, rock, ballet, etc.)
   * @param title Optional title for searching specific content images
   * @returns A valid image URL
   */
  getImageUrl(imageUrl: string | undefined | null, type: string = 'show', subType: string = '', title: string = ''): string {
    // Check if we have a valid image URL
    if (imageUrl && typeof imageUrl === 'string' && imageUrl.trim() !== '') {
      // If the URL is already a full URL (starts with http or https), return it
      if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
        return imageUrl;
      }
      
      // If it's a relative path and starts with assets, return it as is
      if (imageUrl.startsWith('assets/')) {
        return imageUrl;
      }
      
      // Otherwise, assume it's a relative path and convert to full URL
      // This would be replaced with your actual API base URL in production
      return `https://images.unsplash.com/${imageUrl}`;
    }
    
    // Process types to lowercase for comparison
    const lowerType = type ? type.toLowerCase() : 'show';
    const lowerSubType = subType ? subType.toLowerCase() : '';
    
    // Try to find a specific image based on subtype first
    if (lowerSubType && this.defaultImages[lowerSubType]) {
      return this.defaultImages[lowerSubType];
    }
    
    // Main category mapping with fallbacks
    switch (lowerType) {
      case 'movie':
      case 'film':
      case 'cinema':
        return this.defaultImages['movie'];
        
      case 'action':
      case 'adventure':
        return this.defaultImages['action'];
        
      case 'comedy':
        return this.defaultImages['comedy'];
        
      case 'drama':
        return this.defaultImages['drama'];
        
      case 'horror':
      case 'thriller':
        return this.defaultImages['horror'];
        
      case 'sci-fi':
      case 'scifi':
      case 'science fiction':
        return this.defaultImages['scifi'];
        
      case 'concert':
      case 'music':
        return this.defaultImages['concert'];
        
      case 'rock':
      case 'metal':
        return this.defaultImages['rock'];
        
      case 'pop':
        return this.defaultImages['pop'];
        
      case 'jazz':
      case 'blues':
        return this.defaultImages['jazz'];
        
      case 'classical':
      case 'orchestra':
        return this.defaultImages['classical'];
        
      case 'theater':
      case 'theatre':
        return this.defaultImages['theater'];
        
      case 'musical':
        return this.defaultImages['musical'];
        
      case 'play':
        return this.defaultImages['play'];
        
      case 'ballet':
      case 'dance':
        return this.defaultImages['ballet'];
        
      case 'opera':
        return this.defaultImages['opera'];
        
      case 'event':
        return this.defaultImages['event'];
        
      case 'festival':
        return this.defaultImages['festival'];
        
      case 'conference':
      case 'meeting':
        return this.defaultImages['conference'];
        
      case 'exhibition':
      case 'expo':
        return this.defaultImages['exhibition'];
        
      case 'user':
      case 'profile':
        return this.defaultImages['user'];
        
      case 'venue':
        return this.defaultImages['venue'];
        
      case 'stadium':
      case 'arena':
        return this.defaultImages['stadium'];
        
      case 'cinema':
      case 'theater hall':
        return this.defaultImages['cinema'];
        
      case 'hall':
      case 'auditorium':
        return this.defaultImages['hall'];
        
      default:
        return this.defaultImages['show'];
    }
  }
  
  /**
   * Search for a movie image by title
   * @param title The movie title to search for
   * @returns Observable with the movie poster URL or null if not found
   */
  searchMovieImage(title: string): Observable<string | null> {
    if (!title || !this.movieApiKey) {
      return of(null);
    }
    
    const url = `${this.movieApiUrl}/search/movie?api_key=${this.movieApiKey}&query=${encodeURIComponent(title)}`;
    
    return this.http.get<any>(url).pipe(
      map(response => {
        if (response.results && response.results.length > 0) {
          const movie = response.results[0];
          if (movie.poster_path) {
            return `${this.movieImageBaseUrl}${movie.poster_path}`;
          }
        }
        return null;
      }),
      catchError(error => {
        console.error('Error searching for movie image:', error);
        return of(null);
      })
    );
  }
  
  /**
   * Get a specific movie image by title
   * This method will try to fetch a specific movie image and fall back to default if not found
   * @param title The movie title
   * @param type The content type (movie, show, etc.)
   * @param subType The content subtype (action, comedy, etc.)
   * @returns The image URL (either specific or default)
   */
  getSpecificMovieImage(title: string, type: string = 'movie', subType: string = ''): Observable<string> {
    return this.searchMovieImage(title).pipe(
      map(imageUrl => {
        if (imageUrl) {
          return imageUrl;
        }
        return this.getImageUrl(null, type, subType);
      })
    );
  }
}