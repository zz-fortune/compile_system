package lexical;

/**
 * 存储对对关键字、标识符等的编号
 * 
 * @author zz
 *
 */
public enum CategoryCode {
	ERROR("ERROR", 1), INT("INT", 2), FLOAT("FLOAT", 3), BOOL("BOOL", 4), RECORD("RECORD", 5), IF("IF", 6),
	ELSE("ELSE", 7), DO("DO", 8), WHILE("WHILE", 9), ADD("ADD", 10), SUB("SUB", 11), MUL("MUL", 12), DIV("DIV", 13),
	NE("NE", 14), G("G", 15), L("L", 16), GE("GE", 17), LE("LE", 18), E("E", 19), AND("AND", 20), OR("OR", 21),
	NOT("NOT", 22), ASSIGN("ASSIGN", 23), SEMI("SEMI", 24), COMMA("COMMA", 25), ID("ID", 26), LB("LB", 27),
	RB("RB", 28), LCB("LCB", 29), RCB("RCB", 30), COLON("COLON", 31), NOTE("NOTE", 32), CONSTI("CONSTI", 33),
	CONSTF("CONSTF", 34), TRUE("TRUE", 35), FALSE("FALSE", 36), THEN("THEN",37);

	//	每一类字符包括标记符和编码
	private String name;
	private int index;

	/**
	 * 构造函数
	 * 
	 * @param name 标记符
	 * @param index 编码
	 */
	private CategoryCode(String name, int index) {
		this.name = name;
		this.index = index;
	}

	/**
	 * 获取编码对应的字符类别的标记符
	 * 
	 * @param index 编码
	 * @return 标记符。在不存在的时候返回 {@code null}
	 */
	public static String getName(int index) {
		for (CategoryCode code : CategoryCode.values()) {
			if (code.getIndex() == index) {
				return code.getName();
			}
		}
		return null;
	}

	/**
	 * 获得标记符
	 * 
	 * @return 标记符
	 */
	private String getName() {
		return name;

	}

	/**
	 * 获得编码
	 * 
	 * @return 编码
	 */
	public int getIndex() {
		return index;
	}

	public void t() {

	}

}
