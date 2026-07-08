package contrib.utils.DynamicCompiler_Folder;

import static org.junit.jupiter.api.Assertions.*;

import contrib.utils.DynamicCompiler;
import core.utils.Tuple;
import core.utils.components.path.SimpleIPath;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DynamicCompilerComponentTest {

  @BeforeEach
  void setup() throws Exception {
    Path tempDir = Files.createTempDirectory("dynamicCompiler");
    System.setProperty("BASEREFLECTIONDIR", tempDir.toString());
  }

  @Test
  void compileLoadInstantiateWorksTogether() throws Exception {

    Path source = Files.createTempFile("Greeter", ".java");

    Files.writeString(
        source,
        """
        package test;

        public class Greeter {
            private final String name;
            public Greeter(String name){
                this.name = name;
            }
            public String greet(){
                return "Hello " + name;
            }
        }
        """);

    Object greeter =
        DynamicCompiler.loadUserInstance(
            new SimpleIPath(source.toString()), "test.Greeter", new Tuple<>(String.class, "Bob"));

    Method greet = greeter.getClass().getMethod("greet");

    assertEquals("Hello Bob", greet.invoke(greeter));
  }
}
