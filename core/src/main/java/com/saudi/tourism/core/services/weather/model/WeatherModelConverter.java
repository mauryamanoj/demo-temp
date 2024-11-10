package com.saudi.tourism.core.services.weather.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.services.weather.WeatherConstants;
import com.saudi.tourism.core.services.weather.model.input.Current;
import com.saudi.tourism.core.services.weather.model.input.Weather;
import com.saudi.tourism.core.services.weather.model.input.WeatherResponse;
import com.saudi.tourism.core.services.weather.model.output.Daily;
import com.saudi.tourism.core.services.weather.model.output.ExtendedWeatherModel;
import com.saudi.tourism.core.services.weather.model.output.Hourly;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherModel;
import com.saudi.tourism.core.services.weather.model.output.Temp;
import com.saudi.tourism.core.services.weather.model.output.Today;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.NumberConstants;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Converts WeatherResponse to ExtendedWeatherModel.
 */
public final class WeatherModelConverter {

  /**
   * Converter / mapper functional interface to convert response from OpenWeatherMap to our
   * ExtendedWeatherModel output instance.
   */
  public static final Function<WeatherResponse, ExtendedWeatherModel> EXTENDED_WEATHER_CONVERTER =
      (WeatherResponse input) -> {
        int timeZoneOffset = input.getTimezoneOffset();
        Current currentWeather = input.getCurrent();
        Today.TodayBuilder today = Today.builder();
        today.sunrise(getHourMinutes(currentWeather.getSunrise(), timeZoneOffset));
        today.sunset(getHourMinutes(currentWeather.getSunset(), timeZoneOffset));
        today.temp(currentWeather.getTemp());
        today.feelsLike(currentWeather.getFeelsLike());
        today.humidity(currentWeather.getHumidity());
        today.windSpeed(currentWeather.getWindSpeed());
        String mainWeather = getMainWeather(currentWeather.getWeather());
        today.weather(mainWeather);
        today.icon(mainWeather);
        if (input.getDaily() != null && input.getDaily()[0] != null) {
          today.max(input.getDaily()[0].getTemp().getMax());
          today.min(input.getDaily()[0].getTemp().getMin());
        }
        List<Hourly> hourlyList = Arrays.stream(input.getHourly()).map(
            hourly -> Hourly.builder().time(getHourMinutes(hourly.getDate(), timeZoneOffset))
                .feelsLike(hourly.getFeelsLike()).temp(hourly.getTemp())
                .weather(hourly.getWeather()[0].getMain()).icon(hourly.getWeather()[0].getMain())
                .build())
            .limit(WeatherConstants.HOURS_IN_DAY).collect(Collectors.toList());
        today.hourly(hourlyList);
        List<Daily> dailyList = Arrays.stream(input.getDaily()).map(daily -> {
          Temp temp = new Temp();
          temp.setDay(daily.getTemp().getDay());
          temp.setMax(daily.getTemp().getMax());
          temp.setMin(daily.getTemp().getMin());
          Daily result = new Daily();
          result.setDate(getDate(daily.getDate(), timeZoneOffset));
          result.setTemp(temp);
          String dailyMainWeather = getMainWeather(daily.getWeather());
          result.setWeather(dailyMainWeather);
          result.setIcon(dailyMainWeather);
          return result;
        }).skip(1).limit(WeatherConstants.FORECAST_DAYS_LIMIT).collect(Collectors.toList());
        return new ExtendedWeatherModel(null, null, today.build(), dailyList, null);
      };

  /**
   * Function to convert simple weather API json response to SimpleWeatherModel object.
   */
  public static final Function<String, SimpleWeatherModel> SIMPLE_WEATHER_JSON_CONVERTER =
      simpleWeatherJsonResponse -> {
        SimpleWeatherModel weatherModel = null;

        final JsonParser parser = new JsonParser();
        final JsonElement jsonTree = parser.parse(simpleWeatherJsonResponse);
        if (Objects.nonNull(jsonTree) && jsonTree.isJsonObject()) {
          final JsonObject jsonObject = jsonTree.getAsJsonObject();

          final JsonElement jsonElementWeather =
              jsonObject.get(Constants.CONST_WEATHER).getAsJsonArray()
                  .get(NumberConstants.CONST_ZERO);
          final String weather =
              jsonElementWeather.getAsJsonObject().get(Constants.CONST_MAIN).getAsString();
          final JsonElement jsonElementTemperature = jsonObject.get(Constants.CONST_MAIN);
          weatherModel = SimpleWeatherModel.builder().weather(weather)
              .temp(jsonElementTemperature.getAsJsonObject().get(Constants.CONST_TEMP).getAsFloat())
              .city(jsonObject.get(Constants.CONST_NAME).getAsString())
              .icon(weather)
              .weatherCityId(jsonObject.get(Constants.PN_ID).getAsString()).build();
        }

        return weatherModel;
      };

  /**
   * Converts group weather json response into list of simple weather models.
   */
  public static final Function<String, List<SimpleWeatherModel>> GRP_SIMPLE_WEATHER_JSON_CONVERTER =
      groupJsonResponse -> {
        final JsonParser parser = new JsonParser();
        final JsonElement jsonTree = parser.parse(groupJsonResponse);
        final List<SimpleWeatherModel> result = new LinkedList<>();

        if (Objects.nonNull(jsonTree) && jsonTree.isJsonObject()) {
          final JsonObject jsonObject = jsonTree.getAsJsonObject();
          final JsonElement jsonElementList = jsonObject.get(WeatherConstants.PN_LIST);

          if (Objects.nonNull(jsonElementList) && jsonElementList.isJsonArray()) {
            final JsonArray listArray = jsonElementList.getAsJsonArray();
            listArray.forEach(jsonElement -> result
                .add(SIMPLE_WEATHER_JSON_CONVERTER.apply(jsonElement.toString())));
          }
        }

        return result;
      };

  /**
   * Time HH:MM formatter.
   */
  private static final DateTimeFormatter TIME_HOURS_MINUTES = DateTimeFormatter.ofPattern("HH:mm");

  /**
   * Date YYYY-MM-DD formatter.
   */
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Convert long epoch seconds to local date time.
   * @param epochSecond epoch in seconds
   * @param zoneOffset zone offset
   * @return converted local date time
   */
  private static LocalDateTime toLocalDateTime(long epochSecond, int zoneOffset) {
    return
        LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.ofTotalSeconds(zoneOffset));
  }


  /**
   * Convert a number that came from openweathermap api to HH:MM time format.
   * @param epochSeconds epoch in seconds
   * @param zoneOffset zone offset
   * @return time in HH:MM format.
   */
  private static String getHourMinutes(long epochSeconds, int zoneOffset) {
    return toLocalDateTime(epochSeconds, zoneOffset).format(TIME_HOURS_MINUTES);
  }

  /**
   * Convert a number that came from openweathermap api to YYYY-MM-DD date format.
   * @param epochSeconds epoch in seconds
   * @param zoneOffset zone offset
   * @return date in YYYY-MM-DD format.
   */
  private static String getDate(long epochSeconds, int zoneOffset) {
    return toLocalDateTime(epochSeconds, zoneOffset).format(DATE_FORMATTER);
  }

  /**
   * Safely retrieve weather value from array.
   * @param weather array of Weather objects
   * @return string value
   */
  private static String getMainWeather(Weather[] weather) {
    return Optional.ofNullable(weather)
        .map(weatherArray -> weatherArray[0])
        .map(Weather::getMain)
        .orElse(StringUtils.EMPTY);
  }

  /**
   * Hidden constructor.
   */
  private WeatherModelConverter() { }
}
