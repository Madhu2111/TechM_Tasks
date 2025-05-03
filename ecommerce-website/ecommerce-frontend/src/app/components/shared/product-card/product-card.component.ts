import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Product } from '../../../models/product.model';
import { CartService } from '../../../services/cart.service';
import { NotificationService } from '../../../services/notification.service';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { trigger, state, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-product-card',
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.css'],
  animations: [
    trigger('cardHover', [
      state('normal', style({
        transform: 'translateY(0)',
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)'
      })),
      state('hover', style({
        transform: 'translateY(-10px)',
        boxShadow: '0 12px 20px rgba(0, 0, 0, 0.15)'
      })),
      transition('normal <=> hover', animate('200ms ease-in-out'))
    ])
  ]
})
export class ProductCardComponent {
  @Input() product!: Product;
  @Input() showAddToCart: boolean = true;
  @Output() productClicked = new EventEmitter<Product>();
  
  cardState: 'normal' | 'hover' = 'normal';
  isInWishlist: boolean = false;

  constructor(
    private cartService: CartService,
    private notificationService: NotificationService,
    private authService: AuthService,
    private router: Router
  ) {}

  addToCart(): void {
    try {
      if (!this.product) {
        this.notificationService.showError('Cannot add product to cart: Product data is missing');
        return;
      }
      
      if (!this.authService.isLoggedIn()) {
        this.notificationService.showWarning('Please login to add items to your cart');
        this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
        return;
      }
      
      this.cartService.addToCart(this.product, 1);
      this.notificationService.showSuccess(`${this.product.name} added to cart`);
    } catch (error) {
      console.error('Error adding product to cart:', error);
      this.notificationService.showError('Failed to add product to cart');
    }
  }

  viewDetails(): void {
    this.productClicked.emit(this.product);
    this.router.navigate(['/products', this.product.id]);
  }

  isAdmin(): boolean {
    return this.authService.hasRole('ADMIN');
  }
  
  toggleWishlist(event: Event): void {
    event.stopPropagation();
    this.isInWishlist = !this.isInWishlist;
    if (this.isInWishlist) {
      this.notificationService.showSuccess(`${this.product.name} added to wishlist`);
    } else {
      this.notificationService.showInfo(`${this.product.name} removed from wishlist`);
    }
  }
}