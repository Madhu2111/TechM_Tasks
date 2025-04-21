export interface User {
  id?: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  role: 'ROLE_USER' | 'ROLE_ORGANIZER' | 'ROLE_ADMIN';
  status: 'active' | 'suspended' | 'inactive';
  createdAt?: Date;
  lastLogin?: Date;
  phone?: string;
  address?: string;
  name?: string; // Full name (computed from firstName and lastName)
  profilePicture?: string; // URL to profile picture
  preferences?: UserPreferences; // User preferences
}

export interface AuthResponse {
  user: User;
  token?: string;
  accessToken?: string; // Backend might use this name instead of token
  tokenType?: string;
  expiresIn?: number;
}

export interface LoginRequest {
  username: string;
  password: string;
  email?: string; // Optional since we're using username as email
}

export interface RegisterRequest extends LoginRequest {
  firstName: string;
  lastName: string;
  roles?: string[]; // Array of role names without 'ROLE_' prefix
  termsAccepted: boolean;
  phoneNumber?: string;
}

export interface UserPreferences {
  emailNotifications: boolean;
  smsNotifications: boolean;
  favoriteCategories?: string[];
  language?: string;
  currency?: string;
}

export interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}