package cn.ac.caict.iiiiot.idisc.security;

public class IdentifierValuesDigest {
	// 标识值对应的索引值
	public int index;
	// 标识值的摘要
	public String digest;
	
	public IdentifierValuesDigest(){}
	
	public IdentifierValuesDigest(int index, String digest){
		this.index = index;
		this.digest = digest;
	}
}
