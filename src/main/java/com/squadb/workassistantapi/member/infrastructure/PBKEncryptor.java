package com.squadb.workassistantapi.member.infrastructure;

import com.squadb.workassistantapi.member.domain.PasswordEncryptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

@Slf4j
@Component
public class PBKEncryptor implements PasswordEncryptor {

    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int ITERATION_COUNT = 1000;
    private static final int KEY_LENGTH = 128;
    private static final int SALT_BYTES = 16;
    private static final String HASHED_PASSWORD_DELIMITER = ":";

    @Override
    public String encrypt(String plain) {
        final SecureRandom random = new SecureRandom();
        final byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);

        try {
            final SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
            final KeySpec spec = new PBEKeySpec(plain.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            final byte[] hash = factory.generateSecret(spec).getEncoded();

            return String.join(HASHED_PASSWORD_DELIMITER, SECRET_KEY_ALGORITHM, toHex(salt), String.valueOf(ITERATION_COUNT), String.valueOf(KEY_LENGTH), toHex(hash));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException("Failed to hash password.");
        }
    }

    @Override
    public boolean match(String password, String plain) {
        try {
            final String[] hashedPasswordInfo = password.split(HASHED_PASSWORD_DELIMITER);
            final SecretKeyFactory factory = SecretKeyFactory.getInstance(hashedPasswordInfo[0]);
            final byte[] salt = DatatypeConverter.parseHexBinary(hashedPasswordInfo[1]);
            final int iterationCount = Integer.parseInt(hashedPasswordInfo[2]);
            final int keyLength = Integer.parseInt(hashedPasswordInfo[3]);

            final KeySpec spec = new PBEKeySpec(plain.toCharArray(), salt, iterationCount, keyLength);
            final byte[] hash = factory.generateSecret(spec).getEncoded();
            return hashedPasswordInfo[4].equals(toHex(hash));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private String toHex(byte[] salt) {
        return DatatypeConverter.printHexBinary(salt);
    }
}
