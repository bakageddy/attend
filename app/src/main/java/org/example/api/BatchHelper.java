package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import org.example.data.BatchData;
import org.example.types.Err;
import org.example.types.extractors.BatchDataAddRequest;
import org.example.types.extractors.BatchDataDeleteRequest;
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
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			BatchDataAddRequest.extract(req.getParameterMap())
				.and_then(request -> handle_add(request))
				.or_else(e -> send_err(resp, e));
		// this is to catch IOException and ServletException that may appear
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			BatchDataDeleteRequest.extract(req.getParameterMap())
				.and_then(request -> handle_deletion(request))
				.or_else(e -> send_err(resp, e));
		// this is to catch IOException and ServletException that may appear
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private Result<Void, Err> handle_deletion(BatchDataDeleteRequest request) {
		if (request.getRollnos() != null) {
			return BatchData.delete(request.getBatchid().get(), request.getRollnos().get());
		} else {
			return BatchData.delete(request.getBatchid().get(), request.getRollno().get());
		}
	}

	private static Result<Void, Err> handle_add(BatchDataAddRequest request) {
		if (request.getRollnos() != null) {
			return BatchData.add(request.getBatchid().get(), request.getRollnos().get());
		} else {
			return BatchData.add(request.getBatchid().get(), request.getRollno().get());
		}
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
