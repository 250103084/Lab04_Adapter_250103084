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