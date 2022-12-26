package io.github.professor_forward.teampineapple.walkinclinic.repo;

public enum BookingState {
    PENDING(0),
    CANCELED(1),
    COMPLETE(2);

    public final int code;

    BookingState(int code) {
        this.code = code;
    }

    public static BookingState fromCode(int code) {
        for (BookingState state : values()) {
            if (state.code == code) return state;
        }
        throw new IllegalArgumentException("Unknown booking state code: " + code);
    }
}
