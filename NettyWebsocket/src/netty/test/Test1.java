package netty.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test1 {
//	static { System.setProperty("logback.configurationFile", "classpath/logback.xml");}
//	  private final Logger LOG = LoggerFactory.getLogger(Main.class);

	  private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static void main(String[] args) {
		new Test1().test();
	}
	void test() {
		String str = System.getProperty("logback.configurationFile");
		logger.error("HelloWorld:" + str);
	}
}