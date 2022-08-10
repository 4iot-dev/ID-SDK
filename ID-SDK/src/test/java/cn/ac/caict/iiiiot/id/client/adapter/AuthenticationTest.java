package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.utils.KeyConverter;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuthenticationTest {

    @Test
    public void verifyIdentifierUser() throws Exception {
        String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC0gT2YeLElI3XtUrxUImvfofx8\n" +
                "QrsdEzfiV6WMQVSYTi+fsbBj2PckU3loy6DxdCIn0lQ0Pi0/3DJL5fdf1kSf0xB42VQRIgMlBCOZ\n" +
                "MNN+D2QdDU+jf0FMHyCVceh/0S3NDbdH3ynbADvZnSV2Y8SVnHL7vKU4TTjF4QoR4XVcttQ1Bgsn\n" +
                "euYB4aN9KCpWT5VrZaZqSswGJl0YlOpA0EU/rKisQ7XnBxNNCa85NudEPrxWVqno6xA6Q7ARJBXE\n" +
                "e5v/KX/O8duTQc5PlYZZ+PuI+32GYFw84vioIVKJABcUQWPRzj8k4ix8r0pIeQ5i+rv0dzQ2mr0t\n" +
                "pruPWZ5s15aNAgMBAAECggEAZGc3QeZwcr4EzJum0g3Lfzt1XBEqT4PvI+Go3hlA2u8yoluOSBvZ\n" +
                "bMVZ1SbfQS9eCFSALgcf2FO9TmZ+cTqYNWaL1QzeLCGFKkPhIUb9fTNGdrp+v+z6/0KZN0eDEgXi\n" +
                "uhqVBk9l0sGQiP1WZ0IbiTTa6JgINwcNO1Rv635Db+v4gQpYePnZYqwSu7x2fuEI33Taorrfucv8\n" +
                "M7Zi49fSWq81tx+JN3otFHkyMD11eAYV/aZjwpwscpA8ruYlzzqYp7aMxxgQ4AXJYu71DYp9Hgz2\n" +
                "aQM2dXlAKgXCmrRtJq9DrPw4CMX7NHTAQjlvST+vu/Ja8H+23sRy/xH5k+DaKQKBgQD7tsrjle35\n" +
                "aPyGIDej+VCLOH8dFrIZeh9U0bV+/TL2R1mC/P2i9WVFHNAviNB+qU8DU6tfLWj8dpiZx6BKR4Qa\n" +
                "h4v7Oivjww/ru/5WiClfslsHrOMjiWy/N5ZSk6NQnHAsJncVkT4XIErsc1LaZnccOMwfXW1xVcCN\n" +
                "maAt1xl9HwKBgQC3lA0apt5iG+IkLR8jj4UPVfyy7llLb+zdx0wGh/PziGqgt6ZdY8t8w66hWFb5\n" +
                "n96POmW7DuFaCnNnTiwFpz1d6rwI0XhlrGOmp7NjstvZ+Fk4ap6qpOKSIB8LeN+bOrtx7TZ1UKG1\n" +
                "wLsFD40H2hUxymalwnm4Vp5pJjccW4XK0wKBgADDqlQMlX9nYTTrDiAyVptFnaUx93J6W3P/ewSa\n" +
                "sjfrOYtbR03iXt9Z2gv6518rFnFVJLUSRzpVBduZrpPrKayG8tbdc1qqsfauSHRsz2tZ+ErKrJnk\n" +
                "Be+CtLMlfZ52CyUnLL9lBII/d9rF8t905jGwvnXt67InZ4FGkSTyfUJTAoGAUKrP70wwIDBceMUT\n" +
                "D887Cvgf6Ihv2IRAM1wl/iCzg+oH4MOSaSs2+YYLMH7fCSXE6G8i0MXDJIu/Fj/1fC52+tPw+HcD\n" +
                "Tront82tODwZ+3fzzKSdQCLgJJHU0ne02kM+ptszuO1LgdBE3f5tXGvqMEzeOixwzB3T0iSmxuE4\n" +
                "s10CgYBt6o1BD86oapY92dMMfvYiURskar606pCH7m0y8aQfRQmrM3z5+YWnpbEMbNJsr8uS558X\n" +
                "whhWREGhoDnaowcJ9A8tenDUgDNf98pDKpeudMLG+32YaHPtxWOBTywLVpbh4I1sykLJnwAq+Lve\n" +
                "KMLphT7U+sCwvAKmOQ6vGLhXsg==\n" +
                "-----END PRIVATE KEY-----";
        Authentication authentication = new Authentication();
        Boolean result = authentication.verifyIdentifierUser("300:88.300.15907541011/user002", KeyConverter.fromPkcs8Pem(privateKeyPem,null));
        System.out.println(result);
    }
}