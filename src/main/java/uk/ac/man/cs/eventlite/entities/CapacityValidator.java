package uk.ac.man.cs.eventlite.entities;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CapacityValidator implements ConstraintValidator<PositiveIntegerConstraint, Integer>{
	@Override
    public void initialize(PositiveIntegerConstraint capacitynumber) {
    }
 
    @Override
    public boolean isValid(Integer capacity, ConstraintValidatorContext cxt) {
    	System.out.println(capacity != null && capacity>0);
        return capacity != null && capacity>0;
    }
}
