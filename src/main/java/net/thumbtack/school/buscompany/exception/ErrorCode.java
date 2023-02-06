package net.thumbtack.school.buscompany.exception;


public enum ErrorCode {
    INCORRECT_LOGIN           ("login",    "User with login doesn't exist"),
    DATABASE_EXCEPTION        ("database", "Unsupported exception"),
    INCORRECT_EMAIL           ("email",    "User with email doe's exist"),
    WRONG_PASSWORD            ("password", "Incorrect password"),
    INCORRECT_ADMIN_ACTION    ("cookie",   "You can't exit"),
    INCORRECT_JAVA_SESSION_ID ("cookie",   "Incorrect javaSessionId"),
    INCORRECT_ORDER_DATE      ("date",     "Incorrect date"),
    INCORRECT_SCHEDULE_PERIOD ("period",   "Incorrect period"),
    INCORRECT_ID_TRIP         ("idTrip",   "Incorrect id trip"),
    WRONG_ACTION              ("idTrip",   "You can't do this"),
    INCORRECT_ORDER           ("idOrder",  "You don't have an order with this id"),
    BUSY_PLACE                ("place",    "Place is busy"),
    INCORRECT_PLACE           ("place",    "Place is not exist"),
    ALL_PLACE_BUSY            ("passenger","Not enough place"),
    INCORRECT_PASSENGER       ("passenger","Passenger doesn't exist in order"),
    INCORRECT_BUS_NAME        ("busName",  "This bus doesn't exist")
    ;

    private String field;
    private String message;

    ErrorCode(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
