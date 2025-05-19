package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;

import org.example.data.Batch;
import org.example.types.Err;
import org.example.types.extractors.BatchSearchRequest;
import org.example.util.Response;
import org.example.util.Result;
import org.example.util.Serializer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/api/batch/search")
public class BatchSearch extends HttpServlet {
	private static Result<Void, Err> search_and_serialize_to(BatchSearchRequest request, PrintWriter dst) {
		if (request.getBatchname() != null) {
			return Batch.search(request.getBatchname())
				.and_then(results -> Serializer.serialize_to(results, dst));
		} else if (request.getTeacherid().isPresent()) {
			return Batch.search(request.getTeacherid().get())
				.and_then(results -> Serializer.serialize_to(results, dst));
		} else {
			return Batch.search(request.getBatchid().get())
				.and_then(results -> Serializer.serialize_to(results, dst));
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try (
			PrintWriter out = resp.getWriter();
		) {
			BatchSearchRequest.extract(req.getParameterMap())
				.and_then(search_request -> search_and_serialize_to(search_request, out))
				.or_else(e -> Response.send_err(resp, e));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
