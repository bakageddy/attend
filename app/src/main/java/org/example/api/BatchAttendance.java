package org.example.api;

import org.example.util.Parser;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * BatchAttendance
 */
@WebServlet(urlPatterns = "/api/attendance/batch")
public class BatchAttendance extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String batchid_param = req.getParameter("batchid");
		if (batchid_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST, 
				"batchid must be set!"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> batchid = Parser.parse_long(batchid_param);
		if (batchid.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST, 
				"batchid must be numeric and positive!"
			);
			resp.flushBuffer();
			return;
		}

		String subjectid_param = req.getParameter("subjectid");
		if (subjectid_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"subjectid must be set!"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> subjectid = Parser.parse_long(subjectid_param);
		if (subjectid.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"subjectid must be numeric and positive!"
			);
			resp.flushBuffer();
			return;
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO: Think if batch deleting attendance is a good idea...
		// Maybe, maybe not
		// Do i need to delete?
		resp.setStatus(418); // I am a teapot
	}
}
