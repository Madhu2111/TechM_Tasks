import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { trigger, transition, style, animate, state, query, stagger } from '@angular/animations';
import { Product } from '../../models/product.model';
import { ProductService } from '../../services/product.service';
import { NotificationService } from '../../services/notification.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  animations: [
    trigger('fadeInOut', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('0.5s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ]),
      transition(':leave', [
        animate('0.5s ease-in', style({ opacity: 0, transform: 'translateY(20px)' }))
      ])
    ]),
    // We'll use CSS animation for the brand slider instead of Angular animations
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('1s ease-out', style({ opacity: 1 }))
      ])
    ])
  ]
})
export class HomeComponent implements OnInit {
  featuredProducts: Product[] = [];
  newArrivals: Product[] = [];
  categories: string[] = [];
  loading = true;
  newsletterForm: FormGroup;
  subscribing = false;

  constructor(
    private productService: ProductService,
    private notificationService: NotificationService,
    private cartService: CartService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {
    this.newsletterForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit(): void {
    this.loadFeaturedProducts();
    this.loadNewArrivals();
    
    // Initialize Bootstrap carousels
    setTimeout(() => {
      const carouselElements = document.querySelectorAll('.carousel');
      carouselElements.forEach(carousel => {
        new (window as any).bootstrap.Carousel(carousel, {
          interval: 5000,
          touch: true
        });
      });
    }, 1000);
  }

  loadFeaturedProducts(): void {
    this.productService.getProducts(0, 4, 'id', 'asc').subscribe({
      next: (response) => {
        this.featuredProducts = response.content || [];
        this.extractCategories();
        this.loading = false;
      },
      error: (error) => {
        this.notificationService.showError('Failed to load featured products');
        this.loading = false;
        
        // Fallback data for development/demo
        if (this.featuredProducts.length === 0) {
          this.createDemoProducts();
        }
      }
    });
  }

  loadNewArrivals(): void {
    this.productService.getProducts(0, 8, 'id', 'desc').subscribe({
      next: (response) => {
        this.newArrivals = response.content || [];
      },
      error: (error) => {
        console.error('Failed to load new arrivals', error);
        
        // Fallback data for development/demo
        if (this.newArrivals.length === 0) {
          this.createDemoProducts();
        }
      }
    });
  }

  // Create demo products if API fails
  createDemoProducts(): void {
    const demoProducts: Product[] = [
      {
        id: 1,
        name: 'Premium Wireless Headphones',
        description: 'High-quality wireless headphones with noise cancellation',
        price: 199.99,
        category: 'Electronics',
        brand: 'AudioTech',
        imageUrl: 'assets/images/product-1.jpg',
        stockQuantity: 15
      },
      {
        id: 2,
        name: 'Slim Fit T-Shirt',
        description: 'Comfortable cotton t-shirt with modern design',
        price: 24.99,
        category: 'Clothing',
        brand: 'FashionStyle',
        imageUrl: 'assets/images/product-2.jpg',
        stockQuantity: 50
      },
      {
        id: 3,
        name: 'Smart Watch Series 5',
        description: 'Latest smartwatch with health monitoring features',
        price: 299.99,
        category: 'Electronics',
        brand: 'TechGear',
        imageUrl: 'assets/images/product-3.jpg',
        stockQuantity: 8
      },
      {
        id: 4,
        name: 'Organic Coffee Beans',
        description: 'Premium organic coffee beans from South America',
        price: 14.99,
        category: 'Food & Beverages',
        brand: 'OrganicBrew',
        imageUrl: 'assets/images/product-4.jpg',
        stockQuantity: 100
      },
      {
        id: 5,
        name: 'Leather Wallet',
        description: 'Genuine leather wallet with multiple card slots',
        price: 49.99,
        category: 'Accessories',
        brand: 'LuxLeather',
        imageUrl: 'assets/images/product-5.jpg',
        stockQuantity: 30
      },
      {
        id: 6,
        name: 'Yoga Mat',
        description: 'Non-slip yoga mat for comfortable workouts',
        price: 29.99,
        category: 'Sports & Fitness',
        brand: 'FitLife',
        imageUrl: 'assets/images/product-6.jpg',
        stockQuantity: 25
      },
      {
        id: 7,
        name: 'Stainless Steel Water Bottle',
        description: 'Eco-friendly insulated water bottle',
        price: 19.99,
        category: 'Home & Kitchen',
        brand: 'EcoWare',
        imageUrl: 'assets/images/product-7.jpg',
        stockQuantity: 40
      },
      {
        id: 8,
        name: 'Wireless Charging Pad',
        description: 'Fast wireless charging for compatible devices',
        price: 34.99,
        category: 'Electronics',
        brand: 'PowerTech',
        imageUrl: 'assets/images/product-8.jpg',
        stockQuantity: 12
      }
    ];
    
    this.featuredProducts = demoProducts.slice(0, 4);
    this.newArrivals = demoProducts.slice(4, 8);
    this.extractCategories();
    this.loading = false;
  }

  extractCategories(): void {
    const categoriesSet = new Set<string>();
    
    // Combine featured products and new arrivals to extract categories
    const allProducts = [...this.featuredProducts, ...this.newArrivals];
    
    allProducts.forEach(product => {
      if (product.category) {
        categoriesSet.add(product.category);
      }
    });
    
    this.categories = Array.from(categoriesSet).slice(0, 4);
    
    // If no categories found, add some default ones
    if (this.categories.length === 0) {
      this.categories = ['Electronics', 'Clothing', 'Home & Kitchen', 'Sports & Fitness'];
    }
  }

  navigateToCategory(category: string): void {
    this.router.navigate(['/products'], { queryParams: { category } });
  }

  navigateToProduct(product: Product): void {
    this.router.navigate(['/products', product.id]);
  }
  
  addToCart(product: Product): void {
    this.cartService.addToCart(product, 1);
    this.notificationService.showSuccess(`${product.name} added to cart`);
  }
  
  subscribeNewsletter(): void {
    if (this.newsletterForm.invalid) {
      return;
    }
    
    this.subscribing = true;
    const email = this.newsletterForm.get('email')?.value;
    
    // Simulate API call
    setTimeout(() => {
      this.subscribing = false;
      this.notificationService.showSuccess(`Thank you for subscribing with ${email}!`);
      this.newsletterForm.reset();
    }, 1500);
  }
}
