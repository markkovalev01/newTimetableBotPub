package ru.lit.timetableBotApplication.botCore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import lombok.extern.slf4j.Slf4j;
import ru.lit.timetableBotApplication.model.ResponseGroup;
import ru.lit.timetableBotApplication.model.ResponseLesson;
import ru.lit.timetableBotApplication.timetableAPI.APIService;
import ru.lit.timetableBotApplication.utils.RequestUtil;

@Slf4j
@Component
public class TimetableBot extends TelegramLongPollingBot {


    private static final String[] CLASSES =
        {"8:00-9:30",
            "9:40-11:10",
            "11:20-12:50",
            "13:00-14:30",
            "14:40-16:10",
            "16:20-17:50",
            "18:00-19:30",
            "19:40-21:10"};

    private static final String[] MONTH_NAME =
        {"Январь",
            "Февраль",
            "Март",
            "Апрель",
            "Июнь",
            "Июль",
            "Август",
            "Сентябрь",
            "Октябрь",
            "Ноябрь",
            "Декабрь"};


    @Autowired
    private APIService apiService;

    private List<String> history = new ArrayList<>();
    private Integer messageId = 0;
    String id;

    @Override
    public void onUpdateReceived(Update update) {

        log.info("Receive new Update. updateID: " + update.getUpdateId());
        Long chatId;
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        HashMap<String, List<String>> request;
        EditMessageText message = new EditMessageText();
        message.setMessageId(this.messageId);
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            history.add(update.getCallbackQuery().getData());
            request = RequestUtil.parseRequestWithMultipleParam(update.getCallbackQuery().getData());
        } else {
            chatId = update.getMessage().getChatId();
            history.add(update.getMessage().getText());
            request = RequestUtil.parseRequestWithMultipleParam(update.getMessage().getText());
        }


        if (request.get("id") != null && !request.get("id").equals(Strings.EMPTY)) {
            this.id = request.get("id").get(0);
        }

        if (Objects.isNull(request)) {
            log.error("Wrong request");
            return;
        }
        this.history = this.history.stream().distinct().collect(Collectors.toList());

        if (request.get("command").get(0).equals("/start")) {
            this.id = Strings.EMPTY;
            if (this.history.size() > 1) {
                this.history.clear();
            }
            SendMessage messageS = new SendMessage();
            messageS.setChatId(chatId);
            BotKeyboard startKeyboard = new StartKeyboard();
            inlineKeyboardMarkup.setKeyboard(startKeyboard.getKeyboard());
            messageS.setReplyMarkup(inlineKeyboardMarkup);
            messageS.setText("Выберите");
            try {
                Message mess = execute(messageS);
                this.messageId = mess.getMessageId();
                return;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        if (request.get("command").get(0).equals("/getStudents")) {
            ResponseGroup[] res = apiService.getSubGroup("students");
            String textMessage = "Выберите";
            BotKeyboard listKeyboard = new ListKeyboard();
            for (int i = 0; i < res.length; i++) {
                if (res[i] != null) {
                    ((ListKeyboard) listKeyboard).addButton(res[i].toString(), "/getBy?id=" + res[i].getId(), 2);
                }
            }
            ((ListKeyboard) listKeyboard).addBackButton("/start");
            inlineKeyboardMarkup.setKeyboard(listKeyboard.getKeyboard());
            message.setChatId(chatId);
            message.setReplyMarkup(inlineKeyboardMarkup);
            message.setText(textMessage);
            try {
                Message mess = (Message) execute(message);
                this.messageId = mess.getMessageId();
                return;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        if (request.get("command").get(0).equals("/getTeachers")) {
            BotKeyboard listKeyboard = new ListKeyboard();
            ResponseGroup[] res = apiService.getSubGroup("stuff");
            String textMessage = "Выберите букву";
            for (int i = 0; i < res.length; i++) {
                if (res[i] != null) {
                    ((ListKeyboard) listKeyboard).addButton(res[i].toString(), "/getBy?id=" + res[i].getId(), 5);
                }
            }
            ((ListKeyboard) listKeyboard).addBackButton("/start");
            inlineKeyboardMarkup.setKeyboard(listKeyboard.getKeyboard());
            message.setChatId(chatId);
            message.setReplyMarkup(inlineKeyboardMarkup);
            message.setText(textMessage);
            try {
                Message mess = (Message) execute(message);
                this.messageId = mess.getMessageId();
                return;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        if (request.get("command").get(0).equals("/getBy")) {
            ResponseGroup[] res;
            if (!this.id.equals(Strings.EMPTY) || request.get("id") == null) {
                res = apiService.getSubGroup(this.id);
            } else {
                res = apiService.getSubGroup(request.get("id").get(0));
                this.id = request.get("id").get(0);
            }
            String textMessage;
            if (res.length == 0) {
                if (request.get("date") == null) {
                    this.id = request.get("id").get(0);
                    DateKeyboard dateKeyboard = new DateKeyboard();
                    dateKeyboard.setId(request.get("id").get(0));
                    inlineKeyboardMarkup.setKeyboard(dateKeyboard.getKeyboard());
                    message.setChatId(chatId);
                    message.setReplyMarkup(inlineKeyboardMarkup);
                    message.setText("Выберите");

                } else {
                    ResponseLesson[] schedule = apiService
                        .getSchedule(request.get("date").get(0) + "T00:00:00", request.get("date").get(0) + "T23:00:00",
                            this.id);
                    String groupName = apiService.getGroupName(this.id);
                    textMessage = String.format("Расписание для группы %s \n", groupName);
                    for (int i = 0; i < schedule.length; i++) {
                        if (schedule[i] != null) {
                            textMessage += String.format(
                                "Пара %d(%s) \n" +
                                    "%s \n" +
                                    "--------------------\n",
                                i + 1, CLASSES[i], schedule[i]);
                        }
                    }
                    BotKeyboard listKeyboard = new ListKeyboard();
                    ((ListKeyboard) listKeyboard).addBackButton("/back");
                    inlineKeyboardMarkup.setKeyboard(listKeyboard.getKeyboard());
                    message.setReplyMarkup(inlineKeyboardMarkup);
                    message.setChatId(chatId);
                    message.setText(textMessage);
                }

                try {
                    Message mess = (Message) execute(message);
                    this.messageId = mess.getMessageId();
                    return;
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            BotKeyboard listKeyboard = new ListKeyboard();
            for (int i = 0; i < res.length; i++) {
                if (res[i] != null) {
                    String callbackData = "/getBy?" + "id=" + res[i].getId();
                    ((ListKeyboard) listKeyboard).addButton(res[i].toString(),
                        callbackData, 2);
                }
            }
            ((ListKeyboard) listKeyboard).addBackButton("/back");
            inlineKeyboardMarkup.setKeyboard(listKeyboard.getKeyboard());
            textMessage = "Выберите";
            message.setChatId(chatId);
            message.setReplyMarkup(inlineKeyboardMarkup);
            message.setText(textMessage);
            try {
                Message mess = (Message) execute(message);
                this.messageId = mess.getMessageId();
                return;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }
        if (request.get("command").get(0).equals("/back")) {
            String messageData;
            if (this.history.size() <= 1) {
                messageData = "/start";
            } else {
                this.history.remove(this.history.size() - 1);
                this.history.remove(this.history.size() - 1);
                messageData = this.history.get(this.history.size() - 1);
            }
            MyUpdate myUpdate = new MyUpdate();
            myUpdate.setUpdateId(update.getUpdateId());
            MyMessage myMessage = new MyMessage();
            myMessage.setChat(update.getCallbackQuery().getMessage().getChat());
            myMessage.setText(messageData);
            myUpdate.setMessage(myMessage);
            this.onUpdateReceived(myUpdate);
        }

        if (request.get("command").get(0).equals("/getCalendar")) {
            BotKeyboard calendarKeyboard = new CalendarKeyboard();
            String messageText;
            if (this.id != Strings.EMPTY || request.get("id") == null) {

                ((CalendarKeyboard) calendarKeyboard).setId(this.id);
            } else {
                ((CalendarKeyboard) calendarKeyboard).setId(request.get("id").get(0));
            }
            if (request.get("month") != null || request.get("year") != null) {
                int month, year;
                if (request.get("month") == null) {
                    month = LocalDate.now().getMonth().getValue();
                } else {
                    month = Integer.parseInt(request.get("month").get(0));
                }
                if (request.get("year") == null) {
                    year = LocalDate.now().getYear();
                } else {
                    year = Integer.parseInt(request.get("year").get(0));
                }
                ((CalendarKeyboard) calendarKeyboard).setMonthYear(month, year);
                messageText = String.format("%s %d", MONTH_NAME[month - 1], year);
                inlineKeyboardMarkup.setKeyboard(calendarKeyboard.getKeyboard());
            } else {
                inlineKeyboardMarkup.setKeyboard(calendarKeyboard.getKeyboard());
                messageText = String
                    .format("%s %d", MONTH_NAME[LocalDate.now().getMonth().getValue() - 1], LocalDate.now().getYear());
            }
            message.setChatId(chatId);
            message.setReplyMarkup(inlineKeyboardMarkup);
            message.setText(messageText);
            try {
                Message mess = (Message) execute(message);
                this.messageId = mess.getMessageId();
                return;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }


    @Value("${telegram.username}")
    private String username;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Value("${telegram.token}")
    private String token;

    @Override
    public String getBotToken() {
        return token;
    }

    class MyUpdate extends Update {

        private Integer updateId;
        private Message message;

        @Override
        public Integer getUpdateId() {
            return updateId;
        }

        public void setUpdateId(Integer updateId) {
            this.updateId = updateId;
        }

        @Override
        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }


    }

    class MyMessage extends Message {
        private Chat chat;
        private String text;

        public void setChat(Chat chat) {
            this.chat = chat;
        }

        @Override
        public Long getChatId() {
            return this.chat.getId();
        }

        @Override
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
