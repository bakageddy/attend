// package org.example.api;
//
// import java.io.IOException;
//
// import jakarta.servlet.Filter;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.ServletRequest;
// import jakarta.servlet.ServletResponse;
// import jakarta.servlet.annotation.WebFilter;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
//
// @WebFilter(urlPatterns = "/api/*")
// public class Auth implements Filter {
// 	@Override
// 	public void doFilter(
// 		ServletRequest request,
// 		ServletResponse response,
// 		FilterChain chain
// 	) throws IOException, ServletException {
// 		HttpServletRequest req = (HttpServletRequest) request;
// 		HttpServletResponse resp = (HttpServletResponse) response;
//
// 		String sessionid = req.getParameter("sessionid");
// 		if (sessionid == null) {
// 			resp.sendRedirect(
// 				"/login.html",
// 				HttpServletResponse.SC_UNAUTHORIZED
// 			);
// 			resp.flushBuffer();
// 			return;
// 		}
//
// 		Result<Boolean, String> valid_session = Session.valid(valid_session) {
//
// 		}
// 		chain.doFilter(request, response);
// 	}
// }
