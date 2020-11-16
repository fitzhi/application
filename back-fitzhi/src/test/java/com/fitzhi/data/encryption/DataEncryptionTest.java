package com.fitzhi.data.encryption;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * Test of the 2 methods :
 * <u>
 * <li> 
 * {@link DataEncryption#encryptMessage(String)}
 * </li>
 * <li> 
 * {@link DataEncryption#decryptMessage(String)}
 * </li>
 * </ul>
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
 public class DataEncryptionTest {
	
	@Test
	public void displayEncryptData() throws Exception {
		String data = DataEncryption.encryptMessage("thePassword");
		Assert.assertFalse("thePassword".equals(data));
		// System.out.println(DataEncryption.encryptMessage("thePassword"));
	}

	@Test
	public void displayEncryptDecryptData() throws Exception {
		String dataEncrypted = DataEncryption.encryptMessage("test message");
		String data = DataEncryption.decryptMessage(dataEncrypted);
		Assert.assertTrue("test message".equals(data));
	}	
}

