package com.fitzhi.data.encryption;

import static com.fitzhi.Error.CODE_ENCRYPTION_FAILED;
import static com.fitzhi.Error.MESSAGE_ENCRYPTION_FAILED;

import java.security.Key;
import java.text.MessageFormat;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import com.fitzhi.exception.ApplicationException;

/**
 * <p>
 * Class in charge of encrypt, decrypt the password.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class DataEncryption {

	// DES Key
	private static byte[] dataEncryptionStandartKey = "Fitzhi_128bitkey".getBytes();

	// IV Key
	private static byte[] initializationVectorKey = "vector_1789".getBytes();
	
	private static IvParameterSpec ivSpec;

	private static Key secretKey = null;

	/**
	 * Security transformation
	 */
	private final static String TRANSFORMATION = "DES/CBC/PKCS5Padding"; // Good one for Sonar : "AES/GCM/NoPadding";

	private static Cipher getCipher() throws Exception {
		return Cipher.getInstance(TRANSFORMATION); 
	}

	private static void initializeSecrets() throws Exception {
		// rebuild key using SecretKeySpec
		if (secretKey == null) {
			DESKeySpec dks = new DESKeySpec(dataEncryptionStandartKey);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			secretKey = keyFactory.generateSecret(dks);
			ivSpec = new IvParameterSpec(initializationVectorKey);
		}		
	}

	/**
	 * <b>ENCRYPT</b> a message using the DES encryption mecanism.
	 * @param data the data to encrypt (actually password only are encrypted)
	 * @return the data encrypted 
	 * @throws ApplicationException thrown if the encryption fails
	 */
	public static String encryptMessage(String data) throws ApplicationException {
		
		try {

			Cipher cipher = getCipher();
			
			initializeSecrets();
			
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
		
			int blockSize = cipher.getBlockSize();
			byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
						
			byte[] cipherText = cipher.doFinal(plaintext);

			return Base64.getEncoder().encodeToString(cipherText);

		} catch (final Exception e) {
			e.printStackTrace();
			throw new ApplicationException(CODE_ENCRYPTION_FAILED, 
					MessageFormat.format(MESSAGE_ENCRYPTION_FAILED, e.getLocalizedMessage()), e);
		}
	}
	
	/**
	 * <b>DECRYPT</b> a message using the DES encryption mecanism.
	 * @param encryptedData the encrypted data
	 * @return the de-crypted result 
	 * @throws ApplicationException thrown if the encryption fails
	 */
	public static String decryptMessage(String encryptedData) throws ApplicationException  {

		try {

			Cipher cipher = getCipher();

			initializeSecrets();

			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
			byte[] cipherText = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
			
			return new String(cipherText).trim();

		} catch (final Exception e) {
			e.printStackTrace();
			throw new ApplicationException(CODE_ENCRYPTION_FAILED, 
					MessageFormat.format(MESSAGE_ENCRYPTION_FAILED, e.getLocalizedMessage()), e);
		}
	}
}
