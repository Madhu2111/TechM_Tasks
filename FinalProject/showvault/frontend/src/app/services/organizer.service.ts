import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';

import { Show, ShowAnalytics, AudienceDemographics } from '../models/show.model';

export interface OrganizerDashboardStats {
  totalShows: number;
  activeShows: number;
  totalRevenue: number;
  totalTicketsSold: number;
  upcomingShows: number;
  revenueByMonth: { month: string; revenue: number }[];
  ticketsSoldByShow: { showId: number; showTitle: string; ticketsSold: number }[];
}

export interface SalesReport {
  totalRevenue: number;
  ticketsSold: number;
  averageTicketPrice: number;
  occupancyRate: number; // percentage
  revenueByShow: { showId: number; showTitle: string; revenue: number }[];
  revenueByMonth: { month: string; amount: number }[]; // Using amount instead of revenue
  revenueByCategory: { category: string; revenue: number }[];
  salesByDay: { date: string; sales: number }[];
  topSellingShows: { showId: number; showTitle: string; ticketsSold: number }[];
  topSellingVenues: { venueId: number; venueName: string; ticketsSold: number }[];
  // Optional properties
  conversionRate?: number;
  revenueTrend?: 'increasing' | 'decreasing' | 'stable';
  maxMonthlyRevenue?: number;
  revenueByPaymentMethod?: { method: string; revenue: number }[];
}

export interface CustomerMessage {
  id?: number;
  showId: number;
  scheduleId?: number;
  subject: string;
  message: string;
  recipientType: 'ALL' | 'TICKET_HOLDERS' | 'SPECIFIC_USERS';
  recipientIds?: number[]; // User IDs if SPECIFIC_USERS
  status: 'DRAFT' | 'SENT' | 'SCHEDULED';
  scheduledDate?: string;
  sentDate?: string;
  sentCount?: number;
  openCount?: number;
  clickCount?: number;
}

@Injectable({
  providedIn: 'root'
})
export class OrganizerService {
  private apiUrl = 'http://localhost:8080/api/organizer';
  private httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient) { }

  // Dashboard statistics
  getDashboardStats(startDate?: string, endDate?: string): Observable<OrganizerDashboardStats> {
    let params = new HttpParams();
    
    if (startDate) {
      params = params.set('startDate', startDate);
    }
    
    if (endDate) {
      params = params.set('endDate', endDate);
    }
    
    return this.http.get<OrganizerDashboardStats>(`${this.apiUrl}/dashboard`, { params }).pipe(
      catchError(error => {
        console.error('Error fetching dashboard stats:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch dashboard stats'));
      })
    );
  }

  // Sales reports
  getSalesReport(dateFrom?: string, dateTo?: string, showId?: number): Observable<SalesReport> {
    let params = new HttpParams();
    
    if (dateFrom) {
      params = params.set('dateFrom', dateFrom);
    }
    
    if (dateTo) {
      params = params.set('dateTo', dateTo);
    }
    
    if (showId) {
      params = params.set('showId', showId.toString());
    }
    
    return this.http.get<SalesReport>(`${this.apiUrl}/sales-report`, { params }).pipe(
      catchError(error => {
        console.error('Error fetching sales report:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch sales report'));
      })
    );
  }

  // Audience demographics
  getAudienceDemographics(showId?: number): Observable<AudienceDemographics> {
    let params = new HttpParams();
    
    if (showId) {
      params = params.set('showId', showId.toString());
    }
    
    return this.http.get<AudienceDemographics>(`${this.apiUrl}/audience-demographics`, { params }).pipe(
      catchError(error => {
        console.error('Error fetching audience demographics:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch audience demographics'));
      })
    );
  }

  // Customer communication
  getMessages(showId?: number): Observable<CustomerMessage[]> {
    let params = new HttpParams();
    
    if (showId) {
      params = params.set('showId', showId.toString());
    }
    
    return this.http.get<CustomerMessage[]>(`${this.apiUrl}/messages`, { params }).pipe(
      catchError(error => {
        console.error('Error fetching messages:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch messages'));
      })
    );
  }

  sendMessage(message: CustomerMessage): Observable<CustomerMessage> {
    return this.http.post<CustomerMessage>(`${this.apiUrl}/messages`, message, this.httpOptions).pipe(
      catchError(error => {
        console.error('Error sending message:', error);
        return throwError(() => new Error(error.message || 'Failed to send message'));
      })
    );
  }

  updateMessage(messageId: number, message: CustomerMessage): Observable<CustomerMessage> {
    return this.http.put<CustomerMessage>(`${this.apiUrl}/messages/${messageId}`, message, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error updating message ${messageId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to update message'));
      })
    );
  }

  deleteMessage(messageId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/messages/${messageId}`, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error deleting message ${messageId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to delete message'));
      })
    );
  }

  // Performance metrics
  getShowPerformanceMetrics(showId: number): Observable<ShowAnalytics> {
    return this.http.get<ShowAnalytics>(`${this.apiUrl}/shows/${showId}/performance`).pipe(
      catchError(error => {
        console.error(`Error fetching performance metrics for show ${showId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to fetch performance metrics'));
      })
    );
  }
}