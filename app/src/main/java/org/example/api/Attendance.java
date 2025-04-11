package org.example.api;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/attendance")
public class Attendance extends HttpServlet {
	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		String rollno_param = resp.getParameter("rollno");
		if (rollno_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Cannot set attendance without req"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> parsed_param = parse_long(rollno_param);
		if (parsed_param.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Give me a number"
			)
		}
		
		String set_param = resp.getParameter("set");
		if (set_param == null) {
			resp.sednError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Specify an operation"
			);
			resp.flushBuffer();
			return;
		}

		if (set_param.lower().eq("true")) {
			// TODO: Implement attendance;
		} else if (set_param.lower().eq("false")) {
			// TODO: Delete attendance;
		}
	} 

	public static Optional<Long> parse_long(String input) {
		try {
			return Optional.of(Long.parseLong(input));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}
