package shop.itbug.scanner_gun.entry;

public class BtDeviceModel {

    private String name;
    private String address;
    private Integer signalIntensity;

    public BtDeviceModel(){}

    public BtDeviceModel(String name, String address,Integer signalIntensity) {
        this.name = name;
        this.address = address;
        this.signalIntensity = signalIntensity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getSignalIntensity() {
        return signalIntensity;
    }

    public void setSignalIntensity(Integer signalIntensity) {
        this.signalIntensity = signalIntensity;
    }
}
