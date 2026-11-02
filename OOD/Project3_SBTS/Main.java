package project3_sbts;

/**
 * Project 3: Smart Campus Bus Transportation System (SBTS)
 * Demonstrates all 5 required design patterns:
 * 1. Singleton: TransportControlCenter
 * 2. Factory: TicketFactory (StudentTicket, FacultyTicket, VisitorDayPassTicket)
 * 3. Observer: BusTracker (Subject) notifying PassengerMobileApp, CampusTransportDisplay (Observers)
 * 4. Strategy: RouteSelectionStrategy (ShortestDistanceRoute, FastestTimeRoute, EcoFriendlyRoute)
 * 5. Adapter: PaymentGatewayAdapter (adapts legacy ExternalCampusBankAPI to standard PaymentGateway)
 */

import java.util.*;

// ==========================================
// 1. SINGLETON PATTERN: TransportControlCenter
// ==========================================
class TransportControlCenter {
    private static volatile TransportControlCenter instance;
    private final Map<String, String> activeBusFleet = new HashMap<>();

    private TransportControlCenter() {
        System.out.println("[Singleton] TransportControlCenter initialized (Central Dispatch online).");
    }

    public static TransportControlCenter getInstance() {
        if (instance == null) {
            synchronized (TransportControlCenter.class) {
                if (instance == null) {
                    instance = new TransportControlCenter();
                }
            }
        }
        return instance;
    }

    public void updateBusLocation(String busNumber, String currentStop) {
        activeBusFleet.put(busNumber, currentStop);
        System.out.println("[TransportControlCenter] Fleet Telemetry -> " + busNumber + " is currently at: " + currentStop);
    }

    public String getBusLocation(String busNumber) {
        return activeBusFleet.get(busNumber);
    }
}

// ==========================================
// 2. FACTORY PATTERN: Ticket Type Creation
// ==========================================
interface Ticket {
    String getTicketId();
    String getCategory();
    double calculateFare(double baseFare);
    void printTicket();
}

class StudentTicket implements Ticket {
    private final String ticketId;
    public StudentTicket(String ticketId) { this.ticketId = ticketId; }
    @Override public String getTicketId() { return ticketId; }
    @Override public String getCategory() { return "Student (Subsidized)"; }
    @Override public double calculateFare(double baseFare) { return baseFare * 0.50; } // 50% discount
    @Override
    public void printTicket() {
        System.out.println("[Ticket #" + ticketId + "] Category: " + getCategory() + " | Effective Fare: 50% of base fare.");
    }
}

class FacultyTicket implements Ticket {
    private final String ticketId;
    public FacultyTicket(String ticketId) { this.ticketId = ticketId; }
    @Override public String getTicketId() { return ticketId; }
    @Override public String getCategory() { return "Faculty & Staff"; }
    @Override public double calculateFare(double baseFare) { return baseFare * 0.75; } // 25% discount
    @Override
    public void printTicket() {
        System.out.println("[Ticket #" + ticketId + "] Category: " + getCategory() + " | Effective Fare: 75% of base fare.");
    }
}

class VisitorDayPassTicket implements Ticket {
    private final String ticketId;
    public VisitorDayPassTicket(String ticketId) { this.ticketId = ticketId; }
    @Override public String getTicketId() { return ticketId; }
    @Override public String getCategory() { return "Visitor Unlimited Day Pass"; }
    @Override public double calculateFare(double baseFare) { return baseFare * 2.0; } // Fixed day pass premium
    @Override
    public void printTicket() {
        System.out.println("[Ticket #" + ticketId + "] Category: " + getCategory() + " | All-day access across all campus lines.");
    }
}

class TicketFactory {
    public static Ticket createTicket(String category, String ticketId) {
        switch (category.toLowerCase()) {
            case "student":
                return new StudentTicket(ticketId);
            case "faculty":
                return new FacultyTicket(ticketId);
            case "visitor":
                return new VisitorDayPassTicket(ticketId);
            default:
                throw new IllegalArgumentException("Unknown ticket category: " + category);
        }
    }
}

// ==========================================
// 3. OBSERVER PATTERN: Bus Arrival Notification
// ==========================================
interface BusArrivalObserver {
    void updateArrival(String busNumber, String stopName, int minutesRemaining);
}

class PassengerMobileApp implements BusArrivalObserver {
    private final String passengerName;
    public PassengerMobileApp(String passengerName) { this.passengerName = passengerName; }

    @Override
    public void updateArrival(String busNumber, String stopName, int minutesRemaining) {
        System.out.println("  [Mobile App -> " + passengerName + "] Bus " + busNumber + " arriving at '" + stopName + "' in ~" + minutesRemaining + " minutes.");
    }
}

class CampusTransportDisplay implements BusArrivalObserver {
    private final String terminalName;
    public CampusTransportDisplay(String terminalName) { this.terminalName = terminalName; }

    @Override
    public void updateArrival(String busNumber, String stopName, int minutesRemaining) {
        System.out.println("  [LED Display @ " + terminalName + "] Bus: " + busNumber + " | Stop: " + stopName + " | ETA: " + minutesRemaining + " min.");
    }
}

class BusTracker {
    private final String busNumber;
    private final List<BusArrivalObserver> observers = new ArrayList<>();

    public BusTracker(String busNumber) {
        this.busNumber = busNumber;
    }

    public void registerObserver(BusArrivalObserver observer) {
        observers.add(observer);
    }

    public void unregisterObserver(BusArrivalObserver observer) {
        observers.remove(observer);
    }

    public void broadcastArrivalETA(String stopName, int minutesRemaining) {
        System.out.println("\n[BusTracker:" + busNumber + "] Approaching: " + stopName + " (ETA: " + minutesRemaining + " mins)");
        for (BusArrivalObserver obs : observers) {
            obs.updateArrival(busNumber, stopName, minutesRemaining);
        }
    }
}

// ==========================================
// 4. STRATEGY PATTERN: Route Selection Algorithm
// ==========================================
interface RouteSelectionStrategy {
    void planBusRoute(String startCampusGate, String destinationFaculty);
}

class ShortestRouteSelection implements RouteSelectionStrategy {
    @Override
    public void planBusRoute(String startCampusGate, String destinationFaculty) {
        System.out.println("[Route Strategy: Shortest Distance] Direct perimeter loop from " + startCampusGate + " to " + destinationFaculty + " (Distance: 2.8 km).");
    }
}

class FastestRouteSelection implements RouteSelectionStrategy {
    @Override
    public void planBusRoute(String startCampusGate, String destinationFaculty) {
        System.out.println("[Route Strategy: Fastest Time] Express ring-road bypass avoiding pedestrian plaza from " + startCampusGate + " to " + destinationFaculty + " (Duration: 6 mins).");
    }
}

class EcoRouteSelection implements RouteSelectionStrategy {
    @Override
    public void planBusRoute(String startCampusGate, String destinationFaculty) {
        System.out.println("[Route Strategy: Eco-Friendly Mode] Electric Shuttle regenerative braking lane through Central Campus Green from " + startCampusGate + " to " + destinationFaculty + " (Zero Emissions).");
    }
}

class RouteContext {
    private RouteSelectionStrategy strategy;

    public RouteContext(RouteSelectionStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(RouteSelectionStrategy strategy) {
        this.strategy = strategy;
    }

    public void executeRoutePlanning(String origin, String dest) {
        strategy.planBusRoute(origin, dest);
    }
}

// ==========================================
// 5. ADAPTER PATTERN: Payment Gateway Integration
// ==========================================
// Standard target interface for bus fare processing
interface PaymentGateway {
    boolean processPayment(String accountId, double amount);
    String getReceiptId();
}

// Incompatible legacy external campus bank API
class ExternalCampusBankAPI {
    private String lastTxnCode;

    public int debitAccount(String studentOrStaffCardId, int amountInPenceOrCents) {
        this.lastTxnCode = "BANK-TXN-" + System.currentTimeMillis();
        // Return 0 for success
        return 0;
    }

    public String retrieveTransactionReference() {
        return lastTxnCode;
    }
}

// Adapter converting ExternalCampusBankAPI to PaymentGateway
class PaymentGatewayAdapter implements PaymentGateway {
    private final ExternalCampusBankAPI legacyBankAPI;
    private String receipt;

    public PaymentGatewayAdapter(ExternalCampusBankAPI legacyBankAPI) {
        this.legacyBankAPI = legacyBankAPI;
    }

    @Override
    public boolean processPayment(String accountId, double amount) {
        int cents = (int) Math.round(amount * 100);
        int responseCode = legacyBankAPI.debitAccount(accountId, cents);
        if (responseCode == 0) {
            this.receipt = legacyBankAPI.retrieveTransactionReference();
            return true;
        }
        return false;
    }

    @Override
    public String getReceiptId() {
        return receipt;
    }
}

// ==========================================
// MAIN DRIVER & DEMONSTRATION
// ==========================================
public class Main {
    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println(" PROJECT 3: SMART CAMPUS BUS TRANSPORTATION SYSTEM (SBTS) - DEMO");
        System.out.println("=========================================================================\n");

        // 1. Singleton Pattern Demo
        System.out.println("--- 1. Testing Singleton Pattern (TransportControlCenter) ---");
        TransportControlCenter controlCenter1 = TransportControlCenter.getInstance();
        TransportControlCenter controlCenter2 = TransportControlCenter.getInstance();

        controlCenter1.updateBusLocation("Bus-Red-01", "Curzon Hall Terminal");
        System.out.println("Verifying single control center: c1 == c2 ? " + (controlCenter1 == controlCenter2));
        System.out.println("Reading bus location from second instance reference: " + controlCenter2.getBusLocation("Bus-Red-01"));
        System.out.println();

        // 2. Factory Pattern Demo
        System.out.println("--- 2. Testing Factory Pattern (TicketFactory) ---");
        Ticket studentTicket = TicketFactory.createTicket("student", "TCK-STU-501");
        Ticket facultyTicket = TicketFactory.createTicket("faculty", "TCK-FAC-102");
        Ticket visitorTicket = TicketFactory.createTicket("visitor", "TCK-VIS-099");

        double baseFare = 20.0; // 20 BDT
        studentTicket.printTicket();
        System.out.println("  -> Billed fare: " + studentTicket.calculateFare(baseFare) + " BDT");
        facultyTicket.printTicket();
        System.out.println("  -> Billed fare: " + facultyTicket.calculateFare(baseFare) + " BDT");
        visitorTicket.printTicket();
        System.out.println("  -> Billed fare: " + visitorTicket.calculateFare(baseFare) + " BDT");
        System.out.println();

        // 3. Observer Pattern Demo
        System.out.println("--- 3. Testing Observer Pattern (Bus Arrival Notification) ---");
        BusTracker trackerBus10 = new BusTracker("Campus-Express-10");
        PassengerMobileApp passenger1 = new PassengerMobileApp("Sadia Ahmed");
        CampusTransportDisplay displayGate1 = new CampusTransportDisplay("Science Annex Station");

        trackerBus10.registerObserver(passenger1);
        trackerBus10.registerObserver(displayGate1);

        trackerBus10.broadcastArrivalETA("Science Annex Station", 4);
        trackerBus10.broadcastArrivalETA("Science Annex Station", 1);
        System.out.println();

        // 4. Strategy Pattern Demo
        System.out.println("--- 4. Testing Strategy Pattern (Bus Route Selection) ---");
        RouteContext routeContext = new RouteContext(new ShortestRouteSelection());
        routeContext.executeRoutePlanning("Main Campus Gate", "Computer Science Faculty");

        System.out.println("\nSwitching to Fastest Route (via bypass)...");
        routeContext.setStrategy(new FastestRouteSelection());
        routeContext.executeRoutePlanning("Main Campus Gate", "Computer Science Faculty");

        System.out.println("\nSwitching to Eco-Friendly Route (Zero emission electric lane)...");
        routeContext.setStrategy(new EcoRouteSelection());
        routeContext.executeRoutePlanning("Main Campus Gate", "Computer Science Faculty");
        System.out.println();

        // 5. Adapter Pattern Demo
        System.out.println("--- 5. Testing Adapter Pattern (Payment Gateway Integration) ---");
        ExternalCampusBankAPI legacyBank = new ExternalCampusBankAPI();
        PaymentGateway paymentGateway = new PaymentGatewayAdapter(legacyBank);

        boolean success = paymentGateway.processPayment("STUDENT-ID-2022-01-44", 10.0);
        System.out.println("Payment successfully processed? " + success);
        System.out.println("Bank Transaction Code received: " + paymentGateway.getReceiptId());

        System.out.println("\n[SBTS Project Execution Completed Successfully]");
    }
}
