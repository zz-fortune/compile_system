package semantics;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CodeItem {
	private int offset;	//	代码的首地址
	private String actionType = null;	//	操作类型
	private String arg1 = null;	//	参数一
	private String arg2 = null;	//	参数二
	private String result = null;	//	结果

	public CodeItem(int offset, String actionType, String arg1, String arg2, String result) {
		this.offset = offset;
		this.actionType = actionType;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.result = result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getResult() {
		return result;
	}
	
	public String getActionType() {
		return actionType;
	}
	
	public IntegerProperty offsetProperty() {
		return new SimpleIntegerProperty(offset);
	}
	
	public StringProperty actionProperty() {
		return new SimpleStringProperty(actionType);
	}
	
	public StringProperty arg1Property() {
		return new SimpleStringProperty(arg1);
	}
	
	public StringProperty arg2Property() {
		return new SimpleStringProperty(arg2);
	}
	
	public StringProperty resultProperty() {
		return new SimpleStringProperty(result);
	}
	
	public StringProperty codeProperty() {
		StringBuffer buffer=new StringBuffer();
		if (actionType.contains("j")) {
			if (arg1==null) {
				buffer.append("goto "+result);
			}else {
				buffer.append("if "+arg1+" is false, goto "+result);
			}
		}else if (actionType.equals("=")) {
			buffer.append(result+" = "+arg1);
		}else {
			buffer.append(result+" = "+arg1+" "+actionType+" "+arg2);
		}
		return new SimpleStringProperty(buffer.toString());
	}
	
	@Override
	public String toString() {
		StringBuffer buffer=new StringBuffer();
		buffer.append(offset+": ("+this.actionType);
		if (this.arg1!=null) {
			buffer.append(", "+arg1);
		}else {
			buffer.append(", -");
		}
		if (arg2!=null) {
			buffer.append(", "+arg2);
		}else {
			buffer.append(", -");
		}
		buffer.append(", "+result+")");
		return buffer.toString();
	}

}
