package org.example.types;

public class Err {
	public ErrKind kind;
	String err;
	public Err(ErrKind kind, String err) {
		this.err = err;
	}

	public String toString() {
		return "Kind: " + kind.toString() + "Cause: " + this.err;
	}
}
