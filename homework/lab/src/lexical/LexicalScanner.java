package lexical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 词法分析器的扫描器
 * 
 * @author zz
 *
 */
public class LexicalScanner {

	private final List<LexicalToken> tokens = new ArrayList<>(); // 已处理的 token 列表
	private LexicalBuffer buffer; // 缓存区
	private StringBuffer token = new StringBuffer(); // 一个 token 的缓存区
	private List<String> errors= new ArrayList<String>();

	private Map<String, SignTableItem> signTable = new HashMap<String, SignTableItem>(); // 符号表

	/**
	 * 构造器
	 * 
	 * @param buffer 缓存区
	 */
	public LexicalScanner(LexicalBuffer buffer) {
		this.buffer = buffer;
	}

	/**
	 * 设置扫描器的缓存区
	 * 
	 * @param buffer 缓存区
	 */
	public void setBuffer(LexicalBuffer buffer) {
		this.buffer = buffer;
	}

	/**
	 * 开始扫描
	 */
	public void start() {
		char ch;

		// 	当还有字符未处理，循环读取字符
		while (buffer.available()) {
			ch = buffer.next();

			// 	遇到空格，直接跳过
			if (ch == ' ') {
				removeSpace();
			} else if (isLetter(ch) || ch == '_') { // 处理标识符，包括变量个关键字
				this.token.append(ch);
				recognizeV();
			} else if (isDigit(ch)) { // 处理数字，包括整数和浮点数
				this.token.append(ch);
				recognizeN();
			} else if (ch == '#') { // 结束符，结束
				break;
			} else { // 处理其余的特殊符号
				this.token.append(ch);
				recognizeO(ch);
			}
		}
		this.tokens.add(new LexicalToken("#", "END", "-", buffer.getRawNum()));

	}

	/**
	 * 获取分析出的token列表
	 * 
	 * @return 分析出的token列表
	 */
	public List<LexicalToken> getTokens() {
		return this.tokens;
	}

	/**
	 * 判断一个字符是否为字母
	 * 
	 * @param ch 带判断的字符
	 * @return {@code true} 当其是字母
	 */
	private boolean isLetter(char ch) {
		return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
	}

	/**
	 * 判断一个字符是否是数字字符
	 * 
	 * @param ch 带判断的字符
	 * @return 当其是数字
	 */
	private boolean isDigit(char ch) {
		return ch >= '0' && ch <= '9';
	}

	/**
	 * 去掉空格
	 */
	private void removeSpace() {
		char ch;
		do {
			ch = buffer.next();
		} while (ch == ' ');
		buffer.rollback();
	}

	/**
	 * 处理标识符，包括变量个关键字
	 */
	private void recognizeV() {
		char ch = buffer.next();

		//	 获得完整的标识符
		while (isLetter(ch) || isDigit(ch) || ch == '_') {
			this.token.append(ch);
			ch = buffer.next();
		}
		buffer.rollback();
		String word = this.token.toString();
		this.token.delete(0, token.length());
		String category, value;

		//	 判断是否为关键字
		if (KeyWord.getId(word) != -1) {
			category = CategoryCode.getName(KeyWord.getId(word));
			value = "-";
		} else {
			this.signTable.put(word, null);
			category = CategoryCode.getName(26);
			value = word;
		}
		this.tokens.add(new LexicalToken(word, category, value, buffer.getRawNum()));
		
	}

	/**
	 * 处理数字，包括整数和浮点数
	 */
	private void recognizeN() {
		char ch;
		boolean flag = false;
		ch = buffer.next();

		// 获取一个完整的数字
		while (isDigit(ch) || ch == '.') {
			if (isDigit(ch)) {
				this.token.append(ch);
			} else { // 当前字符是小数点

				// 若是已经读到过小数点，
				if (flag) {
					break;
				} else {
					flag = true;
					token.append(ch);
				}
			}
			ch = buffer.next();
		}
		buffer.rollback();

		String word, category, value;
		word = this.token.toString();
		this.token.delete(0, token.length());

		// 若数字为小数
		if (flag) {
			category = CategoryCode.getName(34);

			// 如果最后一位是小数点，在末尾加上 0
			if (word.lastIndexOf('.') == word.length() - 1) {
				value = word + "0";
			} else {
				value = word;
			}
		} else { // 若为整数
			category = CategoryCode.getName(33);
			value = word;
		}
		this.tokens.add(new LexicalToken(word, category, value, buffer.getRawNum()));
	}

	/**
	 * 处理其余字符
	 * 
	 * @param first 读到的第一个字符
	 */
	private void recognizeO(char first) {
		char ch;
		String word, category, value = null;
		switch (first) {
		case '/': // 若第一个是 '/'，则可能是注释
			ch = buffer.next();

			// 处理注释
			if (ch == '*') {
				this.token.append(ch);
				while (true) {
					ch = buffer.next();
					this.token.append(ch);
					if (ch == '*') {
						ch = buffer.next();
						this.token.append(ch);
						if (ch == '/') {
							break;
						}
					}
				}
				category = CategoryCode.getName(32);
				value = this.token.toString();
			} else { // 处理除号
				buffer.rollback();
				category = CategoryCode.getName(13);
			}
			break;
		case '>': // 处理大于等于符号和大于符号
			ch = buffer.next();
			if (ch == '=') {
				this.token.append(ch);
				category = CategoryCode.getName(17);
			} else {
				category = CategoryCode.getName(15);
				buffer.rollback();
			}
			break;
		case '<': // 处理小于等于符号和小于符号
			ch = buffer.next();
			if (ch == '=') {
				this.token.append(ch);
				category = CategoryCode.getName(18);
			} else {
				category = CategoryCode.getName(16);
				buffer.rollback();
			}
			break;
		case '=': // 处理等于符号和赋值符号
			ch = buffer.next();
			if (ch == '=') {
				this.token.append(ch);
				category = CategoryCode.getName(19);
			} else {
				category = CategoryCode.getName(23);
				buffer.rollback();
			}
			break;
		case ';': // 处理 ';'
			category = CategoryCode.getName(24);
			break;
		case ',': // 处理','
			category = CategoryCode.getName(25);
			break;
		case '+': // 处理'+'
			category = CategoryCode.getName(10);
			break;
		case '-': // 处理'-'
			category = CategoryCode.getName(11);
			break;
		case '*': // 处理'*'
			category = CategoryCode.getName(12);
			break;
		case '{': // 处理'{'
			category = CategoryCode.getName(29);
			break;
		case '}': // 处理'}'
			category = CategoryCode.getName(30);
			break;
		case '(': // 处理'('
			category = CategoryCode.getName(27);
			break;
		case ')': // 处理')'
			category = CategoryCode.getName(28);
			break;
		case ':': // 处理':'
			category = CategoryCode.getName(29);
			break;
		default: // 处理非法字符
			category = CategoryCode.getName(1);
			errors.add(this.token.toString());
			break;
		}

		word = this.token.toString();
		this.token.delete(0, token.length());

		if (value == null) {
			value = "-";
		}

		this.tokens.add(new LexicalToken(word, category, value, buffer.getRawNum()));
	}
	
	public List<String> getErrors() {
		return errors;
	}

	public Map<String, SignTableItem> getSignTable() {
		return signTable;
	}

}
