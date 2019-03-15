package uk.ac.man.cs.eventlite.entities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PostCodeValidator implements ConstraintValidator<PostCodeConstraint, String>{
	@Override
    public void initialize(PostCodeConstraint capacitynumber) {
    }
 
    @Override
    public boolean isValid(String postcode, ConstraintValidatorContext cxt) {
    	String regexp="^([A-PR-UWYZ](([0-9](([0-9]|[A-HJKSTUW])?)?)|([A-HK-Y][0-9]([0-9]|[ABEHMNPRVWXY])?)) [0-9][ABD-HJLNP-UW-Z]{2})|GIR 0AA$";
    	Pattern pattern = Pattern.compile(regexp);
    	Matcher matcher = pattern.matcher(postcode.toUpperCase());
    	if (matcher.matches()) {
    		return true;
    	} else {
    		return false;
    	}
    }
}
