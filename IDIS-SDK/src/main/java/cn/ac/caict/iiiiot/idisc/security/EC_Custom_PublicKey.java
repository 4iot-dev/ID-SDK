package cn.ac.caict.iiiiot.idisc.security;

import java.security.PublicKey;

public class EC_Custom_PublicKey implements PublicKey{
	public byte[] x;
	public byte[] y;
	@Override
	public String getAlgorithm() {
		return "EC";
	}

	@Override
	public byte[] getEncoded() {
		return null;
	}

	@Override
	public String getFormat() {
		return null;
	}

}
