package org.example.data;

import org.example.util.Result;

public class Authentication {

	public static Result<Boolean, String> sign_in(long teacherid, String password) {
		return Result.err("NOT IMPLEMENTED");
	}

	public static Result<Boolean, String> log_in(long teacherid, long sessionid) {
		return Result.err("NOT IMPLEMENTED");
	}

	public static Result<Boolean, String> log_out(long teacherid, long sessionid) {
		return Result.err("NOT IMPLEMENTED");
	}
}
