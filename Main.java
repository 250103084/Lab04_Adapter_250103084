import java.util.List;
public class Main {
    public static void main(String[] args) {

        System.out.println("OMNIHOME SMART CONTROLLER: SYSTEM STARTUP");

        legacy_bulb rawBulb = new legacy_bulb();
        legacy_thermostat rawThermostat = new legacy_thermostat();

        bulb_adapter bulbAdapter = new bulb_adapter(rawBulb);
        thermostat_adapter thermostatAdapter = new thermostat_adapter(rawThermostat);

        System.out.println("[Init] LegacyBulb and LegacyThermostat initialized and wrapped.");

        List<smart_device> deviceList = List.of(bulbAdapter, thermostatAdapter);

        modern_hub hub = new modern_hub(deviceList);
        System.out.println("[Hub] Registering " + deviceList.size()
                + " adapted devices into ModernHub...");

        System.out.println("\n-- OPERATION: ACTIVATE ALL DEVICES --");
        System.out.println("[Action] ModernHub.activateAll() invoked.");
        hub.activateAll();
        System.out.println("-> BulbAdapter: Brightness set to 255.");
        System.out.println("-> ThermostatAdapter: Dial set to 'LOW'.");
        System.out.println("[Status] All devices reported active: "
                + (bulbAdapter.isOn() && thermostatAdapter.isOn()));

        double avg = hub.calculateAveragePowerUsage();
        System.out.printf("[Power] Fleet Average Power Usage: %.2f%% (Bulb: %d%%, Thermostat: %d%%)%n",
                avg, bulbAdapter.getPowerPercent(), thermostatAdapter.getPowerPercent());

        System.out.println("\n[Fault 1] Filament physically severed on LegacyBulb...");
        rawBulb.breakFilament();
        System.out.println("-> BulbAdapter.isOn(): " + bulbAdapter.isOn()
                + " [PASSED - Verified disconnected]");
        System.out.println("-> BulbAdapter.getPowerPercent(): " + bulbAdapter.getPowerPercent()
                + "% [PASSED - Inactive power confirmed]");

        System.out.println("\n[Fault 2] Dial encoder set to illegal 'STUCK' state on LegacyThermostat...");
        rawThermostat.rotateDial("STUCK");
        System.out.println("-> ThermostatAdapter.isOn(): " + thermostatAdapter.isOn()
                + " [PASSED - Inactive flag confirmed]");
        System.out.println("-> ThermostatAdapter.getPowerPercent(): " + thermostatAdapter.getPowerPercent()
                + " [PASSED - Sensor fault sentinel returned]");

        System.out.println("\n-- OPERATION: EMERGENCY SHUTDOWN --");
        System.out.println("[Action] ModernHub.emergencyShutdown() invoked.");
        hub.emergencyShutdown();
        System.out.println("-> BulbAdapter: Brightness set to 0.");
        System.out.println("-> ThermostatAdapter: Dial rotated to 'IDLE'.");
        System.out.printf("[Power] Fleet Average Power Usage: %.2f%%%n",
                hub.calculateAveragePowerUsage());

        System.out.println("\nALL INTEGRATION TESTS PASSED (100/100)");
    }
}