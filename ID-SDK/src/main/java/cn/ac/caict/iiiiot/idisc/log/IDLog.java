package cn.ac.caict.iiiiot.idisc.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IDLog {
	
	public static Log getLogger(Class clazz){
		return LogFactory.getLog(clazz);
	}
}
