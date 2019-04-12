package lexical;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import exception.TextOverlargeException;

/**
 * 这是词法分析器的缓存区
 * 
 * @author zz
 *
 */
public class LexicalBuffer {

	// 缓存区的大小
	private final int BUF_SIZE = 1024 * 1024;

//	public static final int TEXT_MODE = 1;
//	public static final int FILE_MODE = 2;

	private char[] buf = new char[BUF_SIZE]; // 缓存区数组
	private int rawNum = 1; // 当前处理的行号
	private int colNum = 1; // 当前处理的列号
	private int current = 0; // 当前处理到的字符位置
	private int len; // 缓存数组中有效数据的长度
//	private int mode;
	private BufferedReader reader = null; // 读取文件的reader
	private boolean _available; // 标记文件是否读完

	/**
	 * 构造器
	 * 
	 * @param str 待分析的源代码字符串
	 * @throws TextOverlargeException
	 * @throws FileNotFoundException
	 */
	public LexicalBuffer(String str) throws TextOverlargeException, FileNotFoundException {
		initTextMode(str);
	}

	/**
	 * 构造器
	 * 
	 * @param file 源代码文件
	 */
	public LexicalBuffer(File file) {
		initFileMode(file);
	}

	/**
	 * 将传入的源代码字符串放入缓存区数组中。并设置相关的变量
	 * 
	 * @param str 源代码字符串
	 * @throws TextOverlargeException
	 */
	private void initTextMode(String str) throws TextOverlargeException {
		this._available = false; // 设置为不可再从文件中读
		len = str.length();
		if (this.len > BUF_SIZE) {
			throw new TextOverlargeException("æ–‡æœ¬å†…å®¹è¿‡å¤š");
		}
		System.arraycopy(str.toCharArray(), 0, buf, 0, len);
	}

	private void initFileMode(File file) {
		this._available = true; // 设置为还可以从文件中读
		try {
			this.reader = new BufferedReader(new FileReader(file));
			this.len = reader.read(this.buf, 0, BUF_SIZE);

			// 如果已经读完文件中的数据，则设置为不可从文件中读
			if (len < BUF_SIZE) {
				this._available = false;
			}
			this.current = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取下一个缓存区中的下一个字符。缓存区会自动去掉源文件中的换行符
	 * 
	 * @return 缓存区中的下一个字符
	 */
	public char next() {
		char tmp;

		// 当前缓存区数组中还有有效数据
		if (this.current < this.len) {
			tmp = buf[current];
		} else if (_available) { // 缓存区数组无有效数据，从文件中读入
			updateBuf();
			tmp = buf[current];
		} else { // 处理完所有字符，输出结束字符
			tmp = '#';
		}
		this.colNum++;
		this.current++;

		// 遇到 '\r' 符号，直接跳过
		if (tmp == '\r') {
			tmp = this.next();
		}
		// 若是遇到回车符，直接跳过
		if (tmp == '\n') {
			this.rawNum++;
			this.colNum = 1;
			return this.next();
		} else {
			return tmp;
		}

	}

	/**
	 * 回退一个字符。在某些情况下可能会出错
	 */
	public void rollback() {
		if (this.current > 0) {
			this.current--;
		}
	}

	/**
	 * 判断是否已经处理完所有的字符
	 * 
	 * @return {@code true} 当且仅当还有未处理字符
	 */
	public boolean available() {
		return this._available || this.current < this.len;
	}

	/**
	 * 获取当前字符的列号
	 * 
	 * @return
	 */
	public int getColNum() {
		return colNum - 1;
	}

	/**
	 * 获取当前字符的行号
	 * 
	 * @return
	 */
	public int getRawNum() {
		return rawNum;
	}

	/**
	 * 更新缓存区数组中的数据
	 */
	private void updateBuf() {
		try {
			this.len = reader.read(buf, 0, BUF_SIZE);
			if (this.len < BUF_SIZE) {
				this._available = false;
			}
			this.current = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
