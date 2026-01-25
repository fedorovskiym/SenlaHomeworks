package com.senla.task1.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Duration;

@Converter
public class DurationConverter implements AttributeConverter<Duration, Long> {

    @Override
    public Long convertToDatabaseColumn(Duration duration) {
        return duration != null ? duration.getSeconds() : null;
    }

    @Override
    public Duration convertToEntityAttribute(Long duration) {
        return duration != null ? Duration.ofSeconds(duration) : Duration.ZERO;
    }
}
