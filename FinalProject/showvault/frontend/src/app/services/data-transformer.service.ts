import { Injectable } from '@angular/core';
import { Show, ShowSchedule, Venue } from '../models/show.model';

@Injectable({
  providedIn: 'root'
})
export class DataTransformerService {

  constructor() { }

  /**
   * Transforms backend show data to match the frontend Show interface
   * @param backendShow The show data from the backend
   * @returns A show object that matches the frontend Show interface
   */
  transformShow(backendShow: any): Show {
    if (!backendShow) {
      console.error('Received null or undefined show data');
      return this.createEmptyShow();
    }

    console.log('Transforming backend show data:', backendShow);

    // Create a show object with default values for required fields
    const transformedShow: Show = {
      id: backendShow.id || 0,
      title: backendShow.title || 'Untitled Show',
      type: this.determineShowType(backendShow),
      image: backendShow.posterUrl || backendShow.imageUrl || '',
      date: this.extractDate(backendShow),
      venue: this.extractVenueName(backendShow),
      price: this.extractPrice(backendShow),
      description: backendShow.description || '',
      posterUrl: backendShow.posterUrl || '',
      imageUrl: backendShow.imageUrl || '',
      genre: backendShow.genre || '',
      duration: backendShow.duration || 0,
      status: this.mapStatus(backendShow.status)
    };
    
    // Process schedules if available
    if (backendShow.schedules && Array.isArray(backendShow.schedules)) {
      transformedShow.schedules = this.transformSchedules(backendShow.schedules);
    }

    console.log('Transformed show:', transformedShow);
    return transformedShow;
  }
  
  /**
   * Transforms backend schedule data to match the frontend ShowSchedule interface
   * @param schedules Array of schedule data from the backend
   * @returns Array of schedule objects that match the frontend ShowSchedule interface
   */
  transformSchedules(schedules: any[]): ShowSchedule[] {
    if (!schedules || !Array.isArray(schedules)) {
      return [];
    }
    
    return schedules.map(schedule => {
      const transformedSchedule: ShowSchedule = {
        id: schedule.id || 0,
        showId: schedule.showId || 0,
        showDate: schedule.showDate || schedule.date || new Date().toISOString().split('T')[0],
        showTime: schedule.showTime || schedule.time || '',
        basePrice: typeof schedule.basePrice === 'number' ? schedule.basePrice : 
                  (typeof schedule.price === 'number' ? schedule.price : 0),
        availableSeats: schedule.availableSeats || 0,
        totalSeats: schedule.totalSeats || 0,
        status: schedule.status || 'SCHEDULED'
      };
      
      // Process venue if available
      if (schedule.venue) {
        transformedSchedule.venue = this.transformVenue(schedule.venue);
      }
      
      return transformedSchedule;
    });
  }
  
  /**
   * Transforms backend venue data to match the frontend Venue interface
   * @param venue Venue data from the backend
   * @returns A venue object that matches the frontend Venue interface
   */
  transformVenue(venue: any): Venue {
    if (!venue) {
      return {
        id: 0,
        name: 'Unknown Venue'
      };
    }
    
    return {
      id: venue.id || 0,
      name: venue.name || 'Unknown Venue',
      address: venue.address || '',
      city: venue.city || '',
      state: venue.state || '',
      zipCode: venue.zipCode || '',
      capacity: venue.capacity || 0,
      amenities: venue.amenities || [],
      contactInfo: venue.contactInfo || '',
      imageUrl: venue.imageUrl || ''
    };
  }

  /**
   * Transforms an array of backend show data to match the frontend Show interface
   * @param backendShows Array of show data from the backend
   * @returns Array of show objects that match the frontend Show interface
   */
  transformShows(backendShows: any[]): Show[] {
    if (!backendShows || !Array.isArray(backendShows)) {
      console.error('Received null, undefined, or non-array show data');
      return [];
    }

    console.log(`Transforming ${backendShows.length} backend shows`);
    return backendShows.map(show => this.transformShow(show));
  }

  /**
   * Creates an empty show object with default values
   * @returns An empty show object
   */
  private createEmptyShow(): Show {
    return {
      title: 'Untitled Show',
      type: 'Other',
      image: '',
      date: new Date().toISOString(),
      venue: 'Unknown Venue',
      price: 0,
      description: ''
    };
  }

  /**
   * Determines the show type based on backend data
   * @param backendShow The show data from the backend
   * @returns The show type
   */
  private determineShowType(backendShow: any): Show['type'] {
    if (!backendShow) return 'Other';

    // Try to determine type from genre or explicit type field
    const genre = (backendShow.genre || '').toLowerCase();
    const type = (backendShow.type || '').toLowerCase();

    if (type === 'movie' || genre === 'movie' || genre.includes('film')) {
      return 'Movie';
    } else if (type === 'theater' || genre === 'theater' || genre.includes('play') || genre.includes('drama')) {
      return 'Theater';
    } else if (type === 'concert' || genre === 'concert' || genre.includes('music') || genre.includes('band')) {
      return 'Concert';
    } else if (type === 'event' || genre === 'event' || genre.includes('festival') || genre.includes('conference')) {
      return 'Event';
    }

    return 'Other';
  }

  /**
   * Extracts the venue name from backend data
   * @param backendShow The show data from the backend
   * @returns The venue name
   */
  private extractVenueName(backendShow: any): string {
    if (!backendShow) return 'Unknown Venue';

    // Check for venue in different possible formats
    if (backendShow.venue && typeof backendShow.venue === 'object') {
      return backendShow.venue.name || 'Unknown Venue';
    } else if (backendShow.venue && typeof backendShow.venue === 'string') {
      return backendShow.venue;
    } else if (backendShow.venueName) {
      return backendShow.venueName;
    }

    // If we have schedules, try to get venue from the first schedule
    if (backendShow.schedules && backendShow.schedules.length > 0) {
      const schedule = backendShow.schedules[0];
      if (schedule.venue && typeof schedule.venue === 'object') {
        return schedule.venue.name || 'Unknown Venue';
      } else if (schedule.venue && typeof schedule.venue === 'string') {
        return schedule.venue;
      } else if (schedule.venueName) {
        return schedule.venueName;
      }
    }

    return 'Unknown Venue';
  }

  /**
   * Extracts the price from backend data
   * @param backendShow The show data from the backend
   * @returns The price
   */
  private extractPrice(backendShow: any): number {
    if (!backendShow) return 0;

    // Check for price in different possible formats
    if (typeof backendShow.price === 'number') {
      return backendShow.price;
    } else if (typeof backendShow.basePrice === 'number') {
      return backendShow.basePrice;
    }

    // If we have schedules, try to get price from the first schedule
    if (backendShow.schedules && backendShow.schedules.length > 0) {
      const schedule = backendShow.schedules[0];
      if (typeof schedule.basePrice === 'number') {
        return schedule.basePrice;
      } else if (typeof schedule.price === 'number') {
        return schedule.price;
      }
    }

    return 0;
  }

  /**
   * Extracts the date from backend data
   * @param backendShow The show data from the backend
   * @returns A date string in ISO format
   */
  private extractDate(backendShow: any): string {
    if (!backendShow) return '';

    // Try to find a date in different possible fields
    if (backendShow.date) {
      return backendShow.date;
    } else if (backendShow.showDate) {
      return backendShow.showDate;
    } else if (backendShow.createdAt) {
      return backendShow.createdAt;
    } else if (backendShow.schedules && backendShow.schedules.length > 0) {
      const schedule = backendShow.schedules[0];
      if (schedule.showDate) {
        return schedule.showDate;
      }
    }

    return '';
  }

  /**
   * Maps backend status to frontend status
   * @param status The backend status
   * @returns The frontend status
   */
  private mapStatus(status: string): Show['status'] {
    if (!status) return 'UPCOMING';

    const statusUpper = status.toUpperCase();
    
    if (statusUpper === 'UPCOMING' || statusUpper === 'SCHEDULED') {
      return 'UPCOMING';
    } else if (statusUpper === 'ONGOING') {
      return 'ONGOING';
    } else if (statusUpper === 'COMPLETED') {
      return 'COMPLETED';
    } else if (statusUpper === 'CANCELLED') {
      return 'CANCELLED';
    }

    return 'UPCOMING';
  }
}