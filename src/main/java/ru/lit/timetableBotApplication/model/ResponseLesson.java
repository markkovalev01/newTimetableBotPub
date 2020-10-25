package ru.lit.timetableBotApplication.model;

import lombok.Data;

/**
 * Lesson for response
 */
@Data
public class ResponseLesson {
    private static final String GROUP_SEPARATOR = ", ";

    private String name;
    private String type;
    private String classroom;
    private String subject;

    private ResponseLesson(final String name, final String type, final String classroom, final String subject) {
        this.name = name;
        this.type = type;
        this.classroom = classroom;
        this.subject = subject;
    }

    public String toString() {
        return String.format(
            "Название: %s \n" +
                "Тип: %s \n" +
                "Аудитория: %s \n" +
                "Преподаватель: %s \n"
            , this.name, this.type, this.classroom, this.subject);
    }

}