package net.thumbtack.school.buscompany.validator.passport;

import net.thumbtack.school.buscompany.dto.request.RegPassengerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;

public class PassportValidator implements ConstraintValidator<Passport, List<RegPassengerDto>> {
    private final Logger LOGGER = LoggerFactory.getLogger(PassportValidator.class);

    @Override
    public void initialize(Passport constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<RegPassengerDto> passengers, ConstraintValidatorContext constraintValidatorContext) {
        LOGGER.debug("Valid Passengers:{}", passengers);

        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate("Passport passengers should be unique:"+passengers)
                .addConstraintViolation();

        return passengers.size() == passengers.stream().map(RegPassengerDto::getPassport).collect(Collectors.toSet()).size();
    }


}
