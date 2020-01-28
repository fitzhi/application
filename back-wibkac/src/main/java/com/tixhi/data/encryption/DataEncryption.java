package com.tixhi.data.encryption;

import static com.tixhi.Error.CODE_ENCRYPTION_FAILED;
import static com.tixhi.Error.MESSAGE_ENCRYPTION_FAILED;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.tixhi.exception.SkillerException;

/**
 * <p>
 * Class in charge of encrypt, decrypt the password.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class DataEncryption {

	private static byte[] decodedKey = Base64.getDecoder().decode("techxhi128bitkey");

	private static SecretKey originalKey = null;
	
	/**
	 * <b>ENCRYPT</b> a message
	 * @param data the data to encrypt (actually password only are encrypted)
	 * @return the data encrypted 
	 * @throws SkillerException thrown if the encryption fails
	 */
	public static String encryptMessage(String data) throws SkillerException {
		
		try {
            Cipher cipher = Cipher.getInstance("AES");
            
            // rebuild key using SecretKeySpec
            if (originalKey == null) {
            	originalKey = new SecretKeySpec(Arrays.copyOf(decodedKey, 16), "AES");
            }
            cipher.init(Cipher.ENCRYPT_MODE, originalKey);
            byte[] cipherText = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(cipherText);
		} catch (final Exception e) {
			throw new SkillerException(CODE_ENCRYPTION_FAILED, 
					MessageFormat.format(MESSAGE_ENCRYPTION_FAILED, e.getLocalizedMessage()), e);
		}
	}
	
	/**
	 * <b>DECRYPT</b> a message
	 * @param encryptedData the encrypted data
	 * @return the data de-crypted 
	 * @throws SkillerException thrown if the encryption fails
	 */
	public static String decryptMessage(String encryptedData) throws SkillerException  {

        try {
            Cipher cipher = Cipher.getInstance("AES");
            // Build key using SecretKeySpec
            if (originalKey == null) {
            	originalKey = new SecretKeySpec(Arrays.copyOf(decodedKey, 16), "AES");
            }
            cipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] cipherText = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            
            return new String(cipherText);

        } catch (final Exception e) {
			throw new SkillerException(CODE_ENCRYPTION_FAILED, 
					MessageFormat.format(MESSAGE_ENCRYPTION_FAILED, e.getLocalizedMessage()), e);
		}
	}

}
