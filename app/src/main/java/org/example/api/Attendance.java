package org.example.api;

import java.io.IOException;

import org.example.types.Err;
import org.example.types.extractors.AttendanceStudentRequest;
import org.example.util.Response;
import org.example.util.Result;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/attendance/")
public class Attendance extends HttpServlet {
	private static Result<Void, Err> handle_insertion(AttendanceStudentRequest req) {
		Result<Void, Err> result = org.example.data.Attendance.enter_student(
			req.getRollno().get(),
			req.getTeacherid().get(),
			req.getSubjectid().get(),
			req.getPeriod(),
			req.getDate()
		);
		return result;
	}

	private static Result<Void, Err> handle_deletion(AttendanceStudentRequest req) {
		Result<Void, Err> result = org.example.data.Attendance.delete_student(
			req.getRollno().get(),
			req.getTeacherid().get(),
			req.getSubjectid().get(),
			req.getPeriod(),
			req.getDate()
		);
		return result;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AttendanceStudentRequest.extract(req.getParameterMap())
				.and_then(request -> handle_insertion(request))
				.or_else(e -> Response.send_err(resp, e));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	} 

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AttendanceStudentRequest.extract(req.getParameterMap())
				.and_then(request -> handle_deletion(request))
				.or_else(e -> Response.send_err(resp, e));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
