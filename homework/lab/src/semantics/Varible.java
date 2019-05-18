package semantics;

import java.util.ArrayList;
import java.util.List;

public class Varible {

	private String name;	//	节点代表的名字，如变量的名称
	private String type = null;	//	节点代表的变量的类型
	private String value = null;	//	节点的值
	private String begin;	//	节点代表的代码块的起始位置
	private String next;	//	节点代表的代码块之后的位置
	private List<Integer> nextList=new ArrayList<Integer>();	//	节点的 nextlist 列表
	private List<Integer> trueList=new ArrayList<Integer>();	//	节点的 truelist 列表
	private List<Integer> falseList= new ArrayList<Integer>();	//	节点的 falselist 列表

	
	public Varible() {
		// TODO Auto-generated constructor stub
	}
	public Varible(String name) {
		this.name = name;
	}

	public Varible(String name, String type, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	public Varible(String type, String value) {
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}
	
	public List<Integer> getNextList() {
		return nextList;
	}
	
	public String getBegin() {
		return begin;
	}
	
	public String getNext() {
		return next;
	}
	
	public List<Integer> getTrueList() {
		return trueList;
	}
	
	public List<Integer> getFalseList() {
		return falseList;
	}
	
	

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public void makeNextList(int i) {
		this.nextList.add(i);
	}
	
	
	
	public void makeTrueList(int i) {
		this.trueList.add(i);
	}
	
	public void makeFalseList(int i) {
		this.falseList.add(i);
	}
	
	public void mergeTrue(List<Integer> list) {
		this.trueList.addAll(list);
	}
	
	public void mergeFalse(List<Integer> list) {
		this.falseList.addAll(list);
	}
	
	public void mergeNext(List<Integer> list) {
		this.nextList.addAll(list);
	}
	
	public void setBegin(String begin) {
		this.begin = begin;
	}
	
	public void setNext(String next) {
		this.next = next;
	}
	
	
	@Override
	public String toString() {
		return "("+name+", "+type+", "+value+")";
	}

}
