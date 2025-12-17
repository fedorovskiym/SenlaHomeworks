package com.senla.task1.config;

import com.senla.task1.annotations.ConfigProperty;

public class AutoServiceConfig {

    @ConfigProperty(propertyName = "AutoServiceConfig.allowAddGaragePlace")
    private boolean allowAddGaragePlace;
    @ConfigProperty(propertyName = "AutoServiceConfig.allowDeleteGaragePlaces")
    private boolean allowDeleteGaragePlace;
    @ConfigProperty(propertyName = "AutoServiceConfig.allowShiftOrdersTime")
    private boolean allowShiftOrdersTime;
    @ConfigProperty(propertyName = "AutoServiceConfig.allowDeleteOrder")
    private boolean allowDeleteOrder;

    public boolean isAllowAddGaragePlace() {
        return allowAddGaragePlace;
    }

    public boolean isAllowDeleteGaragePlace() {
        return allowDeleteGaragePlace;
    }

    public boolean isAllowShiftOrdersTime() {
        return allowShiftOrdersTime;
    }

    public boolean isAllowDeleteOrder() {
        return allowDeleteOrder;
    }

    @Override
    public String toString() {
        return "AutoServiceConfig{" +
                "allowAddGaragePlace=" + allowAddGaragePlace +
                ", allowDeleteGaragePlace=" + allowDeleteGaragePlace +
                ", allowShiftOrdersTime=" + allowShiftOrdersTime +
                ", allowDeleteOrder=" + allowDeleteOrder +
                '}';
    }
}
