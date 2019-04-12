package exception;

/**
 * 文本信息过长
 * 
 * @author zz
 *
 */
public class TextOverlargeException extends Exception{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7379215414014141803L;
	
	public TextOverlargeException(String str) {
		super(str);
	}
	
}
