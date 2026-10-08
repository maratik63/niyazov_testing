package utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Формирует {@code target/allure-results/environment.properties} — блок Environment в отчёте Allure.
 * <p>
 * Статические данные (ОС, JDK, версии библиотек) известны сразу, а список браузеров пополняется
 * по мере инициализации драйверов в {@code BaseTest}, поэтому файл перезаписывается при каждой
 * регистрации (запись синхронизирована — тесты могут идти параллельно).
 */
public final class AllureEnvironmentWriter {

    private static final Path RESULTS_DIR = Paths.get("target", "allure-results");
    private static final Set<String> BROWSERS = ConcurrentHashMap.newKeySet();

    private AllureEnvironmentWriter() {
    }

    /** Регистрирует браузер текущего теста и обновляет environment.properties. */
    public static synchronized void registerBrowser(String browser) {
        BROWSERS.add(browser);
        write();
    }

    private static void write() {
        try {
            Files.createDirectories(RESULTS_DIR);
            StringBuilder sb = new StringBuilder();
            sb.append("os.name=").append(System.getProperty("os.name")).append('\n');
            sb.append("os.version=").append(System.getProperty("os.version")).append('\n');
            sb.append("java.version=").append(System.getProperty("java.version")).append('\n');
            sb.append("selenium=4.49.0\n");
            sb.append("testng=7.12.0\n");
            sb.append("allure=2.29.1\n");
            sb.append("driver.manager=WebDriverManager 6.3.4\n");
            List<String> browsers = BROWSERS.stream().sorted()
                    .map(b -> Character.toUpperCase(b.charAt(0)) + b.substring(1))
                    .toList();
            sb.append("browser=").append(String.join("; ", browsers)).append('\n');

            Path file = RESULTS_DIR.resolve("environment.properties");
            try (OutputStream out = Files.newOutputStream(file)) {
                out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            // Окружение — вспомогательная информация: падать из-за него тест не должен
            System.err.println("Не удалось записать environment.properties: " + e.getMessage());
        }
    }
}
