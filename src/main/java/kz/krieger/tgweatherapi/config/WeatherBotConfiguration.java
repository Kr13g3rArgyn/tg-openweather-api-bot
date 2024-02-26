package kz.krieger.tgweatherapi.config;

import kz.krieger.tgweatherapi.bot.WeatherBotComponent;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class WeatherBotConfiguration {

    @Bean
    public TelegramBotsApi telegramBotsApi(WeatherBotComponent exchangeRatesBot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(exchangeRatesBot);
        return api;
    }
    @Bean
    public OkHttpClient okHttpClient(){
        return new OkHttpClient();
    }
}
