package kz.krieger.tgweatherapi.services.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import kz.krieger.tgweatherapi.client.BotClient;
import kz.krieger.tgweatherapi.services.WeatherApiServices;
import kz.krieger.tgweatherapi.util.ExceptionHandler;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WeatherApiServicesImpl implements WeatherApiServices {
    @Autowired
    private BotClient botClient;

    @Override
    public String getWeatherInfo(String cityName) throws ExceptionHandler {
        try{
            Optional<String> jsonObject = botClient.getWeatherByCity(cityName);
            if (jsonObject.isEmpty()){
                throw new ExceptionHandler("Couldn't take json");
            }
            return extractInfoFromWeatherApi(jsonObject.get());
            }
        catch (ExceptionHandler E){
            throw new ExceptionHandler("Problems with taking data from API");
        }
}
    private String extractInfoFromWeatherApi(String jsonResponse) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
        StringBuilder weatherInfo = new StringBuilder("\n");
        JsonObject main = jsonObject.getAsJsonObject("main");
        if (main == null) {
            return String.valueOf(weatherInfo.append("Wrong name, try again!"));
        } else {
            //City Name
            if (jsonObject.has("name")) {
                String city = jsonObject.get("name").getAsString();
                weatherInfo.append("City name: ").append(city).append("\n");
            } else {
                weatherInfo.append("City name is not available in the response\n");
            }
            //Temperature
            double temp = main.getAsJsonPrimitive("temp").getAsDouble();
            double feelsLike = main.getAsJsonPrimitive("feels_like").getAsDouble();
            double tempMin = main.getAsJsonPrimitive("temp_min").getAsDouble();
            double tempMax = main.getAsJsonPrimitive("temp_max").getAsDouble();
            int humidity = main.getAsJsonPrimitive("humidity").getAsInt();

            weatherInfo.append("Temperature: \n").append(temp).append("°C\n");
            weatherInfo.append("Feels like: \n").append(feelsLike).append("°C\n");
            weatherInfo.append("Minimal temperature: \n").append(tempMin).append("°C\n");
            weatherInfo.append("Maximal temperature: \n").append(tempMax).append("°C\n");
            weatherInfo.append("Humidity: \n").append(humidity).append("%\n");

            //WeatherArray
            JsonArray weatherArray = jsonObject.getAsJsonArray("weather");
            JsonObject weather = weatherArray.get(0).getAsJsonObject();
            String description = weather.getAsJsonPrimitive("description").getAsString();

            weatherInfo.append("Description: ").append(description).append("\n");

            //Wind
            JsonObject wind = jsonObject.getAsJsonObject("wind");
            double windSpeed = wind.getAsJsonPrimitive("speed").getAsDouble();

            weatherInfo.append("Wind speed: ").append(windSpeed).append("m/s\n");
            return weatherInfo.toString();
        }
    }

}
