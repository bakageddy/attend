package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import org.example.util.Parser;
import org.example.util.Result;

import com.zaxxer.hikari.pool.HikariPool;

import jakarta.servlet.ServletContext;
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
		Optional<Long[]> rollno_group = Optional.empty();
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

		ServletContext ctx = getServletContext();
		HikariPool pool = (HikariPool) ctx.getAttribute("cnx_pool");
		try (
			Connection cnx = pool.getConnection();
			PrintWriter out = resp.getWriter();
		) {

			Result<Void, String> result;
			if (rollno.isPresent()) {
				result = delete_batch(cnx, batchid.get(), rollno.get());
			} else if (rollno_group.isPresent()) {
				result = delete_batch(cnx, batchid.get(), rollno_group.get());
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

		} catch (SQLException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			resp.flushBuffer();
			return;
		}
	}

	private static Result<Void, String> delete_batch(Connection cnx, Long batchid, Long rollno) {
		try {
			PreparedStatement stmt = cnx.prepareStatement("DELETE FROM BatchData WHERE batchid=? AND rollno=?");
			stmt.setLong(1, batchid);
			stmt.setLong(2, rollno);
			int no = stmt.executeUpdate();
			if (no != 1) {
				return Result.err("BEEP BOOP, no elements deleted at BatchData.delete_value");
			}
			return Result.ok(null);
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
	}

	private static Result<Void, String> delete_batch(Connection cnx, Long batchid, Long[] rollnos) {
		String query = prepare_delete(batchid, rollnos);
		try {
			PreparedStatement stmt = cnx.prepareStatement(query);
			int no = stmt.executeUpdate();
			if (no != rollnos.length) {
				return Result.err("BEEP BOOP, no of elements is inconsistent");
			}
			return Result.ok(null);
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
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
		Optional<Long[]> rollnos = Optional.empty();
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

		ServletContext ctx = getServletContext();
		HikariPool pool = (HikariPool) ctx.getAttribute("cnx_pool");
		try (
			Connection cnx = pool.getConnection();
			PrintWriter out = resp.getWriter();
		) {
			Result<Void, String> result;
			if (rollnos.isPresent()) {
				result = insert_batch(cnx, batchid.get(), rollnos.get());
			} else if (rollno.isPresent()){
				result = insert_batch(cnx, batchid.get(), rollno.get());
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
		} catch (SQLException e) {
			resp.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				e.getMessage()
			);
			resp.flushBuffer();
			return;
		} 

	}

	private static Result<Void, String> insert_batch(Connection cnx, Long batchid, Long rollno) {
		try {
			PreparedStatement stmt = cnx.prepareStatement("INSERT INTO BatchData(batchid, rollno) VALUES(?, ?);");
			stmt.setLong(1, batchid);
			stmt.setLong(2, rollno);

			int no_of_rows = stmt.executeUpdate();
			if (no_of_rows != 1) {
				return Result.err("BEEP BOOP, INSERT FAILED at BatchHelper.insert_value");
			}
			return Result.ok(null);
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
	}

	private static Result<Void, String> insert_batch(Connection cnx, Long batchid, Long[] rollnos) {
		String query = construct_query(batchid, rollnos);
		System.out.println(query);
		try {
			PreparedStatement stmt = cnx.prepareStatement(query);
			int no_of_rows = stmt.executeUpdate();

			if (no_of_rows != rollnos.length) {
				return Result.err("BEEP BOOP, INSERT FAILED at BatchHelper.insert_value");
			}
			return Result.ok(null);
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
	}

	private static String construct_query(Long batchid, Long[] rollnos) {
		StringBuilder s = new StringBuilder();
		s.append("INSERT INTO BatchData(batchid, rollno) VALUES");
		for (int i = 0; i < rollnos.length; i++) {
			if (i != 0) s.append(",");

			s.append("(")
				.append(batchid)
				.append(",")
				.append(rollnos[i])
				.append(")");
		}
		s.append(";");
		return s.toString();
	}

	private static String prepare_delete(Long batchid, Long[] rollnos) {
		StringBuilder s = new StringBuilder();
		s.append("DELETE FROM BatchData WHERE batchid=")
			.append(batchid)
			.append(" AND (");
		for (int i = 0; i < rollnos.length; i++) {
			if (i != 0) s.append(" OR ");
			s.append("rollno=")
				.append(rollnos[i]);
		}
		s.append(");");
		return s.toString();
	}
}
