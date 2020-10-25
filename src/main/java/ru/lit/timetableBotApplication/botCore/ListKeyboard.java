package ru.lit.timetableBotApplication.botCore;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class ListKeyboard implements BotKeyboard {
    private final List<List<InlineKeyboardButton>> keyboard;
    private final Boolean backButton = false;

    ListKeyboard() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        this.keyboard = new ArrayList<>();
        this.keyboard.add(row);
    }

    public void addButton(String text, String callbackData, int rowSize) {
        if (this.backButton) {
            List<InlineKeyboardButton> row = this.keyboard.get(this.keyboard.size() - 2);
            if (row.size() == 5 || row.size() == rowSize) {
                row = new ArrayList<>();
                this.keyboard.add(this.keyboard.size() - 1, row);
            }
            row.add(new InlineKeyboardButton().setText(text).setCallbackData(callbackData));
            return;
        }
        List<InlineKeyboardButton> row = this.keyboard.get(this.keyboard.size() - 1);
        if (row.size() == 5 || row.size() == rowSize) {
            row = new ArrayList<>();
            this.keyboard.add(row);
        }
        row.add(new InlineKeyboardButton().setText(text).setCallbackData(callbackData));
    }

    public void addBackButton(String backTo) {
        if (this.backButton) {
            return;
        }
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(new InlineKeyboardButton().setText("Назад").setCallbackData(backTo));
        this.keyboard.add(row);
    }


    @Override
    public List<List<InlineKeyboardButton>> getKeyboard() {
        return keyboard;
    }
}
