package com.fitzhi.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <strong>Why does this filter exist ?</strong>
 * </p>
 * 
 * <i>
 * CORS requests require that before GET an OPTIONS request is sent to the server. 
 * To make it worse, that request does not contain any authentication headers. 
 * I would like to have this request always returning with 200 status without any authentication done on the server. <br/>
 * Is it possible? <strong>yes it is !</strong> See below...
 * </i><br/><br/>
 * @see <a href="https://stackoverflow.com/questions/25136532/allow-options-http-method-for-oauth-token-request">Discussion feed</a>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCorsFilter implements Filter {

    /**
	 * Origins allowed to access the server<br/>
	 * By default, for testing purpose, <b>*</b> is stored in the application properties file 
	 */
	@Value("${allowedOrigins}")
	private String allowedOrigins;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        response.setHeader("Access-Control-Allow-Origin", allowedOrigins);
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",  "*");
        response.setHeader("Access-Control-Expose-Headers", "*, Authorization");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }

}