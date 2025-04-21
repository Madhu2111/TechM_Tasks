import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError, of } from 'rxjs';
import { catchError, map, delay } from 'rxjs/operators';

import { Show, ShowFilter, ShowsResponse, ShowReview } from '../models/show.model';
import { ShowSchedule, Venue, SeatMap, ShowAnalytics, Promotion } from '../models/show-interfaces.model';
import { DataTransformerService } from './data-transformer.service';

@Injectable({
  providedIn: 'root'
})
export class ShowService {
  private apiUrl = 'http://localhost:8080/api/shows';
  private httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(
    private http: HttpClient,
    private dataTransformerService: DataTransformerService
  ) { }

  getAllShows(): Observable<Show[]> {
    return this.http.get<any[]>(this.apiUrl).pipe(
      map(response => {
        console.log('Raw getAllShows response:', response);
        return this.dataTransformerService.transformShows(response);
      }),
      catchError(error => {
        console.error('Error fetching shows:', error);
        return of([]); // Return empty array instead of throwing error
      })
    );
  }

  getShowById(id: number): Observable<Show> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map(response => {
        console.log(`Raw getShowById(${id}) response:`, response);
        let show = this.dataTransformerService.transformShow(response);
        
        // If the show doesn't have schedules, add sample schedules with venues
        if (!show.schedules || show.schedules.length === 0) {
          show = this.addSampleSchedulesAndVenues(show);
        }
        
        return show;
      }),
      catchError(error => {
        console.error(`Error fetching show ${id}:`, error);
        
        // For development/testing, return a sample show with schedules and venues
        console.log('Returning sample show data for development');
        return of(this.createSampleShow(id));
      })
    );
  }
  
  private addSampleSchedulesAndVenues(show: Show): Show {
    // Create sample venues
    const venues: Venue[] = [
      {
        id: 1,
        name: 'Cinema City',
        address: '123 Main St',
        city: 'New York',
        state: 'NY',
        capacity: 200
      },
      {
        id: 2,
        name: 'Regal Theaters',
        address: '456 Broadway',
        city: 'New York',
        state: 'NY',
        capacity: 150
      },
      {
        id: 3,
        name: 'AMC Multiplex',
        address: '789 Park Ave',
        city: 'New York',
        state: 'NY',
        capacity: 300
      }
    ];
    
    // Create sample schedules for the next 7 days
    const schedules: ShowSchedule[] = [];
    const today = new Date();
    
    for (let i = 0; i < 7; i++) {
      const date = new Date(today);
      date.setDate(date.getDate() + i);
      const dateStr = date.toISOString().split('T')[0];
      
      // Add 3-4 showtimes per day across different venues
      venues.forEach((venue, venueIndex) => {
        const times = ['10:00', '13:30', '16:45', '19:30', '22:15'];
        const venueTimes = times.slice(venueIndex, venueIndex + 3); // Different times for each venue
        
        venueTimes.forEach((time, timeIndex) => {
          schedules.push({
            id: schedules.length + 1,
            showId: show.id,
            showDate: dateStr,
            showTime: time,
            basePrice: show.price,
            venue: venue,
            availableSeats: Math.floor(Math.random() * venue.capacity!),
            totalSeats: venue.capacity,
            status: 'SCHEDULED'
          });
        });
      });
    }
    
    // Add schedules to the show
    return {
      ...show,
      schedules
    };
  }
  
  private createSampleShow(id: number): Show {
    // Create a sample show
    const show: Show = {
      id: id,
      title: 'Sample Show',
      type: 'Movie',
      image: 'assets/images/movie1.jpg',
      date: new Date().toISOString().split('T')[0],
      time: '19:00',
      venue: 'Multiple Venues',
      price: 12.99,
      description: 'This is a sample show created for development purposes. It includes multiple venues and showtimes.',
      duration: 120,
      availableSeats: 500,
      totalSeats: 1000,
      genre: 'Action'
    };
    
    // Add sample schedules and venues
    return this.addSampleSchedulesAndVenues(show);
  }

  filterShows(filter: ShowFilter): Observable<Show[] | ShowsResponse> {
    // Build query parameters
    let params = new HttpParams();

    if (filter.type) {
      params = params.set('type', filter.type);
    }

    if (filter.date) {
      params = params.set('date', filter.date);
    }
    
    if (filter.dateFrom) {
      params = params.set('dateFrom', filter.dateFrom);
    }
    
    if (filter.dateTo) {
      params = params.set('dateTo', filter.dateTo);
    }

    if (filter.venue) {
      params = params.set('venue', filter.venue);
    }

    if (filter.priceMin !== undefined) {
      params = params.set('priceMin', filter.priceMin.toString());
    }

    if (filter.priceMax !== undefined) {
      params = params.set('priceMax', filter.priceMax.toString());
    }

    if (filter.search) {
      params = params.set('search', filter.search);
    }
    
    if (filter.status) {
      params = params.set('status', filter.status);
    }
    
    if (filter.genre) {
      params = params.set('genre', filter.genre);
    }
    
    if (filter.page !== undefined) {
      params = params.set('page', filter.page.toString());
    }
    
    if (filter.pageSize !== undefined) {
      params = params.set('size', filter.pageSize.toString());
    }
    
    if (filter.sort) {
      params = params.set('sort', filter.sort);
    }

    return this.http.get<any>(`${this.apiUrl}/filter`, { params }).pipe(
      map(response => {
        console.log('Raw filter response:', response);
        
        // Handle different response formats
        if (Array.isArray(response)) {
          // If response is an array, transform each show
          const transformedShows = this.dataTransformerService.transformShows(response);
          return transformedShows;
        } else if (response && typeof response === 'object' && response.content) {
          // If response is a paginated response
          const transformedContent = this.dataTransformerService.transformShows(response.content);
          return {
            ...response,
            content: transformedContent
          } as ShowsResponse;
        } else {
          // If response is empty or invalid
          console.warn('Received unexpected response format:', response);
          return [] as Show[];
        }
      }),
      catchError(error => {
        console.error('Error filtering shows:', error);
        // Return empty array instead of throwing error to avoid breaking the UI
        return of([] as Show[]);
      })
    );
  }
  
  getShowReviews(showId: number): Observable<ShowReview[]> {
    return this.http.get<ShowReview[]>(`${this.apiUrl}/${showId}/reviews`).pipe(
      catchError(error => {
        console.error(`Error fetching reviews for show ${showId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to fetch reviews'));
      })
    );
  }
  
  addReview(showId: number, review: ShowReview): Observable<ShowReview> {
    return this.http.post<ShowReview>(`${this.apiUrl}/${showId}/reviews`, review, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error adding review for show ${showId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to add review'));
      })
    );
  }
  
  getRecommendedShows(showId?: number): Observable<Show[]> {
    const url = showId ? `${this.apiUrl}/recommended/${showId}` : `${this.apiUrl}/recommended`;
    return this.http.get<Show[]>(url).pipe(
      catchError(error => {
        console.error('Error fetching recommended shows:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch recommended shows'));
      })
    );
  }
  
  getTrendingShows(): Observable<Show[]> {
    return this.http.get<Show[]>(`${this.apiUrl}/trending`).pipe(
      catchError(error => {
        console.error('Error fetching trending shows:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch trending shows'));
      })
    );
  }

  // Methods for organizers
  createShow(show: Show): Observable<Show> {
    return this.http.post<Show>(this.apiUrl, show, this.httpOptions).pipe(
      catchError(error => {
        console.error('Error creating show:', error);
        return throwError(() => new Error(error.message || 'Failed to create show'));
      })
    );
  }

  updateShow(id: number, show: Show): Observable<Show> {
    return this.http.put<Show>(`${this.apiUrl}/${id}`, show, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error updating show ${id}:`, error);
        return throwError(() => new Error(error.message || 'Failed to update show'));
      })
    );
  }

  deleteShow(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error deleting show ${id}:`, error);
        return throwError(() => new Error(error.message || 'Failed to delete show'));
      })
    );
  }
  
  // Methods for organizers to manage schedules
  getSchedules(showId: number): Observable<ShowSchedule[]> {
    return this.http.get<ShowSchedule[]>(`${this.apiUrl}/${showId}/schedules`).pipe(
      catchError(error => {
        console.error(`Error fetching schedules for show ${showId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to fetch schedules'));
      })
    );
  }
  
  getScheduleById(showId: number, scheduleId: number): Observable<ShowSchedule> {
    return this.http.get<ShowSchedule>(`${this.apiUrl}/${showId}/schedules/${scheduleId}`).pipe(
      catchError(error => {
        console.error(`Error fetching schedule ${scheduleId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to fetch schedule'));
      })
    );
  }
  
  addSchedule(showId: number, schedule: ShowSchedule): Observable<ShowSchedule> {
    return this.http.post<ShowSchedule>(`${this.apiUrl}/${showId}/schedules`, schedule, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error adding schedule for show ${showId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to add schedule'));
      })
    );
  }
  
  updateSchedule(showId: number, scheduleId: number, schedule: ShowSchedule): Observable<ShowSchedule> {
    return this.http.put<ShowSchedule>(`${this.apiUrl}/${showId}/schedules/${scheduleId}`, schedule, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error updating schedule ${scheduleId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to update schedule'));
      })
    );
  }
  
  deleteSchedule(showId: number, scheduleId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${showId}/schedules/${scheduleId}`, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error deleting schedule ${scheduleId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to delete schedule'));
      })
    );
  }
  
  // Venue management
  getVenues(): Observable<Venue[]> {
    return this.http.get<Venue[]>(`${this.apiUrl}/venues`).pipe(
      catchError(error => {
        console.error('Error fetching venues:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch venues'));
      })
    );
  }
  
  getVenueById(venueId: number): Observable<Venue> {
    return this.http.get<Venue>(`${this.apiUrl}/venues/${venueId}`).pipe(
      catchError(error => {
        console.error(`Error fetching venue ${venueId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to fetch venue'));
      })
    );
  }
  
  // Seat mapping
  getSeatMap(showId: number, scheduleId: number): Observable<SeatMap> {
    return this.http.get<SeatMap>(`${this.apiUrl}/${showId}/schedules/${scheduleId}/seat-map`).pipe(
      catchError(error => {
        console.error(`Error fetching seat map for schedule ${scheduleId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to fetch seat map'));
      })
    );
  }
  
  updateSeatMap(showId: number, scheduleId: number, seatMap: SeatMap): Observable<SeatMap> {
    return this.http.put<SeatMap>(`${this.apiUrl}/${showId}/schedules/${scheduleId}/seat-map`, seatMap, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error updating seat map for schedule ${scheduleId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to update seat map'));
      })
    );
  }
  
  // Analytics
  getShowAnalytics(showId: number): Observable<ShowAnalytics> {
    return this.http.get<ShowAnalytics>(`${this.apiUrl}/${showId}/analytics`).pipe(
      catchError(error => {
        console.error(`Error fetching analytics for show ${showId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to fetch analytics'));
      })
    );
  }
  
  // Promotions
  getPromotions(showId: number): Observable<Promotion[]> {
    return this.http.get<Promotion[]>(`${this.apiUrl}/${showId}/promotions`).pipe(
      catchError(error => {
        console.error(`Error fetching promotions for show ${showId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to fetch promotions'));
      })
    );
  }
  
  getAllPromotions(): Observable<Promotion[]> {
    return this.http.get<Promotion[]>(`http://localhost:8080/api/promotions`).pipe(
      catchError(error => {
        console.error('Error fetching all promotions:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch all promotions'));
      })
    );
  }
  
  createPromotion(showId: number, promotion: Promotion): Observable<Promotion> {
    return this.http.post<Promotion>(`${this.apiUrl}/${showId}/promotions`, promotion, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error creating promotion for show ${showId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to create promotion'));
      })
    );
  }
  
  updatePromotion(showId: number, promotionId: number, promotion: Promotion): Observable<Promotion> {
    return this.http.put<Promotion>(`${this.apiUrl}/${showId}/promotions/${promotionId}`, promotion, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error updating promotion ${promotionId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to update promotion'));
      })
    );
  }
  
  deletePromotion(showId: number, promotionId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${showId}/promotions/${promotionId}`, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error deleting promotion ${promotionId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to delete promotion'));
      })
    );
  }

  // User Favorites Methods
  getUserFavorites(): Observable<Show[]> {
    return this.http.get<Show[]>(`${this.apiUrl}/favorites`).pipe(
      catchError(error => {
        console.error('Error fetching user favorites:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch user favorites'));
      })
    );
  }

  addToFavorites(showId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${showId}/favorite`, {}, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error adding show ${showId} to favorites:`, error);
        return throwError(() => new Error(error.message || 'Failed to add to favorites'));
      })
    );
  }

  removeFromFavorites(showId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${showId}/favorite`, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error removing show ${showId} from favorites:`, error);
        return throwError(() => new Error(error.message || 'Failed to remove from favorites'));
      })
    );
  }

  // User Ratings Methods
  getUserRatings(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/ratings`).pipe(
      catchError(error => {
        console.error('Error fetching user ratings:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch user ratings'));
      })
    );
  }

  updateRating(ratingId: number, ratingData: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/ratings/${ratingId}`, ratingData, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error updating rating ${ratingId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to update rating'));
      })
    );
  }

  deleteRating(ratingId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/ratings/${ratingId}`, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error deleting rating ${ratingId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to delete rating'));
      })
    );
  }
}