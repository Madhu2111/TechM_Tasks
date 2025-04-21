export interface Venue {
  id?: number;
  name: string;
  city: string;
  country: string;
  capacity: number;
  address?: string;
  description?: string;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface VenueResponse {
  id: number;
  name: string;
  city: string;
  country: string;
  capacity: number;
  address: string | null;
  description: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface VenueFilters {
  city?: string;
  country?: string;
  minCapacity?: number;
}