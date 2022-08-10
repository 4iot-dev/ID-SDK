package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.core.IdentifierResolveEngine;
import cn.ac.caict.iiiiot.id.client.log.IDLog;
import cn.hutool.core.io.IoUtil;
import com.google.gson.Gson;
import org.apache.commons.logging.Log;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * todo: 不应该通过单例方式来确定配置,应该在初始化传过来,如果没有传则可以使用默认的配置
 * 防止我们复杂环境的调用需要同时有不同的配置
 */
public class Configuration {

    private Log logger = IDLog.getLogger(Configuration.class);

    private static Configuration configuration = new Configuration();

    public static Configuration getInstance() {
        return configuration;
    }

    private Configuration() {
        config = new HashMap<>();
    }

    public Configuration(Map<String, Object> config) {
        this.config = config;
    }

    private Map<String, Object> config = null;

    public Map<String,Object> loadConfig() {
        logger.info("begin----loadConfig()---");
        String path = System.getProperty("user.dir");
        File fConfig = new File(path, ".id-sdk/config.json");

        InputStream is = null;
        BufferedReader br = null;
        Reader input = null;
        try {
            if (fConfig.exists()) {
                logger.debug("配置文件路径：" + fConfig.getAbsolutePath());
                is = new BufferedInputStream(new FileInputStream(fConfig));
            } else {
                logger.debug("加载默认配置文件");
                is = IdentifierResolveEngine.class.getResourceAsStream("/conf/config.json");
                if (is == null) {
                    logger.error("读取ID-SDK的配置文件失败");
                }
            }
            input = new InputStreamReader(is);
            br = new BufferedReader(input);
            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s.trim());
            }
            Gson gson = new Gson();
            Map<String,Object> configMap = gson.fromJson(sb.toString(), Map.class);
            logger.debug("配置信息：" + config.toString());
            logger.info("end----loadConfig()---");

            return configMap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                IoUtil.close(input);
            }
            if (br != null) {
                IoUtil.close(br);
            }
            if (is != null) {
                IoUtil.close(is);
            }
        }
        return null;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
}
