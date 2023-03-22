package br.uefs.api_rest.model;

import java.math.BigDecimal;

public class ConsumptionModel {
    private String dateTime;
    private BigDecimal consumption;

    public ConsumptionModel(String dateTime, BigDecimal consumption) {
        this.dateTime = dateTime;
        this.consumption = consumption;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public BigDecimal getConsumption() {
        return consumption;
    }

    public void setConsumption(BigDecimal consumption) {
        this.consumption = consumption;
    }
}
