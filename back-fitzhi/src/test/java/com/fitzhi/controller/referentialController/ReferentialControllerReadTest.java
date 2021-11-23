package com.fitzhi.controller.referentialController;

import static com.fitzhi.Error.CODE_FILE_REFERENTIAL_NOFOUND;
import static com.fitzhi.Error.CODE_IO_EXCEPTION;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.fitzhi.controller.ReferentialController;
import com.fitzhi.controller.util.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * <p>
 * Test of the class {@link ReferentialController}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ReferentialControllerReadTest {

	/**
	 * Initialization of the Google JSON parser.
	 */
	Gson gson = new GsonBuilder()
		      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).create();

	@Autowired
	private MockMvc mvc;
	
	/**
	 * Testing the nominal load of a referential file.
	 * 
	 * @throws Exception
	 */
	@Test
	public void nominalRead() throws Exception {
		this.mvc.perform(get("/api/referential/test"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.test", is(1)))
				.andDo(print())
				.andReturn();
	}
	
	/**
	 * The load failed because the referential file doesn't exist.
	 * 
	 * @throws Exception
	 */
	@Test
	public void notFound() throws Exception {
		this.mvc.perform(get("/api/referential/unknown"))
				.andExpect(status().isNotFound())
				.andDo(print())
				.andExpect(jsonPath("$.code", is(CODE_FILE_REFERENTIAL_NOFOUND)))
				.andExpect(jsonPath("$.message", is("The referential file unknown.json does not exist!")))
				.andReturn();
	}

	/**
	 * Opps ! An IO error occurs.
	 * 
	 * @throws Exception
	 */
	@Test
	public void ioOops() throws Exception {
		this.mvc.perform(get("/api/referential/error"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code", is(CODE_IO_EXCEPTION)))
				.andDo(print())
				.andReturn();
	}
}
