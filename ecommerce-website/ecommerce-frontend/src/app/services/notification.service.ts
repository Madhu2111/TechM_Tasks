import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export enum NotificationType {
  Success = 'success',
  Error = 'error',
  Info = 'info',
  Warning = 'warning'
}

export interface Notification {
  type: NotificationType;
  message: string;
  id?: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationSubject = new Subject<Notification>();
  notifications$ = this.notificationSubject.asObservable();
  private counter = 0;

  constructor() { }

  showSuccess(message: string): void {
    this.show(message, NotificationType.Success);
  }

  showError(message: string): void {
    this.show(message, NotificationType.Error);
  }

  showInfo(message: string): void {
    this.show(message, NotificationType.Info);
  }

  showWarning(message: string): void {
    this.show(message, NotificationType.Warning);
  }

  private show(message: string, type: NotificationType): void {
    this.counter++;
    this.notificationSubject.next({
      id: this.counter,
      type,
      message
    });
  }
}