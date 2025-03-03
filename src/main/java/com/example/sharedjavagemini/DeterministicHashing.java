package com.example.sharedjavagemini;

import com.github.javafaker.Faker;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Random;

public class DeterministicHashing {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final Faker faker = new Faker(new Locale("en-US"));

    public static String deterministicHash(String input, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            String saltedInput = input + salt;
            byte[] encodedhash = digest.digest(saltedInput.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String generateFakeValue(String input, String salt, String column) {

        String hashed = deterministicHash(input, salt);

        if (hashed == null) {
            return null; // Handle hashing failure
        }
        long seed = bytesToLong(hashed);

        Random random = new Random(seed);
        Faker seededFaker = new Faker(random);

        switch (column.toLowerCase()) {
            case "name":
                return seededFaker.name().fullName();
            case "address":
                return seededFaker.address().fullAddress();
            case "creditaccountnumber":
                return seededFaker.finance().creditCard();
            case "phone":
                return seededFaker.phoneNumber().phoneNumber();
            default:
                return "Unknown Column";
        }
    }

    private static long bytesToLong(String hexString) {
        long result = 0;
        for (int i = 0; i < Math.min(16, hexString.length()); i++) { // Use first 16 hex chars (8 bytes)
            int digit = Character.digit(hexString.charAt(i), 16);
            result = (result << 4) | digit;
        }
        return result;
    }

    public static String generateUpdateSQL(String tableName, String primaryKeyColumn1, String primaryKeyColumn2,
                                           String primaryKeyValue1, String primaryKeyValue2,
                                           String salt) {

        String fakeName = generateFakeValue(primaryKeyValue1 + primaryKeyValue2, salt, "name");
        String fakeAddress = generateFakeValue(primaryKeyValue1 + primaryKeyValue2, salt, "address");
        String fakeCreditCard = generateFakeValue(primaryKeyValue1 + primaryKeyValue2, salt, "creditaccountnumber");
        String fakePhone = generateFakeValue(primaryKeyValue1 + primaryKeyValue2, salt, "phone");

        return String.format("UPDATE %s SET name = '%s', address = '%s', creditaccountnumber = '%s', phone = '%s' WHERE %s = '%s' AND %s = '%s';",
                tableName, fakeName, fakeAddress, fakeCreditCard, fakePhone, primaryKeyColumn1, primaryKeyValue1, primaryKeyColumn2, primaryKeyValue2);
    }

    public static void main(String[] args) {
        String tableName = "Customers";
        String primaryKeyColumn1 = "CustomerID";
        String primaryKeyColumn2 = "OrderNumber";
        String primaryKeyValue1 = "123";
        String primaryKeyValue2 = "456";
        String salt = "mySecretSalt"; // Keep this secret!

        String updateSQL = generateUpdateSQL(tableName, primaryKeyColumn1, primaryKeyColumn2,
                primaryKeyValue1, primaryKeyValue2, salt);
        System.out.println(updateSQL);

        // Example of direct fake value generation
        String fakeName = generateFakeValue(primaryKeyValue1 + primaryKeyValue2, salt, "name");
        System.out.println("Fake Name: " + fakeName);
    }
}