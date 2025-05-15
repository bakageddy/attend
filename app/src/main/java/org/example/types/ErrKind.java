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
	DBTimeout {
		public String toString() {
			return "DBTimeout";
		}
	},
	DBConnectionErr {
		public String toString() {
			return "DBConnectionErr";
		}
	};
	public abstract String toString();
}
