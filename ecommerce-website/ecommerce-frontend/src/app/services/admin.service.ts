import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = `${environment.apiUrl}/api/admin`;

  constructor(private http: HttpClient) { }

  // Dashboard
  getDashboardStats(): Observable<any> {
    console.log('AdminService - Getting dashboard stats from:', `${this.apiUrl}/dashboard`);
    return this.http.get(`${this.apiUrl}/dashboard`);
  }

  // User Management
  getAllUsers(page: number = 0, size: number = 10, sortBy: string = 'id', direction: string = 'asc'): Observable<any> {
    return this.http.get(`${this.apiUrl}/users`, {
      params: { page: page.toString(), size: size.toString(), sortBy, direction }
    });
  }

  getUserById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/users/${id}`);
  }

  updateUser(id: number, userData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/users/${id}`, userData);
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/users/${id}`);
  }

  // Order Management
  getAllOrders(page: number = 0, size: number = 10, sortBy: string = 'createdAt', direction: string = 'desc'): Observable<any> {
    return this.http.get(`${this.apiUrl}/orders`, {
      params: { page: page.toString(), size: size.toString(), sortBy, direction }
    });
  }

  getOrderById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/orders/${id}`);
  }

  updateOrderStatus(id: number, status: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/orders/${id}/status`, { status });
  }

  getOrdersByStatus(status: string, page: number = 0, size: number = 10): Observable<any> {
    return this.http.get(`${this.apiUrl}/orders/status/${status}`, {
      params: { page: page.toString(), size: size.toString() }
    });
  }

  // Reports
  getSalesReport(startDate?: string, endDate?: string, groupBy: string = 'daily'): Observable<any> {
    let params: any = { groupBy };
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;
    
    return this.http.get(`${this.apiUrl}/reports/sales`, { params });
  }

  getProductPerformanceReport(): Observable<any> {
    return this.http.get(`${this.apiUrl}/reports/products`);
  }

  getUserActivityReport(): Observable<any> {
    return this.http.get(`${this.apiUrl}/reports/users`);
  }
}