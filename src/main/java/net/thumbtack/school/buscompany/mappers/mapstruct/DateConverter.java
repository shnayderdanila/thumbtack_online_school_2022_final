package net.thumbtack.school.buscompany.mappers.mapstruct;

import org.apache.commons.lang3.EnumUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Mapper
public interface DateConverter {
    DateConverter MAPPER = Mappers.getMapper(DateConverter.class);

    default List<Integer> stringsToInt(String...parse){

        List<Integer> dates = new ArrayList<>();

        for(String s : parse){

            try {
                int date = Integer.parseInt(s);

                if(date < 1 || date > 31){
                    return null;
                }

                dates.add(date);

            } catch (NumberFormatException e){
                return null;
            }
        }
        return dates;
    }

    default List<DayOfWeek> stringsToWeekDays(String...parse){

        List<DayOfWeek> weekDays = new ArrayList<>();

        for(String s : parse){

            DayOfWeek dayWeek = EnumUtils.getEnumIgnoreCase(DayOfWeek.class, s);

            if(dayWeek == null) {

                return null;

            }

            weekDays.add(dayWeek);
        }

        return weekDays;
    }

}



