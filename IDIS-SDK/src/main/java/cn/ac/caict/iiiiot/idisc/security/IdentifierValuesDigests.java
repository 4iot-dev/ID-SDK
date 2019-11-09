package cn.ac.caict.iiiiot.idisc.security;

import java.util.List;

public class IdentifierValuesDigests {
	// 摘要算法
	public String alg;
	// 各标识值摘要列表
	public List<IdentifierValuesDigest> digests;
	
	public IdentifierValuesDigests(){}
	
	public IdentifierValuesDigests(String alg, List<IdentifierValuesDigest> digests){
		this.alg = alg;
		this.digests = digests;
	}
}
