package ru.lit.timetableBotApplication.botCore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class DateKeyboard implements BotKeyboard {
    String id;

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public List<List<InlineKeyboardButton>> getKeyboard() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> back = new ArrayList<>();
        row.add(new InlineKeyboardButton().setText("Сегодня")
            .setCallbackData(String.format("/getBy?id=%s&date=%s", this.id, LocalDate.now())));
        row.add(new InlineKeyboardButton().setText("Выбрать дату").setCallbackData("/getCalendar?id=" + this.id));
        back.add(new InlineKeyboardButton().setText("Назад").setCallbackData("/back"));
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row);
        keyboard.add(back);
        return keyboard;
    }
}
