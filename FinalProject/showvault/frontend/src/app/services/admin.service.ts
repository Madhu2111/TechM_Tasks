import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { delay, catchError } from 'rxjs/operators';
import { User } from '../models/user.model';
import { Show } from '../models/show.model';
import { Booking, BookingStatus, PaymentStatus } from '../models/booking.model';

export interface DashboardStats {
  totalUsers: number;
  newUsers: number;
  totalShows: number;
  activeShows: number;
  upcomingShows: number;
  totalBookings: number;
  bookingsThisMonth: number;
  totalRevenue: number;
  recentBookings: Booking[];
  popularShows: Array<Show & { ticketsSold: number }>;
  userGrowth: Array<{ date: string; count: number }>;
  recentActivity: Array<{ timestamp: string; user: string; action: string; details?: string }>;
  systemHealth: {
    status: 'healthy' | 'warning' | 'critical';
    uptime: string;
    serverLoad: number;
    memoryUsage: number;
    diskUsage: number;
    activeConnections: number;
    responseTime: number;
  };
}

export interface UserReport {
  totalUsers: number;
  activeUsers: number;
  newUsers: number;
  retentionRate: number;
  userTypes: {
    regular: number;
    organizer: number;
    admin: number;
  };
  growthByMonth: Array<{
    month: string;
    count: number;
  }>;
  maxMonthlyUsers: number;
  registrationSources: {
    [key: string]: number;
  };
  mostActiveUsers: Array<{
    id?: number;
    name: string;
    email?: string;
    role: string;
    bookings: number;
    lastActive: Date;
  }>;
}

export interface SalesReport {
  totalRevenue: number;
  ticketsSold: number;
  averageTicketPrice: number;
  revenueByShow: Array<{ showId: number; showTitle: string; revenue: number }>;
  revenueByMonth: Array<{ month: string; amount: number }>;
  maxMonthlyRevenue: number;
  topSellingShows: Array<{ 
    id: number;
    name: string;
    organizer: string;
    ticketsSold: number;
    revenue: number;
    averagePrice: number;
  }>;
  revenueTrend: 'increasing' | 'decreasing' | 'stable';
  revenueByCategory: Array<{ category: string; revenue: number }>;
  revenueByPaymentMethod: Array<{ method: string; revenue: number }>;
  refundRate: number;
  conversionRate: number;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  getDashboardStats(startDate?: string, endDate?: string): Observable<DashboardStats> {
    let params = new HttpParams();
    
    if (startDate) {
      params = params.set('startDate', startDate);
    }
    
    if (endDate) {
      params = params.set('endDate', endDate);
    }
    
    return this.http.get<DashboardStats>(`${this.apiUrl}/dashboard/stats`, { params }).pipe(
      catchError(error => {
        console.error('Error fetching dashboard stats:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch dashboard stats'));
      })
    );
  }

  getUserReport(startDate?: string, endDate?: string): Observable<UserReport> {
    let params = new HttpParams();
    
    if (startDate) {
      params = params.set('startDate', startDate);
    }
    
    if (endDate) {
      params = params.set('endDate', endDate);
    }
    
    return this.http.get<UserReport>(`${this.apiUrl}/reports/users`, { params }).pipe(
      catchError(error => {
        console.error('Error fetching user report:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch user report'));
      })
    );
  }

  getSalesReport(startDate?: string, endDate?: string): Observable<SalesReport> {
    let params = new HttpParams();
    
    if (startDate) {
      params = params.set('startDate', startDate);
    }
    
    if (endDate) {
      params = params.set('endDate', endDate);
    }
    
    return this.http.get<SalesReport>(`${this.apiUrl}/reports/sales`, { params }).pipe(
      catchError(error => {
        console.error('Error fetching sales report:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch sales report'));
      })
    );
  }

  getUsers(page: number = 1, limit: number = 10, filters?: { 
    role?: string; 
    status?: string; 
    search?: string;
    sortBy?: string;
    sortOrder?: 'asc' | 'desc';
  }): Observable<{ users: User[]; total: number }> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());

    if (filters) {
      if (filters.role) {
        params = params.set('role', filters.role);
      }
      if (filters.status) {
        params = params.set('status', filters.status);
      }
      if (filters.search) {
        params = params.set('search', filters.search);
      }
      if (filters.sortBy) {
        params = params.set('sortBy', filters.sortBy);
        if (filters.sortOrder) {
          params = params.set('sortOrder', filters.sortOrder);
        }
      }
    }

    return this.http.get<{ users: User[]; total: number }>(`${this.apiUrl}/users`, { params }).pipe(
      catchError(error => {
        console.error('Error fetching users:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch users'));
      })
    );
  }

  updateUserStatus(userId: number, status: 'active' | 'suspended' | 'inactive'): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/users/${userId}/status`, { status }, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error updating user ${userId} status:`, error);
        return throwError(() => new Error(error.message || 'Failed to update user status'));
      })
    );
  }

  getUserById(userId: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/users/${userId}`).pipe(
      catchError(error => {
        console.error(`Error fetching user ${userId}:`, error);
        return throwError(() => new Error(error.message || 'User not found'));
      })
    );
  }

  updateUserRole(userId: number, role: 'user' | 'organizer' | 'admin'): Observable<User> {
    const mappedRole = role === 'user' ? 'ROLE_USER' : 
                      role === 'organizer' ? 'ROLE_ORGANIZER' : 
                      'ROLE_ADMIN';
    
    return this.http.put<User>(`${this.apiUrl}/users/${userId}/role`, { role: mappedRole }, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error updating user ${userId} role:`, error);
        return throwError(() => new Error(error.message || 'Failed to update user role'));
      })
    );
  }

  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/${userId}`, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error deleting user ${userId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to delete user'));
      })
    );
  }
  
  resetUserPassword(userId: number): Observable<{ success: boolean; message: string }> {
    return this.http.post<{ success: boolean; message: string }>(
      `${this.apiUrl}/users/${userId}/reset-password`,
      {},
      this.httpOptions
    ).pipe(
      catchError(error => {
        console.error(`Error resetting password for user ${userId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to reset password'));
      })
    );
  }

  getAuditLogs(page: number = 1, limit: number = 10, filters?: {
    userId?: number;
    action?: string;
    dateFrom?: string;
    dateTo?: string;
    sortBy?: string;
    sortOrder?: 'asc' | 'desc';
  }): Observable<{
    logs: Array<{
      id: number;
      userId: number;
      userName: string;
      userEmail: string;
      action: string;
      details: string;
      ipAddress: string;
      timestamp: Date;
    }>;
    total: number;
  }> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());

    if (filters) {
      if (filters.userId) {
        params = params.set('userId', filters.userId.toString());
      }
      
      if (filters.action) {
        params = params.set('action', filters.action);
      }
      
      if (filters.dateFrom) {
        params = params.set('dateFrom', filters.dateFrom);
      }
      
      if (filters.dateTo) {
        params = params.set('dateTo', filters.dateTo);
      }
      
      if (filters.sortBy) {
        params = params.set('sortBy', filters.sortBy);
        if (filters.sortOrder) {
          params = params.set('sortOrder', filters.sortOrder);
        }
      }
    }

    return this.http.get<{
      logs: Array<{
        id: number;
        userId: number;
        userName: string;
        userEmail: string;
        action: string;
        details: string;
        ipAddress: string;
        timestamp: Date;
      }>;
      total: number;
    }>(`${this.apiUrl}/audit-logs`, { params }).pipe(
      catchError(error => {
        console.error('Error fetching audit logs:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch audit logs'));
      })
    );
  }
  
  getSystemHealth(): Observable<{
    status: 'healthy' | 'warning' | 'critical';
    uptime: string;
    serverLoad: number;
    memoryUsage: number;
    diskUsage: number;
    activeConnections: number;
    responseTime: number;
    services: Array<{
      name: string;
      status: 'up' | 'down' | 'degraded';
      responseTime: number;
      lastChecked: Date;
    }>;
    recentErrors: Array<{
      timestamp: Date;
      service: string;
      errorCode: string;
      message: string;
    }>;
  }> {
    return this.http.get<{
      status: 'healthy' | 'warning' | 'critical';
      uptime: string;
      serverLoad: number;
      memoryUsage: number;
      diskUsage: number;
      activeConnections: number;
      responseTime: number;
      services: Array<{
        name: string;
        status: 'up' | 'down' | 'degraded';
        responseTime: number;
        lastChecked: Date;
      }>;
      recentErrors: Array<{
        timestamp: Date;
        service: string;
        errorCode: string;
        message: string;
      }>;
    }>(`${this.apiUrl}/system/health`).pipe(
      catchError(error => {
        console.error('Error fetching system health:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch system health'));
      })
    );
  }
  
  getPlatformSettings(): Observable<{
    general: {
      siteName: string;
      siteDescription: string;
      contactEmail: string;
      supportPhone: string;
      maintenanceMode: boolean;
    };
    security: {
      passwordPolicy: {
        minLength: number;
        requireUppercase: boolean;
        requireLowercase: boolean;
        requireNumbers: boolean;
        requireSpecialChars: boolean;
      };
      sessionTimeout: number;
      maxLoginAttempts: number;
      twoFactorAuth: boolean;
    };
    email: {
      provider: string;
      fromEmail: string;
      fromName: string;
      templates: Array<{ name: string; subject: string; lastUpdated: Date }>;
    };
    payment: {
      providers: Array<{ name: string; enabled: boolean; testMode: boolean }>;
      currency: string;
      taxRate: number;
    };
  }> {
    return this.http.get<{
      general: {
        siteName: string;
        siteDescription: string;
        contactEmail: string;
        supportPhone: string;
        maintenanceMode: boolean;
      };
      security: {
        passwordPolicy: {
          minLength: number;
          requireUppercase: boolean;
          requireLowercase: boolean;
          requireNumbers: boolean;
          requireSpecialChars: boolean;
        };
        sessionTimeout: number;
        maxLoginAttempts: number;
        twoFactorAuth: boolean;
      };
      email: {
        provider: string;
        fromEmail: string;
        fromName: string;
        templates: Array<{ name: string; subject: string; lastUpdated: Date }>;
      };
      payment: {
        providers: Array<{ name: string; enabled: boolean; testMode: boolean }>;
        currency: string;
        taxRate: number;
      };
    }>(`${this.apiUrl}/settings`).pipe(
      catchError(error => {
        console.error('Error fetching platform settings:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch platform settings'));
      })
    );
  }
  
  updatePlatformSettings(settings: any): Observable<{ success: boolean; message: string }> {
    return this.http.put<{ success: boolean; message: string }>
      (`${this.apiUrl}/settings`, settings, this.httpOptions).pipe(
        catchError(error => {
          console.error('Error updating platform settings:', error);
          return throwError(() => new Error(error.message || 'Failed to update platform settings'));
        })
      );
  }
  
  private httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  getBookings(page: number = 1, limit: number = 10, status?: BookingStatus, date?: string): Observable<{ bookings: Booking[]; total: number }> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());

    if (status) {
      params = params.set('status', status);
    }

    if (date) {
      params = params.set('date', date);
    }

    return this.http.get<{ bookings: Booking[]; total: number }>(`${this.apiUrl}/bookings`, { params }).pipe(
      catchError(error => {
        console.error('Error fetching bookings:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch bookings'));
      })
    );
  }

  updateBookingStatus(bookingId: number, status: BookingStatus): Observable<Booking> {
    return this.http.put<Booking>(`${this.apiUrl}/bookings/${bookingId}/status`, { status }, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error updating booking ${bookingId} status:`, error);
        return throwError(() => new Error(error.message || 'Failed to update booking status'));
      })
    );
  }

  processRefund(bookingId: number): Observable<Booking> {
    return this.http.post<Booking>(`${this.apiUrl}/bookings/${bookingId}/refund`, {}, this.httpOptions).pipe(
      catchError(error => {
        console.error(`Error processing refund for booking ${bookingId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to process refund'));
      })
    );
  }
}