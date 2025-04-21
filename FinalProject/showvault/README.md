# ShowVault

ShowVault is a comprehensive ticket booking platform for movies, theaters, concerts, and events. It provides a seamless experience for users to browse shows, select theaters, book seats, and manage their bookings.

## Features

- **Show Browsing**: Browse and search for movies, theaters, concerts, and events
- **Theater Selection**: View available theaters and showtimes for a selected show
- **Seat Selection**: Interactive seat map for selecting seats
- **Booking Management**: View and manage bookings
- **User Authentication**: Secure login and registration
- **Admin Dashboard**: Manage shows, theaters, and bookings

## New Features

### Theater Selection

The application now includes a dedicated theater selection page that allows users to:
- View all available theaters for a selected show
- See showtimes across different theaters
- Filter by date
- Select the number of tickets before proceeding to seat selection

### Interactive Seat Map

The seat selection page now features an interactive seat map that:
- Shows the theater layout with rows and seats
- Indicates available, reserved, and sold seats
- Highlights different seat categories (Standard, Premium, VIP)
- Allows users to select multiple seats
- Shows pricing information for each seat

## Setup Instructions

### Prerequisites

- Java 11 or higher
- Node.js 14 or higher
- MySQL 8.0 or higher

### Database Setup

1. Navigate to the `database` directory:
   ```
   cd database
   ```

2. Run the sample data script:
   ```
   run_sample_data.bat
   ```

3. Add sample venues and seats for the theater selection feature:
   ```
   run_add_venues_and_seats.bat
   ```

### Backend Setup

1. Navigate to the `backend` directory:
   ```
   cd backend
   ```

2. Build the application:
   ```
   ./mvnw clean package
   ```

3. Run the application:
   ```
   ./mvnw spring-boot:run
   ```

### Frontend Setup

1. Navigate to the `frontend` directory:
   ```
   cd frontend
   ```

2. Install dependencies:
   ```
   npm install
   ```

3. Run the application:
   ```
   ng serve
   ```

4. Open your browser and navigate to `http://localhost:4200`

## Technologies Used

- **Frontend**: Angular, Bootstrap
- **Backend**: Spring Boot, Spring Security, Spring Data JPA
- **Database**: MySQL
- **Authentication**: JWT

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.