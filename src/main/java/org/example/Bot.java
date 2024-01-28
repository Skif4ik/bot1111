package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class Bot extends TelegramLongPollingBot {

    private boolean screaming = false;

    private InlineKeyboardMarkup keyboardM1; //создаем клавивтуру
    private InlineKeyboardMarkup keyboardM2;







    @Override
    public String getBotUsername() {
        return "obmennic_btc_bot";
    }

    @Override
    public String getBotToken() {
        return "";
    }

    @Override
    public void onUpdateReceived(Update update) {

        var next = InlineKeyboardButton.builder() //создаем кнопки
                .text("Next").callbackData("next")
                .build();

        var back = InlineKeyboardButton.builder()
                .text("Back").callbackData("back")
                .build();

        var url = InlineKeyboardButton.builder()
                .text("Tutorial")
                .url("https://core.telegram.org/bots/api")
                .build();

        keyboardM1 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(next))
                .keyboardRow(List.of(back))
                .keyboardRow(List.of(url))
                .build();

//Buttons are wrapped in lists since each keyboard is a set of button rows - Кнопки упакованы в списки, поскольку каждая клавиатура представляет собой набор строк кнопок
        keyboardM2 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(back))
                .keyboardRow(List.of(url))
                .build();

        // System.out.println(update); //выводит всю информацию в консоль о пользователе написавшем сообщении

        var msg = update.getMessage();
        var user = msg.getFrom();
        var text = msg.getText();
        var userId = msg.getFrom().getId();
//        System.out.println(user.getFirstName() + " wrote " + msg.getText()); //выводит в консоль имя пользователя и его сообщение

        var id = user.getId();
        // sendText(id, msg.getText()); //эхо

        // copyMessage(id, msg.getMessageId()); //эхо

        if (text.equals("/start")) {
                sendText(userId, "Hello, welcome to the exchanger bot");
            }
        var txt = msg.getText();
        if(msg.isCommand()){
            if(msg.getText().equals("/scream"))         //If the command was /scream, we switch gears
                screaming = true;
            else if (msg.getText().equals("/whisper"))  //Otherwise, we return to normal
                screaming = false;
            else if (txt.equals("/start"))
                sendMenu(id, "<b>Menu 1</b>", keyboardM1);
            else if (txt.equals("/start"))
                sendMenu(id, "<b>Menu 2/b>", keyboardM2);
            return;                                     //We don't want to echo commands, so we exit
        }

        if(screaming)                            //If we are screaming -Если мы кричим
            scream(id, update.getMessage());     //Call a custom method -Вызов пользовательского метода
        else
            copyMessage(id, msg.getMessageId()); //Else proceed normally - В остальном действуйте как обычно

       }

    private void scream(Long id, Message msg) {
        if(msg.hasText())
            sendText(id, msg.getText().toUpperCase());
        else
            copyMessage(id, msg.getMessageId());  //We can't really scream a sticker - Мы не можем кричать наклейку
    }

    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to -Кому мы отправляем сообщение
                .text(what).build();    //Message content - Содержимое сообщения
        try {
            execute(sm);                        //Actually sending the message - Собственно отправка сообщения
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here - Любая ошибка будет напечатана здесь
        }
    }

    public void copyMessage(Long who, Integer msgId) {
        CopyMessage cm = CopyMessage.builder()
                .fromChatId(who.toString())  //We copy from the user -копируем у пользователя
                .chatId(who.toString())      //And send it back to him -И отправь это ему обратно
                .messageId(msgId)            //Specifying what message -уточняя какое сообщение
                .build();
        try {
            execute(cm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMenu(Long who, String txt, InlineKeyboardMarkup kb){
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(kb).build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void buttonTap(Long id, String queryId, String data, int msgId) throws TelegramApiException {

        EditMessageText newTxt = EditMessageText.builder()
                .chatId(id.toString())
                .messageId(msgId).text("").build();

        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                .chatId(id.toString()).messageId(msgId).build();

        if(data.equals("next")) {
            newTxt.setText("MENU 2");
            newKb.setReplyMarkup(keyboardM2);
        } else if(data.equals("back")) {
            newTxt.setText("MENU 1");
            newKb.setReplyMarkup(keyboardM1);
        }

        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(queryId).build();

        execute(close);
        execute(newTxt);
        execute(newKb);
    }


}
