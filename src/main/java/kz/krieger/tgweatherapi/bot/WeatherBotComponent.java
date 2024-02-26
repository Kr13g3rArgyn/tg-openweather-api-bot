package kz.krieger.tgweatherapi.bot;

import kz.krieger.tgweatherapi.services.WeatherApiServices;
import kz.krieger.tgweatherapi.util.ExceptionHandler;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.log.LogMessage;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

@Component
public class WeatherBotComponent extends TelegramLongPollingBot {
    private static final String START = "/start";
    private static final String HELP = "/help";
    private static final String CITY = "";

    @Autowired
    private WeatherApiServices weatherApiServices;

    public WeatherBotComponent(@Value("${bot.token}")String botToken){
        super(botToken);
    }
    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()){
            /* SMS не пришло */
            return;
        }
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        switch (message){
            case START -> {
                String username = update.getMessage().getChat().getUserName();
                startCommand(chatId,username);
            }
            case HELP -> {
                helpCommand(chatId);
            }

            default -> {
                cityCommand(chatId, message);}
        }
    }
    @Override
    public String getBotUsername() {
        return "javaWeatherApi_bot";
    }

    private void sendMessage(Long chatId, String text)  {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr,text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void startCommand(Long chatId, String userName) throws TelegramApiException {
        var text = "Hello " + userName +" to my small telegram bot with OpenWeather API!\n" +
                "\n" +
                "Here you can find info about weather in any place around the world!\n" +
                "Just type the name correctly and I will try to give you the answer for a question: \n" +
                "Should I take my umbrella today?\n" +
                "\n" +
                "Please, type name of the city that world knows!(RU/EN)";
        var formattedText = String.format(text,userName);
        sendMessage(chatId,formattedText);
    }

    private void unknownCommand(Long chatId) {
        var text = "Unknown command or city name! Please, type help for commands or check for the accuracy of the city name!";
        sendMessage(chatId, text);
    }

    private void helpCommand(Long chatId) {
        var text = """
                Info about bot
                (Here must be any commands that would help for our user)
                Для получения информаций о доступных функции воспользуйтесь командами:
                /help - for help
                /start - to start/restart
                /someCommand - (это примерочная команда)
                """;
        sendMessage(chatId, text);
    }

    private void cityCommand(Long chatId, String cityName) {
        try {
            var city = weatherApiServices.getWeatherInfo(cityName);
            var text = "Weather information for %s:\n%s";
            var formattedText = String.format(text, cityName, city);
            sendMessage(chatId, formattedText);
        } catch (ExceptionHandler e) {
            sendMessage(chatId, "Invalid city name. Please try again.");
        }
    }
    private boolean isValidName(String cityName){
        return cityName != null && !cityName.isBlank();
    }


}
