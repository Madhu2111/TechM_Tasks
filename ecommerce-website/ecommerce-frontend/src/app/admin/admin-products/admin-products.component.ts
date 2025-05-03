import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-admin-products',
  templateUrl: './admin-products.component.html',
  styleUrls: ['./admin-products.component.css']
})
export class AdminProductsComponent implements OnInit {
  products: Product[] = [];
  isLoading = true;
  error: string | null = null;
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalItems = 0;
  totalPages = 0;
  
  // Filtering and sorting
  searchTerm = '';
  categoryFilter = '';
  sortBy = 'name,asc';
  
  // Make Math available in the template
  Math = Math;

  constructor(
    private productService: ProductService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.isLoading = true;
    
    // Extract sort field and direction
    const [sortField, direction] = this.sortBy.split(',');
    
    this.productService.getProducts(
      this.currentPage, 
      this.pageSize, 
      sortField, 
      direction,
      this.searchTerm,
      this.categoryFilter
    ).subscribe({
      next: (response) => {
        this.products = response.content;
        this.totalItems = response.totalElements;
        this.totalPages = response.totalPages;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading products', error);
        this.error = 'Failed to load products. Please try again.';
        this.isLoading = false;
        this.notificationService.showError('Failed to load products');
      }
    });
  }

  applyFilter(): void {
    this.currentPage = 0; // Reset to first page when filter changes
    this.loadProducts();
  }

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadProducts();
    }
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxPagesToShow = 5;
    
    if (this.totalPages <= maxPagesToShow) {
      // Show all pages if there are few
      for (let i = 1; i <= this.totalPages; i++) {
        pages.push(i);
      }
    } else {
      // Show a window of pages around the current page
      let startPage = Math.max(1, this.currentPage - 1);
      let endPage = Math.min(this.totalPages, startPage + maxPagesToShow - 1);
      
      // Adjust if we're near the end
      if (endPage - startPage < maxPagesToShow - 1) {
        startPage = Math.max(1, endPage - maxPagesToShow + 1);
      }
      
      for (let i = startPage; i <= endPage; i++) {
        pages.push(i);
      }
    }
    
    return pages;
  }

  deleteProduct(id: number | undefined): void {
    if (id === undefined) {
      this.notificationService.showError('Cannot delete product: Invalid product ID');
      return;
    }
    
    if (confirm('Are you sure you want to delete this product?')) {
      this.productService.deleteProduct(id).subscribe({
        next: () => {
          this.notificationService.showSuccess('Product deleted successfully');
          this.loadProducts(); // Reload the list
        },
        error: (error) => {
          console.error('Error deleting product', error);
          this.notificationService.showError('Failed to delete product');
        }
      });
    }
  }
}
