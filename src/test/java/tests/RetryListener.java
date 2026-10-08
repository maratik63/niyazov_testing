package tests;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Подключает {@link BrowserCrashRetryAnalyzer} ко всем тестам сразу,
 * чтобы не указывать retryAnalyzer в каждой аннотации {@code @Test}.
 * Регистрируется в testng.xml.
 */
public class RetryListener implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass,
                          Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(BrowserCrashRetryAnalyzer.class);
    }
}
