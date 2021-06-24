package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import com.sun.tools.javac.comp.Check;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.*;

public class MonitorTest {
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Test
    public void test() {
        try {
            Long l = monitor("88.111.1",5);
            System.out.println(l);
        } catch (IdentifierException e) {
            e.printStackTrace();
        } catch (IdentifierAdapterException e) {
            e.printStackTrace();
        }

    }

    private Long monitor(String prefix,int timeout) throws IdentifierException, IdentifierAdapterException {

        IDAdapter idAdapter = IDAdapterFactory.cachedInstance();
        PrefixSiteInfo prefixSiteInfo = idAdapter.resolveSiteByProxy(prefix);

        Long result = -1l;
        FutureTask<Long> futureTask = new FutureTask<>(new Callable<Long>() {

            @Override
            public Long call() throws Exception {
                return doMonitor(prefixSiteInfo);
            }
        });

        executorService.execute(futureTask);
        try {
            result = futureTask.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            futureTask.cancel(true);
        }
        return result;
    }

    private Long doMonitor(PrefixSiteInfo prefixSiteInfo){
        long begin = System.currentTimeMillis();
        IDAdapter adapter = IDAdapterFactory.newInstance(prefixSiteInfo.getIp(), prefixSiteInfo.getPort());
        try {
            adapter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis() - begin;
    }
}
