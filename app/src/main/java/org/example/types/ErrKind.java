package org.example.types;

public enum ErrKind {
	Null {
		@Override
		public String toString() {
			return "Null";
		}
	},
	OutOfMemory {
		@Override
		public String toString() {
			return "OutOfMemory";
		}
	},
	NumberFormat {
		@Override
		public String toString() {
			return "NumberFormat";
		}
	},
	Unreachable {
		@Override
		public String toString() {
			return "Unreachable";
		}
	},
	ClassNotFound {
		@Override
		public String toString() {
			return "ClassNotFound";
		}
	},
	InitiliazationFailure {
		@Override
		public String toString() {
			return "InitiliazationFailure";
		}
	},
	IllegalState {
		public String toString() {
			return "IllegalState";
		}
	};
	public abstract String toString();
}
