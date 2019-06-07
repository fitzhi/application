package fr.skiller.security;

import static fr.skiller.security.AuthorizationServerConfiguration.TRUSTED_CLIENT_USERNAME;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class TokenLoader {
	
	private static final String GRANT_TYPE = "grant_type";
	private static final String USER = "username";
	private static final String PSSWORD = "password";

	/**
	 * @param username the user name
	 * @param password the password
	 * @return
	 * @throws Exception
	 */
	public static String obtainAccessToken(MockMvc mvc, String username, String password) throws Exception {
		  
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    params.add(GRANT_TYPE, PSSWORD);
	    params.add(USER, username);
	    params.add(PSSWORD, password);
	 
	    ResultActions result 
	      = mvc.perform(post("/oauth/token")
	        .params(params)
	        .with(httpBasic(TRUSTED_CLIENT_USERNAME, "secret"))
	        .accept("application/json;charset=UTF-8"))
	        .andExpect(status().isOk())
	        .andExpect(content().contentType("application/json;charset=UTF-8"));

	 
	    String resultString = result.andReturn().getResponse().getContentAsString();
	    
	    JacksonJsonParser jsonParser = new JacksonJsonParser();
	    return jsonParser.parseMap(resultString).get("access_token").toString();
	}

	public static String obtainAccessMockToken(MockMvc mvc) throws Exception {
		return obtainAccessToken(mvc, "bill", "abc123");
	}

}
