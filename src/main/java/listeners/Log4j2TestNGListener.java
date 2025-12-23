package listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Ensures log directory exists and (re)configures Log4j2 using classpath log4j2.xml.
 */
public class Log4j2TestNGListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(Log4j2TestNGListener.class);
    private static final String DEFAULT_LOG_DIR = "test-output/logs";

    @Override
    public void onStart(ITestContext context) {
        try {
            String logDir = System.getProperty("LOG_DIR", DEFAULT_LOG_DIR);
            Path logPath = Paths.get(logDir);
            Files.createDirectories(logPath);

            // Ensure the property matches what log4j2.xml expects
            System.setProperty("LOG_DIR", logDir);


            URL cfg = Thread.currentThread().getContextClassLoader().getResource("log4j2.xml");
            if (cfg != null) {
                Configurator.initialize(null, cfg.toString());
            } else {
                Configurator.reconfigure();
            }

            logger.info("TestNG suite started: " + context.getName() + " — logDir=" + logDir);
        } catch (Exception e) {
            System.err.println("Failed to prepare Log4j2 logging: " + e.getMessage());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("TestNG suite finished: " + context.getName());

    }
}
