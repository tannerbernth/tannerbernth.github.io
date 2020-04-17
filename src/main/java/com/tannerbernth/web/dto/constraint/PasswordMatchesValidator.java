package com.tannerbernth.web.dto.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.tannerbernth.web.dto.PasswordDto;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> { 

	@Override
	public void initialize(PasswordMatches constraintAnnotation) { }

	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context){   
		PasswordDto passwords = (PasswordDto) object;
		return  passwords.getPassword().equals(passwords.getMatchingPassword());
	}
}