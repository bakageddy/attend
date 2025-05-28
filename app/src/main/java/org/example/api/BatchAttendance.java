package org.example.api;

import java.io.IOException;

import org.example.data.Attendance;
import org.example.types.extractors.AttendanceBatchRequest;
import org.example.util.Response;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/attendance/batch")
public class BatchAttendance extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AttendanceBatchRequest.extract(req.getParameterMap())
				.and_then(request -> Attendance.enter_batch(
					request.getBatchid().get(),
					request.getTeacherid().get(), 
					request.getSubjectid().get(), 
					request.getPeriod(), 
					request.getDate()
				)).or_else(e -> Response.send_err(resp, e));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO: Think if batch deleting attendance is a good idea...
		// Maybe, maybe not
		resp.setStatus(418); // I am a teapot
	}
}
