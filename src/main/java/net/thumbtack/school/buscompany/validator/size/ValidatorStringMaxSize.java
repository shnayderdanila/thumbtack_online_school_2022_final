package net.thumbtack.school.buscompany.validator.size;

import net.thumbtack.school.buscompany.configuration.ValidationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidatorStringMaxSize implements ConstraintValidator<StringMaxSize, String> {
    @Autowired
    ValidationProperties properties;

    private final Logger LOGGER = LoggerFactory.getLogger(ValidatorStringMaxSize.class);

    @Override
    public void initialize(StringMaxSize constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s == null) {
            return false;
        }
        LOGGER.debug("String max size validated {}", s);

        constraintValidatorContext.disableDefaultConstraintViolation();

        constraintValidatorContext
                .buildConstraintViolationWithTemplate("Your value:" + s + " is too large (more than " + properties.getMax_name_length() + " characters)")
                .addConstraintViolation();

        return s.length() <= properties.getMax_name_length();
    }
}
