package com.showvault.config;

import com.showvault.models.ERole;
import com.showvault.models.Role;
import com.showvault.models.User;
import com.showvault.model.Booking;
import com.showvault.model.BookingPayment;
import com.showvault.model.BookingStatus;
import com.showvault.model.Seat;
import com.showvault.model.SeatBooking;
import com.showvault.model.Show;
import com.showvault.model.ShowSchedule;
import com.showvault.model.Venue;
import com.showvault.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * This class initializes the database with required data when the application starts.
 * It runs after Hibernate has created all the tables.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VenueRepository venueRepository;
    
    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private ShowRepository showRepository;
    
    @Autowired
    private ShowScheduleRepository showScheduleRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private SeatBookingRepository seatBookingRepository;
    
    @Autowired
    private BookingPaymentRepository bookingPaymentRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Initialize data if it doesn't exist
        initRoles();
        initUsers();
        initVenues();
        initSeats();
        initShows();
        initShowSchedules();
        initBookings();
    }

    private void initRoles() {
        // Check if roles already exist
        if (roleRepository.count() == 0) {
            // Create roles
            Arrays.asList(ERole.values()).forEach(role -> {
                roleRepository.save(new Role(role));
            });
            
            System.out.println("Roles initialized successfully");
        } else {
            System.out.println("Roles already initialized");
        }
    }
    
    private void initUsers() {
        // Check if users already exist
        if (userRepository.count() == 0) {
            // Get roles
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Role not found"));
            Role organizerRole = roleRepository.findByName(ERole.ROLE_ORGANIZER)
                .orElseThrow(() -> new RuntimeException("Role not found"));
            
            // Create admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@showvault.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setActive(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);
            
            // Create test user
            User testUser = new User();
            testUser.setUsername("testuser");
            testUser.setEmail("test@showvault.com");
            testUser.setPassword(passwordEncoder.encode("test123"));
            testUser.setFirstName("Test");
            testUser.setLastName("User");
            testUser.setActive(true);
            testUser.setCreatedAt(LocalDateTime.now());
            testUser.setUpdatedAt(LocalDateTime.now());
            testUser.setRoles(Set.of(userRole));
            userRepository.save(testUser);
            
            // Create organizer user
            User organizer = new User();
            organizer.setUsername("organizer");
            organizer.setEmail("organizer@showvault.com");
            organizer.setPassword(passwordEncoder.encode("organizer123"));
            organizer.setFirstName("Event");
            organizer.setLastName("Organizer");
            organizer.setActive(true);
            organizer.setCreatedAt(LocalDateTime.now());
            organizer.setUpdatedAt(LocalDateTime.now());
            organizer.setRoles(Set.of(organizerRole));
            userRepository.save(organizer);
            
            // Create additional regular users
            User johnDoe = new User();
            johnDoe.setUsername("john.doe");
            johnDoe.setEmail("john.doe@example.com");
            johnDoe.setPassword(passwordEncoder.encode("test123"));
            johnDoe.setFirstName("John");
            johnDoe.setLastName("Doe");
            johnDoe.setPhoneNumber("1234567890");
            johnDoe.setActive(true);
            johnDoe.setCreatedAt(LocalDateTime.now());
            johnDoe.setUpdatedAt(LocalDateTime.now());
            johnDoe.setRoles(Set.of(userRole));
            userRepository.save(johnDoe);
            
            User janeSmith = new User();
            janeSmith.setUsername("jane.smith");
            janeSmith.setEmail("jane.smith@example.com");
            janeSmith.setPassword(passwordEncoder.encode("test123"));
            janeSmith.setFirstName("Jane");
            janeSmith.setLastName("Smith");
            janeSmith.setPhoneNumber("9876543210");
            janeSmith.setActive(true);
            janeSmith.setCreatedAt(LocalDateTime.now());
            janeSmith.setUpdatedAt(LocalDateTime.now());
            janeSmith.setRoles(Set.of(userRole));
            userRepository.save(janeSmith);
            
            User bobJohnson = new User();
            bobJohnson.setUsername("bob.johnson");
            bobJohnson.setEmail("bob.johnson@example.com");
            bobJohnson.setPassword(passwordEncoder.encode("test123"));
            bobJohnson.setFirstName("Bob");
            bobJohnson.setLastName("Johnson");
            bobJohnson.setPhoneNumber("5551234567");
            bobJohnson.setActive(true);
            bobJohnson.setCreatedAt(LocalDateTime.now());
            bobJohnson.setUpdatedAt(LocalDateTime.now());
            bobJohnson.setRoles(Set.of(userRole));
            userRepository.save(bobJohnson);
            
            User aliceWilliams = new User();
            aliceWilliams.setUsername("alice.williams");
            aliceWilliams.setEmail("alice.williams@example.com");
            aliceWilliams.setPassword(passwordEncoder.encode("test123"));
            aliceWilliams.setFirstName("Alice");
            aliceWilliams.setLastName("Williams");
            aliceWilliams.setPhoneNumber("7778889999");
            aliceWilliams.setActive(true);
            aliceWilliams.setCreatedAt(LocalDateTime.now());
            aliceWilliams.setUpdatedAt(LocalDateTime.now());
            aliceWilliams.setRoles(Set.of(userRole));
            userRepository.save(aliceWilliams);
            
            System.out.println("Users initialized successfully");
        } else {
            System.out.println("Users already initialized");
        }
    }
    
    private void initVenues() {
        // Check if venues already exist
        if (venueRepository.count() == 0) {
            // Create venues
            Venue grandTheater = new Venue();
            grandTheater.setName("Grand Theater");
            grandTheater.setAddress("123 Main Street");
            grandTheater.setCity("New York");
            grandTheater.setState("NY");
            grandTheater.setCountry("USA");
            grandTheater.setCapacity(500);
            grandTheater.setCreatedAt(LocalDateTime.now());
            grandTheater.setUpdatedAt(LocalDateTime.now());
            venueRepository.save(grandTheater);
            
            Venue cityConcertHall = new Venue();
            cityConcertHall.setName("City Concert Hall");
            cityConcertHall.setAddress("456 Broadway");
            cityConcertHall.setCity("Los Angeles");
            cityConcertHall.setState("CA");
            cityConcertHall.setCountry("USA");
            cityConcertHall.setCapacity(1000);
            cityConcertHall.setCreatedAt(LocalDateTime.now());
            cityConcertHall.setUpdatedAt(LocalDateTime.now());
            venueRepository.save(cityConcertHall);
            
            Venue starlightArena = new Venue();
            starlightArena.setName("Starlight Arena");
            starlightArena.setAddress("789 Park Avenue");
            starlightArena.setCity("Chicago");
            starlightArena.setState("IL");
            starlightArena.setCountry("USA");
            starlightArena.setCapacity(2000);
            starlightArena.setCreatedAt(LocalDateTime.now());
            starlightArena.setUpdatedAt(LocalDateTime.now());
            venueRepository.save(starlightArena);
            
            Venue royalPlayhouse = new Venue();
            royalPlayhouse.setName("Royal Playhouse");
            royalPlayhouse.setAddress("321 Queen Street");
            royalPlayhouse.setCity("Toronto");
            royalPlayhouse.setState("ON");
            royalPlayhouse.setCountry("Canada");
            royalPlayhouse.setCapacity(300);
            royalPlayhouse.setCreatedAt(LocalDateTime.now());
            royalPlayhouse.setUpdatedAt(LocalDateTime.now());
            venueRepository.save(royalPlayhouse);
            
            Venue metroStadium = new Venue();
            metroStadium.setName("Metro Stadium");
            metroStadium.setAddress("555 Stadium Road");
            metroStadium.setCity("Dallas");
            metroStadium.setState("TX");
            metroStadium.setCountry("USA");
            metroStadium.setCapacity(5000);
            metroStadium.setCreatedAt(LocalDateTime.now());
            metroStadium.setUpdatedAt(LocalDateTime.now());
            venueRepository.save(metroStadium);
            
            System.out.println("Venues initialized successfully");
        } else {
            System.out.println("Venues already initialized");
        }
    }
    
    private void initSeats() {
        // Check if seats already exist
        if (seatRepository.count() == 0) {
            List<Venue> venues = venueRepository.findAll();
            if (venues.isEmpty()) {
                System.out.println("No venues found, skipping seat initialization");
                return;
            }
            
            // Get the first venue (Grand Theater)
            Venue grandTheater = venues.stream()
                .filter(v -> v.getName().equals("Grand Theater"))
                .findFirst()
                .orElse(venues.get(0));
                
            // Get the second venue (City Concert Hall)
            Venue cityConcertHall = venues.stream()
                .filter(v -> v.getName().equals("City Concert Hall"))
                .findFirst()
                .orElse(venues.size() > 1 ? venues.get(1) : venues.get(0));
                
            // Get the third venue (Starlight Arena)
            Venue starlightArena = venues.stream()
                .filter(v -> v.getName().equals("Starlight Arena"))
                .findFirst()
                .orElse(venues.size() > 2 ? venues.get(2) : venues.get(0));
                
            // Get the fourth venue (Royal Playhouse)
            Venue royalPlayhouse = venues.stream()
                .filter(v -> v.getName().equals("Royal Playhouse"))
                .findFirst()
                .orElse(venues.size() > 3 ? venues.get(3) : venues.get(0));
                
            // Get the fifth venue (Metro Stadium)
            Venue metroStadium = venues.stream()
                .filter(v -> v.getName().equals("Metro Stadium"))
                .findFirst()
                .orElse(venues.size() > 4 ? venues.get(4) : venues.get(0));
            
            // Create seats for Grand Theater
            // Standard seats (rows A-E)
            for (char row = 'A'; row <= 'E'; row++) {
                for (int seatNum = 1; seatNum <= 10; seatNum++) {
                    Seat seat = new Seat();
                    seat.setVenue(grandTheater);
                    seat.setRowName(String.valueOf(row));
                    seat.setSeatNumber(seatNum);
                    seat.setCategory(Seat.SeatCategory.STANDARD);
                    seat.setPriceMultiplier(BigDecimal.valueOf(1.0));
                    seatRepository.save(seat);
                }
            }
            
            // Premium seats (rows F-H)
            for (char row = 'F'; row <= 'H'; row++) {
                for (int seatNum = 1; seatNum <= 10; seatNum++) {
                    Seat seat = new Seat();
                    seat.setVenue(grandTheater);
                    seat.setRowName(String.valueOf(row));
                    seat.setSeatNumber(seatNum);
                    seat.setCategory(Seat.SeatCategory.PREMIUM);
                    seat.setPriceMultiplier(BigDecimal.valueOf(1.5));
                    seatRepository.save(seat);
                }
            }
            
            // VIP seats (rows I-J)
            for (char row = 'I'; row <= 'J'; row++) {
                for (int seatNum = 1; seatNum <= 10; seatNum++) {
                    Seat seat = new Seat();
                    seat.setVenue(grandTheater);
                    seat.setRowName(String.valueOf(row));
                    seat.setSeatNumber(seatNum);
                    seat.setCategory(Seat.SeatCategory.VIP);
                    seat.setPriceMultiplier(BigDecimal.valueOf(2.0));
                    seatRepository.save(seat);
                }
            }
            
            // Create seats for City Concert Hall
            // Standard seats (rows A-G)
            for (char row = 'A'; row <= 'G'; row++) {
                for (int seatNum = 1; seatNum <= 15; seatNum++) {
                    Seat seat = new Seat();
                    seat.setVenue(cityConcertHall);
                    seat.setRowName(String.valueOf(row));
                    seat.setSeatNumber(seatNum);
                    seat.setCategory(Seat.SeatCategory.STANDARD);
                    seat.setPriceMultiplier(BigDecimal.valueOf(1.0));
                    seatRepository.save(seat);
                }
            }
            
            // Premium seats (rows H-K)
            for (char row = 'H'; row <= 'K'; row++) {
                for (int seatNum = 1; seatNum <= 15; seatNum++) {
                    Seat seat = new Seat();
                    seat.setVenue(cityConcertHall);
                    seat.setRowName(String.valueOf(row));
                    seat.setSeatNumber(seatNum);
                    seat.setCategory(Seat.SeatCategory.PREMIUM);
                    seat.setPriceMultiplier(BigDecimal.valueOf(1.5));
                    seatRepository.save(seat);
                }
            }
            
            // Create basic seats for other venues
            // Starlight Arena
            for (char row = 'A'; row <= 'J'; row++) {
                for (int seatNum = 1; seatNum <= 20; seatNum++) {
                    Seat seat = new Seat();
                    seat.setVenue(starlightArena);
                    seat.setRowName(String.valueOf(row));
                    seat.setSeatNumber(seatNum);
                    seat.setCategory(row <= 'E' ? Seat.SeatCategory.STANDARD : 
                                    row <= 'H' ? Seat.SeatCategory.PREMIUM : 
                                    Seat.SeatCategory.VIP);
                    seat.setPriceMultiplier(row <= 'E' ? BigDecimal.valueOf(1.0) : 
                                           row <= 'H' ? BigDecimal.valueOf(1.5) : 
                                           BigDecimal.valueOf(2.0));
                    seatRepository.save(seat);
                }
            }
            
            // Royal Playhouse
            for (char row = 'A'; row <= 'F'; row++) {
                for (int seatNum = 1; seatNum <= 10; seatNum++) {
                    Seat seat = new Seat();
                    seat.setVenue(royalPlayhouse);
                    seat.setRowName(String.valueOf(row));
                    seat.setSeatNumber(seatNum);
                    seat.setCategory(row <= 'C' ? Seat.SeatCategory.STANDARD : 
                                row <= 'E' ? Seat.SeatCategory.PREMIUM : 
                                Seat.SeatCategory.VIP);
                    seat.setPriceMultiplier(row <= 'C' ? BigDecimal.valueOf(1.0) : 
                                           row <= 'E' ? BigDecimal.valueOf(1.5) : 
                                           BigDecimal.valueOf(2.0));
                    seatRepository.save(seat);
                }
            }
            
            // Metro Stadium
            for (char row = 'A'; row <= 'Z'; row++) {
                for (int seatNum = 1; seatNum <= 20; seatNum++) {
                    if (row > 'J') break; // Limit the number of seats for demo purposes
                    Seat seat = new Seat();
                    seat.setVenue(metroStadium);
                    seat.setRowName(String.valueOf(row));
                    seat.setSeatNumber(seatNum);
                    seat.setCategory(row <= 'F' ? Seat.SeatCategory.STANDARD : 
                                row <= 'I' ? Seat.SeatCategory.PREMIUM : 
                                Seat.SeatCategory.VIP);
                    seat.setPriceMultiplier(row <= 'F' ? BigDecimal.valueOf(1.0) : 
                                           row <= 'I' ? BigDecimal.valueOf(1.5) : 
                                           BigDecimal.valueOf(2.0));
                    seatRepository.save(seat);
                }
            }
            
            System.out.println("Seats initialized successfully");
        } else {
            System.out.println("Seats already initialized");
        }
    }
    
    private void initShows() {
        // Check if shows already exist
        if (showRepository.count() == 0) {
            // Get the organizer user or admin as fallback
            User organizer = userRepository.findByUsername("organizer")
                .orElse(userRepository.findByUsername("admin")
                    .orElse(userRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("No users found in the database"))));
            
            // Create shows
            Show hamilton = new Show();
            hamilton.setTitle("Hamilton");
            hamilton.setDescription("A musical about the life of American Founding Father Alexander Hamilton.");
            hamilton.setType("Theater");
            hamilton.setDuration(180);
            hamilton.setGenre("Musical");
            hamilton.setLanguage("English");
            hamilton.setPosterUrl("https://example.com/posters/hamilton.jpg");
            hamilton.setTrailerUrl("https://example.com/trailers/hamilton.mp4");
            hamilton.setStatus(Show.ShowStatus.UPCOMING);
            hamilton.setCreatedBy(organizer);
            hamilton.setCreatedAt(LocalDateTime.now());
            hamilton.setUpdatedAt(LocalDateTime.now());
            showRepository.save(hamilton);
            
            Show lionKing = new Show();
            lionKing.setTitle("The Lion King");
            lionKing.setDescription("A musical based on the 1994 Walt Disney Animation Studios film of the same name.");
            lionKing.setType("Theater");
            lionKing.setDuration(150);
            lionKing.setGenre("Musical");
            lionKing.setLanguage("English");
            lionKing.setPosterUrl("https://example.com/posters/lionking.jpg");
            lionKing.setTrailerUrl("https://example.com/trailers/lionking.mp4");
            lionKing.setStatus(Show.ShowStatus.UPCOMING);
            lionKing.setCreatedBy(organizer);
            lionKing.setCreatedAt(LocalDateTime.now());
            lionKing.setUpdatedAt(LocalDateTime.now());
            showRepository.save(lionKing);
            
            Show cirqueDuSoleil = new Show();
            cirqueDuSoleil.setTitle("Cirque du Soleil: Alegría");
            cirqueDuSoleil.setDescription("A classic Cirque du Soleil show that showcases breathtaking acrobatics.");
            cirqueDuSoleil.setType("Event");
            cirqueDuSoleil.setDuration(120);
            cirqueDuSoleil.setGenre("Circus");
            cirqueDuSoleil.setLanguage("Multiple");
            cirqueDuSoleil.setPosterUrl("https://example.com/posters/alegria.jpg");
            cirqueDuSoleil.setTrailerUrl("https://example.com/trailers/alegria.mp4");
            cirqueDuSoleil.setStatus(Show.ShowStatus.UPCOMING);
            cirqueDuSoleil.setCreatedBy(organizer);
            cirqueDuSoleil.setCreatedAt(LocalDateTime.now());
            cirqueDuSoleil.setUpdatedAt(LocalDateTime.now());
            showRepository.save(cirqueDuSoleil);
            
            Show rockSymphony = new Show();
            rockSymphony.setTitle("Rock Symphony");
            rockSymphony.setDescription("A symphony orchestra performing classic rock hits.");
            rockSymphony.setType("Concert");
            rockSymphony.setDuration(90);
            rockSymphony.setGenre("Concert");
            rockSymphony.setLanguage("Instrumental");
            rockSymphony.setPosterUrl("https://example.com/posters/rocksymphony.jpg");
            rockSymphony.setTrailerUrl("https://example.com/trailers/rocksymphony.mp4");
            rockSymphony.setStatus(Show.ShowStatus.UPCOMING);
            rockSymphony.setCreatedBy(organizer);
            rockSymphony.setCreatedAt(LocalDateTime.now());
            rockSymphony.setUpdatedAt(LocalDateTime.now());
            showRepository.save(rockSymphony);
            
            Show comedyNight = new Show();
            comedyNight.setTitle("Comedy Night");
            comedyNight.setDescription("A night of stand-up comedy featuring top comedians.");
            comedyNight.setType("Event");
            comedyNight.setDuration(120);
            comedyNight.setGenre("Comedy");
            comedyNight.setLanguage("English");
            comedyNight.setPosterUrl("https://example.com/posters/comedynight.jpg");
            comedyNight.setTrailerUrl("https://example.com/trailers/comedynight.mp4");
            comedyNight.setStatus(Show.ShowStatus.UPCOMING);
            comedyNight.setCreatedBy(organizer);
            comedyNight.setCreatedAt(LocalDateTime.now());
            comedyNight.setUpdatedAt(LocalDateTime.now());
            showRepository.save(comedyNight);
            
            System.out.println("Shows initialized successfully");
        } else {
            System.out.println("Shows already initialized");
        }
    }
    
    private void initShowSchedules() {
        // Check if show schedules already exist
        if (showScheduleRepository.count() == 0) {
            List<Show> shows = showRepository.findAll();
            List<Venue> venues = venueRepository.findAll();
            
            if (shows.isEmpty() || venues.isEmpty()) {
                System.out.println("No shows or venues found, skipping show schedule initialization");
                return;
            }
            
            // Get shows
            Show hamilton = shows.stream()
                .filter(s -> s.getTitle().equals("Hamilton"))
                .findFirst()
                .orElse(shows.get(0));
                
            Show lionKing = shows.stream()
                .filter(s -> s.getTitle().equals("The Lion King"))
                .findFirst()
                .orElse(shows.size() > 1 ? shows.get(1) : shows.get(0));
                
            Show cirqueDuSoleil = shows.stream()
                .filter(s -> s.getTitle().equals("Cirque du Soleil: Alegría"))
                .findFirst()
                .orElse(shows.size() > 2 ? shows.get(2) : shows.get(0));
                
            Show rockSymphony = shows.stream()
                .filter(s -> s.getTitle().equals("Rock Symphony"))
                .findFirst()
                .orElse(shows.size() > 3 ? shows.get(3) : shows.get(0));
                
            Show comedyNight = shows.stream()
                .filter(s -> s.getTitle().equals("Comedy Night"))
                .findFirst()
                .orElse(shows.size() > 4 ? shows.get(4) : shows.get(0));
                
            // Get venues
            Venue grandTheater = venues.stream()
                .filter(v -> v.getName().equals("Grand Theater"))
                .findFirst()
                .orElse(venues.get(0));
                
            Venue cityConcertHall = venues.stream()
                .filter(v -> v.getName().equals("City Concert Hall"))
                .findFirst()
                .orElse(venues.size() > 1 ? venues.get(1) : venues.get(0));
                
            Venue starlightArena = venues.stream()
                .filter(v -> v.getName().equals("Starlight Arena"))
                .findFirst()
                .orElse(venues.size() > 2 ? venues.get(2) : venues.get(0));
                
            Venue royalPlayhouse = venues.stream()
                .filter(v -> v.getName().equals("Royal Playhouse"))
                .findFirst()
                .orElse(venues.size() > 3 ? venues.get(3) : venues.get(0));
                
            Venue metroStadium = venues.stream()
                .filter(v -> v.getName().equals("Metro Stadium"))
                .findFirst()
                .orElse(venues.size() > 4 ? venues.get(4) : venues.get(0));
            
            // Create show schedules
            // Hamilton at Grand Theater
            ShowSchedule hamiltonSchedule1 = new ShowSchedule();
            hamiltonSchedule1.setShow(hamilton);
            hamiltonSchedule1.setVenue(grandTheater);
            hamiltonSchedule1.setShowDate(LocalDate.now().plusDays(7));
            hamiltonSchedule1.setStartTime(LocalTime.of(19, 0));
            hamiltonSchedule1.setEndTime(LocalTime.of(22, 0));
            hamiltonSchedule1.setBasePrice(BigDecimal.valueOf(75.00));
            hamiltonSchedule1.setStatus(ShowSchedule.ScheduleStatus.SCHEDULED);
            hamiltonSchedule1.setCreatedAt(LocalDateTime.now());
            hamiltonSchedule1.setUpdatedAt(LocalDateTime.now());
            showScheduleRepository.save(hamiltonSchedule1);
            
            ShowSchedule hamiltonSchedule2 = new ShowSchedule();
            hamiltonSchedule2.setShow(hamilton);
            hamiltonSchedule2.setVenue(grandTheater);
            hamiltonSchedule2.setShowDate(LocalDate.now().plusDays(8));
            hamiltonSchedule2.setStartTime(LocalTime.of(19, 0));
            hamiltonSchedule2.setEndTime(LocalTime.of(22, 0));
            hamiltonSchedule2.setBasePrice(BigDecimal.valueOf(75.00));
            hamiltonSchedule2.setStatus(ShowSchedule.ScheduleStatus.SCHEDULED);
            hamiltonSchedule2.setCreatedAt(LocalDateTime.now());
            hamiltonSchedule2.setUpdatedAt(LocalDateTime.now());
            showScheduleRepository.save(hamiltonSchedule2);
            
            ShowSchedule hamiltonSchedule3 = new ShowSchedule();
            hamiltonSchedule3.setShow(hamilton);
            hamiltonSchedule3.setVenue(grandTheater);
            hamiltonSchedule3.setShowDate(LocalDate.now().plusDays(9));
            hamiltonSchedule3.setStartTime(LocalTime.of(14, 0));
            hamiltonSchedule3.setEndTime(LocalTime.of(17, 0));
            hamiltonSchedule3.setBasePrice(BigDecimal.valueOf(65.00));
            hamiltonSchedule3.setStatus(ShowSchedule.ScheduleStatus.SCHEDULED);
            hamiltonSchedule3.setCreatedAt(LocalDateTime.now());
            hamiltonSchedule3.setUpdatedAt(LocalDateTime.now());
            showScheduleRepository.save(hamiltonSchedule3);
            
            // The Lion King at City Concert Hall
            ShowSchedule lionKingSchedule1 = new ShowSchedule();
            lionKingSchedule1.setShow(lionKing);
            lionKingSchedule1.setVenue(cityConcertHall);
            lionKingSchedule1.setShowDate(LocalDate.now().plusDays(14));
            lionKingSchedule1.setStartTime(LocalTime.of(19, 30));
            lionKingSchedule1.setEndTime(LocalTime.of(22, 0));
            lionKingSchedule1.setBasePrice(BigDecimal.valueOf(85.00));
            lionKingSchedule1.setStatus(ShowSchedule.ScheduleStatus.SCHEDULED);
            lionKingSchedule1.setCreatedAt(LocalDateTime.now());
            lionKingSchedule1.setUpdatedAt(LocalDateTime.now());
            showScheduleRepository.save(lionKingSchedule1);
            
            ShowSchedule lionKingSchedule2 = new ShowSchedule();
            lionKingSchedule2.setShow(lionKing);
            lionKingSchedule2.setVenue(cityConcertHall);
            lionKingSchedule2.setShowDate(LocalDate.now().plusDays(15));
            lionKingSchedule2.setStartTime(LocalTime.of(19, 30));
            lionKingSchedule2.setEndTime(LocalTime.of(22, 0));
            lionKingSchedule2.setBasePrice(BigDecimal.valueOf(85.00));
            lionKingSchedule2.setStatus(ShowSchedule.ScheduleStatus.SCHEDULED);
            lionKingSchedule2.setCreatedAt(LocalDateTime.now());
            lionKingSchedule2.setUpdatedAt(LocalDateTime.now());
            showScheduleRepository.save(lionKingSchedule2);
            
            ShowSchedule lionKingSchedule3 = new ShowSchedule();
            lionKingSchedule3.setShow(lionKing);
            lionKingSchedule3.setVenue(cityConcertHall);
            lionKingSchedule3.setShowDate(LocalDate.now().plusDays(16));
            lionKingSchedule3.setStartTime(LocalTime.of(14, 30));
            lionKingSchedule3.setEndTime(LocalTime.of(17, 0));
            lionKingSchedule3.setBasePrice(BigDecimal.valueOf(75.00));
            lionKingSchedule3.setStatus(ShowSchedule.ScheduleStatus.SCHEDULED);
            lionKingSchedule3.setCreatedAt(LocalDateTime.now());
            lionKingSchedule3.setUpdatedAt(LocalDateTime.now());
            showScheduleRepository.save(lionKingSchedule3);
            
            // Cirque du Soleil at Starlight Arena
            ShowSchedule cirqueSchedule1 = new ShowSchedule();
            cirqueSchedule1.setShow(cirqueDuSoleil);
            cirqueSchedule1.setVenue(starlightArena);
            cirqueSchedule1.setShowDate(LocalDate.now().plusDays(21));
            cirqueSchedule1.setStartTime(LocalTime.of(20, 0));
            cirqueSchedule1.setEndTime(LocalTime.of(22, 0));
            cirqueSchedule1.setBasePrice(BigDecimal.valueOf(95.00));
            cirqueSchedule1.setStatus(ShowSchedule.ScheduleStatus.SCHEDULED);
            cirqueSchedule1.setCreatedAt(LocalDateTime.now());
            cirqueSchedule1.setUpdatedAt(LocalDateTime.now());
            showScheduleRepository.save(cirqueSchedule1);
            
            ShowSchedule cirqueSchedule2 = new ShowSchedule();
            cirqueSchedule2.setShow(cirqueDuSoleil);
            cirqueSchedule2.setVenue(starlightArena);
            cirqueSchedule2.setShowDate(LocalDate.now().plusDays(22));
            cirqueSchedule2.setStartTime(LocalTime.of(20, 0));
            cirqueSchedule2.setEndTime(LocalTime.of(22, 0));
            cirqueSchedule2.setBasePrice(BigDecimal.valueOf(95.00));
            cirqueSchedule2.setStatus(ShowSchedule.ScheduleStatus.SCHEDULED);
            cirqueSchedule2.setCreatedAt(LocalDateTime.now());
            cirqueSchedule2.setUpdatedAt(LocalDateTime.now());
            showScheduleRepository.save(cirqueSchedule2);
            
            ShowSchedule cirqueSchedule3 = new ShowSchedule();
            cirqueSchedule3.setShow(cirqueDuSoleil);
            cirqueSchedule3.setVenue(starlightArena);
            cirqueSchedule3.setShowDate(LocalDate.now().plusDays(23));
            cirqueSchedule3.setStartTime(LocalTime.of(15, 0));
            cirqueSchedule3.setEndTime(LocalTime.of(17, 0));
            cirqueSchedule3.setBasePrice(BigDecimal.valueOf(85.00));
            cirqueSchedule3.setStatus(ShowSchedule.ScheduleStatus.SCHEDULED);
            cirqueSchedule3.setCreatedAt(LocalDateTime.now());
            cirqueSchedule3.setUpdatedAt(LocalDateTime.now());
            showScheduleRepository.save(cirqueSchedule3);
            
            // Rock Symphony at Royal Playhouse
            ShowSchedule rockSchedule1 = new ShowSchedule();
            rockSchedule1.setShow(rockSymphony);
            rockSchedule1.setVenue(royalPlayhouse);
            rockSchedule1.setShowDate(LocalDate.now().plusDays(10));
            rockSchedule1.setStartTime(LocalTime.of(20, 0));
            rockSchedule1.setEndTime(LocalTime.of(21, 30));
            rockSchedule1.setBasePrice(BigDecimal.valueOf(55.00));
            rockSchedule1.setStatus(ShowSchedule.ScheduleStatus.SCHEDULED);
            rockSchedule1.setCreatedAt(LocalDateTime.now());
            rockSchedule1.setUpdatedAt(LocalDateTime.now());
            showScheduleRepository.save(rockSchedule1);
            
            ShowSchedule rockSchedule2 = new ShowSchedule();
            rockSchedule2.setShow(rockSymphony);
            rockSchedule2.setVenue(royalPlayhouse);
            rockSchedule2.setShowDate(LocalDate.now().plusDays(11));
            rockSchedule2.setStartTime(LocalTime.of(20, 0));
            rockSchedule2.setEndTime(LocalTime.of(21, 30));
            rockSchedule2.setBasePrice(BigDecimal.valueOf(55.00));
            rockSchedule2.setStatus(ShowSchedule.ScheduleStatus.SCHEDULED);
            rockSchedule2.setCreatedAt(LocalDateTime.now());
            rockSchedule2.setUpdatedAt(LocalDateTime.now());
            showScheduleRepository.save(rockSchedule2);
            
            // Comedy Night at Metro Stadium
            ShowSchedule comedySchedule = new ShowSchedule();
            comedySchedule.setShow(comedyNight);
            comedySchedule.setVenue(metroStadium);
            comedySchedule.setShowDate(LocalDate.now().plusDays(5));
            comedySchedule.setStartTime(LocalTime.of(21, 0));
            comedySchedule.setEndTime(LocalTime.of(23, 0));
            comedySchedule.setBasePrice(BigDecimal.valueOf(45.00));
            comedySchedule.setStatus(ShowSchedule.ScheduleStatus.SCHEDULED);
            comedySchedule.setCreatedAt(LocalDateTime.now());
            comedySchedule.setUpdatedAt(LocalDateTime.now());
            showScheduleRepository.save(comedySchedule);
            
            System.out.println("Show schedules initialized successfully");
        } else {
            System.out.println("Show schedules already initialized");
        }
    }
    
    private void initBookings() {
        // Check if bookings already exist
        if (bookingRepository.count() == 0) {
            List<User> users = userRepository.findAll();
            List<ShowSchedule> schedules = showScheduleRepository.findAll();
            List<Seat> seats = seatRepository.findAll();
            
            if (users.isEmpty() || schedules.isEmpty() || seats.isEmpty()) {
                System.out.println("No users, show schedules, or seats found, skipping booking initialization");
                return;
            }
            
            // Get users
            User johnDoe = users.stream()
                .filter(u -> u.getUsername().equals("john.doe"))
                .findFirst()
                .orElse(users.get(0));
                
            User janeSmith = users.stream()
                .filter(u -> u.getUsername().equals("jane.smith"))
                .findFirst()
                .orElse(users.size() > 1 ? users.get(1) : users.get(0));
                
            User bobJohnson = users.stream()
                .filter(u -> u.getUsername().equals("bob.johnson"))
                .findFirst()
                .orElse(users.size() > 2 ? users.get(2) : users.get(0));
                
            User aliceWilliams = users.stream()
                .filter(u -> u.getUsername().equals("alice.williams"))
                .findFirst()
                .orElse(users.size() > 3 ? users.get(3) : users.get(0));
                
            User testUser = users.stream()
                .filter(u -> u.getUsername().equals("testuser"))
                .findFirst()
                .orElse(users.size() > 4 ? users.get(4) : users.get(0));
            
            // Get show schedules
            ShowSchedule hamiltonSchedule = schedules.stream()
                .filter(s -> s.getShow().getTitle().equals("Hamilton") && s.getShowDate().isAfter(LocalDate.now()))
                .findFirst()
                .orElse(schedules.get(0));
                
            ShowSchedule lionKingSchedule = schedules.stream()
                .filter(s -> s.getShow().getTitle().equals("The Lion King") && s.getShowDate().isAfter(LocalDate.now()))
                .findFirst()
                .orElse(schedules.size() > 1 ? schedules.get(1) : schedules.get(0));
                
            ShowSchedule cirqueSchedule = schedules.stream()
                .filter(s -> s.getShow().getTitle().equals("Cirque du Soleil: Alegría") && s.getShowDate().isAfter(LocalDate.now()))
                .findFirst()
                .orElse(schedules.size() > 2 ? schedules.get(2) : schedules.get(0));
                
            ShowSchedule rockSchedule = schedules.stream()
                .filter(s -> s.getShow().getTitle().equals("Rock Symphony") && s.getShowDate().isAfter(LocalDate.now()))
                .findFirst()
                .orElse(schedules.size() > 3 ? schedules.get(3) : schedules.get(0));
                
            ShowSchedule comedySchedule = schedules.stream()
                .filter(s -> s.getShow().getTitle().equals("Comedy Night") && s.getShowDate().isAfter(LocalDate.now()))
                .findFirst()
                .orElse(schedules.size() > 4 ? schedules.get(4) : schedules.get(0));
            
            // Create bookings
            // John Doe books Hamilton
            Booking johnBooking = new Booking();
            johnBooking.setBookingNumber("BK-20230001");
            johnBooking.setUser(johnDoe);
            johnBooking.setShowSchedule(hamiltonSchedule);
            johnBooking.setBookingDate(LocalDateTime.now().minusDays(2));
            johnBooking.setTotalAmount(BigDecimal.valueOf(225.00));
            johnBooking.setStatus(BookingStatus.CONFIRMED);
            johnBooking.setCreatedAt(LocalDateTime.now());
            johnBooking.setUpdatedAt(LocalDateTime.now());
            bookingRepository.save(johnBooking);
            
            // Find seats for John's booking
            List<Seat> grandTheaterSeats = seats.stream()
                .filter(s -> s.getVenue().getId().equals(hamiltonSchedule.getVenue().getId()))
                .filter(s -> s.getRowName().equals("G"))
                .filter(s -> s.getSeatNumber() == 5 || s.getSeatNumber() == 6)
                .collect(java.util.stream.Collectors.toList());
            
            if (grandTheaterSeats.size() >= 2) {
                // Create seat bookings for John
                SeatBooking johnSeatBooking1 = new SeatBooking();
                johnSeatBooking1.setBooking(johnBooking);
                johnSeatBooking1.setSeat(grandTheaterSeats.get(0));
                johnSeatBooking1.setPrice(BigDecimal.valueOf(112.50));
                seatBookingRepository.save(johnSeatBooking1);
                
                SeatBooking johnSeatBooking2 = new SeatBooking();
                johnSeatBooking2.setBooking(johnBooking);
                johnSeatBooking2.setSeat(grandTheaterSeats.get(1));
                johnSeatBooking2.setPrice(BigDecimal.valueOf(112.50));
                seatBookingRepository.save(johnSeatBooking2);
                
                // Create booking payment for John
                BookingPayment johnPayment = new BookingPayment();
                johnPayment.setBooking(johnBooking);
                johnPayment.setMethod(BookingPayment.PaymentMethod.CREDIT_CARD);
                johnPayment.setTransactionId("TXN-20230001-ABCDEF");
                johnPayment.setAmount(BigDecimal.valueOf(225.00));
                johnPayment.setStatus(BookingPayment.PaymentStatus.COMPLETED);
                johnPayment.setPaymentDate(LocalDateTime.now().minusDays(2));
                bookingPaymentRepository.save(johnPayment);
            }
            
            // Jane Smith books The Lion King
            Booking janeBooking = new Booking();
            janeBooking.setBookingNumber("BK-20230002");
            janeBooking.setUser(janeSmith);
            janeBooking.setShowSchedule(lionKingSchedule);
            janeBooking.setBookingDate(LocalDateTime.now().minusDays(3));
            janeBooking.setTotalAmount(BigDecimal.valueOf(255.00));
            janeBooking.setStatus(BookingStatus.CONFIRMED);
            janeBooking.setCreatedAt(LocalDateTime.now());
            janeBooking.setUpdatedAt(LocalDateTime.now());
            bookingRepository.save(janeBooking);
            
            // Find seats for Jane's booking
            List<Seat> cityConcertHallSeats = seats.stream()
                .filter(s -> s.getVenue().getId().equals(lionKingSchedule.getVenue().getId()))
                .filter(s -> s.getRowName().equals("H"))
                .filter(s -> s.getSeatNumber() == 7 || s.getSeatNumber() == 8)
                .collect(java.util.stream.Collectors.toList());
            
            if (cityConcertHallSeats.size() >= 2) {
                // Create seat bookings for Jane
                SeatBooking janeSeatBooking1 = new SeatBooking();
                janeSeatBooking1.setBooking(janeBooking);
                janeSeatBooking1.setSeat(cityConcertHallSeats.get(0));
                janeSeatBooking1.setPrice(BigDecimal.valueOf(127.50));
                seatBookingRepository.save(janeSeatBooking1);
                
                SeatBooking janeSeatBooking2 = new SeatBooking();
                janeSeatBooking2.setBooking(janeBooking);
                janeSeatBooking2.setSeat(cityConcertHallSeats.get(1));
                janeSeatBooking2.setPrice(BigDecimal.valueOf(127.50));
                seatBookingRepository.save(janeSeatBooking2);
                
                // Create booking payment for Jane
                BookingPayment janePayment = new BookingPayment();
                janePayment.setBooking(janeBooking);
                janePayment.setMethod(BookingPayment.PaymentMethod.CREDIT_CARD);
                janePayment.setTransactionId("TXN-20230002-GHIJKL");
                janePayment.setAmount(BigDecimal.valueOf(255.00));
                janePayment.setStatus(BookingPayment.PaymentStatus.COMPLETED);
                janePayment.setPaymentDate(LocalDateTime.now().minusDays(3));
                bookingPaymentRepository.save(janePayment);
            }
            
            // Create similar bookings for Bob, Alice, and Test User
            // (simplified for brevity)
            
            System.out.println("Bookings initialized successfully");
        } else {
            System.out.println("Bookings already initialized");
        }
    }
}