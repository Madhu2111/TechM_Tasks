import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Cart } from '../../models/cart.model';
import { Order } from '../../models/order.model';
import { CartService } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';
import { NotificationService } from '../../services/notification.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {
  checkoutForm!: FormGroup;
  cart!: Cart;
  isSubmitting = false;
  sameAsShipping = false;
  shippingCost = 499; // Fixed shipping cost in INR
  taxRate = 0.18; // 18% GST in India
  checkoutStep = 1; // 1: Address, 2: Payment, 3: Review
  paymentMethods = [
    { id: 'CREDIT_CARD', name: 'Credit/Debit Card', icon: 'bi-credit-card' },
    { id: 'UPI', name: 'UPI Payment', icon: 'bi-phone' },
    { id: 'NET_BANKING', name: 'Net Banking', icon: 'bi-bank' },
    { id: 'WALLET', name: 'Digital Wallet', icon: 'bi-wallet2' },
    { id: 'COD', name: 'Cash on Delivery', icon: 'bi-cash' }
  ];

  constructor(
    private fb: FormBuilder,
    private cartService: CartService,
    private orderService: OrderService,
    private notificationService: NotificationService,
    private router: Router,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    // Check if user is logged in
    if (!this.authService.isLoggedIn()) {
      this.notificationService.showWarning('Please login to proceed with checkout');
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/checkout' } });
      return;
    }
    
    this.initForm();
    this.loadCart();
  }

  get shippingAddressForm() {
    return this.checkoutForm.get('shippingAddress') as FormGroup;
  }

  get billingAddressForm() {
    return this.checkoutForm.get('billingAddress') as FormGroup;
  }

  private initForm(): void {
    this.checkoutForm = this.fb.group({
      shippingAddress: this.fb.group({
        street: ['', Validators.required],
        city: ['', Validators.required],
        state: ['', Validators.required],
        zipCode: ['', Validators.required],
        country: ['', Validators.required]
      }),
      billingAddress: this.fb.group({
        street: ['', Validators.required],
        city: ['', Validators.required],
        state: ['', Validators.required],
        zipCode: ['', Validators.required],
        country: ['', Validators.required]
      }),
      paymentMethod: ['', Validators.required]
    });
  }

  private loadCart(): void {
    this.cartService.cart$.subscribe(cart => {
      this.cart = cart;
      
      // Redirect to cart page if cart is empty
      if (!cart || cart.items.length === 0) {
        this.router.navigate(['/cart']);
      }
    });
  }

  onSameAsShippingChange(event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    this.sameAsShipping = checkbox.checked;
    
    if (this.sameAsShipping) {
      // Copy shipping address values to billing address
      const shippingValues = this.shippingAddressForm.value;
      this.billingAddressForm.patchValue(shippingValues);
      
      // Disable billing address validation
      Object.keys(this.billingAddressForm.controls).forEach(key => {
        this.billingAddressForm.get(key)?.disable();
      });
    } else {
      // Enable billing address validation
      Object.keys(this.billingAddressForm.controls).forEach(key => {
        this.billingAddressForm.get(key)?.enable();
      });
    }
  }

  calculateTax(): number {
    return this.cart ? this.cart.totalPrice * this.taxRate : 0;
  }

  calculateTotal(): number {
    if (!this.cart) return 0;
    return this.cart.totalPrice + this.shippingCost + this.calculateTax();
  }

  nextStep(): void {
    if (this.checkoutStep === 1) {
      // Validate address form
      if (this.shippingAddressForm.invalid) {
        this.markFormGroupTouched(this.shippingAddressForm);
        return;
      }
      
      if (!this.sameAsShipping && this.billingAddressForm.invalid) {
        this.markFormGroupTouched(this.billingAddressForm);
        return;
      }
      
      this.checkoutStep = 2;
    } else if (this.checkoutStep === 2) {
      // Validate payment method
      if (!this.checkoutForm.get('paymentMethod')?.value) {
        this.notificationService.showWarning('Please select a payment method');
        return;
      }
      
      this.checkoutStep = 3;
    }
  }
  
  previousStep(): void {
    if (this.checkoutStep > 1) {
      this.checkoutStep--;
    }
  }
  
  onSubmit(): void {
    if (this.checkoutForm.invalid) {
      // Mark all fields as touched to trigger validation messages
      this.markFormGroupTouched(this.checkoutForm);
      return;
    }

    this.isSubmitting = true;

    // If billing address is same as shipping, use shipping address values
    const formValues = this.checkoutForm.value;
    if (this.sameAsShipping) {
      formValues.billingAddress = formValues.shippingAddress;
    }

    // Create order object
    const order: Order = {
      items: this.cart.items,
      totalPrice: this.calculateTotal(),
      shippingAddress: formValues.shippingAddress,
      billingAddress: formValues.billingAddress,
      paymentMethod: formValues.paymentMethod,
      status: 'PENDING'
    };

    // Submit order
    this.orderService.createOrder(order).subscribe({
      next: (createdOrder) => {
        this.isSubmitting = false;
        this.notificationService.showSuccess('Order placed successfully!');
        
        // Process payment
        this.processPayment(createdOrder.id!);
      },
      error: (error) => {
        this.isSubmitting = false;
        this.notificationService.showError('Failed to place order. Please try again.');
        console.error('Order creation error:', error);
      }
    });
  }

  private processPayment(orderId: number): void {
    const paymentDetails = {
      paymentMethod: this.checkoutForm.value.paymentMethod
    };

    this.orderService.processPayment(orderId, paymentDetails).subscribe({
      next: () => {
        // Clear cart after successful payment
        this.cartService.clearCart();
        
        // Navigate to order confirmation page
        this.router.navigate(['/orders', orderId]);
      },
      error: (error) => {
        this.notificationService.showError('Payment processing failed. Please contact support.');
        console.error('Payment processing error:', error);
      }
    });
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
}
