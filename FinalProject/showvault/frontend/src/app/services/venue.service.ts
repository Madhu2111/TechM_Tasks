import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Venue } from '../models/venue.model';

@Injectable({
  providedIn: 'root'
})
export class VenueService {
  private apiUrl = 'http://localhost:8080/api/venues';
  private httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient) {}

  getAllVenues(): Observable<Venue[]> {
    return this.http.get<Venue[]>(this.apiUrl)
      .pipe(catchError(this.handleError));
  }

  getVenueById(id: number): Observable<Venue> {
    return this.http.get<Venue>(`${this.apiUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  getVenuesByCity(city: string): Observable<Venue[]> {
    return this.http.get<Venue[]>(`${this.apiUrl}/city/${city}`)
      .pipe(catchError(this.handleError));
  }

  getVenuesByCountry(country: string): Observable<Venue[]> {
    return this.http.get<Venue[]>(`${this.apiUrl}/country/${country}`)
      .pipe(catchError(this.handleError));
  }

  getVenuesByMinimumCapacity(capacity: number): Observable<Venue[]> {
    return this.http.get<Venue[]>(`${this.apiUrl}/capacity/${capacity}`)
      .pipe(catchError(this.handleError));
  }

  getAllCities(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/cities`)
      .pipe(catchError(this.handleError));
  }

  getAllCountries(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/countries`)
      .pipe(catchError(this.handleError));
  }

  createVenue(venue: Venue): Observable<Venue> {
    return this.http.post<Venue>(this.apiUrl, venue, this.httpOptions)
      .pipe(
        map(response => {
          // Validate response data
          if (!response || !response.id) {
            throw new Error('Invalid venue data received from server');
          }
          return response;
        }),
        catchError(this.handleError)
      );
  }

  updateVenue(id: number, venue: Venue): Observable<Venue> {
    return this.http.put<Venue>(`${this.apiUrl}/${id}`, venue, this.httpOptions)
      .pipe(
        map(response => {
          // Validate response data
          if (!response || !response.id) {
            throw new Error('Invalid venue data received from server');
          }
          return response;
        }),
        catchError(this.handleError)
      );
  }

  deleteVenue(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, this.httpOptions)
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.error.message || error.message}`;
    }
    
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}