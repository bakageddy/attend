package org.example.api;

import java.io.IOException;
import java.util.Optional;

import org.example.data.BatchData;
import org.example.util.Parser;
import org.example.util.Result;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/batch/student")
public class BatchHelper extends HttpServlet {
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String batchid_param = req.getParameter("batchid");
		if (batchid_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Must set batchid"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> batchid = Parser.parse_long(batchid_param);
		if (batchid.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"batchid must be postively numeric"
			);
			resp.flushBuffer();
			return;
		}

		String rollno_param = req.getParameter("rollno");
		Optional<Long> rollno = Optional.empty();
		if (rollno_param != null) {
			rollno = Parser.parse_long(rollno_param);
			if (rollno.isEmpty()) {
				resp.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"rollno must be set"
				);
				resp.flushBuffer();
				return;
			}
		}

		String rollno_group_param = req.getParameter("rollno[]");
		Optional<long[]> rollno_group = Optional.empty();
		if (rollno_group_param != null) {
			rollno_group = Parser.parse_long(rollno_group_param.split(","));
			if (rollno_group.isEmpty()) {
				resp.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"rollno[] is messy"
				);
				resp.flushBuffer();
				return;
			}
		}

		if (rollno_group.isEmpty() && rollno.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"must set either rollno[] or rollno"
			);
			resp.flushBuffer();
			return;
		}

		Result<Void, String> result;
		if (rollno.isPresent()) {
			result = BatchData.delete(batchid.get(), rollno.get());
		} else if (rollno_group.isPresent()) {
			result = BatchData.delete(batchid.get(), rollno_group.get());
		} else {
			result = Result.err("How do you get here?! This is unreachable");
		}

		if (result.isErr()) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, result.err_msg());
			resp.flushBuffer();
			return;
		}

		resp.setStatus(HttpServletResponse.SC_OK);
		resp.flushBuffer();
		return;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String batchid_param = req.getParameter("batchid");
		if (batchid_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Must set batchid"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> batchid = Parser.parse_long(batchid_param);
		if (batchid.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"batchid must be postively numeric"
			);
			resp.flushBuffer();
			return;
		}

		String rollno_param = req.getParameter("rollno");
		Optional<Long> rollno = Optional.empty();
		if (rollno_param != null) {
			rollno = Parser.parse_long(rollno_param);
			if (rollno.isEmpty()) {
				resp.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"rollno must be set or rollno[] must be set"
				);
				resp.flushBuffer();
				return;
			}
		}

		String rollno_group_param = req.getParameter("rollno[]");
		Optional<long[]> rollnos = Optional.empty();
		if (rollno_group_param != null) {
			rollnos = Parser.parse_long(rollno_group_param.split(","));
			if (rollnos.isEmpty()) {
				resp.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"rollno[] is messy"
				);
				resp.flushBuffer();
				return;
			}
		}

		if (rollnos.isEmpty() && rollno.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"must set either rollno[] or rollno"
			);
			resp.flushBuffer();
			return;
		}

		Result<Void, String> result;
		if (rollnos.isPresent()) {
			result = BatchData.add(batchid.get(), rollnos.get());
		} else if (rollno.isPresent()){
			result = BatchData.add(batchid.get(), rollno.get());
		} else {
			result = Result.err("How did you get here?! It's unreachable");
		}

		if (result.isErr()) {
			resp.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				result.err_msg()
			);
			resp.flushBuffer();
			return;
		}

		resp.setStatus(HttpServletResponse.SC_CREATED);
		resp.flushBuffer();
		return;
	}
}
