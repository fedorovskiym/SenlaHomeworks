package com.senla.ProductService.annotation;

import jakarta.validation.constraints.Min;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Min(value = 1, message = "Id must be greater than 0")
public @interface CheckId {
}
