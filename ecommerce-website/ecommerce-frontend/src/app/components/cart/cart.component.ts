import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartItem } from '../../models/cart.model';
import { CartService } from '../../services/cart.service';
import { NotificationService } from '../../services/notification.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  cartItems: CartItem[] = [];
  totalPrice = 0;
  loading = false;

  constructor(
    private cartService: CartService,
    private notificationService: NotificationService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.cartService.cart$.subscribe(cart => {
      this.cartItems = cart.items;
      this.totalPrice = cart.totalPrice;
    });
  }

  onQuantityChange(event: Event, item: CartItem): void {
    const input = event.target as HTMLInputElement;
    const quantity = +input.value;
    this.updateQuantity(item, quantity);
  }

  updateQuantity(item: CartItem, quantity: number): void {
    if (quantity <= 0) {
      this.removeItem(item);
      return;
    }
    
    if (quantity > item.product.stockQuantity) {
      this.notificationService.showWarning(`Only ${item.product.stockQuantity} items available in stock`);
      quantity = item.product.stockQuantity;
    }
    
    this.cartService.updateQuantity(item.product.id!, quantity);
  }

  removeItem(item: CartItem): void {
    this.cartService.removeFromCart(item.product.id!);
    this.notificationService.showInfo(`${item.product.name} removed from cart`);
  }

  clearCart(): void {
    this.cartService.clearCart();
    this.notificationService.showInfo('Cart cleared');
  }

  proceedToCheckout(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/checkout']);
    } else {
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/checkout' } });
    }
  }

  continueShopping(): void {
    this.router.navigate(['/products']);
  }
}
