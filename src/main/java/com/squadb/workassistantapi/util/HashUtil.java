package com.squadb.workassistantapi.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

@Slf4j
@UtilityClass
public class HashUtil {

    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int ITERATION_COUNT = 1000;
    private static final int KEY_LENGTH = 128;
    private static final int SALT_BYTES = 16;
    private static final String HASHED_PASSWORD_DELIMITER = ":";

    public static String hashPassword(String plainPassword) {
        final SecureRandom random = new SecureRandom();
        final byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);

        try {
            final SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
            final KeySpec spec = new PBEKeySpec(plainPassword.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            final byte[] hash = factory.generateSecret(spec).getEncoded();

            return String.join(HASHED_PASSWORD_DELIMITER, SECRET_KEY_ALGORITHM, toHex(salt), String.valueOf(ITERATION_COUNT), String.valueOf(KEY_LENGTH), toHex(hash));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException("Failed to hash password.");
        }
    }

    public static boolean equalPassword(String passwordInput, String hashedPassword) {
        try {
            final String[] hashedPasswordInfo = hashedPassword.split(HASHED_PASSWORD_DELIMITER);
            final SecretKeyFactory factory = SecretKeyFactory.getInstance(hashedPasswordInfo[0]);
            final byte[] salt = DatatypeConverter.parseHexBinary(hashedPasswordInfo[1]);
            final int iterationCount = Integer.parseInt(hashedPasswordInfo[2]);
            final int keyLength = Integer.parseInt(hashedPasswordInfo[3]);

            final KeySpec spec = new PBEKeySpec(passwordInput.toCharArray(), salt, iterationCount, keyLength);
            final byte[] hash = factory.generateSecret(spec).getEncoded();
            return hashedPasswordInfo[4].equals(toHex(hash));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private static String toHex(byte[] salt) {
        return DatatypeConverter.printHexBinary(salt);
    }
}
