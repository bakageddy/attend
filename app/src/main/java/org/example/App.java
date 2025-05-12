package org.example;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "home", urlPatterns = "/main/")
public class App extends HttpServlet  {
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
	
	public void doGet(
		HttpServletRequest req,
		HttpServletResponse resp
	) throws ServletException, IOException {
		PrintWriter writer = resp.getWriter();
		writer.write("Hello, world!");
	}
}
