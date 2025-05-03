import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Product } from '../models/product.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = `${environment.apiUrl}/api/products`;

  constructor(private http: HttpClient) { }

  getProducts(
    page: number = 0, 
    size: number = 10, 
    sortBy: string = 'id', 
    direction: string = 'asc',
    search: string = '',
    category: string = ''
  ): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('direction', direction);
    
    if (search) {
      params = params.set('search', search);
    }
    
    if (category) {
      params = params.set('category', category);
    }

    console.log(`Fetching products from ${this.apiUrl} with params:`, { page, size, sortBy, direction, search, category });
    
    return this.http.get<any>(this.apiUrl, { params })
      .pipe(
        catchError(error => {
          console.error('Error fetching products:', error);
          // Return empty result with same structure
          return of({
            content: [],
            totalElements: 0,
            totalPages: 0,
            size: size,
            number: page,
            first: page === 0,
            last: true,
            empty: true
          });
        })
      );
  }

  getProductById(id: number): Observable<Product> {
    console.log(`Fetching product details for ID: ${id}`);
    return this.http.get<Product>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(error => {
          console.error(`Error fetching product with ID ${id}:`, error);
          // Return empty product
          return of({
            id: 0,
            name: 'Product not found',
            description: 'The requested product could not be loaded',
            price: 0,
            imageUrl: 'assets/images/placeholder.jpg',
            category: '',
            brand: '',
            stockQuantity: 0
          } as Product);
        })
      );
  }

  searchProducts(query: string, page: number = 0, size: number = 10): Observable<any> {
    let params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('size', size.toString());

    console.log(`Searching products with query: "${query}"`);
    return this.http.get<any>(`${this.apiUrl}/search`, { params })
      .pipe(
        catchError(error => {
          console.error('Error searching products:', error);
          return of({
            content: [],
            totalElements: 0,
            totalPages: 0,
            size: size,
            number: page,
            first: page === 0,
            last: true,
            empty: true
          });
        })
      );
  }

  getProductsByCategory(category: string, page: number = 0, size: number = 10): Observable<any> {
    let params = new HttpParams()
      .set('category', category)
      .set('page', page.toString())
      .set('size', size.toString());

    console.log(`Fetching products for category: "${category}"`);
    return this.http.get<any>(`${this.apiUrl}/category`, { params })
      .pipe(
        catchError(error => {
          console.error(`Error fetching products for category ${category}:`, error);
          return of({
            content: [],
            totalElements: 0,
            totalPages: 0,
            size: size,
            number: page,
            first: page === 0,
            last: true,
            empty: true
          });
        })
      );
  }

  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  updateProduct(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, product);
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}