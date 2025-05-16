package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;

import org.example.data.BatchData;
import org.example.types.Err;
import org.example.types.extractors.BatchCreateRequest;
import org.example.types.extractors.BatchDataFetchRequest;
import org.example.types.extractors.BatchDeleteRequest;
import org.example.util.Result;
import org.example.util.Serializer;
import org.example.util.Response;

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
		try (PrintWriter out = resp.getWriter()) {
			BatchDataFetchRequest.extract(req.getParameterMap())
				.and_then(request -> BatchData.search(request.getBatchid().get()))
				.and_then(results -> Serializer.serialize_to(results, out))
				.and_then(_ignore -> set_status(resp, HttpServletResponse.SC_OK))
				.or_else(err -> send_err(resp, err));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	// Create Empty Batch
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try (
			PrintWriter out = resp.getWriter();
		) {
			BatchCreateRequest.extract(req.getParameterMap())
				.and_then(create_request -> handle_creation(create_request))
				.and_then(id -> Response.send(id, out))
				.or_else(err -> send_err(resp, err));
			return;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	// Delete Batch
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			BatchDeleteRequest.extract(req.getParameterMap())
					.and_then(delete_request -> handle_deletion(delete_request))
					.and_then(_ignore -> set_status(resp, HttpServletResponse.SC_OK))
					.or_else(e -> send_err(resp, e));
			return;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}


	private static Result<Void, Err> set_status(HttpServletResponse resp, int status) {
		resp.setStatus(status);
		return Result.ok(null);
	}

	private static<T> Result<T, Void> send_err(HttpServletResponse resp, Err e) {
		try {
			resp.sendError(
				err_to_status(e),
				e.toString()
			);
			resp.flushBuffer();
			return Result.ok(null);
		} catch (IOException ex) {
			return Result.err(null);
		}
	}



	private static Result<Long, Err> handle_creation(BatchCreateRequest req) {
		return org.example.data.Batch.create(
			req.getTeacherid().get(), 
			req.getName());
	}

	private static Result<Void, Err> handle_deletion(BatchDeleteRequest req) {
		return org.example.data.Batch.delete(
			req.getBatchid().get(),
			req.getTeacherid().get());
	}

	private static int err_to_status(Err e) {
		switch (e.kind) {
			case ElementNotFound:
				return HttpServletResponse.SC_NO_CONTENT;
			case OutOfMemory:
			case ClassNotFound:
			case IllegalState:
			case DBTimeout:
			case IOError:
			case JsonSerializeError:
				return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			default:
				return HttpServletResponse.SC_BAD_REQUEST;
		}
	}
}
