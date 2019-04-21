package grammer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import lexical.LexicalToken;

public class GrammerParser {
	public static int acc = 100000;	//	接收状态的编码
	private List<String> terminals = new ArrayList<String>();	//	终结符的集合
	private List<String> varibles = new ArrayList<String>();	//	非终结符的集合
	private List<LR1Item> grammers = new ArrayList<LR1Item>();	//	推导式的集合
	private Map<String, Set<String>> firsts = new HashMap<String, Set<String>>();	//	非终结符和FIRST集的对应
	private List<Set<LR1Item>> LRfamily = new ArrayList<Set<LR1Item>>();	//	项目集族
	private int[][] actionTable;	//	action 表
	private int[][] gotoTable;	//	goto 表
	private List<String> errors = new ArrayList<String>();	//	语法分析的错误信息列表
	private List<LR1Item> rules = new ArrayList<LR1Item>();	//	语法分析中的规约式集合
	
	/**
	 * 构造器
	 */
	public GrammerParser() {
	}

	/**
	 * 根据生成的 LR（1）分析表和词法分析器产生的 token 序列进行语法分析
	 * 
	 * @param tokens
	 */
	public void parse(List<LexicalToken> tokens) {
		Queue<LexicalToken> queue = new LinkedList<LexicalToken>(tokens);	//	待处理的 token 队列
		Stack<Integer> stateStack = new Stack<Integer>(); //	状态栈
		Stack<String> signStack = new Stack<String>();	//	符号栈
		signStack.push("#");	//	初始化符号栈
		stateStack.push(0);	//	初始化状态栈
		int nextStep;	//	暂存下一个状态/归约式
		
		//	当待处理的 token 队列不为空时循环
		while (!queue.isEmpty()) {
			LexicalToken cur = queue.peek();
			if (cur.getCategory().equals("ERROR")) {	//	遇到非法字符直接跳过
				this.errors.add("Error at Line " + cur.getLineNum() + ": 非法字符");	//	记录错误信息
				queue.poll();
				continue;
			}else if (cur.getCategory().equals("NOTE")) {	//	遇到符号表直接跳过
				continue;
			}
			if ((nextStep = actionTable[stateStack.peek()][terminals.indexOf(cur.toTerminal())]) == acc) {	//	遇到 acc 则分析成功
				rules.add(null);
				queue.poll();
			} else if ((nextStep = actionTable[stateStack.peek()][terminals.indexOf(cur.toTerminal())]) > 0) {	//	移进
				stateStack.push(nextStep - 1);
				signStack.push(cur.toTerminal());
				queue.poll();
			} else if ((nextStep = actionTable[stateStack.peek()][terminals.indexOf(cur.toTerminal())]) < 0) {	//	归约
				nextStep = -nextStep - 1;
				for (int i = 0; i < this.grammers.get(nextStep).getRight().size(); i++) {	//	根据推导式右部长度进行弹栈
					if (this.grammers.get(nextStep).getRight().contains("epsilon")) {	//	将 epsilon 的长度视为 0
						break;
					}
					stateStack.pop();
					signStack.pop();
				}
				
				this.rules.add(this.grammers.get(nextStep));	//	记录归约推导式
				signStack.push(this.grammers.get(nextStep).getLeft());
				stateStack.push(
						gotoTable[stateStack.peek()][varibles.indexOf(this.grammers.get(nextStep).getLeft())] - 1);
			} else {	//	否则进入错误处理
				errorhandle(queue, stateStack, signStack);
			}
		}
		
		//	日志记录
		if (errors.isEmpty()) {
			for (int i = 0; i < rules.size(); i++) {
				if (rules.get(i) == null) {
					System.out.println("acc");
				} else {
					System.out.println(rules.get(i).getGramer());
				}
			}
		} else {
			for (String string : errors) {
				System.out.println(string);
			}
		}

	}

	/**
	 * 错误处理。当分析表中无对应的下一个状态或者是归约式时进行。这里采用恐慌模式进行恢复
	 * 
	 * @param queue 待处理的 token 队列
	 * @param stateStack 状态栈
	 * @param signStack 符号栈
	 */
	private void errorhandle(Queue<LexicalToken> queue, Stack<Integer> stateStack, Stack<String> signStack) {
		boolean flag = true;
		errors.add("Error at Line " + queue.peek().getLineNum() + ": 语法错误");	//	记录语法错误信息
		
		//	弹栈，直到遇到一个状态在 goto 表中有表项
		do {
			for (int i = 0; i < varibles.size(); i++) {
				if (gotoTable[stateStack.peek()][i] > 0) {
					stateStack.push(gotoTable[stateStack.peek()][i] - 1);
					signStack.push(varibles.get(i));
					flag = false;
					break;
				}
			}
			signStack.pop();
			stateStack.pop();
		} while (flag);
		
		//	至少丢弃一个待处理的 token，避免死循环
		queue.poll();
		
		//	丢弃，直到遇到合法的 token
		while (!queue.isEmpty()) {
			if (actionTable[stateStack.peek()][terminals.indexOf(queue.peek().toTerminal())] > 0) {
				break;
			}
			queue.poll();
		}
	}

	/**
	 * 构建LR（1）分析表
	 * 
	 * @param grammer 给定的文法
	 */
	public void buildAnalysisTable(String grammer) {
		preprocessGrammer(grammer);
		System.out.println(this.terminals);
		System.out.println(this.varibles);
//		System.out.println(this.grammers);
		computeFirsts();
		System.out.println(this.firsts);
		computeLRfamily();
//		System.out.println(this.LRfamily.size());
//		for (int i = 0; i < this.LRfamily.size(); i++) {
//			System.out.println(i + ": " + this.LRfamily.get(i));
//		}
		
		//	初始化
		this.actionTable = new int[this.LRfamily.size()][this.terminals.size()];
		this.gotoTable = new int[this.LRfamily.size()][this.varibles.size()];
		Set<String> tmpSet = new HashSet<String>();
		
		//	构造结束项目，便于后面判断
		tmpSet.add("#");
		LR1Item endItem = new LR1Item(this.grammers.get(0).getLeft(), this.grammers.get(0).getRight(),
				this.grammers.get(0).getDotPosition(), tmpSet);
		
		//	依次处理每个状态的 action 表和 goto 表
		for (int i = 0; i < this.LRfamily.size(); i++) {
			for (LR1Item item : this.LRfamily.get(i)) {	
				if (item.equals(endItem)) {	//	如果遇到结束项目，在相应表项标注 acc
					this.actionTable[i][this.terminals.indexOf("#")] = acc;
					continue;
				}
				
				//	如果是归约项目
				if (item.getRight().size() == item.getDotPosition()) {
					
					//	寻找相应的规约推导式的编号
					for (int j = 0; j < this.grammers.size(); j++) {
						if (this.grammers.get(j).getLeft().equals(item.getLeft())
								&& this.grammers.get(j).getRight().equals(item.getRight())) {
							
							//	在相应的状态的每个后继符对应的表项填入该规约式
							for (String string : item.getSuccessor()) {
								this.actionTable[i][this.terminals.indexOf(string)] = -(j + 1);
							}
						}
					}
					continue;
				}
//				String node = item.getRight().get(item.getDotPosition());
//				LR1Item nextItem = new LR1Item(item.getLeft(), item.getRight(), item.getDotPosition() + 1,
//						item.getSuccessor());
//				for (int j = 0; j < this.LRfamily.size(); j++) {
//					if (this.LRfamily.get(j).contains(nextItem)) {
//						if (this.terminals.contains(node)) {
//							this.actionTable[i][this.terminals.indexOf(node)] = j + 1;
//						} else {
//							this.gotoTable[i][this.varibles.indexOf(node)] = j + 1;
//						}
//						break;
//					}
//				}
			}
			
			//	计算该项目集可达的项目集
			Map<String, Set<LR1Item>> nexts = nextCloures(this.LRfamily.get(i));
			for (String key : nexts.keySet()) {
				if (this.terminals.contains(key)) {	//	如果是通过终结符可达，则填入 action 表
					actionTable[i][this.terminals.indexOf(key)] = this.LRfamily.indexOf(nexts.get(key)) + 1;
				} else {	//	如果是通过非终结符到达，则填入 goto 表
					gotoTable[i][this.varibles.indexOf(key)] = this.LRfamily.indexOf(nexts.get(key)) + 1;
				}
			}
		}
		
		//	日志信息
//		for (int i = 0; i < terminals.size(); i++) {
//			System.out.print("\t" + terminals.get(i));
//		}
//		System.out.println();
//		for (int i = 0; i < this.LRfamily.size(); i++) {
//			for (int j = 0; j < this.terminals.size(); j++) {
//				System.out.print("\t" + this.actionTable[i][j]);
//			}
//			System.out.println();
//		}
//		System.out.println("\n");
//		for (int i = 0; i < this.varibles.size(); i++) {
//			System.out.print("\t" + varibles.get(i));
//		}
//		System.out.println();
//		for (int i = 0; i < LRfamily.size(); i++) {
//			for (int j = 0; j < varibles.size(); j++) {
//				System.out.print("\t" + gotoTable[i][j]);
//			}
//			System.out.println();
//		}
	}

	/**
	 * 预处理文法。读入的文法是每行一个推导式，终结符包含的字母是小写，非终结符包含的字母是大写
	 * 空串用 {@code epsilon} 表示。
	 * 
	 * 处理结果会得到三个列表：终结符、非终结符和推导式
	 * 
	 * @param string 文法
	 */
	private void preprocessGrammer(String string) {
		
		//	将语法文件分行
		String[] strings;
		if (string.contains("\r\n")) {
			strings = string.split("\r\n");
		} else {
			strings = string.split("\n");
		}

		LR1Item item;
		Pattern pattern = Pattern.compile("[A-Z0-9]+");
		for (int i = 0; i < strings.length; i++) {
			if (strings[i].length() == 0) {	//	丢弃空行
				continue;
			}
			
			//	将推导式用一个特殊的 LR（1）项目表示
			item = LR1Item.LR1Grammer(strings[i]);
			this.grammers.add(item);
			String left = item.getLeft();
			if (!this.varibles.contains(left)) {	//	将左部符号加入非终结符集
				this.varibles.add(left);
			}
			List<String> right = item.getRight();
			
			//	处理右部符号
			for (int j = 0; j < right.size(); j++) {
				if (pattern.matcher(right.get(j)).matches()) {
					if (!this.varibles.contains(right.get(j))) {
						this.varibles.add(right.get(j));
					}
				} else {
					if (!this.terminals.contains(right.get(j))) {
						this.terminals.add(right.get(j));
					}
				}
			}
		}
		this.terminals.add("#");	//	将结束符加入终结符集
		this.terminals.remove("epsilon");	//	将空串冲终结符集中删除
	}

	/**
	 * 计算非终结符的 FIRST 集
	 */
	private void computeFirsts() {
		
		//	创建集合
		for (int i = 0; i < this.varibles.size(); i++) {
			firsts.put(this.varibles.get(i), new HashSet<String>());
		}

		//	直接加入空串以及可以直接退出的终结符
		for (LR1Item item : this.grammers) {
			if (this.terminals.contains(item.getRight().get(0)) || item.getRight().get(0).equals("epsilon")) {
				firsts.get(item.getLeft()).add(item.getRight().get(0));
			}
		}
		
		//	循环直接所有 FIRST 集不再发生变化
		boolean flag = true;
		while (flag) {
			flag = false;
			
			//	依次考察每个非终结符
			for (String v : this.varibles) {
				for (LR1Item item : this.grammers) {
					
					//	如果是空产生式，直接跳过
					if (!item.getLeft().equals(v) || item.getRight().get(0).equals("epsilon")) {
						continue;
					}
					
					//	依次考察右部每一个符号
					List<String> list = item.getRight();
					for (int i = 0; i < list.size(); i++) {
						int size = this.firsts.get(v).size();
						if (this.terminals.contains(list.get(i))) {	//	遇到终结符，直接退出该条推导式
							if (this.firsts.get(v).add(list.get(i))) {
								flag = true;
							}
							break;
						}
						if (!this.firsts.get(list.get(i)).contains("epsilon")) {	//	不能推出空串，推出该条推导式
							if (this.firsts.get(v).addAll(this.firsts.get(list.get(i)))) {

								flag = true;
							}
							break;
						} else if (!this.firsts.get(v).contains("epsilon") && i < list.size() - 1) {	//	能推出空，考虑右部的下一个符号
							this.firsts.get(v).addAll(this.firsts.get(list.get(i)));
							this.firsts.get(v).remove("epsilon");
						} else {	//	没有下一个符号，结束
							this.firsts.get(v).addAll(this.firsts.get(list.get(i)));
						}
						if (size < this.firsts.get(v).size()) {
							flag = true;
						}
					}
				}
			}
		}
	}

	/**
	 * 计算给定项目的闭包
	 * 
	 * @param item 给定的闭包
	 * @return 对应的项目集
	 */
	private Set<LR1Item> computeClosure(LR1Item item) {
		
		//	待处理的项目
		Queue<LR1Item> queue = new LinkedList<LR1Item>();
		queue.offer(item);
		Set<LR1Item> set = new HashSet<LR1Item>();
		set.add(item);
		
		//	移动初始项目的圆点后会得到新的项目，新的项目也需要处理。将新的项目放入队列中
		while (!queue.isEmpty()) {
			LR1Item item2 = queue.poll();
			if (item2.getRight().size() == item2.getDotPosition()) {	//	跳过归约项目
				continue;
			}
			List<String> beta = item2.getBeta();
			Set<String> suc = firstOfBeta(beta, item2.getSuccessor());
			String v = item2.getRight().get(item2.getDotPosition());
			
			//	找到对应的推导式
			for (int i = 0; i < this.grammers.size(); i++) {
				if (this.grammers.get(i).getLeft().equals(v)) {
					LR1Item newitem = new LR1Item(v, this.grammers.get(i).getRight(), 0, suc);
					if (newitem.getRight().get(0).equals("epsilon")) {	//	处理空产生式
						newitem.setDotPosition(1);
					}
					LR1Item tmpItem = null;
					
					//	合并项目集中除了后继符均相同的项目
					for (LR1Item tmp : set) {
						if (tmp.same(newitem)) {
							tmpItem = tmp;
							break;
						}
					}
					if (tmpItem != null) {
						set.remove(tmpItem);
						if (tmpItem.getSuccessor().addAll(newitem.getSuccessor())) {
							queue.offer(tmpItem);
						}
						set.add(tmpItem);
					} else {
						set.add(newitem);
						queue.offer(newitem);
					}
				}
			}
		}
		return set;
	}

	/**
	 * 计算一个串的 FIRST 集，这里就是为了计算项目的后继符
	 * @param beta 符号串
	 * @param successor 继承的后继符
	 * @return 后继符集合
	 */
	private Set<String> firstOfBeta(List<String> beta, Set<String> successor) {
		Set<String> re = new HashSet<String>();
		if (beta.isEmpty()) {
			re.addAll(successor);
		}
		
		//	依次考察 beta 的每个符号
		for (int i = 0; i < beta.size(); i++) {
			if (this.terminals.contains(beta.get(i))) {
				re.add(beta.get(i));
				break;
			} else if (!this.firsts.get(beta.get(i)).contains("epsilon")) {
				re.addAll(this.firsts.get(beta.get(i)));
				break;
			} else {
				re.addAll(this.firsts.get(beta.get(i)));
				if (i == beta.size() - 1) {
					re.remove("epsilon");
					re.addAll(successor);
				}
			}
		}
		return re;
	}

	/**
	 * 计算项目集族
	 */
	private void computeLRfamily() {
		LR1Item start = this.grammers.get(0);	//	开始项目
		Set<String> tmp = new HashSet<String>();
		tmp.add("#");
		start = new LR1Item(start.getLeft(), start.getRight(), 0, tmp);
		Set<LR1Item> startClosure = computeClosure(start);
		Queue<Set<LR1Item>> queue = new LinkedList<Set<LR1Item>>();
		queue.offer(startClosure);
		this.LRfamily.add(startClosure);
		while (!queue.isEmpty()) {
			Set<LR1Item> current = queue.poll();
			Map<String, Set<LR1Item>> nexts = nextCloures(current);
			for (Set<LR1Item> s : nexts.values()) {
				if (!this.LRfamily.contains(s)) {
					this.LRfamily.add(s);
					queue.offer(s);
				}
			}
		}
	}

	private Map<String, Set<LR1Item>> nextCloures(Set<LR1Item> current) {
		Map<String, Set<LR1Item>> nexts = new HashMap<String, Set<LR1Item>>();
		for (LR1Item item : current) {
			if (item.getRight().size() == item.getDotPosition()) {
				continue;
			}
			Set<LR1Item> newClosure = computeClosure(
					new LR1Item(item.getLeft(), item.getRight(), item.getDotPosition() + 1, item.getSuccessor()));
			if (nexts.get(item.getRight().get(item.getDotPosition())) != null) {
				nexts.get(item.getRight().get(item.getDotPosition())).addAll(newClosure);
			} else {
				nexts.put(item.getRight().get(item.getDotPosition()), newClosure);
			}
		}
		return nexts;
	}

	public List<String> getErrors() {
		return errors;
	}

	public List<LR1Item> getRules() {
		return rules;
	}
	
	public List<String> getTerminals() {
		return terminals;
	}
	
	public List<String> getVaribles() {
		return varibles;
	}
	
	public int[][] getActionTable() {
		return actionTable;
	}
	
	public int[][] getGotoTable() {
		return gotoTable;
	}
	
	public int numOfState() {
		return LRfamily.size();
	}
	public static void main(String[] args) {
		GrammerParser parser = new GrammerParser();
//		parser.buildAnalysisTable("SS->S\n" + "S->L = R\n" + "S->R\n" + "L->* R\n" + "L->id\n" + "R->L");
//		parser.buildAnalysisTable("SS->S\nS->A\nA->B A\nA->epsilon\nB->a B\nB->b");
		parser.buildAnalysisTable("");
	}
}
