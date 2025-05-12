package org.example.api;

import java.io.IOException;
import java.util.Calendar;
import java.util.Optional;

import org.example.util.Result;
import org.example.util.Parser;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/attendance/")
public class Attendance extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String rollno_param = req.getParameter("rollno");
		if (rollno_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Cannot set attendance without req"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> parsed_rollno = Parser.parse_long(rollno_param);
		if (parsed_rollno.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"RollNo must be numeric"
			);
			resp.flushBuffer();
			return;
		}

		String subject_id_param = req.getParameter("subjectid");
		if (subject_id_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Cannot set attendance without subjectid"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> parsed_subjectid = Parser.parse_long(subject_id_param);
		if (parsed_subjectid.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Subject ID must be numeric"
			);
			resp.flushBuffer();
			return;
		}

		String teacher_id_param = req.getParameter("teacherid");
		if (teacher_id_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Teacher ID is required for attendance"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> parsed_teacherid = Parser.parse_long(teacher_id_param);
		if (parsed_teacherid.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Teacher ID must be numeric"
			);
			resp.flushBuffer();
			return;
		}

		String period = req.getParameter("period");
		if (period == null) {
			period = "I"; // TODO: Refactor Default Period, there must be something better than this..
		}

		String date = req.getParameter("date");
		if (date == null) {
			date = default_date();
		}

		Result<Void, String> result = org.example.data.Attendance.enter_student(
			parsed_rollno.get(),
			parsed_teacherid.get(),
			parsed_subjectid.get(),
			period,
			date
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
		String rollno_param = req.getParameter("rollno");
		if (rollno_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Cannot set attendance without req"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> parsed_rollno = Parser.parse_long(rollno_param);
		if (parsed_rollno.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"RollNo must be numeric"
			);
			resp.flushBuffer();
			return;
		}

		String subject_id_param = req.getParameter("subjectid");
		if (subject_id_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Cannot set attendance without subjectid"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> parsed_subjectid = Parser.parse_long(subject_id_param);
		if (parsed_subjectid.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Subject ID must be numeric"
			);
			resp.flushBuffer();
			return;
		}

		String teacher_id_param = req.getParameter("teacherid");
		if (teacher_id_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Teacher ID is required for attendance"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> parsed_teacherid = Parser.parse_long(teacher_id_param);
		if (parsed_teacherid.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Teacher ID must be numeric"
			);
			resp.flushBuffer();
			return;
		}

		String period = req.getParameter("period");
		if (period == null) {
			period = "I";  // TODO: Refactor Default period
		}
		String date = req.getParameter("date");
		if (date == null) {
			date = default_date();
		}

		Result<Void, String> result = org.example.data.Attendance.delete_student(
			parsed_rollno.get(),
			parsed_teacherid.get(),
			parsed_subjectid.get(),
			period,
			date
		);

		if (result.isErr()) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, result.err_msg());
			resp.flushBuffer();
			return;
		}

		resp.setStatus(HttpServletResponse.SC_OK);
		resp.flushBuffer();
		return;
	}

	private static String default_date() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1; // For whatever reason, January is 0 ffs.
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String dateString = String.format("%04d-%02d-%02d", year, month, day);
		return dateString;
	}

}
