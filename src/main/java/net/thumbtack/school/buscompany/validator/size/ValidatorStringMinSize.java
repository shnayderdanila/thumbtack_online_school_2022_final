package net.thumbtack.school.buscompany.validator.size;

import net.thumbtack.school.buscompany.configuration.ValidationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidatorStringMinSize implements ConstraintValidator<StringMinSize, String> {
    @Autowired
    ValidationProperties properties;
    private final Logger LOGGER = LoggerFactory.getLogger(ValidatorStringMinSize.class);

    @Override
    public void initialize(StringMinSize constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s == null) {
            return false;
        }
        LOGGER.debug("String min size validated {}", s);
        constraintValidatorContext.disableDefaultConstraintViolation();

        constraintValidatorContext
                .buildConstraintViolationWithTemplate("Your value:"+s+" is too short (less than " + properties.getMin_password_length() + " characters)")
                .addConstraintViolation();

        return s.length() >= properties.getMin_password_length();
    }
}
