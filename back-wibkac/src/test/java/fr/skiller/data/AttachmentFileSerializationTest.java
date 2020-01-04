package fr.skiller.data;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;

import fr.skiller.data.internal.AttachmentFile;
import fr.skiller.service.FileType;

/**
 * <p>
 * Testing the serialization of {@link AttachmentFile}<br/>
 * Our concern is the {@code enum} type and its serialization. 
 * Our goal is to confirm that the numeric part of the {@code enum} is used.
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class AttachmentFileSerializationTest {

	@Test
	public void test() {
		AttachmentFile af = new AttachmentFile(1, "fileName", FileType.FILE_TYPE_DOC);
		Gson g = new Gson();
		String s = g.toJson(af);
		Assert.assertTrue(s.contains("\"typeOfFile\":\"2\""));
	}
}
