package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import org.example.data.BatchData;
import org.example.util.Parser;
import org.example.util.Result;
import org.example.util.Serializer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/batch")
public class Batch extends HttpServlet {
	// Fetch Batch
	// TODO: implement using refactored BatchData
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String param = req.getParameter("batchid");
		if (param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Must set batchid"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> batchid = Parser.parse_long(param);
		if (batchid.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Need Numerical ID"
			);
		}

		var result = BatchData.search(batchid.get());
		if (result.isEmpty()) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No Batch Members found with the given ID");
			resp.flushBuffer();
			return;
		}

		Result<String, String> payload = Serializer.serialize(result.get());
		if (payload.isErr()) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, payload.err_msg());
			resp.flushBuffer();
			return;
		}

		PrintWriter out = resp.getWriter();
		out.write(payload.unwrap());
		out.flush();
		out.close();
		return;
	}

	// Create Empty Batch
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String teacherid_param = req.getParameter("teacherid");
		if (teacherid_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Must set teacherid"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> teacherid = Parser.parse_long(teacherid_param);
		if (teacherid.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"TeacherID must be postively numeric"
			);
			resp.flushBuffer();
			return;
		}

		String name_param = req.getParameter("name");
		if (name_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Must set batch name"
			);
			resp.flushBuffer();
			return;
		}

		var result = org.example.data.Batch.create(teacherid.get(), name_param);
		if (result.isErr()) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, result.err_msg());
			resp.flushBuffer();
			return;
		}

		Result<String, String> payload = Serializer.serialize(result.unwrap());
		if (payload.isErr()) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, payload.err_msg());
			resp.flushBuffer();
			return;
		}

		PrintWriter out = resp.getWriter();
		out.write(payload.unwrap());
		out.flush();
		out.close();
		return;
	}

	// Delete Batch
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
				"batchid must be numeric"
			);
		}

		String teacherid_param = req.getParameter("teacherid");
		if (teacherid_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Must set teacherid"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> teacherid = Parser.parse_long(teacherid_param);
		if (teacherid.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"teacherid must be numeric"
			);
			resp.flushBuffer();
			return;
		}

		var result = org.example.data.Batch.delete(batchid.get(), teacherid.get());
		if (result.isErr()) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, result.err_msg());
			resp.flushBuffer();
			return;
		}

		resp.setStatus(HttpServletResponse.SC_OK);
		resp.flushBuffer();
		return;
	}
}
