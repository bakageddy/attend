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
		if (rollno_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"rollno must be set"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> rollno = Parser.parse_long(rollno_param);
		if (rollno.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"rollno must be numeric"
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

			Result<Void, String> result = delete_batch(cnx, batchid.get(), rollno.get());
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
		if (rollno_param == null) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"rollno must be set"
			);
			resp.flushBuffer();
			return;
		}

		Optional<Long> rollno = Parser.parse_long(rollno_param);
		if (rollno.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"rollno must be numeric"
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
			Result<Void, String> result = insert_batch(cnx, batchid.get(), rollno.get());
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
}
