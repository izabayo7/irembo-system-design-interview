package rw.companyz.useraccountms.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EOTPStatus {

    NOT_USED("NOT_USED"),

    VERIFIED("VERIFIED"),

    EXPIRED("EXPIRED");


    private String value;

    EOTPStatus(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static EOTPStatus fromValue(String text) {
        for (EOTPStatus b : EOTPStatus.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

}
