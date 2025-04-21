// Additional interfaces for the Show module

export interface ShowSchedule {
  id?: number;
  showId?: number;
  showDate: string;
  showTime?: string;
  basePrice: number;
  venue?: Venue;
  availableSeats?: number;
  totalSeats?: number;
  status?: 'SCHEDULED' | 'CANCELLED' | 'COMPLETED';
  seatMap?: SeatMap;
}

export interface Venue {
  id?: number;
  name: string;
  address?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  capacity?: number;
  sections?: VenueSection[];
  amenities?: string[];
  contactInfo?: string;
  imageUrl?: string;
}

export interface VenueSection {
  id?: number;
  name: string;
  capacity: number;
  priceMultiplier: number; // e.g., 1.0 for standard, 1.5 for premium, 2.0 for VIP
  rows?: number;
  seatsPerRow?: number;
}

export interface SeatMap {
  id?: number;
  scheduleId?: number;
  sections: SeatMapSection[];
  rows: number;
  columns: number;
  layout: string; // JSON string representing the layout
}

export interface SeatMapSection {
  id?: number;
  name: string;
  rows: SeatMapRow[];
  priceCategory: 'STANDARD' | 'PREMIUM' | 'VIP';
  priceMultiplier: number;
}

export interface SeatMapRow {
  rowNumber: number;
  rowLabel: string; // e.g., "A", "B", "C"
  seats: SeatMapSeat[];
}

export interface SeatMapSeat {
  id?: number;
  seatNumber: number;
  status: 'AVAILABLE' | 'RESERVED' | 'SOLD' | 'DISABLED';
  price: number;
  category: 'STANDARD' | 'PREMIUM' | 'VIP';
}

export interface ShowSeat {
  id?: number;
  row: string;
  seatNumber: number;
  status: 'AVAILABLE' | 'RESERVED' | 'SOLD';
  price: number;
  category?: 'STANDARD' | 'PREMIUM' | 'VIP';
}

export interface ShowAnalytics {
  id?: number;
  showId: number;
  totalBookings: number;
  totalRevenue: number;
  averageOccupancy: number;
  totalViews: number;
  averageTicketPrice: number;
  occupancyRate: number;
  salesByCategory: { [key: string]: number };
  peakBookingTimes?: { [key: string]: number };
  demographicData?: { [key: string]: number };
  salesByDay?: { [key: string]: number };
  refundRate?: number;
  audienceDemographics?: {
    ageGroups: { [key: string]: number };
  };
}

export interface Promotion {
  id?: number;
  showId: number;
  name: string;
  code: string;
  description?: string;
  discountType: 'PERCENTAGE' | 'FIXED';
  discountValue: number;
  startDate: Date;
  endDate: Date;
  currentUses: number;
  maxUses: number;
  usedCount?: number;
  minPurchaseAmount?: number;
  status: 'ACTIVE' | 'INACTIVE' | 'EXPIRED';
}