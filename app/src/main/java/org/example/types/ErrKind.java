package org.example.types;

// TODO: Add Documentation
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
	ClassNotFound {
		@Override
		public String toString() {
			return "ClassNotFound";
		}
	},
	Unreachable {
		@Override
		public String toString() {
			return "Unreachable";
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
	},
	NumberFormat {
		@Override
		public String toString() {
			return "NumberFormat";
		}
	},
	IllegalArgument {
		public String toString() {
			return "IllegalArgument";
		}
	},
	ElementNotFound {
		public String toString() {
			return "ElementNotFound";
		}
	},
	InsertionErr {
		public String toString() {
			return "InsertionErr";
		}
	},
	DeleteErr {
		public String toString() {
			return "DeleteErr";
		}
	},
	DBTimeout {
		public String toString() {
			return "DBTimeout";
		}
	},
	DBConnectionErr {
		public String toString() {
			return "DBConnectionErr";
		}
	},
	JsonSerializeError {
		public String toString() {
			return "JsonSerializeError";
		}
	},
	IOError {
		public String toString() {
			return "IOError";
		}
	};

	public abstract String toString();
}
