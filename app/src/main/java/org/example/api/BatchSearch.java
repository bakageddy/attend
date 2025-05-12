package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import org.example.data.Batch;
import org.example.util.Parser;
import org.example.util.Result;
import org.example.util.Serializer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/batch/search")
public class BatchSearch extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pattern_param = req.getParameter("pattern");
		if (pattern_param != null) {
			Result<List<Batch>, String> result = Batch.search(pattern_param);
			if (result.isErr()) {
				resp.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					result.err_msg()
				);
				resp.flushBuffer();
				return;
			}

			Result<String, String> payload = Serializer.serialize(result.unwrap());
			if (payload.isErr()) {
				resp.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					payload.err_msg()
				);
				resp.flushBuffer();
				return;
			}

			PrintWriter out = resp.getWriter();
			resp.setStatus(HttpServletResponse.SC_OK);
			out.write(payload.unwrap());
			out.flush();
			out.close();
			return;
		}

		String teacherid_param = req.getParameter("teacherid");
		if (teacherid_param != null) {
			Optional<Long> teacherid = Parser.parse_long(teacherid_param);
			if (teacherid.isEmpty()) {
				resp.sendError(
					HttpServletResponse.SC_BAD_REQUEST, 
					"teacherid must be numeric"
				);
				resp.flushBuffer();
				return;
			}

			Result<List<Batch>, String> result = Batch.search_teacherid(teacherid.get());
			if (result.isErr()) {
				resp.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					result.err_msg()
				);
				resp.flushBuffer();
				return;
			}

			Result<String, String> payload = Serializer.serialize(result.unwrap());
			if (payload.isErr()) {
				resp.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					payload.err_msg()
				);
				resp.flushBuffer();
				return;
			}

			PrintWriter out = resp.getWriter();
			resp.setStatus(HttpServletResponse.SC_OK);
			out.write(payload.unwrap());
			out.flush();
			out.close();
			return;
		}

		String batchid_param = req.getParameter("batchid");
		if (batchid_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Should set either pattern or batchid or teacherid"
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
			resp.flushBuffer();
			return;
		}

		Result<Batch, String> result = Batch.search(batchid.get());
		if (result.isErr()) {
			if (result.isErr()) {
				resp.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					result.err_msg()
				);
				resp.flushBuffer();
				return;
			}
		}

		Result<String, String> payload = Serializer.serialize(result.unwrap());
		if (payload.isErr()) {
			resp.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				payload.err_msg()
			);
			resp.flushBuffer();
			return;
		}

		resp.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = resp.getWriter();
		out.write(payload.unwrap());
		out.flush();
		out.close();
		return;
	}
}
