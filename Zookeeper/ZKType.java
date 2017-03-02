package coriant.cats.zookeeper;

public enum ZKType {
	CM, STL, TP;
	public static String typeToString(ZKType type) {
		switch (type) {
		case CM:
			return "cmserver";
		case STL:
			return "stlserver";
		case TP:
			return "tpserver";
		default:
			return "otherserver";
		}
	}
}