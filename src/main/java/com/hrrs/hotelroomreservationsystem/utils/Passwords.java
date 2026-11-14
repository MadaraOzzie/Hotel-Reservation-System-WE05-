package com.hrrs.hotelroomreservationsystem.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Passwords {
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    private Passwords() {}

    public static String hash(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        return "pbkdf2_sha256$" + ITERATIONS + "$" + b64(salt) + "$" + b64(hash);
    }

    public static boolean verify(String password, String storedHash) {
        if (password == null || storedHash == null || storedHash.isBlank()) {
            return false;
        }
        String[] parts = storedHash.split("\\$");
        if (parts.length != 4 || !"pbkdf2_sha256".equals(parts[0])) {
            return false;
        }
        int iterations;
        try {
            iterations = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return false;
        }
        byte[] salt = fromB64(parts[2]);
        byte[] expected = fromB64(parts[3]);
        byte[] actual = pbkdf2(password.toCharArray(), salt, iterations, expected.length * 8);
        return MessageDigest.isEqual(expected, actual);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLengthBits) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLengthBits);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Password hashing failed", e);
        }
    }

    private static String b64(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private static byte[] fromB64(String value) {
        return Base64.getUrlDecoder().decode(value);
    }
}
