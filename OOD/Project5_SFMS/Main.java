package project5_sfms;

/**
 * Project 5: Smart Campus Food Management System (SFMS)
 * Demonstrates all 5 required design patterns:
 * 1. Singleton: OrderManager
 * 2. Factory: PaymentMethodFactory (CardPayment, MobileBankingPayment, CampusMealCardPayment)
 * 3. Observer: FoodOrder (Subject) notifying CustomerMobileApp, RestaurantDashboard, DeliveryAgentApp (Observers)
 * 4. Strategy: DeliveryRouteStrategy (FastestExpressRoute, ShortestFootpathRoute, EcoBicycleRoute)
 * 5. Adapter: ThirdPartyPaymentAdapter (adapts legacy 3rd-party ExternalPaymentVendor to PaymentProcessor)
 */

import java.util.*;

// ==========================================
// 1. SINGLETON PATTERN: OrderManager
// ==========================================
class OrderManager {
    private static volatile OrderManager instance;
    private final Map<String, String> activeOrders = new HashMap<>();

    private OrderManager() {
        System.out.println("[Singleton] OrderManager initialized (Campus Food Service Hub online).");
    }

    public static OrderManager getInstance() {
        if (instance == null) {
            synchronized (OrderManager.class) {
                if (instance == null) {
                    instance = new OrderManager();
                }
            }
        }
        return instance;
    }

    public void registerOrder(String orderId, String summary) {
        activeOrders.put(orderId, summary);
        System.out.println("[OrderManager] Order registered -> " + orderId + ": " + summary);
    }

    public String getOrderDetails(String orderId) {
        return activeOrders.get(orderId);
    }
}

// ==========================================
// 2. FACTORY PATTERN: Payment Method Creation
// ==========================================
interface PaymentMethod {
    String getMethodName();

    boolean pay(double amount);
}

class CardPayment implements PaymentMethod {
    private final String cardNumber;

    public CardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public String getMethodName() {
        return "Credit / Debit Card";
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("[Payment: Card] Authorizing $" + amount + " on card ending in "
                + cardNumber.substring(cardNumber.length() - 4) + " -> Approved.");
        return true;
    }
}

class MobileBankingPayment implements PaymentMethod {
    private final String mobileNumber;

    public MobileBankingPayment(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Override
    public String getMethodName() {
        return "bKash / Nagad Mobile Banking";
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("[Payment: Mobile Banking] Executing OTP transaction for $" + amount + " to account "
                + mobileNumber + " -> Completed.");
        return true;
    }
}

class CampusMealCardPayment implements PaymentMethod {
    private final String studentCardId;

    public CampusMealCardPayment(String studentCardId) {
        this.studentCardId = studentCardId;
    }

    @Override
    public String getMethodName() {
        return "Campus Student Meal Card";
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("[Payment: Campus Meal Card] Deducting $" + amount + " from university cafeteria wallet ("
                + studentCardId + ") -> Success.");
        return true;
    }
}

class PaymentMethodFactory {
    public static PaymentMethod createPaymentMethod(String methodType, String accountIdentifier) {
        switch (methodType.toLowerCase()) {
            case "card":
                return new CardPayment(accountIdentifier);
            case "mobile":
                return new MobileBankingPayment(accountIdentifier);
            case "mealcard":
                return new CampusMealCardPayment(accountIdentifier);
            default:
                throw new IllegalArgumentException("Unknown payment method: " + methodType);
        }
    }
}

// ==========================================
// 3. OBSERVER PATTERN: Order Status Notification
// ==========================================
interface OrderStatusObserver {
    void onOrderStatusChanged(String orderId, String newStatus, String info);
}

class CustomerAppObserver implements OrderStatusObserver {
    private final String customerName;

    public CustomerAppObserver(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public void onOrderStatusChanged(String orderId, String newStatus, String info) {
        System.out.println("  [Customer App -> " + customerName + "] Order #" + orderId + " is now " + newStatus + ". ("
                + info + ")");
    }
}

class RestaurantDashboardObserver implements OrderStatusObserver {
    private final String kitchenName;

    public RestaurantDashboardObserver(String kitchenName) {
        this.kitchenName = kitchenName;
    }

    @Override
    public void onOrderStatusChanged(String orderId, String newStatus, String info) {
        System.out.println("  [Kitchen Display @ " + kitchenName + "] Order #" + orderId + " status: " + newStatus);
    }
}

class DeliveryAgentObserver implements OrderStatusObserver {
    private final String riderName;

    public DeliveryAgentObserver(String riderName) {
        this.riderName = riderName;
    }

    @Override
    public void onOrderStatusChanged(String orderId, String newStatus, String info) {
        System.out.println(
                "  [Rider App -> " + riderName + "] Notification: Order #" + orderId + " updated to " + newStatus);
    }
}

class FoodOrderSubject {
    private final String orderId;
    private String status;
    private final List<OrderStatusObserver> observers = new ArrayList<>();

    public FoodOrderSubject(String orderId) {
        this.orderId = orderId;
        this.status = "PLACED";
    }

    public void attach(OrderStatusObserver observer) {
        observers.add(observer);
    }

    public void detach(OrderStatusObserver observer) {
        observers.remove(observer);
    }

    public void setStatus(String newStatus, String info) {
        this.status = newStatus;
        System.out.println("\n[FoodOrder #" + orderId + "] State changed to: " + newStatus);
        for (OrderStatusObserver obs : observers) {
            obs.onOrderStatusChanged(orderId, newStatus, info);
        }
    }
}

// ==========================================
// 4. STRATEGY PATTERN: Delivery Route Selection
// ==========================================
interface DeliveryRouteStrategy {
    void planRoute(String restaurantLocation, String studentHostel);
}

class FastestExpressMotorcycleRoute implements DeliveryRouteStrategy {
    @Override
    public void planRoute(String restaurantLocation, String studentHostel) {
        System.out.println("[Route: Fastest Express] Motorcycle delivery via perimeter ring road from "
                + restaurantLocation + " to " + studentHostel + " (Time: 7 mins).");
    }
}

class DirectCampusPedestrianRoute implements DeliveryRouteStrategy {
    @Override
    public void planRoute(String restaurantLocation, String studentHostel) {
        System.out.println("[Route: Direct Footpath Walk] Pedestrian shortcut through central library walkway from "
                + restaurantLocation + " to " + studentHostel + " (Avoids vehicle checkpoint).");
    }
}

class EcoFriendlyBicycleRoute implements DeliveryRouteStrategy {
    @Override
    public void planRoute(String restaurantLocation, String studentHostel) {
        System.out.println("[Route: Eco Bicycle Path] Campus green lane electric bike delivery from "
                + restaurantLocation + " to " + studentHostel + " (Zero carbon emissions).");
    }
}

class DeliveryContext {
    private DeliveryRouteStrategy routeStrategy;

    public DeliveryContext(DeliveryRouteStrategy routeStrategy) {
        this.routeStrategy = routeStrategy;
    }

    public void setRouteStrategy(DeliveryRouteStrategy routeStrategy) {
        this.routeStrategy = routeStrategy;
    }

    public void dispatchDelivery(String source, String destination) {
        routeStrategy.planRoute(source, destination);
    }
}

// ==========================================
// 5. ADAPTER PATTERN: Third-Party Payment API
// ==========================================
// Standard Target interface required by SFMS
interface PaymentProcessor {
    boolean executePayment(String customerId, double totalCost);

    String getConfirmationTransactionCode();
}

// Incompatible external 3rd-party fintech gateway (e.g., Stripe / SSLCommerz /
// bKash Merchant API)
class ExternalFintechGateway {
    private String txnReference;

    public boolean makeExternalRemittance(String clientToken, long amountInMicroUnits) {
        // Simulates external server API call
        this.txnReference = "EXT-FINTECH-TXN-" + System.currentTimeMillis();
        return true;
    }

    public String getGatewayReference() {
        return txnReference;
    }
}

// Adapter converting ExternalFintechGateway to PaymentProcessor
class ThirdPartyPaymentAdapter implements PaymentProcessor {
    private final ExternalFintechGateway gateway;
    private String confirmedTxn;

    public ThirdPartyPaymentAdapter(ExternalFintechGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public boolean executePayment(String customerId, double totalCost) {
        // Convert double dollars into micro-units or minor units
        long microUnits = (long) (totalCost * 100);
        boolean success = gateway.makeExternalRemittance(customerId, microUnits);
        if (success) {
            this.confirmedTxn = gateway.getGatewayReference();
            return true;
        }
        return false;
    }

    @Override
    public String getConfirmationTransactionCode() {
        return confirmedTxn;
    }
}

// ==========================================
// MAIN DRIVER & DEMONSTRATION
// ==========================================
public class Main {
    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println(" PROJECT 5: SMART CAMPUS FOOD MANAGEMENT SYSTEM (SFMS) - DEMO");
        System.out.println("=========================================================================\n");

        // 1. Singleton Pattern Demo
        System.out.println("--- 1. Testing Singleton Pattern (OrderManager) ---");
        OrderManager manager1 = OrderManager.getInstance();
        OrderManager manager2 = OrderManager.getInstance();

        manager1.registerOrder("ORD-902", "Chicken Biryani (x2), Borhani (x2) - Cafeteria Zone B");
        System.out.println("Verifying single instance: manager1 == manager2 ? " + (manager1 == manager2));
        System.out.println("Querying order via manager2 reference: " + manager2.getOrderDetails("ORD-902"));
        System.out.println();

        // 2. Factory Pattern Demo
        System.out.println("--- 2. Testing Factory Pattern (PaymentMethodFactory) ---");
        PaymentMethod card = PaymentMethodFactory.createPaymentMethod("card", "4111-2222-3333-8890");
        PaymentMethod mobile = PaymentMethodFactory.createPaymentMethod("mobile", "01711-000000");
        PaymentMethod mealCard = PaymentMethodFactory.createPaymentMethod("mealcard", "STU-WALLET-776");

        card.pay(18.50);
        mobile.pay(12.00);
        mealCard.pay(6.50);
        System.out.println();

        // 3. Observer Pattern Demo
        System.out.println("--- 3. Testing Observer Pattern (Order Status Notification) ---");
        FoodOrderSubject order = new FoodOrderSubject("ORD-902");
        CustomerAppObserver customerApp = new CustomerAppObserver("Shadman Sakib");
        RestaurantDashboardObserver kitchenDisplay = new RestaurantDashboardObserver("Central Cafeteria Kitchen");
        DeliveryAgentObserver deliveryRider = new DeliveryAgentObserver("Rider Kamal");

        order.attach(customerApp);
        order.attach(kitchenDisplay);
        order.attach(deliveryRider);

        order.setStatus("PREPARING", "Chef has started grilling and packing food.");
        order.setStatus("OUT_FOR_DELIVERY", "Rider picked up parcel and heading towards Hall 3.");
        order.setStatus("DELIVERED", "Package handed over to student at Hall Lobby.");
        System.out.println();

        // 4. Strategy Pattern Demo
        System.out.println("--- 4. Testing Strategy Pattern (Delivery Route Selection) ---");
        DeliveryContext deliveryContext = new DeliveryContext(new FastestExpressMotorcycleRoute());
        deliveryContext.dispatchDelivery("Central Cafeteria", "Shahidullah Hall");

        System.out.println("\nSwitching to Direct Pedestrian Route (Rainy weather / campus gate closed)...");
        deliveryContext.setRouteStrategy(new DirectCampusPedestrianRoute());
        deliveryContext.dispatchDelivery("Central Cafeteria", "Shahidullah Hall");

        System.out.println("\nSwitching to Eco Bicycle Route (Campus zero-emission green hour)...");
        deliveryContext.setRouteStrategy(new EcoFriendlyBicycleRoute());
        deliveryContext.dispatchDelivery("Central Cafeteria", "Shahidullah Hall");
        System.out.println();

        // 5. Adapter Pattern Demo
        System.out.println("--- 5. Testing Adapter Pattern (Third-Party Payment API) ---");
        ExternalFintechGateway fintechGateway = new ExternalFintechGateway();
        PaymentProcessor paymentProcessor = new ThirdPartyPaymentAdapter(fintechGateway);

        boolean paymentResult = paymentProcessor.executePayment("CUST-USER-5501", 18.50);
        System.out.println("External Payment Authorized? " + paymentResult);
        System.out.println("External Transaction Reference: " + paymentProcessor.getConfirmationTransactionCode());

        System.out.println("\n[SFMS Project Execution Completed Successfully]");
    }
}
