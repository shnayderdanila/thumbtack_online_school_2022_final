package net.thumbtack.school.buscompany.validator.date;

import net.thumbtack.school.buscompany.utils.SimpleFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.format.DateTimeParseException;

public class DateValidator implements ConstraintValidator<Date, String> {

    private final Logger LOGGER = LoggerFactory.getLogger(DateValidator.class);

    @Override
    public void initialize(Date constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        LOGGER.info("Date validated {}", s);
        if(s == null){
            return true;
        }

        try {

            SimpleFormatter.dateFormatter.parse(s);
            return true;

        } catch (DateTimeParseException e) {
            constraintValidatorContext.disableDefaultConstraintViolation();

            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Date:"+s+" should be yyyy-mm-dd")
                    .addConstraintViolation();
            return false;

        }

    }
}
