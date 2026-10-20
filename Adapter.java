class ModernConsole {
    public void playDigital() {
        System.out.println("Playing a digital game on Modern Console!");
    }
}
class RetroCartridge {
    public void insertCartridge() {
        System.out.println("Retro cartridge inserted! Playing retro game...");
    }
}
class CartridgeAdapter extends ModernConsole {
    private RetroCartridge cartridge;
    public CartridgeAdapter(RetroCartridge cartridge) {
        this.cartridge = cartridge;
    }
    @Override
    public void playDigital() {
        cartridge.insertCartridge();
    }
}
public class Adapter {
    static void main(String[] args) {
        RetroCartridge retroGame = new RetroCartridge();
        ModernConsole console = new CartridgeAdapter(retroGame);
        console.playDigital();
    }
}
