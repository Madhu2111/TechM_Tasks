export interface Show {
  id?: number;
  title: string;
  type: 'Movie' | 'Theater' | 'Concert' | 'Event' | 'Other';
  image: string;
  imageUrl?: string;
  image_url?: string;
  date: string;
  time?: string;
  venue: string;
  price: number;
  description?: string;
  duration?: number;
  availableSeats?: number;
  totalSeats?: number;
  organizer?: number; // organizer ID
  status?: 'UPCOMING' | 'ONGOING' | 'COMPLETED' | 'CANCELLED';
  genre?: string;
  category?: string;
  rating?: number;
  reviews?: number;
  posterUrl?: string;
  poster_url?: string;
  trailer_url?: string;
  trailerUrl?: string;
  language?: string;
  schedules?: ShowSchedule[];
  createdAt?: Date;
  updatedAt?: Date;
  created_at?: Date;
  updated_at?: Date;
}

export interface ShowSchedule {
  id?: number;
  showId?: number;
  show_id?: number;
  showDate: string;
  show_date?: string;
  showTime?: string;
  show_time?: string;
  basePrice: number;
  base_price?: number;
  venue?: Venue;
  availableSeats?: number;
  available_seats?: number;
  totalSeats?: number;
  total_seats?: number;
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
  image_url?: string;
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

export interface ShowFilter {
  type?: string;
  date?: string;
  dateFrom?: string;
  dateTo?: string;
  venue?: string;
  priceMin?: number;
  priceMax?: number;
  search?: string;
  status?: string;
  genre?: string;
  page?: number;
  pageSize?: number;
  sort?: string;
}

export interface ShowsResponse {
  content: Show[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
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

export interface AudienceDemographics {
  ageGroups: { [range: string]: number }; // e.g., "18-24": 30, "25-34": 45
  genderDistribution: { [gender: string]: number }; // e.g., "male": 55, "female": 45
  locationDistribution: { [location: string]: number }; // e.g., "New York": 30, "Los Angeles": 20
}

export interface Promotion {
  id?: number;
  showId?: number;
  code: string;
  type: 'PERCENTAGE' | 'FIXED_AMOUNT' | 'BUY_X_GET_Y';
  value: number; // percentage or amount
  startDate: string;
  endDate: string;
  maxUses?: number;
  currentUses?: number;
  minPurchaseAmount?: number;
  applicableCategories?: string[];
  description?: string;
  active: boolean;
}

export interface ShowReview {
  id?: number;
  showId: number;
  userId: number;
  userName?: string;
  rating: number;
  comment?: string;
  createdAt?: Date;
}

export interface SeatMapRow {
  rowNumber: number;
  rowLabel: string; // e.g., "A", "B", "C"
  seats: Seat[];
}

export interface Seat {
  id?: number;
  seatNumber: number;
  status: 'AVAILABLE' | 'RESERVED' | 'SOLD' | 'DISABLED';
  price: number;
  category: 'STANDARD' | 'PREMIUM' | 'VIP';
}

export interface ShowSeat {
  id?: number;
  scheduleId: number;
  row: string;
  seatNumber: number;
  status: 'AVAILABLE' | 'RESERVED' | 'SOLD';
  price: number;
  category?: 'STANDARD' | 'PREMIUM' | 'VIP';
}