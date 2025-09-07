package org.example;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;


public class WeatherService {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    private final String apiKey;

    public WeatherService(String apiKey) {
        this.apiKey = apiKey.trim();
    }

    public String getWeatherByCity(String city) throws Exception {
        String encodedCity = URLEncoder.encode(city.trim(), StandardCharsets.UTF_8);
        String urlString = BASE_URL + "?q=" + encodedCity + "&appid=" + apiKey + "&units=metric";
//        System.out.println(urlString);
        return fetchWeather(urlString);
    }


    public String getWeatherByCoordinates(double lat, double lon) throws Exception {
        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            return "❌ Invalid coordinates! Latitude must be -90 to 90, Longitude -180 to 180.";
        }
        String urlString = BASE_URL + "?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=metric";
        return fetchWeather(urlString);
    }

    private String fetchWeather(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();

        BufferedReader reader;
        if (responseCode == 200) {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                errorResponse.append(line);
            }
            reader.close();
            throw new RuntimeException("❌ API Error (" + responseCode + "): " + errorResponse);
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject obj = new JSONObject(response.toString());

        String city = obj.optString("name", "Unknown");
        JSONObject main = obj.getJSONObject("main");
        double temp = main.getDouble("temp");
        int humidity = main.getInt("humidity");
        String weather = obj.getJSONArray("weather").getJSONObject(0).getString("description");

        long utcTime = obj.getLong("dt");
        int timezoneOffset = obj.getInt("timezone");
//        System.out.println(timezoneOffset);

        long localTimeSeconds = utcTime + timezoneOffset;
        java.time.Instant instant = java.time.Instant.ofEpochSecond(localTimeSeconds);
        java.time.ZonedDateTime localDateTime = instant.atZone(java.time.ZoneOffset.UTC);

        String formattedTime = localDateTime.toLocalDateTime().toString();


        return String.format(
                "City: %s%n Temp: %.2f °C%n Humidity: %d%%%n Weather: %s%n Local Time: %s",
                city, temp, humidity, weather, formattedTime
        );

    }

}
