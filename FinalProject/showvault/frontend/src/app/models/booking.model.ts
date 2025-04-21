import { Show, ShowSchedule, ShowSeat } from './show.model';

export interface Booking {
  id?: number;
  bookingNumber?: string;
  user?: {
    id: number;
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    phoneNumber?: string;
  };
  showSchedule?: {
    id: number;
    showDate: Date;
    startTime: string;
    endTime: string;
    basePrice: number;
    venue?: {
      id: number;
      name: string;
    };
    show?: {
      id: number;
      title: string;
      description: string;
      type: string;
      duration: number;
      posterUrl?: string;
    };
  };
  seatBookings?: {
    id: number;
    seat: {
      id: number;
      rowName: string;
      seatNumber: number;
      category: string;
      priceMultiplier: number;
    };
    price: number;
  }[];
  totalAmount: number;
  bookingDate?: Date;
  status: string | BookingStatus;
  createdAt?: Date;
  updatedAt?: Date;
  
  // Legacy properties for backward compatibility
  userId?: number;
  showId?: number;
  scheduleId?: number;
  seats?: BookingSeat[];
  paymentId?: string;
  paymentStatus?: PaymentStatus | PaymentStatusType;
  show?: Show;
  schedule?: ShowSchedule;
  confirmationCode?: string;
  customerName?: string;
  customerEmail?: string;
  customerPhone?: string;
  showName?: string;
  showDate?: Date;
  showDateTime?: Date;
  refundReason?: string;
  userName?: string;
  userEmail?: string;
  userPhone?: string;
  venueName?: string;
  paymentMethod?: string;
  transactionId?: string;
  ticketPrice?: number;
  notes?: string;
}

export interface BookingSeat {
  id?: number;
  bookingId?: number;
  seatId: number;
  row: string;
  seatNumber: number;
  price: number;
  category?: 'STANDARD' | 'PREMIUM' | 'VIP';
}

export enum BookingStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  CANCELLED = 'CANCELLED',
  EXPIRED = 'EXPIRED',
  REFUND_REQUESTED = 'REFUND_REQUESTED',
  REFUNDED = 'REFUNDED'
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  REFUNDED = 'REFUNDED'
}

// Allow string literals for PaymentStatus
export type PaymentStatusType = 'PENDING' | 'COMPLETED' | 'FAILED' | 'REFUNDED';

export interface PaymentMethod {
  id: string;
  type: 'CREDIT_CARD' | 'DEBIT_CARD' | 'PAYPAL' | 'GOOGLE_PAY' | 'APPLE_PAY' | 'MOCK';
  name: string;
  icon: string;
  lastFour?: string;
  expiryDate?: string;
}

export interface PaymentIntent {
  id: string;
  bookingId: number;
  amount: number;
  currency: string;
  status: PaymentStatus | PaymentStatusType;
  paymentMethod?: PaymentMethod;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface SeatSelectionMap {
  rows: SeatRow[];
  screen?: string;
  legend?: SeatLegend;
}

export interface SeatRow {
  rowLabel: string;
  seats: SeatInfo[];
}

export interface SeatInfo {
  id: number;
  seatNumber: number;
  status: 'AVAILABLE' | 'RESERVED' | 'SOLD' | 'SELECTED';
  price: number;
  category?: 'STANDARD' | 'PREMIUM' | 'VIP';
  isSelected?: boolean;
}

export interface SeatLegend {
  available: string;
  reserved: string;
  sold: string;
  selected: string;
  standard?: string;
  premium?: string;
  vip?: string;
}

export interface BookingSummary {
  show: {
    id: number;
    title: string;
    type: string;
    image?: string;
  };
  schedule: {
    id: number;
    date: string;
    time: string;
    venue: string;
  };
  seats: {
    count: number;
    details: {
      row: string;
      seatNumber: number;
      price: number;
      category?: string;
    }[];
  };
  pricing: {
    subtotal: number;
    fees: number;
    taxes: number;
    total: number;
  };
  customer?: {
    name: string;
    email: string;
    phone?: string;
  };
}

export interface BookingRequest {
  showId: number;
  scheduleId: number;
  seats: BookingSeat[];
  paymentMethodId: string;
  customerName: string;
  customerEmail: string;
  customerPhone?: string;
}

export interface BookingResponse {
  booking: Booking;
  success: boolean;
  message?: string;
  confirmationCode?: string;
  paymentIntent?: PaymentIntent;
}