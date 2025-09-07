package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String apiKey = System.getenv("OPENWEATHER_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("❌ Please set OPENWEATHER_API_KEY as an environment variable.");
            return;
        }

        WeatherService service = new WeatherService(apiKey);
        Scanner scanner = new Scanner(System.in);

        System.out.println("=============== Welcome to Weather App! ================");
        System.out.println("Choose an option:");
        System.out.println("1. Get weather by City Name");
        System.out.println("2. Get weather by Coordinates");
        System.out.print("Enter choice: ");


        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        try {
            String result;
            if (choice == 1) {
                System.out.print("Enter City Name: ");
                String city = scanner.nextLine();
                result = service.getWeatherByCity(city);
            } else if (choice == 2) {
                System.out.print("Enter Latitude: ");
                double lat = scanner.nextDouble();
                System.out.print("Enter Longitude: ");
                double lon = scanner.nextDouble();
                result = service.getWeatherByCoordinates(lat, lon);
            } else {
                result = "Invalid choice!";
            }

            System.out.println("\n--- Weather Report ---");
            System.out.println(result);

        } catch (Exception e) {
            System.out.println("Error fetching weather: " + e.getMessage());
        }

        scanner.close();
    }
}