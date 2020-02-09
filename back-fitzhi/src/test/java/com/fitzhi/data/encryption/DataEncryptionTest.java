package com.fitzhi.data.encryption;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fitzhi.data.encryption.DataEncryption;

/**
 * <p>
 * Test of the 2 methods :
 * <u>
 * <li> 
 * {@link DataEncryption#encryptMessage(byte[])}
 * </li>
 * <li> 
 * {@link DataEncryption#decryptMessage(byte[])}
 * </li>
 * </ul>
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class DataEncryptionTest {

	Logger logger = LoggerFactory.getLogger(DataEncryptionTest.class.getCanonicalName());

	@Test
	public void displayEncryptData() throws Exception {
		String data = DataEncryption.encryptMessage("thePassword");
		logger.debug("data encrypted : ");
		logger.debug(data);
		Assert.assertFalse("thePassword".equals(data));
	}
	
	@Test
	public void displayEncryptDecryptData() throws Exception {
		String dataEncrypted = DataEncryption.encryptMessage("test message");
		String data = DataEncryption.decryptMessage(dataEncrypted);
		Assert.assertTrue("test message".equals(data));
	}	

}

