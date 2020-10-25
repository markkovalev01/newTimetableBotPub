package ru.lit.timetableBotApplication.botCore;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public interface BotKeyboard {
    List<List<InlineKeyboardButton>> getKeyboard();
}
