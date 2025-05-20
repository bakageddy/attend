package org.example.types;

public class Err {
	public ErrKind kind;
	public String err;
	public Err(ErrKind kind, String err) {
		this.kind = kind;
		this.err = err;
	}

	public String toString() {
		return "Kind: " + kind.toString() + " Cause: " + this.err;
	}
}
