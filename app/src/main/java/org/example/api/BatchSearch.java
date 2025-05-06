package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.BatchMapping;
import org.example.util.Parser;
import org.example.util.Result;
import org.example.util.Validator;

import com.google.gson.Gson;
import com.zaxxer.hikari.pool.HikariPool;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/batch/search")
public class BatchSearch extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ServletContext ctx = getServletContext();
		HikariPool pool = (HikariPool) ctx.getAttribute("cnx_pool");
		try (
			Connection cnx = pool.getConnection();
			PrintWriter out = resp.getWriter();
		){
			String pattern_param = req.getParameter("pattern");
			if (pattern_param != null) {
				Optional<String> pattern = Validator.validate_sql(pattern_param);
				if (pattern.isEmpty()) {
					resp.sendError(
						HttpServletResponse.SC_BAD_REQUEST,
						"Please do not hack me T-T"
					);
					resp.flushBuffer();
					return;
				}

				Result<String, String> payload = search_pattern(cnx, pattern.get());
				if (payload.isOk()) {
					resp.setStatus(HttpServletResponse.SC_OK);
					out.write(payload.unwrap());
					out.flush();
					return;
				} else {
					resp.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						payload.err_msg()
					);
					resp.flushBuffer();
					return;
				}
			}

			String batchid_param = req.getParameter("batchid");
			if (batchid_param != null) {
				Optional<Long> batchid = Parser.parse_long(batchid_param);
				if (batchid.isEmpty()) {
					resp.sendError(
						HttpServletResponse.SC_BAD_REQUEST,
						"batchid must be numeric"
					);
					resp.flushBuffer();
					return;
				}

				Result<String, String> payload = search_batchid(cnx, batchid.get());
				if (payload.isErr()) {
					resp.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						payload.err_msg()
					);
					resp.flushBuffer();
					return;
				}

				resp.setStatus(HttpServletResponse.SC_OK);
				out.write(payload.unwrap());
				return;
			} 

			String teacherid_param = req.getParameter("teacherid");
			if (teacherid_param == null) {
				resp.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"Should set either pattern or batchid or teacherid"
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

			Result<String, String> payload = search_teacherid(cnx, teacherid.get());
			if (payload.isErr()) {
				resp.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					payload.err_msg()
				);
				resp.flushBuffer();
				return;
			}
			resp.setStatus(HttpServletResponse.SC_OK);
			out.write(payload.unwrap());
			out.flush();
			return;

		} catch (SQLException e) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				e.getMessage()
			);
		}
	}

	private Result<String, String> search_batchid(Connection cnx, long batchid) {
		try {
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT BatchID, Name, TeacherID FROM Batch WHERE BatchID=?;"
			);
			stmt.setLong(1, batchid);
			ResultSet rst = stmt.executeQuery();

			// Only one result
			if (rst.next()) {
				String batchname = rst.getString(2);
				long teacherid = rst.getLong(3);

				Gson serializer = new Gson();
				String payload = serializer.toJson(new BatchMapping(batchid, teacherid, batchname));

				return Result.ok(payload);
			} else {
				return Result.err("Something wrong, Query returned multiple results");
			}
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
	}

	private Result<String, String> search_teacherid(Connection cnx, long teacherid) {
		try {
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT BatchID, Name, TeacherID FROM Batch WHERE TeacherID=?;"
			);
			stmt.setLong(1, teacherid);
			ResultSet rst = stmt.executeQuery();

			List<BatchMapping> batches = new ArrayList<>();
			while (rst.next()) {
				long batchid = rst.getLong(1);
				String name = rst.getString(2);
				batches.addLast(new BatchMapping(batchid, teacherid, name));
			}

			Gson serializer = new Gson();
			String payload = serializer.toJson(batches);
			return Result.ok(payload);

		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
	}

	private Result<String, String> search_pattern(Connection cnx, String pattern) {
		try {
			PreparedStatement stmt = cnx.prepareStatement(
				"SELECT BatchID, Name, TeacherID FROM Batch WHERE Name LIKE ? LIMIT 20;"
			);

			if (!pattern.endsWith("%")) {
				pattern += "%";
			}
			stmt.setString(1, pattern);

			ResultSet rst = stmt.executeQuery();
			List<BatchMapping> batches = new ArrayList<>();

			while (rst.next()) {
				long batchid = rst.getLong(1);
				String batchname = rst.getString(2);
				long teacherid = rst.getLong(3);

				batches.addLast(new BatchMapping(batchid, teacherid, batchname));
			}

			Gson serializer = new Gson();
			String payload = serializer.toJson(batches);
			return Result.ok(payload);
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
	}
}
