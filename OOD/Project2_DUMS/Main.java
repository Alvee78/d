package project2_dums;

/**
 * Project 2: Dhaka Urban Management System (DUMS)
 * Demonstrates all 5 required design patterns:
 * 1. Singleton: CentralUrbanDatabase
 * 2. Factory: UrbanServiceFactory (WasteManagementService, TrafficControlService, EmergencyResponseService)
 * 3. Observer: ComplaintStatusNotifier (Subject) notifying CitizenApp, GovernmentDashboard (Observers)
 * 4. Strategy: RouteOptimizationStrategy (ShortestRouteStrategy, FastestRouteStrategy, CongestionAvoidanceStrategy)
 * 5. Adapter: ExternalTrafficAPIAdapter (adapts legacy 3rd-party ExternalTrafficSystem to target TrafficDataProvider)
 */

import java.util.*;

// ==========================================
// 1. SINGLETON PATTERN: CentralUrbanDatabase
// ==========================================
class CentralUrbanDatabase {
    private static volatile CentralUrbanDatabase instance;
    private final Map<String, String> municipalRecords = new HashMap<>();

    private CentralUrbanDatabase() {
        System.out.println("[Singleton] CentralUrbanDatabase initialized (Secure Dhaka City DB connected).");
    }

    public static CentralUrbanDatabase getInstance() {
        if (instance == null) {
            synchronized (CentralUrbanDatabase.class) {
                if (instance == null) {
                    instance = new CentralUrbanDatabase();
                }
            }
        }
        return instance;
    }

    public void storeRecord(String recordId, String details) {
        municipalRecords.put(recordId, details);
        System.out.println("[CentralUrbanDatabase] Stored record -> " + recordId + ": " + details);
    }

    public String getRecord(String recordId) {
        return municipalRecords.get(recordId);
    }
}

// ==========================================
// 2. FACTORY PATTERN: Urban Service Creation
// ==========================================
interface UrbanService {
    String getServiceName();
    void executeService(String targetZone);
}

class WasteManagementService implements UrbanService {
    @Override
    public String getServiceName() { return "Dhaka Waste Management Service"; }
    @Override
    public void executeService(String targetZone) {
        System.out.println("[Service: Waste Management] Dispatching compactor trucks to clean bins in zone: " + targetZone);
    }
}

class TrafficControlService implements UrbanService {
    @Override
    public String getServiceName() { return "Dhaka Traffic Signal Automation Service"; }
    @Override
    public void executeService(String targetZone) {
        System.out.println("[Service: Traffic Control] Adjusting adaptive signal timings at intersections in zone: " + targetZone);
    }
}

class EmergencyResponseService implements UrbanService {
    @Override
    public String getServiceName() { return "Urban Emergency & Fire Response Service"; }
    @Override
    public void executeService(String targetZone) {
        System.out.println("[Service: Emergency Response] Alerting nearest ambulance and fire units for zone: " + targetZone);
    }
}

class UrbanServiceFactory {
    public static UrbanService createService(String serviceType) {
        switch (serviceType.toLowerCase()) {
            case "waste":
                return new WasteManagementService();
            case "traffic":
                return new TrafficControlService();
            case "emergency":
                return new EmergencyResponseService();
            default:
                throw new IllegalArgumentException("Unknown urban service type: " + serviceType);
        }
    }
}

// ==========================================
// 3. OBSERVER PATTERN: Complaint Status Notification
// ==========================================
interface ComplaintObserver {
    void onStatusUpdate(String complaintId, String newStatus, String comment);
}

class CitizenAppObserver implements ComplaintObserver {
    private final String citizenName;
    public CitizenAppObserver(String citizenName) { this.citizenName = citizenName; }
    @Override
    public void onStatusUpdate(String complaintId, String newStatus, String comment) {
        System.out.println("  [Citizen App -> " + citizenName + "] Complaint #" + complaintId + " is now [" + newStatus + "]. Note: " + comment);
    }
}

class GovernmentDashboardObserver implements ComplaintObserver {
    @Override
    public void onStatusUpdate(String complaintId, String newStatus, String comment) {
        System.out.println("  [City Corporation Portal] Audit Log: Complaint #" + complaintId + " transitioned to status: " + newStatus);
    }
}

class Complaint {
    private final String complaintId;
    private final String description;
    private String status;
    private final List<ComplaintObserver> observers = new ArrayList<>();

    public Complaint(String complaintId, String description) {
        this.complaintId = complaintId;
        this.description = description;
        this.status = "SUBMITTED";
    }

    public void attach(ComplaintObserver observer) {
        observers.add(observer);
    }

    public void detach(ComplaintObserver observer) {
        observers.remove(observer);
    }

    public void updateStatus(String newStatus, String remarks) {
        this.status = newStatus;
        System.out.println("\n[Complaint #" + complaintId + ": '" + description + "'] State changed to: " + newStatus);
        for (ComplaintObserver observer : observers) {
            observer.onStatusUpdate(complaintId, newStatus, remarks);
        }
    }

    public String getStatus() { return status; }
}

// ==========================================
// 4. STRATEGY PATTERN: Route Optimization
// ==========================================
interface RouteOptimizationStrategy {
    void calculateRoute(String origin, String destination);
}

class ShortestRouteStrategy implements RouteOptimizationStrategy {
    @Override
    public void calculateRoute(String origin, String destination) {
        System.out.println("[Route Strategy: Shortest Distance] Calculating minimum physical distance from " + origin + " to " + destination + " (Distance: 6.2 km).");
    }
}

class FastestRouteStrategy implements RouteOptimizationStrategy {
    @Override
    public void calculateRoute(String origin, String destination) {
        System.out.println("[Route Strategy: Fastest Time] Calculating fastest clear route via elevated expressway from " + origin + " to " + destination + " (Time: 18 mins).");
    }
}

class CongestionAvoidanceStrategy implements RouteOptimizationStrategy {
    @Override
    public void calculateRoute(String origin, String destination) {
        System.out.println("[Route Strategy: Congestion Avoidance] Real-time AI rerouting around Gulshan-Mirpur bottlenecks from " + origin + " to " + destination + " (Avoids 3 gridlocks).");
    }
}

class NavigationContext {
    private RouteOptimizationStrategy strategy;

    public NavigationContext(RouteOptimizationStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(RouteOptimizationStrategy strategy) {
        this.strategy = strategy;
    }

    public void planRoute(String start, String end) {
        strategy.calculateRoute(start, end);
    }
}

// ==========================================
// 5. ADAPTER PATTERN: External Traffic API
// ==========================================
// Standard target interface used across Dhaka Urban Management System
interface TrafficDataProvider {
    int getAverageSpeedKmh(String streetName);
    String getCongestionLevel(String streetName);
}

// Incompatible legacy 3rd-party traffic feed (e.g. Dhaka Traffic Police sensor API)
class ExternalTrafficSystem {
    public double queryVelocityMph(String locationCode) {
        // e.g. 15.5 mph (~25 km/h)
        return 15.53;
    }

    public int fetchTrafficSeverityIndex(String locationCode) {
        // 1 = Light, 2 = Medium, 3 = Heavy, 4 = Gridlock
        return 3;
    }
}

// Adapter converting ExternalTrafficSystem to TrafficDataProvider
class ExternalTrafficAPIAdapter implements TrafficDataProvider {
    private final ExternalTrafficSystem externalTrafficSystem;

    public ExternalTrafficAPIAdapter(ExternalTrafficSystem externalTrafficSystem) {
        this.externalTrafficSystem = externalTrafficSystem;
    }

    @Override
    public int getAverageSpeedKmh(String streetName) {
        double mph = externalTrafficSystem.queryVelocityMph(streetName);
        return (int) Math.round(mph * 1.60934);
    }

    @Override
    public String getCongestionLevel(String streetName) {
        int index = externalTrafficSystem.fetchTrafficSeverityIndex(streetName);
        switch (index) {
            case 1: return "Light Traffic";
            case 2: return "Moderate Flow";
            case 3: return "Heavy Congestion";
            case 4: return "Severe Gridlock";
            default: return "Unknown Condition";
        }
    }
}

// ==========================================
// MAIN DRIVER & DEMONSTRATION
// ==========================================
public class Main {
    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println(" PROJECT 2: DHAKA URBAN MANAGEMENT SYSTEM (DUMS) - DEMO");
        System.out.println("===============================================================\n");

        // 1. Singleton Pattern Demo
        System.out.println("--- 1. Testing Singleton Pattern (CentralUrbanDatabase) ---");
        CentralUrbanDatabase db1 = CentralUrbanDatabase.getInstance();
        CentralUrbanDatabase db2 = CentralUrbanDatabase.getInstance();
        db1.storeRecord("TAX-MIRPUR-4091", "Zone 4 Commercial Property - Active");
        System.out.println("Verifying single database instance: db1 == db2 ? " + (db1 == db2));
        System.out.println("Querying record via db2: " + db2.getRecord("TAX-MIRPUR-4091"));
        System.out.println();

        // 2. Factory Pattern Demo
        System.out.println("--- 2. Testing Factory Pattern (UrbanServiceFactory) ---");
        UrbanService wasteService = UrbanServiceFactory.createService("waste");
        UrbanService trafficService = UrbanServiceFactory.createService("traffic");
        UrbanService emergencyService = UrbanServiceFactory.createService("emergency");

        wasteService.executeService("Dhanmondi Ward 15");
        trafficService.executeService("Farmgate Intersection");
        emergencyService.executeService("Mohakhali Wireless Gate");
        System.out.println();

        // 3. Observer Pattern Demo
        System.out.println("--- 3. Testing Observer Pattern (Citizen Complaint Status) ---");
        Complaint waterlogComplaint = new Complaint("CMP-2026-991", "Severe waterlogging on Begum Rokeya Avenue");
        CitizenAppObserver citizen = new CitizenAppObserver("Tanvir Rahman");
        GovernmentDashboardObserver govtDashboard = new GovernmentDashboardObserver();

        waterlogComplaint.attach(citizen);
        waterlogComplaint.attach(govtDashboard);

        waterlogComplaint.updateStatus("IN_PROGRESS", "Dhaka WASA drainage crew dispatched with suction pumps.");
        waterlogComplaint.updateStatus("RESOLVED", "Drainage cleared and road opened for normal vehicular traffic.");
        System.out.println();

        // 4. Strategy Pattern Demo
        System.out.println("--- 4. Testing Strategy Pattern (Urban Route Optimization) ---");
        NavigationContext navContext = new NavigationContext(new ShortestRouteStrategy());
        navContext.planRoute("Uttara Sector 7", "Motijheel C/A");

        System.out.println("\nSwitching to Fastest Route strategy via Elevated Expressway...");
        navContext.setStrategy(new FastestRouteStrategy());
        navContext.planRoute("Uttara Sector 7", "Motijheel C/A");

        System.out.println("\nSwitching to Congestion Avoidance strategy during rush hour...");
        navContext.setStrategy(new CongestionAvoidanceStrategy());
        navContext.planRoute("Uttara Sector 7", "Motijheel C/A");
        System.out.println();

        // 5. Adapter Pattern Demo
        System.out.println("--- 5. Testing Adapter Pattern (External Traffic Sensor API) ---");
        ExternalTrafficSystem legacySystem = new ExternalTrafficSystem();
        TrafficDataProvider adaptedProvider = new ExternalTrafficAPIAdapter(legacySystem);

        String testCorridor = "Kazi Nazrul Islam Avenue";
        System.out.println("Corridor: " + testCorridor);
        System.out.println("Adapted Speed: " + adaptedProvider.getAverageSpeedKmh(testCorridor) + " km/h");
        System.out.println("Adapted Congestion Status: " + adaptedProvider.getCongestionLevel(testCorridor));

        System.out.println("\n[DUMS Project Execution Completed Successfully]");
    }
}
