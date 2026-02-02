package com.senla.task1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoServiceConfig {

    @Value("${AutoServiceConfig.allowAddGaragePlace}")
    private boolean allowAddGaragePlace;
    @Value("${AutoServiceConfig.allowDeleteGaragePlaces}")
    private boolean allowDeleteGaragePlace;
    @Value("${AutoServiceConfig.allowShiftOrdersTime}")
    private boolean allowShiftOrdersTime;
    @Value("${AutoServiceConfig.allowDeleteOrder}")
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
