package contrib.utils.DynamicCompiler_Folder;

import static org.junit.jupiter.api.Assertions.*;

import contrib.utils.DynamicCompiler;
import core.utils.Tuple;
import core.utils.components.path.SimpleIPath;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DynamicCompilerLoadUserInstanceTest {

  @BeforeEach
  void setup() throws Exception {
    Path tempDir = Files.createTempDirectory("dynamicCompiler");
    System.setProperty("BASEREFLECTIONDIR", tempDir.toString());
  }

  @Test
  void loadUserInstanceCreatesObject() throws Exception {

    Path source = Files.createTempFile("Person", ".java");

    Files.writeString(
        source,
        """
        package test;
        public class Person {
            private final String name;
            public Person(String name) {
                this.name = name;
            }
            public String getName() {
                return name;
            }
        }
        """);

    Object object =
        DynamicCompiler.loadUserInstance(
            new SimpleIPath(source.toString()), "test.Person", new Tuple<>(String.class, "Alice"));

    assertNotNull(object);
    assertEquals("test.Person", object.getClass().getName());
  }

  @Test
  void loadUserInstanceThrowsIllegalArgumentExceptionForNullArgs() {

    assertThrows(
        IllegalArgumentException.class,
        () ->
            DynamicCompiler.loadUserInstance(
                null, "test.Person", (Tuple<Class<?>, Object>[]) null));
  }

  @Test
  void loadUserInstanceThrowsExceptionForWrongConstructor() throws Exception {

    Path source = Files.createTempFile("Person", ".java");

    Files.writeString(
        source,
        """
        package test;
        public class Person {
            public Person(String name) {
            }
        }
        """);

    assertThrows(
        NoSuchMethodException.class,
        () ->
            DynamicCompiler.loadUserInstance(
                new SimpleIPath(source.toString()), "test.Person", new Tuple<>(Integer.class, 5)));
  }
}
