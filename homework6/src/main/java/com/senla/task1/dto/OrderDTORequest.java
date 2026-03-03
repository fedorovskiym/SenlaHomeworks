package com.senla.task1.dto;

import com.senla.task1.models.enums.OrderSortType;

public record OrderDTORequest(Integer fromYear, Integer fromMonth, Integer fromDay,
                              Integer toYear, Integer toMonth, Integer toDay, OrderSortType sortType, Boolean flag) {
}
