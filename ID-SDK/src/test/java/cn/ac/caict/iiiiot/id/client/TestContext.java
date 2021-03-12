package cn.ac.caict.iiiiot.id.client;

public class TestContext {

    private static TestContext testContext;
    private String serverIp = "192.168.150.37";
    private int serverPort = 5647;

    private String prefix = "88.300.15907541011";

    private TestContext() {

    }

    public static TestContext getInstance() {
        if(testContext ==null){
            testContext = new TestContext();
        }
        return testContext;
    }

    public String getServerIp() {
        return serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }
}
