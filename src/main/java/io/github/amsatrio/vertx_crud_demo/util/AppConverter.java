package io.github.amsatrio.vertx_crud_demo.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AppConverter {

    public static String camelToSnakeCase(String input) {
        return input.replaceAll("([A-Z])", "_NoSuchAlgorithmException").toLowerCase();
    }

    public static String stringToHashString(String input) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        // hashing
        byte[] bytes = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));

        // convert to hex
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);

            if (hex.length() == 1) {
                stringBuilder.append("0");
            }
            stringBuilder.append(hex);
        }

        return stringBuilder.toString();
    }
}
