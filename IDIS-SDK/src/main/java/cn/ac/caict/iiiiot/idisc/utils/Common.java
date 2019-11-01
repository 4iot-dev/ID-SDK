package cn.ac.caict.iiiiot.idisc.utils;

import java.util.regex.Pattern;

public abstract class Common {
	public static final Pattern IPV4_REGEX = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
	
	public static final Pattern IPV6_STD_REGEX = Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
	
	public static final Pattern IPV6_COMPRESS_REGEX = Pattern.compile("^((?:[0-9A-Fa-f]{1,4}(:[0-9A-Fa-f]{1,4})*)?)::((?:([0-9A-Fa-f]{1,4}:)*[0-9A-Fa-f]{1,4})?)$");
	
	public static final Pattern PORT_REGEX = Pattern.compile("^([1-9]|[1-9]\\d{1,3}|[1-6][0-5][0-5][0-3][0-5])$");
	
	public static final int RESOLUTION_RESULT_UNKNOWN = 0;
	
	public static final int RESOLUTION_RESULT_TRUSTY = 1;
	
	public static final int RESOLUTION_RESULT_UNTRUSTY = 2;
	
	public static final int SUITABLE_PRIME_NUMBER = 31;
		
	public static final String TEXT_ENCODING = "UTF8";

	public static final int FOUR_SIZE = 4;
	
	public static final int TWO_SIZE = 2;

	public static final int FLAG_1ST_MSG_AUTH = 0x80000000;
	
	public static final int FLAG_2ND_MSG_CERT = 0x40000000;
	
	public static final int FLAG_3RD_MSG_ENCR = 0x20000000;
	
	public static final int FLAG_4TH_MSG_RECU = 0x10000000;
	
	public static final int FLAG_5TH_MSG_CACR = 0x08000000;
	
	public static final int FLAG_6TH_MSG_CONT = 0x04000000;
	
	public static final int FLAG_7TH_MSG_KPAL = 0x02000000;
	
	public static final int FLAG_8TH_MSG_PUBL = 0x01000000;
	
	public static final int FLAG_9TH_MSG_RRDG = 0x00800000;
	
	public static final int FLAG_10TH_MSG_OVRW = 0x00400000;
	
	public static final int FLAG_11TH_MSG_MINT = 0x00200000;
	
	public static final int FLAG_12TH_MSG_DNRF = 0x00100000;
	// opflag字段的第31位标志位确定是否对结果结果进行可信解析认证，1为需要对解析结果做认证，0为不需要对解析结果做可信认证
	public static final int FLAG_31ST_MSG_TRUSTED_QUERY = 0x00000002;
	// 通过Header的opflag字段的第32位标志可信解析是否成功，1代表可信认证成功，0代表可信认证失败
	public static final int FLAG_32ND_MSG_TRUSTED_RESULT = 0x00000001;

	public static final byte ENV_FLAG_COMPRESSED = (byte) 0b10000000;
	
	public static final byte ENV_FLAG_ENCRYPTED = (byte) 0b01000000;
	
	public static final byte ENV_FLAG_TRUNCATED = (byte) 0b00100000;

	public static final byte PERM_ADMIN_READ = 0x8;
	
	public static final byte PERM_ADMIN_WRITE = 0x4;
	
	public static final byte PERM_PUBLIC_READ = 0x2;
	
	public static final byte PERM_PUBLIC_WRITE = 0x1;

	public static final String MSG_INVALID_ARRAY_SIZE = "消息格式错误，无效数组长度！";

	public static final byte ST_NONE = 0;
	
	public static final byte ST_ADMIN = 1;
	
	public static final byte ST_RESOLUTION = 2;
	
	public static final byte ST_RESOLUTION_AND_ADMIN = 3;

	public static final byte BLANK_IDENTIFIER[] = Util.encodeString("/");
	
	public static final byte GLOBAL_NA_PREFIX[] = Util.encodeString("0.");
	
	public static final byte GLOBAL_NA[] = Util.encodeString("0/");
	
	public static final byte NA_IDENTIFIRE_PREFIX[] = Util.encodeString("0.NA/");
	
	public static final String HS_SITE = "HS_SITE";

	public static final byte TYPE_SITE[] = Util.encodeString(HS_SITE);
	
	public static final String HS_SITE_PREFIX = "HS_SITE.PREFIX";
	
	public static final byte TYPE_PREFIX_SITE[] = Util.encodeString(HS_SITE_PREFIX);
	
	public static final String HS_SERV = "HS_SERV";
	
	public static final byte TYPE_SERV[] = Util.encodeString(HS_SERV);
	
	public static final String HS_PUBKEY = "HS_PUBKEY";
	
	public static final byte TYPE_PUBLIC_KEY[] = Util.encodeString(HS_PUBKEY);
	
	public static final String HS_ADMIN = "HS_ADMIN";
	
	public static final byte TYPE_ADMIN[] = Util.encodeString(HS_ADMIN);
	
	public static final String HS_VLIST = "HS_VLIST";
	
	public static final byte TYPE_ADMIN_GROUP[] = Util.encodeString(HS_VLIST);
	
	public static final String HS_SIGNATURE = "HS_SIGNATURE";
	
	public static final byte TYPE_SIGNATURE[] = Util.encodeString(HS_SIGNATURE);
	
	public static final String HS_CERT = "HS_CERT";
	
	public static final byte TYPE_CERT[] = Util.encodeString(HS_CERT);
	
	public static final String URL = "URL";
	
	public static final String EMAIL = "EMAIL";

	public static final byte HASH_ALG_MD5[] = Util.encodeString("MD5");
	
	public static final byte HASH_ALG_SHA1[] = Util.encodeString("SHA1");
	
	public static final byte HASH_ALG_SHA1_ALTERNATE[] = Util.encodeString("SHA-1");
	
	public static final byte HASH_ALG_SHA256[] = Util.encodeString("SHA-256");
	
	public static final byte HASH_ALG_SHA256_ALTERNATE[] = Util.encodeString("SHA256");

	public static final byte HASH_CODE_MD5_OLD_FORMAT = (byte) 0;
	
	public static final byte HASH_CODE_MD5 = (byte) 1;
	
	public static final byte HASH_CODE_SHA1 = (byte) 2;
	
	public static final byte HASH_CODE_SHA256 = (byte) 3;

	public static final int ENCRYPT_NONE = 1;
	
	public static final int ENCRYPT_DES_CBC_PKCS5 = 2;
	
	public static final int ENCRYPT_PBKDF2_DESEDE_CBC_PKCS5 = 3;
	
	public static final int ENCRYPT_PBKDF2_AES_CBC_PKCS5 = 4; 
	
	public static final int MAX_ENCRYPT = 9;

	public static final byte KEY_ENCODING_DSA_PRIVATE[] = Util.encodeString("DSA_PRIV_KEY");

	public static final byte KEY_ENCODING_DSA_PUBLIC[] = Util.encodeString("DSA_PUB_KEY");

	public static final byte KEY_ENCODING_DH_PUBLIC[] = Util.encodeString("DH_PUB_KEY");

	public static final byte KEY_ENCODING_RSA_PRIVATE[] = Util.encodeString("RSA_PRIV_KEY");

	public static final byte KEY_ENCODING_RSACRT_PRIVATE[] = Util.encodeString("RSA_PRIVCRT_KEY");

	public static final byte KEY_ENCODING_RSA_PUBLIC[] = Util.encodeString("RSA_PUB_KEY");
	
	public static final byte[] CREDENTIAL_TYPE_SIGNED = Util.encodeString("HS_SIGNED"); 

	public static final int SITE_RECORD_FORMAT_VERSION = 1;
	// 质询nonce的size
	public static final int CHALLENGE_NONCE_SIZE = 16;

	public static final int MD5_DIGEST_SIZE = 16;

	public static final int SHA1_DIGEST_SIZE = 20;

	public static final int SHA256_DIGEST_SIZE = 32;

	public static final int MESSAGE_HEADER_SIZE = 24;

	public static final int MESSAGE_ENVELOPE_SIZE = 20;

	public static final int MAX_MESSAGE_LENGTH = 16672500;

	public static final int MAX_UDP_PACKET_SIZE = 512;

	public static final int MAX_UDP_DATA_SIZE = MAX_UDP_PACKET_SIZE - MESSAGE_ENVELOPE_SIZE;

	public static final int MAX_IDENTIFIER_VALUES = 2048;

	public static final int MAX_IDENTIFIER_LENGTH = 2048;

	public static final int MAX_ARRAY_SIZE = 16672359;

	public static final int IP_ADDRESS_SIZE_SIXTEEN = 16;
}
