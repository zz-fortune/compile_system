package grammer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LR1Item {

	private String left; // 左部非终结符
	private List<String> right = new ArrayList<String>(); // 右部符号集合
	private int dotPosition; //	圆点的位置
	private Set<String> successor = new HashSet<String>();	//	后继符号集合

	public LR1Item() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 构造器
	 * 
	 * @param left 左部符号
	 * @param right 右部符号列表
	 * @param dotPosition 圆点的位置
	 * @param successor 后继符集
	 */
	public LR1Item(String left, List<String> right, int dotPosition, Set<String> successor) {
		this.left = left;
		for (int i = 0; i < right.size(); i++) {
			this.right.add(right.get(i));
		}
		this.dotPosition = dotPosition;
		for (String string : successor) {
			this.successor.add(string);
		}
	}

	/**
	 * 构建一个特殊的项目，后继符为空集的归约项目
	 * 
	 * @param grammer 推导式
	 * @return 特殊的项目
	 */
	public static LR1Item LR1Grammer(String grammer) {
		LR1Item item = new LR1Item();
		String[] strings = grammer.split("->");
		item.left = strings[0].replace(" ", "");
		strings = strings[1].split(" ");
		for (int i = 0; i < strings.length; i++) {
			if (strings[i].length() > 0) {
				item.right.add(strings[i]);
			}
		}
		item.dotPosition = item.right.size();
		return item;
	}

	
	public String getLeft() {
		return left;
	}

	public List<String> getRight() {
		return right;
	}

	/**
	 * 获得圆点后面的第二符号开始的符号串
	 * 
	 * @return 圆点后面的第二符号开始的符号串
	 */
	public List<String> getBeta() {
		List<String> list = new ArrayList<String>();
		for (int i = this.dotPosition + 1; i < this.right.size(); i++) {
			list.add(this.right.get(i));
		}
		return list;
	}

	public Set<String> getSuccessor() {
		return successor;
	}

	public int getDotPosition() {
		return dotPosition;
	}
	
	public void setDotPosition(int dotPosition) {
		this.dotPosition = dotPosition;
	}
	
	/**
	 * 比较两个项目是否只有后继符不同
	 * 
	 * @param that 带比较的项目
	 * @return {@code true} 当两个项目只有后继符不同
	 */
	public boolean same(LR1Item that) {
		if (!this.left.equals(that.left)) {
			return false;
		}
		
		if (!this.right.equals(that.right)) {
			return false;
		}
		
		if (this.dotPosition!=that.dotPosition) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 返回推导式格式的字符串
	 * 
	 * @return 项目对应的推导式
	 */
	public String getGramer() {
		StringBuffer buffer=new StringBuffer();
		buffer.append(left);
		buffer.append("->");
		for (int i = 0; i < right.size(); i++) {
			buffer.append(right.get(i)+" ");
		}
		return buffer.toString();

	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 == null) {
			return false;
		}

		if (!(arg0 instanceof LR1Item)) {
			return false;
		}

		LR1Item that = (LR1Item) arg0;
		if (!this.left.equals(that.left)) {
			return false;
		}

		if (!this.right.equals(that.right)) {
			return false;
		}

		if (this.dotPosition != that.dotPosition) {
			return false;
		}

		if (!this.successor.equals(that.successor)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return this.left.hashCode() + this.right.hashCode() + this.dotPosition + this.successor.hashCode();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.left);
		buffer.append("->");
		for (int i = 0; i < this.right.size(); i++) {
			if (this.dotPosition == i) {
				buffer.append(".");
			}
			buffer.append(right.get(i)+" ");
		}
		if (this.dotPosition == right.size()) {
			buffer.append(".");
		}
		buffer.append(",");
		for (String string : successor) {
			buffer.append(string+" ");
		}
		return buffer.toString();
	}
}
