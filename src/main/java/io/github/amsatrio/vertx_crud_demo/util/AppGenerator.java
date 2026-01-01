package io.github.amsatrio.vertx_crud_demo.util;

import java.util.Random;
import java.util.UUID;

import io.vertx.ext.web.RoutingContext;

public class AppGenerator {
    public static String generateCacheKey(RoutingContext routingContext) {
        return routingContext.request().method() + ":" +
                routingContext.request().path() +
                (routingContext.request().query() != null ? "?" + routingContext.request().query() : "");
    }

    public static String generateUUID() {
        String uniqueId = UUID.randomUUID().toString();
        return uniqueId;
    }

    public static String randomName() {
        String[] firstNames = { "James", "Mary", "Robert", "Patricia", "John", "Jennifer", "Michael", "Linda" };
        String[] lastNames = { "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis" };

        Random random = new Random();

        String firstName = firstNames[random.nextInt(firstNames.length)];
        String lastName = lastNames[random.nextInt(lastNames.length)];

        return firstName + " " + lastName;
    }

    public static String generateMobileNumber(String prefix, int lengthAfterPrefix) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < lengthAfterPrefix; i++) {
            Random random = new Random();
            int digit = random.nextInt(10); // 0-9
            sb.append(digit);
        }
        return sb.toString();
    }

    public static String generateGibberish(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
