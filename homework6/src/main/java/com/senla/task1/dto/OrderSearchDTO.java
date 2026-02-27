package com.senla.task1.dto;

public record OrderSearchDTO (Integer mechanicId, String status, String sortType, Boolean flag,
                              Boolean submissionDateTime, Boolean completionDateTime, Boolean price,
                              Integer fromYear, Integer fromMonth, Integer fromDay,
                              Integer toYear, Integer toMonth, Integer toDay) {
}
