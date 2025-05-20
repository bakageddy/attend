package org.example.api;

import java.io.IOException;
import java.io.PrintWriter;

import org.example.data.Batch;
import org.example.types.Err;
import org.example.types.ErrKind;
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
	private static Result<String, Err> search_and_serialize(BatchSearchRequest request) {
		if (request.getBatchname() != null) {
			return Batch.search(request.getBatchname())
				.and_then(results -> Serializer.serialize(results));
		} else if (request.getBatchid().isPresent()) {
			return Batch.search(request.getBatchid().get())
				.and_then(results -> Serializer.serialize(results));
		} else if (request.getTeacherid().isPresent()){
			return Batch.search_teacherid(request.getTeacherid().get())
				.and_then(results -> Serializer.serialize(results));
		} else {
			return Result.err(new Err(
				ErrKind.Unreachable, 
				"Something went wrong"
			));
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try (
			PrintWriter out = resp.getWriter();
		) {
			BatchSearchRequest.extract(req.getParameterMap())
				.and_then(search_request -> search_and_serialize(search_request))
				.and_then(results -> Response.send(results, out))
				.or_else(e -> Response.send_err(resp, e));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
