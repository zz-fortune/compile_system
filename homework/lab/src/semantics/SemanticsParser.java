package semantics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import grammer.GrammerParser;
import grammer.LR1Item;
import lexical.SignTableItem;

public class SemanticsParser {

	private static final List<Integer> grammer2action = Arrays.asList(-1, -1, -1, 2, 1, 1, 1, 8, 3, 3, 4, 3, 4, 5, 6, 6,
			7, -1, 12, 16, 17, 18, 13, 14, 15, 3, 4, 3, 4, 9, 5, 10, 6, 6, 11, 11, 11, 11, 11, 11);

	@SuppressWarnings("serial")
	private static final Map<String, Integer> width = new HashMap<String, Integer>() {
		{
			put("int", 4);
			put("float", 4);
			put("bool", 1);
		}
	};

	private List<LR1Item> grammers = new ArrayList<LR1Item>(); // 推导式的集合
	private final Map<String, SignTableItem> signTable = new TreeMap<String, SignTableItem>();
	private final Map<String, SignTableItem> tmpTable = new HashMap<String, SignTableItem>();
	private final List<CodeItem> codes = new ArrayList<CodeItem>();
	private final Map<String, Integer> label2pos = new HashMap<String, Integer>();
	private final Stack<String> emptyLabel = new Stack<String>();
	private int idOffset = 0;
	private int codeOffset;
	private List<Varible> nodes = new ArrayList<Varible>();
	private int tmpCount = 0;
	private int labelCnt = 0;

	private GrammerParser grammerParser;

	public SemanticsParser(int codeOffset, List<LR1Item> grammers) {
		this.codeOffset = codeOffset;
		this.grammers = grammers;
	}

	public void execAction(int grammerNum) {
		int top = this.nodes.size() - 1;
		Varible v = new Varible();
		String label;
		switch (grammer2action.get(grammerNum)) {
		case 1:
			v.setType(nodes.get(top).getType());
			nodes.remove(top);
			nodes.add(v);
			break;
		case 2:
			SignTableItem item = new SignTableItem(nodes.get(top - 1).getName(), nodes.get(top - 2).getType(),
					idOffset);
			if (signTable.get(nodes.get(top - 1).getName()) != null) {
				grammerParser.addError("重复申明变量！");
			} else {
				signTable.put(nodes.get(top - 1).getName(), item);
				idOffset += width.get(signTable.get(nodes.get(top - 1).getName()).getType());
			}
			this.nodes.remove(top);
			this.nodes.remove(top - 1);
			this.nodes.remove(top - 2);
			this.nodes.add(v);
			break;
		case 3:
			String type;
			String re, arg1, arg2;
			if (nodes.get(top).getType().equals("bool") || nodes.get(top - 2).getType().equals("bool")
					|| nodes.get(top).getType().equals(nodes.get(top - 2).getType())) {
				if (!nodes.get(top).getType().equals(nodes.get(top - 2).getType())) {
					grammerParser.addError("类型不匹配，bool类型的量不能参与算术运算！");
				}
				type = nodes.get(top).getType();
				re = newTmp(type);
				arg1 = nodes.get(top - 2).getValue();
				arg2 = nodes.get(top).getValue();
			} else {
				grammerParser.addWarning("类别不匹配，发生自动类型转换");
				type = "float";
				if (nodes.get(top).getType().equals("int")) {
					arg1=nodes.get(top-2).getValue();
					arg2=newTmp(type);
					codes.add(new CodeItem(codeOffset, "change", nodes.get(top).getValue(), null, arg2));
					codeOffset++;
				} else {
					arg1=newTmp(type);
					arg2=nodes.get(top).getValue();
					codes.add(new CodeItem(codeOffset, "change", nodes.get(top-2).getValue(), null, arg1));
					codeOffset++;
				}
				re = newTmp(type);
			}
			v.setValue(re);
			v.setType(type);
			codes.add(new CodeItem(codeOffset, nodes.get(top - 1).getName(), arg1, arg2, v.getValue()));
			codeOffset += 1;
			this.nodes.remove(top);
			this.nodes.remove(top - 1);
			this.nodes.remove(top - 2);
			this.nodes.add(v);
			break;
		case 4:
			v.setType(nodes.get(top).getType());
			v.setValue(nodes.get(top).getValue());
			this.nodes.remove(top);
			this.nodes.add(v);
			break;
		case 5:
			v.setType(nodes.get(top - 1).getType());
			v.setValue(nodes.get(top - 1).getValue());
			this.nodes.remove(top);
			this.nodes.remove(top - 1);
			this.nodes.remove(top - 2);
			this.nodes.add(v);
			break;
		case 6:
			if (signTable.get(nodes.get(top).getValue()) == null) {
				this.signTable.put(nodes.get(top).getValue(),
						new SignTableItem(nodes.get(top).getValue(), nodes.get(top).getType(), 0));
			}
			v.setType(signTable.get(nodes.get(top).getValue()).getType());
			v.setValue(nodes.get(top).getValue());
			this.nodes.remove(top);
			this.nodes.add(v);
			break;
		case 7:
			if (signTable.get(nodes.get(top).getValue()) == null) {
				grammerParser.addError("引用未定义的变量！");
				v.setType("int");
				v.setValue(nodes.get(top).getValue());
				this.nodes.remove(top);
				this.nodes.add(v);
			} else {
				v.setType(signTable.get(nodes.get(top).getValue()).getType());
				v.setValue(nodes.get(top).getValue());
				this.nodes.remove(top);
				this.nodes.add(v);
			}
			break;
		case 8:
			if (signTable.get(nodes.get(top - 3).getValue()) == null) {
				grammerParser.addError("引用未定义的变量！");
			}
			codes.add(new CodeItem(codeOffset, nodes.get(top - 2).getName(), nodes.get(top - 1).getValue(), null,
					nodes.get(top - 3).getName()));
			codeOffset += 1;
			this.nodes.remove(top);
			this.nodes.remove(top - 1);
			this.nodes.remove(top - 2);
			this.nodes.remove(top - 3);
			this.nodes.add(v);
			break;
		case 9:
			v.setValue(newTmp(nodes.get(top).getType()));
			v.setType(nodes.get(top).getType());
			codes.add(new CodeItem(codeOffset, nodes.get(top - 1).getName(), nodes.get(top).getValue(), null,
					v.getValue()));
			codeOffset += 1;
			this.nodes.remove(top);
			this.nodes.remove(top - 1);
			this.nodes.add(v);
			break;
		case 10:
			v.setValue(newTmp("bool"));
			v.setType("bool");
			codes.add(new CodeItem(codeOffset, nodes.get(top - 1).getName(), nodes.get(top - 2).getValue(),
					nodes.get(top).getValue(), v.getValue()));
			codeOffset += 1;
			this.nodes.remove(top);
			this.nodes.remove(top - 1);
			this.nodes.remove(top - 2);
			this.nodes.add(v);
			break;
		case 11:
			v.setType(nodes.get(top).getType());
			v.setName(nodes.get(top).getName());
			this.nodes.remove(top);
			this.nodes.add(v);
			break;
		case 12:
			v.setBegin(emptyLabel.peek());
			v.setNext(emptyLabel.peek());
			label2pos.put(emptyLabel.pop(), codeOffset);
			for (int i = 0; i < 15; i++) {
				this.nodes.remove(top - i);
			}
			this.nodes.add(v);
			break;
		case 13:
			v.setBegin(nodes.get(top - 8).getBegin());
			v.setNext(emptyLabel.peek());
			codes.add(new CodeItem(codeOffset, "j", null, null, v.getBegin()));
			codeOffset++;
			label2pos.put(emptyLabel.peek(), codeOffset);
			for (int i = 0; i < 10; i++) {
				this.nodes.remove(top - i);
			}
			this.nodes.add(v);
			emptyLabel.pop();
			break;
		case 14:
			label = newLabel();
			v.setBegin(label);
			v.setNext(label);
			this.nodes.add(v);
			label2pos.put(label, codeOffset);
			break;
		case 15:
			label = newLabel();
			this.nodes.add(v);
			emptyLabel.push(label);
			codes.add(new CodeItem(codeOffset, "jf", nodes.get(top - 2).getValue(), null, label));
			codeOffset++;
			break;
		case 16:
			label = newLabel();
			v.setBegin(label);
			v.setNext(label);
			this.nodes.add(v);
			label2pos.put(label, codeOffset);
			break;
		case 17:
			label = newLabel();
			this.nodes.add(v);
			emptyLabel.add(label);
			codes.add(new CodeItem(codeOffset, "jf", nodes.get(top - 2).getValue(), null, label));
			codeOffset++;
			break;
		case 18:
			label = newLabel();
			codes.add(new CodeItem(codeOffset, "j", null, null, label));
			codeOffset++;
			label2pos.put(emptyLabel.pop(), codeOffset);
			emptyLabel.push(label);
			this.nodes.add(v);
			break;
		default:
			int len;
			if (this.grammers.get(grammerNum).getRight().contains("epsilon")) {
				len = 0;
			} else {
				len = this.grammers.get(grammerNum).getRight().size();
			}
			for (int i = 0; i < len; i++) { // 根据推导式右部长度进行弹栈
				this.nodes.remove(top - i);
			}
			this.nodes.add(v);
			break;
		}
	}
	
	public Map<String, SignTableItem> getSignTable() {
		return signTable;
	}
	
	public void backPatch() {
		for (CodeItem item:codes) {
			if (item.getActionType().contains("j")) {
				item.setResult(String.valueOf(label2pos.get(item.getResult())));
			}
		}
	}

	public void printf() {
		System.out.println("符号表");
		System.out.println(this.signTable);
		System.out.println("中间代码");
		System.out.println(this.codes);
		System.out.println("符号与代码的关系");
		System.out.println(label2pos);
	}

	public void pushNode(Varible node) {
		this.nodes.add(node);
	}

	private String newTmp(String type) {
		String tmp = "$" + tmpCount;
		tmpCount += 1;
		tmpTable.put(tmp, new SignTableItem(tmp, type, 0));
		return tmp;
	}

	private String newLabel() {
		String label = "L" + labelCnt;
		labelCnt++;
		return label;
	}

	public void setGrammerParser(GrammerParser grammerParser) {
		this.grammerParser = grammerParser;
	}
	
	public List<CodeItem> getCodes() {
		return codes;
	}
	
	public Map<String, Integer> getLabel2pos() {
		return label2pos;
	}

	public static void main(String[] args) {
		System.out.println(width);
	}

}
