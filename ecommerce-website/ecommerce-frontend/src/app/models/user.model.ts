export interface User {
  id?: number;
  username: string;
  email: string;
  role: string;
}

export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

export interface SignUpRequest {
  username: string;
  email: string;
  password: string;
  role?: string;
}

export interface JwtAuthResponse {
  accessToken: string;
  tokenType: string;
}