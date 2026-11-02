package project1_aas;

/**
 * Project 1: Agricultural Automation System (AAS)
 * Demonstrates all 5 required design patterns:
 * 1. Singleton: FarmDataManager
 * 2. Factory: SensorFactory (creating SoilSensor, WeatherSensor, CropHealthSensor)
 * 3. Observer: AlertNotifier (Subject) notifying FarmerApp, IrrigationController (Observers)
 * 4. Strategy: IrrigationStrategy (NormalIrrigation, WaterSavingIrrigation, EmergencyDroughtIrrigation)
 * 5. Adapter: WeatherAPIAdapter (adapts legacy 3rd-party ExternalWeatherService to target WeatherService)
 */

import java.util.*;

// ==========================================
// 1. SINGLETON PATTERN: FarmDataManager
// ==========================================
class FarmDataManager {
    private static volatile FarmDataManager instance;
    private final Map<String, Object> farmTelemetry = new HashMap<>();

    private FarmDataManager() {
        System.out.println("[Singleton] FarmDataManager initialized (Central Farm Database connected).");
    }

    public static FarmDataManager getInstance() {
        if (instance == null) {
            synchronized (FarmDataManager.class) {
                if (instance == null) {
                    instance = new FarmDataManager();
                }
            }
        }
        return instance;
    }

    public void recordData(String key, Object value) {
        farmTelemetry.put(key, value);
        System.out.println("[FarmDataManager] Telemetry updated -> " + key + ": " + value);
    }

    public Object getData(String key) {
        return farmTelemetry.get(key);
    }
}

// ==========================================
// 2. FACTORY PATTERN: Sensor Creation
// ==========================================
interface Sensor {
    String getSensorId();
    String getType();
    void readData();
}

class SoilMoistureSensor implements Sensor {
    private final String id;
    public SoilMoistureSensor(String id) { this.id = id; }
    @Override public String getSensorId() { return id; }
    @Override public String getType() { return "Soil Moisture Sensor"; }
    @Override public void readData() {
        System.out.println("[Sensor:" + id + "] Reading soil moisture: 24.5% (Volumetric Water Content)");
    }
}

class CropHealthDroneSensor implements Sensor {
    private final String id;
    public CropHealthDroneSensor(String id) { this.id = id; }
    @Override public String getSensorId() { return id; }
    @Override public String getType() { return "Drone Multispectral Camera Sensor"; }
    @Override public void readData() {
        System.out.println("[Sensor:" + id + "] NDVI index computed: 0.78 (Healthy Crop Foliage)");
    }
}

class EnvironmentalWeatherSensor implements Sensor {
    private final String id;
    public EnvironmentalWeatherSensor(String id) { this.id = id; }
    @Override public String getSensorId() { return id; }
    @Override public String getType() { return "Environmental Weather Sensor"; }
    @Override public void readData() {
        System.out.println("[Sensor:" + id + "] Temperature: 32C, Humidity: 68%, Solar Radiation: 820 W/m2");
    }
}

class SensorFactory {
    public static Sensor createSensor(String type, String id) {
        switch (type.toLowerCase()) {
            case "soil":
                return new SoilMoistureSensor(id);
            case "drone":
                return new CropHealthDroneSensor(id);
            case "weather":
                return new EnvironmentalWeatherSensor(id);
            default:
                throw new IllegalArgumentException("Unknown sensor type: " + type);
        }
    }
}

// ==========================================
// 3. OBSERVER PATTERN: Weather / Pest Alerts
// ==========================================
interface FarmAlertObserver {
    void onAlert(String alertType, String message);
}

class FarmerMobileApp implements FarmAlertObserver {
    private final String farmerName;
    public FarmerMobileApp(String farmerName) { this.farmerName = farmerName; }
    @Override
    public void onAlert(String alertType, String message) {
        System.out.println("  [Push Notification -> " + farmerName + "'s Mobile App] " + alertType + ": " + message);
    }
}

class AutomatedIrrigationController implements FarmAlertObserver {
    @Override
    public void onAlert(String alertType, String message) {
        if ("DROUGHT_WARNING".equalsIgnoreCase(alertType)) {
            System.out.println("  [IrrigationController] Received DROUGHT_WARNING -> Pre-scheduling irrigation cycle.");
        } else if ("HEAVY_RAIN_WARNING".equalsIgnoreCase(alertType)) {
            System.out.println("  [IrrigationController] Received HEAVY_RAIN_WARNING -> Pausing scheduled pumps.");
        }
    }
}

class AlertPublisher {
    private final List<FarmAlertObserver> observers = new ArrayList<>();

    public void registerObserver(FarmAlertObserver observer) {
        observers.add(observer);
    }

    public void unregisterObserver(FarmAlertObserver observer) {
        observers.remove(observer);
    }

    public void notifyAlert(String alertType, String message) {
        System.out.println("[AlertPublisher] Broadcasting alert: " + alertType);
        for (FarmAlertObserver observer : observers) {
            observer.onAlert(alertType, message);
        }
    }
}

// ==========================================
// 4. STRATEGY PATTERN: Irrigation Strategy
// ==========================================
interface IrrigationStrategy {
    void executeIrrigation(double soilMoistureLevel, int pumpCapacityLitresPerHour);
}

class NormalIrrigationStrategy implements IrrigationStrategy {
    @Override
    public void executeIrrigation(double soilMoistureLevel, int pumpCapacityLitresPerHour) {
        System.out.println("[Strategy: Normal Mode] Moisture is " + soilMoistureLevel + "%. Running standard irrigation cycle at " + pumpCapacityLitresPerHour + " L/hr.");
    }
}

class WaterSavingDripStrategy implements IrrigationStrategy {
    @Override
    public void executeIrrigation(double soilMoistureLevel, int pumpCapacityLitresPerHour) {
        System.out.println("[Strategy: Water-Saving Drip Mode] Moisture is " + soilMoistureLevel + "%. Optimizing water usage via micro-drip at " + (pumpCapacityLitresPerHour * 0.4) + " L/hr.");
    }
}

class EmergencyDroughtStrategy implements IrrigationStrategy {
    @Override
    public void executeIrrigation(double soilMoistureLevel, int pumpCapacityLitresPerHour) {
        System.out.println("[Strategy: Emergency Drought Mode] Critical moisture level " + soilMoistureLevel + "%! Running high-volume emergency pumps at " + pumpCapacityLitresPerHour + " L/hr.");
    }
}

class IrrigationContext {
    private IrrigationStrategy strategy;

    public IrrigationContext(IrrigationStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(IrrigationStrategy strategy) {
        this.strategy = strategy;
    }

    public void performIrrigation(double moisture, int pumpCapacity) {
        strategy.executeIrrigation(moisture, pumpCapacity);
    }
}

// ==========================================
// 5. ADAPTER PATTERN: Weather API Integration
// ==========================================
// Target interface required by Agricultural Automation System
interface WeatherService {
    double getTemperatureCelsius();
    double getRainfallProbabilityPercentage();
    String getForecastSummary();
}

// Incompatible external 3rd-party library/service
class ExternalLegacyWeatherAPI {
    public double fetchTemperatureFahrenheit() {
        return 91.4; // 91.4 F = 33 C
    }
    public double fetchPrecipitationChanceRatio() {
        return 0.75; // 0.75 ratio = 75%
    }
    public String retrieveRawWeatherPayload() {
        return "JSON: { sky: 'overcast', wind_knots: 12, storm_risk: 'moderate' }";
    }
}

// Adapter converting ExternalLegacyWeatherAPI to WeatherService
class WeatherAPIAdapter implements WeatherService {
    private final ExternalLegacyWeatherAPI externalAPI;

    public WeatherAPIAdapter(ExternalLegacyWeatherAPI externalAPI) {
        this.externalAPI = externalAPI;
    }

    @Override
    public double getTemperatureCelsius() {
        double fahrenheit = externalAPI.fetchTemperatureFahrenheit();
        return Math.round(((fahrenheit - 32) * 5.0 / 9.0) * 10.0) / 10.0;
    }

    @Override
    public double getRainfallProbabilityPercentage() {
        return externalAPI.fetchPrecipitationChanceRatio() * 100.0;
    }

    @Override
    public String getForecastSummary() {
        return "Adapted Forecast: " + externalAPI.retrieveRawWeatherPayload();
    }
}

// ==========================================
// MAIN DRIVER & DEMONSTRATION
// ==========================================
public class Main {
    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println(" PROJECT 1: AGRICULTURAL AUTOMATION SYSTEM (AAS) - DEMO");
        System.out.println("===============================================================\n");

        // 1. Singleton Pattern Demo
        System.out.println("--- 1. Testing Singleton Pattern (FarmDataManager) ---");
        FarmDataManager manager1 = FarmDataManager.getInstance();
        FarmDataManager manager2 = FarmDataManager.getInstance();
        manager1.recordData("Field_A_Nitrogen_Level", "Optimal (45 ppm)");
        System.out.println("Verifying single instance: manager1 == manager2 ? " + (manager1 == manager2));
        System.out.println("Data read from manager2: " + manager2.getData("Field_A_Nitrogen_Level"));
        System.out.println();

        // 2. Factory Pattern Demo
        System.out.println("--- 2. Testing Factory Pattern (SensorFactory) ---");
        Sensor soilSensor = SensorFactory.createSensor("soil", "SNSR-SOIL-101");
        Sensor droneSensor = SensorFactory.createSensor("drone", "SNSR-DRONE-007");
        Sensor weatherSensor = SensorFactory.createSensor("weather", "SNSR-ENV-202");

        soilSensor.readData();
        droneSensor.readData();
        weatherSensor.readData();
        System.out.println();

        // 3. Observer Pattern Demo
        System.out.println("--- 3. Testing Observer Pattern (Weather / Pest Alert System) ---");
        AlertPublisher alertPublisher = new AlertPublisher();
        FarmerMobileApp farmerApp = new FarmerMobileApp("Farmer Karim");
        AutomatedIrrigationController controller = new AutomatedIrrigationController();

        alertPublisher.registerObserver(farmerApp);
        alertPublisher.registerObserver(controller);

        alertPublisher.notifyAlert("DROUGHT_WARNING", "Soil moisture dropped below threshold in Sector 3.");
        alertPublisher.notifyAlert("PEST_ALERT", "Stem borer larvae detected in Sector 1 paddy field by Drone Camera.");
        System.out.println();

        // 4. Strategy Pattern Demo
        System.out.println("--- 4. Testing Strategy Pattern (Irrigation Strategies) ---");
        IrrigationContext irrigationContext = new IrrigationContext(new NormalIrrigationStrategy());
        irrigationContext.performIrrigation(35.0, 1000);

        System.out.println("Switching to Water-Saving Drip Strategy for water conservation...");
        irrigationContext.setStrategy(new WaterSavingDripStrategy());
        irrigationContext.performIrrigation(30.0, 1000);

        System.out.println("Switching to Emergency Drought Strategy due to critical dryness...");
        irrigationContext.setStrategy(new EmergencyDroughtStrategy());
        irrigationContext.performIrrigation(12.0, 1000);
        System.out.println();

        // 5. Adapter Pattern Demo
        System.out.println("--- 5. Testing Adapter Pattern (Weather API Integration) ---");
        ExternalLegacyWeatherAPI thirdPartyAPI = new ExternalLegacyWeatherAPI();
        WeatherService weatherService = new WeatherAPIAdapter(thirdPartyAPI);

        System.out.println("Converted Temperature (°C): " + weatherService.getTemperatureCelsius() + " °C");
        System.out.println("Converted Rain Probability: " + weatherService.getRainfallProbabilityPercentage() + "%");
        System.out.println("Forecast Message: " + weatherService.getForecastSummary());

        System.out.println("\n[AAS Project Execution Completed Successfully]");
    }
}
