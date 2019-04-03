package fr.skiller.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtils {

	private static final Random RANDOM = new SecureRandom();
	private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final int ITERATIONS = 10000;
	private static final int KEY_LENGTH = 256;

	/**
	 * Generate the Salt code
	 * 
	 * @param length
	 *            length of the Salt code
	 * @return the salt code
	 */
	public static String getSalt(int length) {
		StringBuilder returnValue = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}

		return new String(returnValue);
	}

	/**
	 * Generate the secret key
	 * 
	 * @param password
	 *            the end user password
	 * @param salt
	 *            the cryptographic salt code
	 * @return secret key created from the end user code and the salt code
	 */
	public static byte[] hash(char[] password, byte[] salt) {
		PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
		Arrays.fill(password, Character.MIN_VALUE);
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			return skf.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
		} finally {
			spec.clearPassword();
		}
	}

	/**
	 * Generate the secured password
	 * 
	 * @param password
	 *            the end user password
	 * @param salt
	 *            the salt code previously created
	 * @return the secured password
	 */
	public static String generateSecurePassword(final String password, final String salt) {
		String returnValue = null;

		byte[] securePassword = hash(password.toCharArray(), salt.getBytes());

		returnValue = Base64.getEncoder().encodeToString(securePassword);

		return returnValue;
	}

	/**
	 * Check the password sent from the Web Application.
	 * 
	 * @param providedPassword
	 *            sent by the user for connection
	 * @param securedPassword
	 *            secured password read from the file system
	 * @param salt
	 *            salt password read from the file system
	 * @return {@code true} if the password match, {@code false} otherwise
	 */
	public static boolean verifyUserPassword(final String providedPassword, final String securedPassword,
			final String salt) {
		boolean returnValue = false;

		// Generate New secure password with the same salt
		String newSecurePassword = generateSecurePassword(providedPassword, salt);

		// Check if two passwords are equal
		returnValue = newSecurePassword.equalsIgnoreCase(securedPassword);

		return returnValue;
	}
}