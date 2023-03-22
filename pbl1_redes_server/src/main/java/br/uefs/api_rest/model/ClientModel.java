package br.uefs.api_rest.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class ClientModel {
    private final int smartMeterCode;
    private final int id;
    private final String name;
    private List<ConsumptionModel> consumptions;
    private BigDecimal totalConsumptions;
    private List<InvoiceModel> invoices;

    public ClientModel(int id, String name, int smartMeterId) {
        this.name = name;
        this.id = id;
        this.smartMeterCode = smartMeterId;
        this.consumptions = new LinkedList<>();
        this.invoices = new ArrayList<>();
        this.totalConsumptions = BigDecimal.valueOf(0.0);
        this.totalConsumptions.setScale(2, RoundingMode.HALF_UP);
    }

    public int getSmartMeterCode() {
        return smartMeterCode;
    }

    public String getName() {
        return name;
    }

    public List<ConsumptionModel> getConsumptions() {
        return consumptions;
    }

    public BigDecimal getTotalConsumptions() {
        return totalConsumptions;
    }
    public void setTotalConsumptions(BigDecimal totalConsumptions) {
        this.totalConsumptions = totalConsumptions;
    }
    public int getId() {
        return id;
    }

    public void setConsumptions(List<ConsumptionModel> consumptions) {
        this.consumptions = consumptions;
    }

    public List<InvoiceModel> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<InvoiceModel> invoices) {
        this.invoices = invoices;
    }
}
