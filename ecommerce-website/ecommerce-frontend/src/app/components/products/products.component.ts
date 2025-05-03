import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Product } from '../../models/product.model';
import { ProductService } from '../../services/product.service';
import { NotificationService } from '../../services/notification.service';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  loading = true;
  totalItems = 0;
  totalPages = 0;
  currentPage = 0;
  pageSize = 12;
  sortBy = 'id';
  sortDirection = 'asc';
  categories: string[] = [];
  selectedCategory = '';
  priceRange = { min: 0, max: 1000 };
  searchQuery = '';
  searchSubject = new Subject<string>();

  constructor(
    private productService: ProductService,
    private notificationService: NotificationService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.setupSearchObservable();
    
    this.route.queryParams.subscribe(params => {
      this.currentPage = params['page'] ? parseInt(params['page']) : 0;
      this.pageSize = params['size'] ? parseInt(params['size']) : 12;
      this.sortBy = params['sortBy'] || 'id';
      this.sortDirection = params['direction'] || 'asc';
      this.selectedCategory = params['category'] || '';
      this.searchQuery = params['search'] || '';
      
      this.loadProducts();
    });
  }

  setupSearchObservable(): void {
    this.searchSubject.pipe(
      debounceTime(500),
      distinctUntilChanged()
    ).subscribe(searchQuery => {
      this.searchQuery = searchQuery;
      this.currentPage = 0;
      this.updateUrlParams();
    });
  }

  loadProducts(): void {
    this.loading = true;
    
    if (this.searchQuery) {
      this.searchProducts();
    } else if (this.selectedCategory) {
      this.loadProductsByCategory();
    } else {
      this.loadAllProducts();
    }
  }

  loadAllProducts(): void {
    console.log('Loading all products...');
    this.productService.getProducts(
      this.currentPage, 
      this.pageSize, 
      this.sortBy, 
      this.sortDirection
    ).subscribe({
      next: (response) => {
        console.log('Products loaded successfully:', response);
        if (!response || !response.content) {
          console.error('Invalid response format:', response);
          this.products = [];
          this.filteredProducts = [];
          this.totalItems = 0;
          this.totalPages = 0;
          this.loading = false;
          return;
        }
        this.handleProductsResponse(response);
        this.extractCategories();
      },
      error: (error) => {
        console.error('Failed to load products:', error);
        this.notificationService.showError('Failed to load products');
        this.products = [];
        this.filteredProducts = [];
        this.totalItems = 0;
        this.totalPages = 0;
        this.loading = false;
      }
    });
  }

  loadProductsByCategory(): void {
    console.log(`Loading products for category: ${this.selectedCategory}`);
    this.productService.getProductsByCategory(
      this.selectedCategory,
      this.currentPage,
      this.pageSize
    ).subscribe({
      next: (response) => {
        console.log('Category products loaded successfully:', response);
        if (!response || !response.content) {
          console.error('Invalid response format:', response);
          this.products = [];
          this.filteredProducts = [];
          this.totalItems = 0;
          this.totalPages = 0;
          this.loading = false;
          return;
        }
        this.handleProductsResponse(response);
      },
      error: (error) => {
        console.error('Failed to load products by category:', error);
        this.notificationService.showError('Failed to load products by category');
        this.products = [];
        this.filteredProducts = [];
        this.totalItems = 0;
        this.totalPages = 0;
        this.loading = false;
      }
    });
  }

  searchProducts(): void {
    console.log(`Searching products with query: "${this.searchQuery}"`);
    this.productService.searchProducts(
      this.searchQuery,
      this.currentPage,
      this.pageSize
    ).subscribe({
      next: (response) => {
        console.log('Search results:', response);
        if (!response || !response.content) {
          console.error('Invalid response format:', response);
          this.products = [];
          this.filteredProducts = [];
          this.totalItems = 0;
          this.totalPages = 0;
          this.loading = false;
          return;
        }
        this.handleProductsResponse(response);
      },
      error: (error) => {
        console.error('Failed to search products:', error);
        this.notificationService.showError('Failed to search products');
        this.products = [];
        this.filteredProducts = [];
        this.totalItems = 0;
        this.totalPages = 0;
        this.loading = false;
      }
    });
  }

  handleProductsResponse(response: any): void {
    try {
      if (!response) {
        console.error('Empty response received');
        this.products = [];
        this.filteredProducts = [];
        this.totalItems = 0;
        this.totalPages = 0;
        this.loading = false;
        return;
      }
      
      this.products = response.content || [];
      this.filteredProducts = this.products;
      this.totalItems = response.totalElements || 0;
      this.totalPages = response.totalPages || 0;
      
      console.log(`Loaded ${this.products.length} products, total: ${this.totalItems}, pages: ${this.totalPages}`);
      
      // Check if products have all required fields
      this.products.forEach((product, index) => {
        if (!product.id || !product.name) {
          console.warn(`Product at index ${index} has missing required fields:`, product);
        }
      });
    } catch (error) {
      console.error('Error handling products response:', error);
      this.products = [];
      this.filteredProducts = [];
      this.totalItems = 0;
      this.totalPages = 0;
    } finally {
      this.loading = false;
    }
  }

  extractCategories(): void {
    try {
      const categoriesSet = new Set<string>();
      
      if (!this.products || this.products.length === 0) {
        console.log('No products available to extract categories');
        this.categories = [];
        return;
      }
      
      this.products.forEach(product => {
        if (product && product.category) {
          categoriesSet.add(product.category);
        }
      });
      
      this.categories = Array.from(categoriesSet).sort();
      console.log(`Extracted ${this.categories.length} categories:`, this.categories);
    } catch (error) {
      console.error('Error extracting categories:', error);
      this.categories = [];
    }
  }

  onSearch(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.searchSubject.next(value);
  }

  onCategoryChange(category: string): void {
    this.selectedCategory = category;
    this.currentPage = 0;
    this.updateUrlParams();
  }

  onSortChange(event: Event): void {
    const sortOption = (event.target as HTMLSelectElement).value;
    const [sortBy, sortDirection] = sortOption.split('-');
    this.sortBy = sortBy;
    this.sortDirection = sortDirection;
    this.updateUrlParams();
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.updateUrlParams();
  }

  updateUrlParams(): void {
    const queryParams: any = {
      page: this.currentPage,
      size: this.pageSize,
      sortBy: this.sortBy,
      direction: this.sortDirection
    };

    if (this.selectedCategory) {
      queryParams.category = this.selectedCategory;
    }

    if (this.searchQuery) {
      queryParams.search = this.searchQuery;
    }

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams,
      queryParamsHandling: 'merge'
    });
  }

  clearFilters(): void {
    this.selectedCategory = '';
    this.searchQuery = '';
    this.currentPage = 0;
    this.sortBy = 'id';
    this.sortDirection = 'asc';
    this.updateUrlParams();
    this.loadProducts();
  }
  
  resetFilters(): void {
    this.clearFilters();
  }
  
  getFilteredSuggestions(): string[] {
    const suggestions = ['Smartphone', 'Smart Watch', 'Laptop', 'Headphones', 'Camera', 'Tablet'];
    if (!this.searchQuery || this.searchQuery.length <= 2) {
      return [];
    }
    return suggestions.filter(s => 
      s.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }
  
  selectSuggestion(suggestion: string): void {
    this.searchQuery = suggestion;
    this.updateUrlParams();
  }
}
