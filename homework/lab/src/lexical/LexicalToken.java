package lexical;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 词法分析器输出的 token 类型
 * 
 * @author zz
 *
 */
public class LexicalToken {

	private StringProperty word; // 合法的token序列
	private StringProperty category; // token序列对应的类别标记符
	private StringProperty value; // token序列对应的属性值
	private int lineNum = -1;

	/**
	 * 构造器
	 * 
	 * @param word     合法的token序列
	 * @param category token序列对应的类别标记符
	 * @param value    token序列对应的属性值
	 */
	public LexicalToken(String word, String category, String value) {
		this.word = new SimpleStringProperty(word);
		this.category = new SimpleStringProperty(category);
		this.value = new SimpleStringProperty(value);
	}

	public LexicalToken(String word, String category, String value, int num) {
		this.word = new SimpleStringProperty(word);
		this.category = new SimpleStringProperty(category);
		this.value = new SimpleStringProperty(value);
		this.lineNum = num;
	}

	public StringProperty wordProperty() {
		return word;
	}

	public String getWord() {
		return word.get();
	}

	public void setWord(String word) {
		this.word = new SimpleStringProperty(word);
	}

	public StringProperty categoryProperty() {
		return category;
	}

	public String getCategory() {
		return category.get();
	}

	public void setCategory(String category) {
		this.category = new SimpleStringProperty(category);
	}

	public StringProperty valueProperty() {
		return value;
	}

	public String getValue() {
		return value.get();
	}

	public void setValue(String value) {
		this.value = new SimpleStringProperty(value);
	}

	public StringProperty tokenProperty() {
		return new SimpleStringProperty("<" + this.getCategory() + ", " + this.getCategory() + ">");
	}

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}
	
	public String toTerminal() {
		if (category.get().equals("ID") || category.get().equals("CONSTI")||category.get().equals("CONSTF")) {
			return category.get().toLowerCase();
		}else {
			return word.get();
		}
	}

	@Override
	public int hashCode() {
		return this.word.hashCode() + this.category.hashCode() + this.value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof LexicalToken)) {
			return false;
		}

		LexicalToken token = (LexicalToken) obj;
		if (!token.getWord().equals(this.getWord())) {
			return false;
		}

		if (!token.getCategory().equals(this.getCategory())) {
			return false;
		}

		if (!token.getValue().equals(this.getValue())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.getWord() + "\t<" + this.getCategory() + ", " + this.getValue() + ">";
	}
}
