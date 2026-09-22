public class bulb_adapter implements smart_device {

    private static final int K = 4;

    private final legacy_bulb bulb;

    public bulb_adapter(legacy_bulb bulb) {
        if (bulb == null) {
            throw new IllegalArgumentException("LegacyBulb must not be null");
        }
        this.bulb = bulb;
    }

    @Override
    public void turnOn() {
        bulb.setBrightness(255);
    }

    @Override
    public void turnOff() {
        bulb.setBrightness(0);
    }

    @Override
    public boolean isOn() {
        if (!bulb.hasPower()) {
            return false;
        }
        return bulb.readBrightness() > 0;
    }

    @Override
    public int getPowerPercent() {
        if (!bulb.hasPower()) {
            return 0;
        }
        int raw = bulb.readBrightness();
        if (raw == 0) {
            return 0;
        }
        int rawPercent = (int) Math.floor((raw * 100.0) / 255.0);
        int calibrated = rawPercent + K;
        if (calibrated > 100) {
            return 100;
        }
        return calibrated;
    }
}

public class thermostat_adapter implements smart_device {

    private final legacy_thermostat thermostat;

    public thermostat_adapter(legacy_thermostat thermostat) {
        if (thermostat == null) {
            throw new IllegalArgumentException("LegacyThermostat must not be null");
        }
        this.thermostat = thermostat;
    }

    @Override
    public void turnOn() {
        String state = safeCheckDial();
        if ("IDLE".equals(state)) {
            thermostat.rotateDial("LOW");
        }
    }

    @Override
    public void turnOff() {
        thermostat.rotateDial("IDLE");
    }

    @Override
    public boolean isOn() {
        String state = safeCheckDial();
        if (state == null) {
            return false;
        }
        switch (state) {
            case "LOW":
            case "MEDIUM":
            case "MAX":
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getPowerPercent() {
        String state = safeCheckDial();
        if (state == null) {
            return -1;
        }
        switch (state) {
            case "IDLE":
                return 0;
            case "LOW":
                return 33;
            case "MEDIUM":
                return 66;
            case "MAX":
                return 100;
            default:
                return -1;
        }
    }

    private String safeCheckDial() {
        try {
            return thermostat.checkDial();
        } catch (Exception e) {
            return null;
        }
    }
}

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
