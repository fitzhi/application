package com.fitzhi.data.encryption;

import static com.fitzhi.Error.CODE_ENCRYPTION_FAILED;
import static com.fitzhi.Error.MESSAGE_ENCRYPTION_FAILED;

import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.fitzhi.exception.ApplicationException;

/**
 * <p>
 * <font color="red">
 * This data en(de)cryption class is unplugged, until the installation of a key store in Fitzhì
 * </font>
 * </p>
 */
public class UnpluggedDataEncryption {

    public static final int AES_KEY_SIZE = 256;
    public static final int GCM_IV_LENGTH = 12;
    public static final int GCM_TAG_LENGTH = 16;

    private static String fitkey = "YkkP9Wc4TxEebi/s/3R9Hj133/dQr3G7tVj9pU4XQro=";
 
    private static boolean initialized = false;

    private static SecretKey secretKey = null;

    private static byte[] IV = null;

    /**
     * Decode the secret key.
     * @param key the constant key of Fitzhi. 
     * @return the build secretKey.
     */
    public static SecretKey decodeKeyFromString(String key) {
        
        /* Decodes a Base64 encoded String into a byte array */
        byte[] decodedKey = Base64.getDecoder().decode(key);

        /* Constructs a secret key from the given byte array */
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        return secretKey;
    }
 
    /**
     * Initialize the Encrypter.
     */
    public static void initializeEncrypter() {
       
        // Generate the Secret Key
        secretKey = decodeKeyFromString(fitkey);

        IV = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);

        initialized = true;
    }

    /**
     * Encrypt the given plain text.
     * @param plainText the plain text to be encrypted
     * @return the encrypted plain text
     * @throws ApplicationException thrown if the encryption fails
     */
    public static String encryptMessage(String plaintext) throws ApplicationException {
        if (!initialized) {
            initializeEncrypter();
        }
        return encrypt(plaintext.getBytes(), secretKey, IV);
    }

    private static String encrypt(byte[] plaintext, SecretKey key, byte[] IV) throws ApplicationException {

        try {
            // Get Cipher Instance
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            
            // Create SecretKeySpec
            SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
            
            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);
            
            // Initialize Cipher for ENCRYPT_MODE
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
            
            // Perform Encryption
            byte[] cipherText = cipher.doFinal(plaintext);

            return Base64.getEncoder().encodeToString(cipherText);
        } catch (final Exception e) {
			throw new ApplicationException(CODE_ENCRYPTION_FAILED, 
					MessageFormat.format(MESSAGE_ENCRYPTION_FAILED, e.getLocalizedMessage()), e);
        } 
        
    }

    /**
     * Decrypt an encrypted text.
     * @param encryptedText the given encrypted text
     * @return the decrypted original text.
     * @throws ApplicationException thrown if the decryption fails
     */
    public static String decryptMessage(String encryptedText) throws ApplicationException {
        if (!initialized) {
            initializeEncrypter();
        }
        return decrypt(Base64.getDecoder().decode(encryptedText), secretKey, IV);
    }

    public static String decrypt(byte[] cipherText, SecretKey key, byte[] IV) throws ApplicationException {

        try {
            // Get Cipher Instance
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            
            // Create SecretKeySpec
            SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
            
            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);
            
            // Initialize Cipher for DECRYPT_MODE
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
            
            // Perform Decryption
            byte[] decryptedText = cipher.doFinal(cipherText);
            
            return new String(decryptedText);

        } catch (final Exception e) {
			throw new ApplicationException(CODE_ENCRYPTION_FAILED, 
					MessageFormat.format(MESSAGE_ENCRYPTION_FAILED, e.getLocalizedMessage()), e);
        } 
   }
}