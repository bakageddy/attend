package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import org.example.BatchDataMapping;
import org.example.Student;
import org.example.util.Result;
import org.example.util.Parser;
import org.example.util.Validator;

import com.google.gson.Gson;
import com.zaxxer.hikari.pool.HikariPool;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/batch")
public class Batch extends HttpServlet {
	// Fetch Batch
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


		ServletContext ctx = getServletContext();
		HikariPool pool = (HikariPool) ctx.getAttribute("cnx_pool");
		try (
			Connection cnx = pool.getConnection();
			PrintWriter out = resp.getWriter();
		) {

			Result<String, String> payload = get_batch_members(cnx, batchid.get());
			if (payload.isErr()) {
				resp.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					payload.err_msg()
				);
				resp.flushBuffer();
				return;
			}

			out.write(payload.unwrap());
			out.flush();
			return;
		} catch (SQLException exp) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exp.getMessage());
		}
	}

	private Result<String, String> get_batch_members(Connection cnx, Long batchid) {
		try {
			PreparedStatement stmt = cnx.prepareStatement("SELECT Batch.BatchID, Batch.TeacherID, Student.RollNo, Student.Name FROM Batch JOIN BatchData ON Batch.BatchID = BatchData.BatchID JOIN Student ON BatchData.RollNo = Student.RollNo WHERE Batch.BatchID = ?;");
			stmt.setLong(1, batchid);
			ResultSet rst = stmt.executeQuery();

			ArrayList<Student> students = new ArrayList<>();

			Long teacherid = (Long) 0L;
			while (rst.next()) {
				teacherid = rst.getLong(1);
				Long rollno = rst.getLong(3);
				String name = rst.getString(4);
				students.addLast(new Student(rollno, name));
			}

			// TODO: You can do better than this.. 
			// make a explicit type invariant
			if (teacherid == 0L) {
				return Result.err("No such batch");
			}

			BatchDataMapping batch_data = new BatchDataMapping(batchid, teacherid, students);
			Gson serializer = new Gson();
			String payload = serializer.toJson(batch_data);
			stmt.close();
			return Result.ok(payload);
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
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

		Optional<String> name = Validator.validate_sql(name_param);
		if (name.isEmpty()) {
			resp.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Don't try to hack me T-T"
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
			Result<Long, String> result = create_batch(cnx, teacherid.get(), name.get());
			if (result.isErr()) {
				resp.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					result.err_msg()
				);
				resp.flushBuffer();
				return;
			}

			resp.setStatus(HttpServletResponse.SC_CREATED);
			out.write(
				String.valueOf(result.unwrap())
			);
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

	private static Result<Long, String> create_batch(Connection cnx, Long teacherid, String name) {
		try {

			PreparedStatement stmt = cnx.prepareStatement("INSERT INTO Batch(Name, TeacherID) VALUES(?, ?) RETURNING BatchID;");
			stmt.setString(1, name);
			stmt.setLong(2, teacherid);

			ResultSet rst = stmt.executeQuery();
			Optional<Long> batchid = Optional.empty();
			if (rst.next()) {
				batchid = Optional.of(rst.getLong(1));
			}

			if (batchid.isEmpty()) {
				return Result.err("Failed to create batch");
			} else {
				return Result.ok(batchid.get());
			}
			
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
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

		ServletContext ctx = getServletContext();
		HikariPool pool = (HikariPool) ctx.getAttribute("cnx_pool");
		try (
			Connection cnx = pool.getConnection();
			PrintWriter out = resp.getWriter();
		) {
			Result<Void, String> result = delete_batch(cnx, batchid.get(), teacherid.get());
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
		} catch (SQLException e) {
			resp.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				e.getMessage()
			);
			resp.flushBuffer();
			return;
		}
	}

	private static Result<Void, String> delete_batch(Connection cnx, Long batchid, Long teacherid) {
		try {
			PreparedStatement stmt = cnx.prepareStatement(
				"DELETE FROM Batch WHERE batchid=? AND teacherid=?"
			);
			stmt.setLong(1, batchid);
			stmt.setLong(2, teacherid);
			int no = stmt.executeUpdate();
			if (no != 1) {
				return Result.err("Failed to delete batch");
			}
			return Result.ok(null);
		} catch (SQLException e) {
			return Result.err(e.getMessage());
		}
	}
}
