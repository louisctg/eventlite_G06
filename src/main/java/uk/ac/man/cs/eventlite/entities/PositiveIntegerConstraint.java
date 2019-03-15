package uk.ac.man.cs.eventlite.entities;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.RetentionPolicy;  
import java.lang.annotation.ElementType;   

@Documented
@Constraint(validatedBy = CapacityValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveIntegerConstraint {
	String message() default "Shall be a positive integer here";
	Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
