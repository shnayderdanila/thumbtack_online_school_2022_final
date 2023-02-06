package net.thumbtack.school.buscompany.utils;



import java.time.format.DateTimeFormatter;

public class SimpleFormatter {
    public final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    public final static DateTimeFormatter dateWithTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
