package com.fitzhi.data.encryption;

import static com.fitzhi.Error.CODE_ENCRYPTION_FAILED;
import static com.fitzhi.Error.MESSAGE_ENCRYPTION_FAILED;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.fitzhi.exception.ApplicationException;

/**
 * <p>
 * Class in charge of encrypt, decrypt the password.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class DataEncryption {

	private static byte[] decodedKey = Base64.getDecoder().decode("techxhi128bitkey");

	private static SecretKey originalKey = null;
	
	/**
	 * Security transformation
	 */
	private final static String TRANSFORMATION = "AES"; // Good one for Sonar : "AES/GCM/NoPadding";

	private static Cipher getCipher() throws Exception {
		return Cipher.getInstance(TRANSFORMATION); //NOSONAR
	}

	/**
	 * <b>ENCRYPT</b> a message
	 * @param data the data to encrypt (actually password only are encrypted)
	 * @return the data encrypted 
	 * @throws ApplicationException thrown if the encryption fails
	 */
	public static String encryptMessage(String data) throws ApplicationException {
		
		try {

			Cipher cipher = getCipher();
            
            // rebuild key using SecretKeySpec
            if (originalKey == null) {
            	originalKey = new SecretKeySpec(Arrays.copyOf(decodedKey, 16), TRANSFORMATION);
            }

			cipher.init(Cipher.ENCRYPT_MODE, originalKey);
			byte[] cipherText = cipher.doFinal(data.getBytes());
			
            return Base64.getEncoder().encodeToString(cipherText);
		} catch (final Exception e) {
			throw new ApplicationException(CODE_ENCRYPTION_FAILED, 
					MessageFormat.format(MESSAGE_ENCRYPTION_FAILED, e.getLocalizedMessage()), e);
		}
	}
	
	/**
	 * <b>DECRYPT</b> a message
	 * @param encryptedData the encrypted data
	 * @return the data de-crypted 
	 * @throws ApplicationException thrown if the encryption fails
	 */
	public static String decryptMessage(String encryptedData) throws ApplicationException  {

        try {

			Cipher cipher = getCipher();

			// Build key using SecretKeySpec
            if (originalKey == null) {
            	originalKey = new SecretKeySpec(Arrays.copyOf(decodedKey, 16), TRANSFORMATION);
            }

			cipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] cipherText = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            
            return new String(cipherText);

        } catch (final Exception e) {
			throw new ApplicationException(CODE_ENCRYPTION_FAILED, 
					MessageFormat.format(MESSAGE_ENCRYPTION_FAILED, e.getLocalizedMessage()), e);
		}
	}
}
