package com.fitzhi.bean.impl;

import com.fitzhi.bean.HttpConnectionHandler;
import com.fitzhi.exception.ApplicationException;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Connection token. 
 */
@AllArgsConstructor()
@Data class Token {
	private String access_token;
	private String refresh_token;
}

/**
 * Implementation of the connection handler.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Service
@Slf4j
@Profile("slave")
public @Data class HttpConnectionHandlerImpl implements HttpConnectionHandler {
    
    /**
     * The authentication token sent back by the server.
     */
    private Token token;

    @Override
    public void connection(String login, String pass) throws ApplicationException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Connecion with login %s", login));
        }
        token = new Token("access_token", "refresh_token");
    }
    
}
