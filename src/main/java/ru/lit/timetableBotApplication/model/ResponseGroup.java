package ru.lit.timetableBotApplication.model;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ResponseGroup {
    private String id;
    private String displayedName;

    public String toString() {
        return displayedName;
    }

}
