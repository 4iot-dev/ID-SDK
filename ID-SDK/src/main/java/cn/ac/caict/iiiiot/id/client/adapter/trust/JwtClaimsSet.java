package cn.ac.caict.iiiiot.id.client.adapter.trust;

public class JwtClaimsSet {
    public String iss;
    public String sub;
    public Long exp;
    public Long nbf;
    public Long iat;

    public boolean isDateInRange(long nowInSeconds) {
        if (exp != null && nowInSeconds > exp) return false;
        if (nbf != null && nowInSeconds < nbf) return false;
        return true;
    }

    public boolean isSelfIssued() {
        return sub != null && sub.equals(iss);
    }
}