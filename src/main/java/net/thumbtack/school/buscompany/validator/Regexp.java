package net.thumbtack.school.buscompany.validator;

public class Regexp {

    public static final String TIME = "^[0-9]?[0-9]:([0-5]?[0-9])$";

    public static final String TIME_24 = "^(2[0-3]|[01]?[0-9]):([0-5]?[0-9])$";

    public static final String DATE = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$";

    public static final String NAME = "[а-яА-Я- ]*";

    public static final String LOGIN = "[a-zA-Zа-яА-Я0-9]*";

}
