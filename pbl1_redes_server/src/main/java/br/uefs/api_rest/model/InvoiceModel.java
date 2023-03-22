package br.uefs.api_rest.model;

public class InvoiceModel {
    private String name;
    private int smartMeterCode;
    private double price;

    public InvoiceModel(String name, int smartMeterCode, double price) {
        this.name = name;
        this.smartMeterCode = smartMeterCode;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getSmartMeterCode() {
        return smartMeterCode;
    }

    public double getPrice() {
        return price;
    }
}
