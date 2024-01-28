package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class App
{
    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot(); //We moved this line out of the register method, to access it later -Мы удалили эту строку из метода Register, чтобы получить к ней доступ позже
        botsApi.registerBot(new Bot());
        bot.sendText(785482605L, "Hello World!"); //The L just turns the Integer into a Long - L просто превращает целое число в длинное

    }
}
