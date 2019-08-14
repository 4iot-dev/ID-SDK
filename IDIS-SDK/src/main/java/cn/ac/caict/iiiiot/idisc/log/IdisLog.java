package cn.ac.caict.iiiiot.idisc.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IdisLog {
	
	public static Log getLogger(Class clazz){
		return LogFactory.getLog(clazz);
	}
}
