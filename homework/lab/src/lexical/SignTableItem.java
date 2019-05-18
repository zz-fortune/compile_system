package lexical;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SignTableItem {

	private String name;	//	关键字
	private String category;	//	类别
	private String type;	//	对应的变量类型
	private int addr;	//	偏移量，也就是地址

	public SignTableItem(String name, String category, int addr) {
		this.name = name;
		this.addr = addr;
		if (category.equals("bool")) {
			this.type = "bool";
			this.category="ID";
		} else if (category.equals("int")) {
			this.type = "int";
			this.category="ID";
		} else if (category.equals("float")) {
			this.type = "float";
			this.category="ID";
		}else if (category.equals("consti")) {
			this.type = "int";
			this.category="num";
		}else if (category.equals("constf")) {
			this.type = "float";
			this.category="num";
		}
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getCategory() {
		return category;
	}

	public int getAddr() {
		return addr;
	}
	
	public StringProperty nameProperty() {
		return new SimpleStringProperty(name);
	}
	
	public StringProperty categoryProperty() {
		return new SimpleStringProperty(category);
	}
	
	public StringProperty typeProperty() {
		return new SimpleStringProperty(type);
	}
	
	public IntegerProperty offsetProperty() {
		return new SimpleIntegerProperty(addr);
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 == null) {
			return false;
		}

		if (!(arg0 instanceof SignTableItem)) {
			return false;
		}
		SignTableItem that = (SignTableItem) arg0;
		if (!this.name.equals(that.name)) {
			return false;
		}
		if (!this.category.equals(that.category)) {
			return false;
		}
		if (!this.type.equals(that.type)) {
			return false;
		}
		if (this.addr != that.addr) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode() + this.category.hashCode() + this.type.hashCode() + this.addr;
	}

	@Override
	public String toString() {
		return "["+this.name + "\t" + this.category + "\t" + this.type + "\t" + this.addr+"]";
	}

}
