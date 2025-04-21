export interface Venue {
  id: number;
  name: string;
  location: string;
  capacity: number;
  seatCategories: SeatCategory[];
}

export interface SeatCategory {
  type: string;
  capacity: number;
  price: number;
}