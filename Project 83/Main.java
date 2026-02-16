import java.util.ArrayList;
import java.util.List;

class SmartHome {
    private static SmartHome instance;
    private final String homeName = "My Smart Home";

    private SmartHome() {}

    public static synchronized SmartHome getInstance() {
        if (instance == null) {
            instance = new SmartHome();
        }
        return instance;
    }

    public void log(String message) {
        System.out.println("[" + homeName + " LOG]: " + message);
    }
}

interface DeviceVisitor {
    void visit(Light light);
    void visit(Thermostat thermostat);
}

interface Device {
    void enable();
    void disable();
    boolean isEnabled();
    int getPowerUsage();
    void accept(DeviceVisitor visitor);
}

class Light implements Device {
    private boolean on = false;

    @Override
    public void enable() { 
        on = true; 
        System.out.println("Light: On"); 
    }

    @Override
    public void disable() { 
        on = false; 
        System.out.println("Light: Off"); 
    }

    @Override
    public boolean isEnabled() { 
        return on; 
    }

    @Override
    public int getPowerUsage() { 
        return 15;
    }

    @Override
    public void accept(DeviceVisitor visitor) { 
        visitor.visit(this); 
    }
}

class Thermostat implements Device {
    private boolean on = false;

    @Override
    public void enable() { 
        on = true; 
        System.out.println("Thermostat: On"); 
    }

    @Override
    public void disable() { 
        on = false; 
        System.out.println("Thermostat: Off"); 
    }

    @Override
    public boolean isEnabled() { 
        return on; 
    }

    @Override
    public int getPowerUsage() { 
        return 200;
    }

    @Override
    public void accept(DeviceVisitor visitor) { 
        visitor.visit(this); 
    }
}

abstract class RemoteControl {
    protected Device device;
    protected RemoteControl(Device device) { this.device = device; }
    
    public void togglePower() {
        if (device.isEnabled()) device.disable();
        else device.enable();
    }
}

class AdvancedRemote extends RemoteControl {
    public AdvancedRemote(Device device) { super(device); }
    
    public void mute() {
        SmartHome.getInstance().log("Quiet mode activated");
        device.disable();
    }
}

class EnergyAuditVisitor implements DeviceVisitor {
    private int totalPower = 0;

    @Override
    public void visit(Light light) {
        totalPower += light.getPowerUsage();
    }

    @Override
    public void visit(Thermostat thermostat) {
        totalPower += thermostat.getPowerUsage();
    }

    public int getTotalPower() { return totalPower; }
}

public class Main {
    public static void main(String[] args) {
        SmartHome hub = SmartHome.getInstance();
        hub.log("Initializing system");

        Device lamp = new Light();
        Device heater = new Thermostat();

        hub.log("Bridge control:");
        AdvancedRemote remote = new AdvancedRemote(lamp);
        remote.togglePower();
        remote.mute();

        AdvancedRemote heaterRemote = new AdvancedRemote(heater);
        heaterRemote.togglePower();

        hub.log("Energy audit:");
        List<Device> devices = new ArrayList<>();
        devices.add(lamp);
        devices.add(heater);

        EnergyAuditVisitor audit = new EnergyAuditVisitor();
        for (Device d : devices) {
            d.accept(audit);
        }

        System.out.println("System total power: " + audit.getTotalPower() + " W");
    }
}