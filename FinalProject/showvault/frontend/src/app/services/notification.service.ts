import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

interface Notification {
  id: number;
  title: string;
  message: string;
  type: 'info' | 'success' | 'warning' | 'danger';
  timestamp: Date;
  read: boolean;
  actionUrl?: string;
  actionText?: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = 'http://localhost:8080/api/notifications';

  constructor(private http: HttpClient) { }

  getUserNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/user`).pipe(
      catchError(error => {
        console.error('Error fetching notifications:', error);
        return throwError(() => new Error(error.message || 'Failed to fetch notifications'));
      })
    );
  }

  markNotificationAsRead(notificationId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${notificationId}/read`, {}).pipe(
      catchError(error => {
        console.error(`Error marking notification ${notificationId} as read:`, error);
        return throwError(() => new Error(error.message || 'Failed to mark notification as read'));
      })
    );
  }

  markAllNotificationsAsRead(): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/read-all`, {}).pipe(
      catchError(error => {
        console.error('Error marking all notifications as read:', error);
        return throwError(() => new Error(error.message || 'Failed to mark all notifications as read'));
      })
    );
  }

  deleteNotification(notificationId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${notificationId}`).pipe(
      catchError(error => {
        console.error(`Error deleting notification ${notificationId}:`, error);
        return throwError(() => new Error(error.message || 'Failed to delete notification'));
      })
    );
  }

  deleteAllNotifications(): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/all`).pipe(
      catchError(error => {
        console.error('Error deleting all notifications:', error);
        return throwError(() => new Error(error.message || 'Failed to delete all notifications'));
      })
    );
  }
}