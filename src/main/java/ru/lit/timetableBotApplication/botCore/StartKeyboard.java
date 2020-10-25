package ru.lit.timetableBotApplication.botCore;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;


public class StartKeyboard implements BotKeyboard {

    private final InlineKeyboardButton student;
    private final InlineKeyboardButton teacher;
    private final List<InlineKeyboardButton> row;

    StartKeyboard() {
        this.student = new InlineKeyboardButton().setText("Студент").setCallbackData("/getStudents");
        this.teacher = new InlineKeyboardButton().setText("Преподаватель").setCallbackData("/getTeachers");
        this.row = new ArrayList<>();
        this.row.add(student);
        this.row.add(teacher);
    }

    @Override
    public List<List<InlineKeyboardButton>> getKeyboard() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row);
        return keyboard;
    }
}
