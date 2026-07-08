package contrib.utils.DynamicCompiler_Folder;

import static org.junit.jupiter.api.Assertions.*;

import contrib.utils.DynamicCompiler;
import core.utils.components.path.SimpleIPath;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DynamicCompilerCompileAndLoadTest {

  @BeforeEach
  void setup() throws Exception {
    Path tempDir = Files.createTempDirectory("dynamicCompiler");
    System.setProperty("BASEREFLECTIONDIR", tempDir.toString());
  }

  @Test
  void compileAndLoadCompilesValidClass() throws Exception {

    Path source = Files.createTempFile("Hello", ".java");

    Files.writeString(
        source,
        """
                package test;
                public class Hello {
                }
                """);

    Class<?> clazz =
        DynamicCompiler.compileAndLoad(new SimpleIPath(source.toString()), "test.Hello");

    assertNotNull(clazz);
    assertEquals("test.Hello", clazz.getName());
  }

  @Test
  void compileAndLoadReturnsCachedClass() throws Exception {

    Path source = Files.createTempFile("Hello", ".java");

    Files.writeString(
        source,
        """
                package test;
                public class Hello {
                }
                """);

    Class<?> first =
        DynamicCompiler.compileAndLoad(new SimpleIPath(source.toString()), "test.Hello");

    Class<?> second =
        DynamicCompiler.compileAndLoad(new SimpleIPath(source.toString()), "test.Hello");

    assertSame(first, second);
  }

  @Test
  void compileAndLoadThrowsExceptionForInvalidJava() throws Exception {

    Path source = Files.createTempFile("Broken", ".java");

    Files.writeString(
        source,
        """
                package test;
                public class Broken {
                    this is not java
                }
                """);

    assertThrows(
        Exception.class,
        () -> DynamicCompiler.compileAndLoad(new SimpleIPath(source.toString()), "test.Broken"));
  }

  @Test
  void compileAndLoadThrowsExceptionForWrongClassName() throws Exception {

    Path source = Files.createTempFile("Hello", ".java");

    Files.writeString(
        source,
        """
        package test;
        public class Hello {
        }
        """);

    Exception ex =
        assertThrows(
            Exception.class,
            () -> DynamicCompiler.compileAndLoad(new SimpleIPath(source.toString()), "test.Wrong"));

    assertEquals("Compilation failed.", ex.getMessage());
  }
}
