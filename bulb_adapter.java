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