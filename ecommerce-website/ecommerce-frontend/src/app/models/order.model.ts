import { CartItem } from './cart.model';

export interface Address {
  id?: number;
  street: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
}

export interface Order {
  id?: number;
  items: CartItem[];
  totalPrice: number;
  shippingAddress: Address;
  billingAddress: Address;
  paymentMethod: string;
  status: string;
  createdAt?: Date;
}