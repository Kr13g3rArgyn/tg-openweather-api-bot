package kz.krieger.tgweatherapi.client;

import kz.krieger.tgweatherapi.util.ExceptionHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class BotClient {
    @Autowired
    private OkHttpClient okHttpClient;

    @Value("${weather.api.base-url}")
    private String url;

    @Value("${weather.api.app-id}")
    private String userToken;

    public Optional<String> getWeatherByCity(String cityName) throws ExceptionHandler {
        String apiUrl = String.format("%s?q=%s&units=metric&appid=%s",url,cityName,userToken);
        var request = new Request.Builder()
                .url(apiUrl)
                .build();
        try(var response = okHttpClient.newCall(request).execute()) {
            var body = response.body();
            return body == null ? Optional.empty() : Optional.of(body.string());
        }
        catch (IOException ignored){
            throw new ExceptionHandler("Ошибка при получений погоды", ignored);
        }
    }
}
