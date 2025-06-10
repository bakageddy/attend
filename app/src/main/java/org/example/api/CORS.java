package org.example.api;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = "/*")
public class CORS implements Filter {
	@Override
	public void doFilter(
		ServletRequest request,
		ServletResponse response,
		FilterChain chain
	) throws IOException, ServletException {
		HttpServletResponse resp = (HttpServletResponse) response;

		resp.setHeader(
			"Access-Control-Allow-Origin", 
			"*"
		);
		resp.setHeader(
			"Access-Control-Allow-Methods", 
			"GET, POST, DELETE"
		);
		resp.setHeader(
			"Access-Control-Allow-Headers", 
			"*"
		);
		System.out.println("I am running a filter");
		chain.doFilter(request, response);
	}
}
