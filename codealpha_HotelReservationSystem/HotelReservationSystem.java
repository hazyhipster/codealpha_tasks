import java.io.*;
import java.util.*;

class Room {
    int roomNumber;
    String category;
    boolean isBooked;

    Room(int roomNumber, String category, boolean isBooked) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.isBooked = isBooked;
    }

    @Override
    public String toString() {
        return roomNumber + "," + category + "," + isBooked;
    }
}

class Booking {
    String bookingId;
    String customerName;
    int roomNumber;
    String category;
    double amount;

    Booking(String bookingId, String customerName, int roomNumber, String category, double amount) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.roomNumber = roomNumber;
        this.category = category;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return bookingId + "," + customerName + "," + roomNumber + "," + category + "," + amount;
    }
}

public class HotelReservationSystem {

    static Scanner sc = new Scanner(System.in);
    static List<Room> rooms = new ArrayList<>();
    static List<Booking> bookings = new ArrayList<>();

    static final String ROOMS_FILE = "rooms.txt";
    static final String BOOKINGS_FILE = "bookings.txt";

    public static void main(String[] args) {
        loadRooms();
        loadBookings();

        while (true) {
            System.out.println("\n===== HOTEL RESERVATION SYSTEM =====");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Book a Room");
            System.out.println("3. Cancel Reservation");
            System.out.println("4. View All Bookings");
            System.out.println("5. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> viewAvailableRooms();
                case 2 -> bookRoom();
                case 3 -> cancelReservation();
                case 4 -> viewAllBookings();
                case 5 -> {
                    saveRooms();
                    saveBookings();
                    System.out.println("Exiting system. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice! Try again.");
            }
        }
    }

    // ---------------- LOAD / SAVE FILES -----------------------

    static void loadRooms() {
        File file = new File(ROOMS_FILE);
        if (!file.exists()) {
            initializeDefaultRooms();
            saveRooms();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(ROOMS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                rooms.add(new Room(Integer.parseInt(p[0]), p[1], Boolean.parseBoolean(p[2])));
            }
        } catch (Exception e) {
            System.out.println("Error loading rooms.");
        }
    }

    static void loadBookings() {
        File file = new File(BOOKINGS_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(BOOKINGS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                bookings.add(new Booking(p[0], p[1], Integer.parseInt(p[2]), p[3], Double.parseDouble(p[4])));
            }
        } catch (Exception e) {
            System.out.println("Error loading bookings.");
        }
    }

    static void saveRooms() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ROOMS_FILE))) {
            for (Room r : rooms) pw.println(r);
        } catch (Exception e) {
            System.out.println("Error saving rooms.");
        }
    }

    static void saveBookings() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking b : bookings) pw.println(b);
        } catch (Exception e) {
            System.out.println("Error saving bookings.");
        }
    }

    // -------------- DEFAULT ROOMS ---------------------------

    static void initializeDefaultRooms() {
        rooms.add(new Room(101, "Standard", false));
        rooms.add(new Room(102, "Standard", false));
        rooms.add(new Room(201, "Deluxe", false));
        rooms.add(new Room(202, "Deluxe", false));
        rooms.add(new Room(301, "Suite", false));
        rooms.add(new Room(302, "Suite", false));
    }

    // -------------------- FEATURES --------------------------

    static void viewAvailableRooms() {
        System.out.println("\nAvailable Rooms:");
        for (Room r : rooms) {
            if (!r.isBooked) {
                System.out.println("Room " + r.roomNumber + " (" + r.category + ")");
            }
        }
    }

    static void bookRoom() {
        System.out.print("\nEnter your name: ");
        String name = sc.nextLine();

        System.out.print("Enter room category (Standard/Deluxe/Suite): ");
        String category = sc.nextLine();

        Room selectedRoom = null;

        for (Room r : rooms) {
            if (!r.isBooked && r.category.equalsIgnoreCase(category)) {
                selectedRoom = r;
                break;
            }
        }

        if (selectedRoom == null) {
            System.out.println("No available rooms in this category.");
            return;
        }

        double price = switch (category.toLowerCase()) {
            case "standard" -> 2000;
            case "deluxe" -> 3500;
            case "suite" -> 5000;
            default -> 0;
        };

        System.out.println("Room found: " + selectedRoom.roomNumber + " | Price: ₹" + price);
        System.out.print("Confirm booking? (yes/no): ");
        if (!sc.nextLine().equalsIgnoreCase("yes")) return;

        String bookingId = "BK" + (bookings.size() + 1);

        selectedRoom.isBooked = true;
        Booking booking = new Booking(bookingId, name, selectedRoom.roomNumber, category, price);
        bookings.add(booking);

        saveRooms();
        saveBookings();

        System.out.println("\nBooking Successful!");
        System.out.println("Booking ID: " + bookingId);
        System.out.println("Room Number: " + selectedRoom.roomNumber);
    }

    static void cancelReservation() {
        System.out.print("Enter Booking ID to cancel: ");
        String id = sc.nextLine();

        Booking bookingToRemove = null;

        for (Booking b : bookings) {
            if (b.bookingId.equals(id)) {
                bookingToRemove = b;
                break;
            }
        }

        if (bookingToRemove == null) {
            System.out.println("Invalid Booking ID.");
            return;
        }

        for (Room r : rooms) {
            if (r.roomNumber == bookingToRemove.roomNumber) {
                r.isBooked = false;
                break;
            }
        }

        bookings.remove(bookingToRemove);

        saveRooms();
        saveBookings();

        System.out.println("Booking Cancelled Successfully.");
    }

    static void viewAllBookings() {
        System.out.println("\n===== ALL BOOKINGS =====");
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Booking b : bookings) {
            System.out.println("ID: " + b.bookingId +
                    " | Name: " + b.customerName +
                    " | Room: " + b.roomNumber +
                    " | Category: " + b.category +
                    " | Amount: ₹" + b.amount);
        }
    }
}
