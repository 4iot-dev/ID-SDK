package cn.ac.caict.iiiiot.id.client.security;

public class Permission {
    public static final String EVERYTHING = "everything";
    public static final String THIS_IDENTIFER = "thisHandle";
    public static final String DERIVED_PREFIXES = "derivedPrefixes";
    public static final String IDENTIFIERS_UNDER_THIS_PREFIX = "handlesUnderThisPrefix";
        
    public String identifier;
    public String perm;    
        
    public Permission() { }
        
    public Permission(String identifier, String permission) {
        this.identifier = identifier;
        this.perm = permission;
    }
}