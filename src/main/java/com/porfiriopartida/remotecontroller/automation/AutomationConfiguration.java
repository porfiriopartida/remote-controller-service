package com.porfiriopartida.remotecontroller.automation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.yaml.snakeyaml.Yaml;

//import javax.swing.plaf.synth.SynthParser;
import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AutomationConfiguration {
    private static final Logger logger = LogManager.getLogger(AutomationConfiguration.class);

    @Resource
    private Environment env;

    @Value("${automation.configuration}")
    private String configurationFile;

    private Map<String, Object> context;

//    public AutomationConfiguration(){
//        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
//        logger.info("Creating config bean...");
//        try {
//            loadContext();
//        } catch (FileNotFoundException e) {
//            logger.error("Couldn't load from yml file automatically.", e);
//        }
//    }


    public void loadContext() throws FileNotFoundException{
        InputStream inputstream = new FileInputStream(configurationFile);
        loadContext(inputstream);
    }
    public void loadContext(InputStream inputStream){
        Yaml yaml = new Yaml();
        this.context = yaml.load(inputStream);
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public Object getProperty(String key) {
        if(this.context == null){
            logger.warn("No context, creating from automation.configuration");
            try {
                loadContext();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        String[] keys = key.split("\\.");
        HashMap<String, Object> currentCtx = (HashMap<String, Object>) this.context;
        for (int i = 0; i < keys.length ; i++) {
            if(i == keys.length - 1){
                return currentCtx.get(keys[i]);
            }

            currentCtx = (HashMap<String, Object>) currentCtx.get(keys[i]);
        }

        return null;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public static void main(String[] args) throws Exception {
        InputStream fins = new FileInputStream("D:\\Java\\remote-controller\\remote-controller\\src\\test\\resources\\application.yml");
        AutomationConfiguration configuration = new AutomationConfiguration();
        configuration.loadContext(fins);
        List<String> list = (List<String>) configuration.getProperty("automation.my_automated_application.test_cases.using_click_command.steps");
        System.out.println(list);
        //this.context.get("automation").get("my_automated_application").get("test_cases").get("using_click_command").get("steps")
        //Assert list is:
//        - click open_directory.png
//                - click application_exe.png
//                - random_popup_close_button.png

    }
}
