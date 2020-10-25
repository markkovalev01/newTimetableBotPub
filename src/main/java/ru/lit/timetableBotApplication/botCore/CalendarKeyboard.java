package ru.lit.timetableBotApplication.botCore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class CalendarKeyboard implements BotKeyboard {

    private final Boolean backButton = false;
    private final String[] DAY_OF_WEEK = {
        "Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"
    };
    int month = LocalDate.now().getMonth().getValue();
    int year = LocalDate.now().getYear();
    String id;
    private List<List<InlineKeyboardButton>> keyboard;

    public void setId(String id) {
        this.id = id;
    }

    public void setMonthYear(int month, int year) {
        this.month = month;
        this.year = year;
    }


    private void addBackButton(String backTo) {
        if (this.backButton) {
            return;
        }
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(new InlineKeyboardButton().setText("Назад").setCallbackData(backTo));
        this.keyboard.add(row);
    }

    private void addNavButtons() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        int newMonth, newYear;
        if (this.month == 1) {
            row.add(new InlineKeyboardButton().setText("<<")
                .setCallbackData(String.format("/getCalendar?month=%d&year=%d", 12, this.year - 1)));
            row.add(new InlineKeyboardButton().setText(">>")
                .setCallbackData(String.format("/getCalendar?month=%d&year=%d", this.month + 1, this.year)));
            keyboard.add(row);
            return;
        }
        if (this.month == 12) {
            row.add(new InlineKeyboardButton().setText("<<")
                .setCallbackData(String.format("/getCalendar?month=%d&year=%d", this.month - 1, this.year)));
            row.add(new InlineKeyboardButton().setText(">>")
                .setCallbackData(String.format("/getCalendar?month=%d&year=%d", 1, this.year + 1)));
            keyboard.add(row);
            return;
        }
        row.add(new InlineKeyboardButton().setText("<<")
            .setCallbackData(String.format("/getCalendar?month=%d&year=%d", this.month - 1, this.year)));
        row.add(new InlineKeyboardButton().setText(">>")
            .setCallbackData(String.format("/getCalendar?month=%d&year=%d", this.month + 1, this.year)));
        keyboard.add(row);
        return;
    }

    @Override
    public List<List<InlineKeyboardButton>> getKeyboard() {
        this.keyboard = new ArrayList<>();
        LocalDate localDate = LocalDate.of(this.year, this.month, 1);
        keyboard.add(new ArrayList<>());
        for (int i = 0; i < localDate.lengthOfMonth(); i++) {
            localDate = LocalDate.of(this.year, this.month, i + 1);
            String day = DAY_OF_WEEK[localDate.getDayOfWeek().getValue() - 1];
            if (keyboard.get(keyboard.size() - 1).size() == 5) {
                keyboard.add(new ArrayList<>());
            }
            keyboard.get(keyboard.size() - 1)
                .add(new InlineKeyboardButton().setText((i + 1) + String.format("(%s)", day))
                    .setCallbackData(String.format("/getBy?date=%s", localDate)));
        }
        addNavButtons();
        addBackButton("/back");
        return this.keyboard;
    }
}
