import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, map, of, switchMap, catchError } from 'rxjs';
import { Cart, CartItem } from '../models/cart.model';
import { Product } from '../models/product.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = `${environment.apiUrl}/api/cart`;
  private cartSubject = new BehaviorSubject<Cart>({
    items: [],
    totalPrice: 0
  });
  public cart$ = this.cartSubject.asObservable();

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {
    this.loadCart();
    
    // Subscribe to auth changes to handle cart synchronization
    this.authService.currentUser$.subscribe(user => {
      if (user && this.authService.isLoggedIn()) {
        // User logged in with valid token - fetch their server cart
        console.log('User logged in, fetching server cart');
        this.fetchServerCart();
      } else if (!user) {
        // User logged out - just load local cart
        console.log('User logged out, loading local cart');
        this.loadCart();
      } else {
        // User object exists but token might be invalid
        console.log('User object exists but token might be invalid');
        if (!this.authService.isLoggedIn()) {
          console.log('Token invalid, logging out');
          this.authService.logout();
        }
      }
    });
  }

  private loadCart(): void {
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
      this.cartSubject.next(JSON.parse(savedCart));
    }
  }

  private saveCart(cart: Cart): void {
    localStorage.setItem('cart', JSON.stringify(cart));
    this.cartSubject.next(cart);
    
    // If user is logged in, sync with server
    if (this.authService.isLoggedIn()) {
      this.syncCartWithServer(cart);
    }
  }
  
  private fetchServerCart(): void {
    // Double-check if user is logged in with a valid token
    if (!this.authService.isLoggedIn()) {
      console.log('User not logged in or token invalid, skipping server cart fetch');
      return;
    }
    
    // Try the main endpoint first
    this.http.get<Cart>(`${this.apiUrl}`).pipe(
      catchError(error => {
        console.log('Error fetching cart from main endpoint, trying /user endpoint', error);
        // If the main endpoint fails, try the /user endpoint as fallback
        return this.http.get<Cart>(`${this.apiUrl}/user`).pipe(
          catchError(userEndpointError => {
            console.error('Error fetching user cart from both endpoints', userEndpointError);
            
            // If we get a 401 or 403, the token is invalid or expired
            if (userEndpointError.status === 401 || userEndpointError.status === 403) {
              console.log('Authentication error during cart fetch, logging out');
              this.authService.logout();
            }
            
            return of(null);
          })
        );
      })
    ).subscribe(serverCart => {
      if (serverCart) {
        this.cartSubject.next(serverCart);
        localStorage.setItem('cart', JSON.stringify(serverCart));
      }
    });
  }
  
  private syncCartWithServer(cart: Cart): void {
    // First check if token is valid before attempting to sync
    const token = this.authService.getToken();
    if (!token) {
      console.log('No token available, skipping cart sync');
      return;
    }
    
    // Check if token is expired by trying to decode it
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      
      const { exp } = JSON.parse(jsonPayload);
      const currentTime = Date.now() / 1000;
      
      if (exp < currentTime) {
        console.log('Token expired, logging out and skipping cart sync');
        this.authService.logout();
        return;
      }
    } catch (error) {
      console.error('Error checking token validity', error);
      this.authService.logout();
      return;
    }
    
    // Create a deep copy of the cart to avoid circular reference issues
    const cartCopy: Cart = {
      items: cart.items.map(item => ({
        product: { 
          id: item.product.id,
          name: item.product.name,
          price: item.product.price,
          description: item.product.description,
          imageUrl: item.product.imageUrl,
          category: item.product.category,
          brand: item.product.brand,
          stockQuantity: item.product.stockQuantity
        },
        quantity: item.quantity
      })),
      totalPrice: cart.totalPrice
    };
    
    console.log('Sending cart to server:', JSON.stringify(cartCopy));
    
    // If we get here, token should be valid, so proceed with sync
    this.http.post<Cart>(`${this.apiUrl}/sync`, cartCopy).pipe(
      catchError(error => {
        console.error('Error syncing cart with server', error);
        
        // If we get a 401 or 403, the token is invalid or expired
        if (error.status === 401 || error.status === 403) {
          console.log('Authentication error during cart sync, logging out');
          this.authService.logout();
        }
        
        return of(null);
      })
    ).subscribe(response => {
      if (response) {
        console.log('Cart synced successfully with server');
      }
    });
  }

  addToCart(product: Product, quantity: number = 1): void {
    try {
      // Make sure product is valid
      if (!product || !product.id) {
        console.error('Invalid product object', product);
        return;
      }
      
      const currentCart = this.cartSubject.value;
      const existingItemIndex = currentCart.items.findIndex(item => 
        item.product && item.product.id === product.id
      );

      if (existingItemIndex !== -1) {
        // Update quantity if item already exists
        const updatedItems = [...currentCart.items];
        updatedItems[existingItemIndex] = {
          ...updatedItems[existingItemIndex],
          quantity: updatedItems[existingItemIndex].quantity + quantity
        };

        const updatedCart = {
          ...currentCart,
          items: updatedItems,
          totalPrice: this.calculateTotalPrice(updatedItems)
        };

        this.saveCart(updatedCart);
      } else {
        // Add new item - create a clean copy of the product to avoid circular references
        const productCopy = {
          id: product.id,
          name: product.name,
          description: product.description,
          price: product.price,
          imageUrl: product.imageUrl,
          category: product.category,
          brand: product.brand,
          stockQuantity: product.stockQuantity
        };
        
        const newItem: CartItem = {
          product: productCopy,
          quantity
        };

        const updatedCart = {
          ...currentCart,
          items: [...currentCart.items, newItem],
          totalPrice: this.calculateTotalPrice([...currentCart.items, newItem])
        };

        this.saveCart(updatedCart);
      }
    } catch (error) {
      console.error('Error adding product to cart', error);
    }
  }

  updateQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeFromCart(productId);
      return;
    }

    const currentCart = this.cartSubject.value;
    const existingItemIndex = currentCart.items.findIndex(item => item.product.id === productId);

    if (existingItemIndex !== -1) {
      const updatedItems = [...currentCart.items];
      updatedItems[existingItemIndex] = {
        ...updatedItems[existingItemIndex],
        quantity
      };

      const updatedCart = {
        ...currentCart,
        items: updatedItems,
        totalPrice: this.calculateTotalPrice(updatedItems)
      };

      this.saveCart(updatedCart);
    }
  }

  removeFromCart(productId: number): void {
    const currentCart = this.cartSubject.value;
    const updatedItems = currentCart.items.filter(item => item.product.id !== productId);

    const updatedCart = {
      ...currentCart,
      items: updatedItems,
      totalPrice: this.calculateTotalPrice(updatedItems)
    };

    this.saveCart(updatedCart);
  }

  clearCart(): void {
    const emptyCart: Cart = {
      items: [],
      totalPrice: 0
    };
    this.saveCart(emptyCart);
  }

  private calculateTotalPrice(items: CartItem[]): number {
    return items.reduce((total, item) => {
      return total + (item.product.price * item.quantity);
    }, 0);
  }

  getCartItemCount(): Observable<number> {
    return this.cart$.pipe(
      map(cart => {
        return cart.items.reduce((count, item) => count + item.quantity, 0);
      })
    );
  }

  // Server-side cart operations (for authenticated users)
  getServerCart(): Observable<Cart> {
    return this.http.get<Cart>(this.apiUrl);
  }

  syncCart(): Observable<Cart> {
    // Check if user is logged in with a valid token before syncing
    if (!this.authService.isLoggedIn()) {
      console.log('User not logged in or token invalid, returning empty observable');
      return of(this.cartSubject.value);
    }
    
    // Create a deep copy of the cart to avoid circular reference issues
    const cartCopy: Cart = {
      items: this.cartSubject.value.items.map(item => ({
        product: { 
          id: item.product.id,
          name: item.product.name,
          price: item.product.price,
          description: item.product.description,
          imageUrl: item.product.imageUrl,
          category: item.product.category,
          brand: item.product.brand,
          stockQuantity: item.product.stockQuantity
        },
        quantity: item.quantity
      })),
      totalPrice: this.cartSubject.value.totalPrice
    };
    
    console.log('Syncing cart with server', JSON.stringify(cartCopy));
    
    return this.http.post<Cart>(`${this.apiUrl}/sync`, cartCopy)
      .pipe(
        catchError(error => {
          console.error('Error in syncCart method', error);
          
          // If we get a 401 or 403, the token is invalid or expired
          if (error.status === 401 || error.status === 403) {
            console.log('Authentication error during cart sync, logging out');
            this.authService.logout();
          }
          
          // Return the current cart value from the subject
          return of(this.cartSubject.value);
        })
      );
  }
}