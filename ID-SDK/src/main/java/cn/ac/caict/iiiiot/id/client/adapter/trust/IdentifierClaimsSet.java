package cn.ac.caict.iiiiot.id.client.adapter.trust;

import cn.ac.caict.iiiiot.id.client.security.Permission;

import java.security.PublicKey;
import java.util.List;

public class IdentifierClaimsSet extends JwtClaimsSet {
    public List<Permission> perms;
    public DigestedHandleValues digests;
    public List<String> chain; //first element authorizes the issuer of this claims set. Second element authorizes the issuer of that authorization...
    public PublicKey publicKey;
    public String content; //Optional string to sign.
}