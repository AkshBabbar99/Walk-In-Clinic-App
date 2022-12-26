package io.github.professor_forward.teampineapple.walkinclinic.repo;

public enum DayOfWeek {
    SUNDAY(1),
    MONDAY(2),
    TUESDAY(3),
    WEDNESDAY(4),
    THURSDAY(5),
    FRIDAY(6),
    SATURDAY(7);

    public final int code;

    DayOfWeek(int code) {
        this.code = code;
    }

    public static DayOfWeek of(int code) {
        for (DayOfWeek day : values()) {
            if (day.code == code) return day;
        }
        throw new IllegalArgumentException("invalid day: " + code);
    }
}
