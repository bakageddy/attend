package org.example.api;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/attendance")
public class Attendance extends HttpServlet {
	// Do not handle /api/attendance
	// get /api/attendance/:rollno
	// get /api/attendance/:class
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String rollno = req.getParameter("rollno");
		if (rollno == null) {
			resp.setStatus(400);
			return;
		}

		String query = String.format("SELECT * FROM Attendance WHERE RollNo=%d;", rollno);
		try (
			Connection cnx = DriverManager.getConnection("jdbc:postgresql://localhost/college");
		) {
			Statement stmt = cnx.createStatement();
			ResultSet rst = stmt.executeQuery(query);
			while (rst.next()) {
			}
		} catch (Exception e) {
			System.err.println("Boo");
		}
	}
}
