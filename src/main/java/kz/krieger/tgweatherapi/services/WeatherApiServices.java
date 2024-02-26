package kz.krieger.tgweatherapi.services;
import kz.krieger.tgweatherapi.util.ExceptionHandler;
public interface WeatherApiServices {
    String getWeatherInfo(String cityName) throws ExceptionHandler;
}
