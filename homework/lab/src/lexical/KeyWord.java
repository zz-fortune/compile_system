package lexical;

/**
 * 记录关键字在代码中的形式与其编码的对应关系
 * 
 * @author zz
 *
 */
public enum KeyWord {

	INT("int", 2), FLOAT("float", 3), BOOL("bool", 4), RECORD("record", 5), IF("if", 6), ELSE("else", 7), DO("do", 8),
	WHILE("while", 9), AND("and", 20), OR("or", 21), NOT("not", 22), TRUE("true", 35), FALSE("false", 36);

	//	关键字在代码中的表现形式
	private String name;
	
	//	编码
	private int id;

	/**
	 * 构造函数
	 * 
	 * @param name 关键字在代码中的表现形式
	 * @param id 编码
	 */
	private KeyWord(String name, int id) {
		this.name = name;
		this.id = id;
	}

	/**
	 * 通过关键字在代码中的表现形式获取关键字的编码
	 * 
	 * @param name 关键字在代码中的表现形式
	 * @return 编码。若不存在该关键字，返回{@code -1}
	 */
	public static int getId(String name) {
		for(KeyWord word:KeyWord.values()) {
			if (word.getName().equals(name)) {
				return word.getId();
			}
		}
		return -1;
	}
	
	/**
	 * 获取关键字在代码中的表现形式
	 * 
	 * @return 关键字在代码中的表现形式
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 获取关键字的编码
	 * 
	 * @return 编码
	 */
	private int getId() {
		return id;

	}
}
