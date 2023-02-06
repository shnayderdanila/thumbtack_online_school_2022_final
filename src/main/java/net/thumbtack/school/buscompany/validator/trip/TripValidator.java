package net.thumbtack.school.buscompany.validator.trip;

import net.thumbtack.school.buscompany.models.Schedule;
import net.thumbtack.school.buscompany.utils.SimpleFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TripValidator implements ConstraintValidator<Trip, Object> {

    private final Logger LOGGER = LoggerFactory.getLogger(TripValidator.class);

    @Override
    public void initialize(Trip constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        LOGGER.debug("Valid Trip:{}", o);

        constraintValidatorContext.disableDefaultConstraintViolation();

        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(o);

        Schedule schedule = (Schedule) beanWrapper.getPropertyValue("schedule");
        LOGGER.debug("Valid Schedule:{}", schedule);

        List<String> dates = (List<String>) beanWrapper.getPropertyValue("dates");
        LOGGER.debug("Valid dates:{}", dates);

        if(schedule != null && dates != null || schedule == null && dates == null){

            LOGGER.info("Valid false. One of two schedule or dates must be specified");

            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Incorrect filling Trip. " +
                            "One of two must be specified schedule or dates")
                    .addPropertyNode("dates")
                    .addConstraintViolation();

            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Incorrect filling Trip. " +
                            "One of two must be specified schedule or dates")
                    .addPropertyNode("schedule")
                    .addConstraintViolation();

            return false;
        }

        if(schedule != null){
            LocalDate fromDate, toDate;

            try {

                fromDate = LocalDate.from(SimpleFormatter.dateFormatter.parse( schedule.getFromDate() ));
                toDate = LocalDate.from(SimpleFormatter.dateFormatter.parse( schedule.getToDate() ));

            } catch (DateTimeParseException e) {

                constraintValidatorContext
                        .buildConstraintViolationWithTemplate("The dates:"+schedule.getToDate() + "," +schedule.getFromDate()+ " must be in the format yyyy-mm-dd")
                        .addPropertyNode("schedule")
                        .addConstraintViolation();

                LOGGER.info("Valid false. The dates:{},{} must be in the format yyyy-mm-dd", schedule.getToDate(), schedule.getFromDate());

                return false;
            }

            if(fromDate.isAfter(toDate)){

                constraintValidatorContext
                        .buildConstraintViolationWithTemplate("Departure date:"+
                                SimpleFormatter.dateFormatter.format(fromDate)
                                + " should be before then arrival date:"+
                                SimpleFormatter.dateFormatter.format(toDate))
                        .addPropertyNode("schedule")
                        .addConstraintViolation();

                LOGGER.info("Valid false. From date:{} must be after to date:{}", schedule.getFromDate(), schedule.getToDate());

                return false;
            }
        }

        if(dates != null){

            for (String date : dates){

                try {

                    SimpleFormatter.dateFormatter.parse(date);

                } catch (DateTimeParseException e) {
                    constraintValidatorContext
                            .buildConstraintViolationWithTemplate("The dates:"+dates+" must be in the format yyyy-mm-dd")
                            .addPropertyNode("dates")
                            .addConstraintViolation();
                    LOGGER.info("Valid false. The dates:{} must be in the format", dates);
                    return false;

                }
            }
        }

        LOGGER.info("Valid Trip true");
        return true;
    }

}
