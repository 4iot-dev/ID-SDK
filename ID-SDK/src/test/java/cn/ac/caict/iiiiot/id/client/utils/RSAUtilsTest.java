package cn.ac.caict.iiiiot.id.client.utils;

import org.junit.Ignore;
import org.junit.Test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.Assert.*;


public class RSAUtilsTest {

    @Ignore
    @Test
    public void test() throws Exception {

        KeyPair keyPair = RSAUtils.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String publicKeyPem = KeyConverter.toX509Pem(publicKey);
        System.out.println(publicKeyPem);

//        publicKeyPem = publicKeyPem.replaceAll("\r\n", "\\\\r\\\\n");
//
//        System.out.println(publicKeyPem);

        String prvPem = KeyConverter.toPkcs8UnencryptedPem(privateKey);
        System.out.println(prvPem);

    }

}