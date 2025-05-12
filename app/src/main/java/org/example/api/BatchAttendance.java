package org.example.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.data.Attendance;
import org.example.data.Student;
import org.example.util.Parser;
import org.example.util.Result;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

		String teacherid_param =  req.getParameter("teacherid");
		if (teacherid_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"teacherid must be set!"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> teacherid = Parser.parse_long(teacherid_param);
		if (teacherid.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"teacherid must be numeric and positive!"
			);
			resp.flushBuffer();
			return;
		}

		String period_param = req.getParameter("period");
		if (period_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"must set period!"
			);
			resp.flushBuffer();
			return;
		}

		String date_param = req.getParameter("date");
		if (date_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"date must be set"
			);
			resp.flushBuffer();
			return;
		}

		Result<Void, String> result = Attendance.enter_batch(
			batchid.get(),
			teacherid.get(),
			subjectid.get(),
			period_param,
			date_param
		);

		if (result.isErr()) {
			resp.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				result.err_msg()
			);
			resp.flushBuffer();
			return;
		}

		resp.setStatus(HttpServletResponse.SC_OK);
		resp.flushBuffer();
		return;
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO: Think if batch deleting attendance is a good idea...
		// Maybe, maybe not
		// Do i need to delete?
		resp.setStatus(418); // I am a teapot
	}
}
