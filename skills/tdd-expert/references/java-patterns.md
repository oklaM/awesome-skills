# Java TDD 模式与最佳实践

## 测试框架选择

### JUnit 5 (推荐)

**优点**：
- Java 标准测试框架
- 强大的断言和假设
- 参数化测试支持
- 良好的 IDE 集成

**基本结构**：
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    @Test
    void addsTwoNumbers() {
        assertEquals(2, 1 + 1);
    }

    @Test
    void throwsExceptionForInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            validateInput(null);
        });
    }
}
```

### TestNG

**优点**：
- 灵活的测试配置
- 并行测试执行
- 数据驱动测试

**基本结构**：
```java
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class CalculatorTest {

    @Test
    public void addsTwoNumbers() {
        assertEquals(2, 1 + 1);
    }
}
```

## JUnit 5 核心模式

### 1. 测试生命周期

```java
import org.junit.jupiter.api.*;

class UserServiceTest {
    private UserService userService;
    private Database database;

    @BeforeAll
    static void setupAll() {
        // 所有测试之前运行一次
        Database.initialize();
    }

    @AfterAll
    static void tearDownAll() {
        // 所有测试之后运行一次
        Database.shutdown();
    }

    @BeforeEach
    void setUp() {
        // 每个测试之前运行
        database = new TestDatabase();
        userService = new UserService(database);
    }

    @AfterEach
    void tearDown() {
        // 每个测试之后运行
        database.cleanup();
    }

    @Test
    void createsUser() {
        userService.createUser("Alice", "alice@example.com");

        assertNotNull(database.findUserByEmail("alice@example.com"));
    }
}
```

### 2. 断言 (Assertions)

```java
import static org.junit.jupiter.api.Assertions.*;

class AssertionExamples {

    @Test
    void basicAssertions() {
        assertEquals(4, 2 + 2);
        assertEquals(4, 2 + 2, "Addition failed");
        assertEquals(4, 2 + 2, () -> "Addition failed: 2 + 2");

        assertNotEquals(5, 2 + 2);

        assertTrue(5 > 3);
        assertFalse(5 < 3);

        assertNull(null);
        assertNotNull(new Object());
    }

    @Test
    void arrayAssertions() {
        int[] expected = {1, 2, 3};
        int[] actual = {1, 2, 3};

        assertArrayEquals(expected, actual);
    }

    @Test
    void objectAssertions() {
        User user1 = new User("Alice");
        User user2 = new User("Alice");

        assertEquals(user1, user2);  // equals()
        assertSame(user1, user1);     // ==
        assertNotSame(user1, user2);
    }

    @Test
    void exceptionAssertions() {
        // 方式 1
        assertThrows(IllegalArgumentException.class, () -> {
            validateInput(null);
        });

        // 方式 2 - 验证异常消息
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validateInput(null);
        });
        assertEquals("Input cannot be null", exception.getMessage());

        // 方式 3 - 验证其他属性
        InvalidInputException ex = assertThrows(InvalidInputException.class, () -> {
            validateInput("invalid");
        });
        assertEquals(400, ex.getErrorCode());
    }

    @Test
    void timeoutAssertion() {
        assertTimeout(Duration.ofSeconds(2), () -> {
            // 必须在 2 秒内完成
            longRunningOperation();
        });
    }

    @Test
    void groupedAssertions() {
        User user = createUser();

        // 所有断言都会执行，即使有失败
        assertAll("user",
            () -> assertEquals("Alice", user.getName()),
            () -> assertEquals("alice@example.com", user.getEmail()),
            () -> assertNotNull(user.getId())
        );
    }
}
```

### 3. 参数化测试

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

class ParameterizedTests {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void testWithValues(int value) {
        assertTrue(value > 0 && value <= 5);
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"", "  ", "\t"})
    void testNullAndEmptyStrings(String input) {
        assertTrue(Strings.isNullOrEmpty(input));
    }

    @ParameterizedTest
    @EnumSource(TimeUnit.class)
    void testWithEnum(TimeUnit timeUnit) {
        assertNotNull(timeUnit);
    }

    @ParameterizedTest
    @CsvSource({
        "apple, 1",
        "banana, 2",
        "'cherry, apple', 3"
    })
    void testWithCsvSource(String fruit, int rank) {
        assertNotNull(fruit);
        assertTrue(rank > 0);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data.csv", numLinesToSkip = 1)
    void testWithCsvFile(String name, int age, String email) {
        assertNotNull(name);
        assertTrue(age > 0);
        assertTrue(email.contains("@"));
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void testWithMethodSource(int input, int expected) {
        assertEquals(expected, square(input));
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(1, 1),
            Arguments.of(2, 4),
            Arguments.of(3, 9),
            Arguments.of(10, 100)
        );
    }
}
```

### 4. 测试套件

```java
import org.junit.platform.suite.api.*;

@Suite
@SuiteDisplayName("All Tests")
@SelectPackages("com.example.tests")
public class AllTests {
}

// 或使用类
@Suite
@SelectClasses({UserServiceTest.class, OrderServiceTest.class})
public class ServiceTests {
}
```

### 5. 禁用和条件测试

```java
class DisabledTests {

    @Test
    @Disabled("Not implemented yet")
    void notImplementedTest() {
        // 这个测试被跳过
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void notOnWindows() {
        // 在 Windows 上跳过
    }

    @Test
    @EnabledIfSystemProperty(named = "run.integration.tests", matches = "true")
    void integrationTest() {
        // 只有在系统属性设置时才运行
    }
}
```

## Mockito 模式

### 1. 创建 Mock

```java
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

class MockitoExamples {

    @Test
    void createMock() {
        // 方式 1
        List<String> mockList = mock(List.class);

        // 方式 2 - 使用注解
        // @Mock
        // private List<String> mockList;

        mockList.add("one");
        mockList.clear();

        verify(mockList).add("one");
        verify(mockList).clear();
    }

    @Test
    void withMockitoAnnotations() {
        // 需要在测试开始时调用
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void withExtension() {
        // JUnit 5 方式
        @ExtendWith(MockitoExtension.class)
        class MyTest {
            @Mock
            private List<String> mockList;

            @Test
            void test() {
                mockList.add("one");
                verify(mockList).add("one");
            }
        }
    }
}
```

### 2. Stubbing 行为

```java
@Test
void stubbingReturnValues() {
    LinkedList<String> mockList = mock(LinkedList.class);

    // 设置返回值
    when(mockList.get(0)).thenReturn("first");
    when(mockList.get(1)).thenThrow(new RuntimeException());

    assertEquals("first", mockList.get(0));

    // 抛出异常
    assertThrows(RuntimeException.class, () -> mockList.get(1));

    // 未 stub 的方法返回默认值
    assertNull(mockList.get(999));
}

@Test
void stubbingWithMatchers() {
    Comparator<String> comparator = mock(Comparator.class);

    // 使用 any() 匹配器
    when(comparator.compare(anyString(), eq("second"))).thenReturn(1);

    assertEquals(1, comparator.compare("first", "second"));
    assertEquals(1, comparator.compare("any", "second"));
}

@Test
void stubbingConsecutiveCalls() {
    MockIterator<String> mockIterator = mock(MockIterator.class);

    // 多次调用返回不同值
    when(mockIterator.next())
        .thenReturn("hello")
        .thenReturn("world")
        .thenThrow(new RuntimeException());

    assertEquals("hello", mockIterator.next());
    assertEquals("world", mockIterator.next());
    assertThrows(RuntimeException.class, () -> mockIterator.next());
}

@Test
void stubbingWithAnswer() {
    List<String> mockList = mock(List.class);

    // 自定义 Answer
    when(mockList.get(anyInt())).thenAnswer(invocation -> {
        int index = invocation.getArgument(0);
        return "element " + index;
    });

    assertEquals("element 0", mockList.get(0));
    assertEquals("element 5", mockList.get(5));
}
```

### 3. 验证行为

```java
@Test
void verification() {
    List<String> mockList = mock(List.class);

    mockList.add("one");
    mockList.add("two");
    mockList.add("two");

    // 基本验证
    verify(mockList).add("one");
    verify(mockList, times(1)).add("one");  // 默认 times(1)
    verify(mockList, times(2)).add("two");
    verify(mockList, never()).add("three");

    // 验证调用次数
    verify(mockList, atLeastOnce()).add("one");
    verify(mockList, atLeast(2)).add("two");
    verify(mockList, atMost(5)).add("two");

    // 验证没有其他交互
    verifyNoMoreInteractions(mockList);

    // 验证从未被调用
    List<String> mock2 = mock(List.class);
    verifyNoInteractions(mock2);
}

@Test
void verificationInOrder() {
    List<String> firstMock = mock(List.class);
    List<String> secondMock = mock(List.class);

    firstMock.add("first");
    secondMock.add("second");
    firstMock.add("third");

    InOrder inOrder = inOrder(firstMock, secondMock);

    // 验证调用顺序
    inOrder.verify(firstMock).add("first");
    inOrder.verify(secondMock).add("second");
    inOrder.verify(firstMock).add("third");
}

@Test
void verificationWithTimeout() {
    List<String> mockList = mock(List.class);

    // 异步操作
    CompletableFuture.runAsync(() -> {
        try {
            Thread.sleep(100);
            mockList.add("delayed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    });

    // 等待最多 1 秒验证调用
    verify(mockList, timeout(1000)).add("delayed");
    verify(mockList, timeout(1000).times(1)).add("delayed");
}
```

### 4. Spy (部分 Mock)

```java
@Test
void spyExample() {
    List<String> list = new ArrayList<>();
    List<String> spyList = spy(list);

    // 真实方法被调用
    spyList.add("one");
    spyList.add("two");

    assertEquals(2, spyList.size());

    // 可以 stub 某些方法
    when(spyList.size()).thenReturn(100);

    assertEquals(100, spyList.size());
    // 但真实数据仍在
    assertEquals("one", spyList.get(0));
}
```

### 5. ArgumentCaptor

```java
@Test
void argumentCaptor() {
    List<String> mockList = mock(List.class);

    mockList.add("Alice");
    mockList.add("Bob");

    // 捕获参数
    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
    verify(mockList, times(2)).add(argumentCaptor.capture());

    // 获取所有捕获的值
    List<String> allValues = argumentCaptor.getAllValues();
    assertEquals(Arrays.asList("Alice", "Bob"), allValues);

    // 获取第一个值
    assertEquals("Alice", argumentCaptor.getValue());
}
```

## Spring Boot 测试

### 1. 单元测试

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void createUser() {
        // Given
        User user = new User("Alice", "alice@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User result = userService.createUser("Alice", "alice@example.com");

        // Then
        assertNotNull(result);
        assertEquals("Alice", result.getName());
        verify(userRepository).save(any(User.class));
    }
}
```

### 2. Web 层测试

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void getUserById() throws Exception {
        User user = new User("Alice", "alice@example.com");
        user.setId(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Alice"))
            .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void createUser() throws Exception {
        User user = new User("Bob", "bob@example.com");
        user.setId(1L);
        when(userService.createUser(any(), any())).thenReturn(user);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Bob\",\"email\":\"bob@example.com\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Bob"));
    }
}
```

### 3. 数据库测试

```java
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail() {
        User user = new User("Alice", "alice@example.com");
        entityManager.persist(user);
        entityManager.flush();

        User found = userRepository.findByEmail("alice@example.com");

        assertThat(found.getEmail()).isEqualTo("alice@example.com");
        assertThat(found.getName()).isEqualTo("Alice");
    }
}
```

### 4. 集成测试

```java
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void fullFlow() {
        // 创建用户
        ResponseEntity<User> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/users",
            new UserRequest("Alice", "alice@example.com"),
            User.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getName()).isEqualTo("Alice");
    }
}
```

## 常见场景模式

### 场景 1: 测试异常

```java
@Test
void testException() {
    assertThrows(IllegalArgumentException.class, () -> {
        validator.validate(null);
    });
}

@Test
void testExceptionWithMessage() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        validator.validate(null);
    });

    assertEquals("Name cannot be null", exception.getMessage());
}
```

### 场景 2: 测试异步代码

```java
@Test
void testAsync() throws Exception {
    CompletableFuture<String> future = asyncOperation();

    // 等待结果
    String result = future.get(2, TimeUnit.SECONDS);

    assertEquals("expected", result);
}
```

### 场景 3: 测试私有方法（不推荐）

```java
// 不推荐测试私有方法，使用反射或测试公共行为
@Test
void testPrivateMethod() throws Exception {
    MyClass obj = new MyClass();
    Method method = MyClass.class.getDeclaredMethod("privateMethod", String.class);
    method.setAccessible(true);

    String result = (String) method.invoke(obj, "input");
    assertEquals("output", result);
}
```

## 最佳实践总结

### ✅ Do

1. **使用描述性测试名称**
   ```java
   @Test
   void returns404WhenUserNotFound() {}
   ```

2. **遵循 Given-When-Then**
   ```java
   @Test
   void createUser() {
       // Given
       UserRequest request = new UserRequest("Alice", "alice@example.com");

       // When
       User result = userService.createUser(request);

       // Then
       assertNotNull(result);
   }
   ```

3. **使用 @BeforeEach 设置测试环境**
   ```java
   @BeforeEach
   void setUp() {
       // 重置状态
   }
   ```

4. **测试公共行为而非实现细节**
   ```java
   // 好
   assertEquals(expected, object.calculate());

   // 不好
   verify(object).internalMethod();
   ```

### ❌ Don't

1. **不要在测试中写复杂逻辑**
2. **不要依赖测试执行顺序**
3. **不要过度使用 Mock**
4. **不要在测试中捕获异常后不处理**
5. **不要测试私有方法**

## 断言库选择

### AssertJ (推荐)

```java
import static org.assertj.core.api.Assertions.*;

@Test
void assertions() {
    User user = new User("Alice", "alice@example.com");

    assertThat(user.getName()).isEqualTo("Alice");
    assertThat(user.getEmail()).contains("@");
    assertThat(user.getAge()).isGreaterThan(0);

    // 链式断言
    assertThat(user)
        .isNotNull()
        .hasFieldOrProperty("name")
        .extracting("name")
        .isEqualTo("Alice");
}
```

AssertJ 提供了更丰富的断言和更好的错误消息，推荐在项目中使用。
