import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * JUnit 5 测试模板
 *
 * 使用说明：
 * 1. 复制此文件到你的测试目录
 * 2. 重命名为 <ClassName>Test.java
 * 3. 根据需要修改导入和测试
 */

@DisplayName("Calculator Tests")
class CalculatorTest {

    private Calculator calculator;

    // =============================================================================
    // 生命周期钩子
    // =============================================================================

    @BeforeAll
    static void setupAll() {
        // 所有测试之前运行一次
        System.out.println("Before all tests");
    }

    @AfterAll
    static void tearDownAll() {
        // 所有测试之后运行一次
        System.out.println("After all tests");
    }

    @BeforeEach
    void setUp() {
        // 每个测试之前运行
        calculator = new Calculator();
    }

    @AfterEach
    void tearDown() {
        // 每个测试之后运行
        calculator = null;
    }

    // =============================================================================
    // 基本测试
    // =============================================================================

    @Test
    @DisplayName("1 + 1 = 2")
    void addsTwoNumbers() {
        assertEquals(2, calculator.add(1, 1));
    }

    @Test
    @DisplayName("5 - 3 = 2")
    void subtractsTwoNumbers() {
        assertEquals(2, calculator.subtract(5, 3));
    }

    // =============================================================================
    // 断言
    // =============================================================================

    @Test
    @DisplayName("Various assertions")
    void variousAssertions() {
        // 相等性
        assertEquals(4, 2 + 2);
        assertEquals(4, 2 + 2, "Addition failed");
        assertNotEquals(5, 2 + 2);

        // 真值判断
        assertTrue(5 > 3);
        assertFalse(5 < 3);
        assertNull(null);
        assertNotNull(new Object());

        // 数组
        int[] expected = {1, 2, 3};
        int[] actual = {1, 2, 3};
        assertArrayEquals(expected, actual);

        // 异常
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.divide(1, 0);
        });

        // 超时
        assertTimeout(Duration.ofSeconds(2), () -> {
            // 必须在 2 秒内完成
            longRunningOperation();
        });

        // 分组断言 - 所有都会执行
        assertAll("user",
            () -> assertEquals("Alice", user.getName()),
            () -> assertEquals("alice@example.com", user.getEmail()),
            () -> assertNotNull(user.getId())
        );
    }

    // =============================================================================
    // 异常测试
    // =============================================================================

    @Test
    @DisplayName("Division by zero throws exception")
    void throwsExceptionWhenDividingByZero() {
        // 方式 1
        assertThrows(ArithmeticException.class, () -> {
            calculator.divide(1, 0);
        });

        // 方式 2 - 验证异常消息
        Exception exception = assertThrows(ArithmeticException.class, () -> {
            calculator.divide(1, 0);
        });
        assertEquals("/ by zero", exception.getMessage());
    }

    // =============================================================================
    // 参数化测试
    // =============================================================================

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("Test with values")
    void testWithValues(int value) {
        assertTrue(value > 0 && value <= 5);
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"", "  ", "\t"})
    @DisplayName("Test with null and empty strings")
    void testNullAndEmptyStrings(String input) {
        assertTrue(Strings.isNullOrEmpty(input));
    }

    @ParameterizedTest
    @EnumSource(TimeUnit.class)
    @DisplayName("Test with enum")
    void testWithEnum(TimeUnit timeUnit) {
        assertNotNull(timeUnit);
    }

    @ParameterizedTest
    @CsvSource({
        "apple, 1",
        "banana, 2",
        "'cherry, apple', 3"
    })
    @DisplayName("Test with CSV source")
    void testWithCsvSource(String fruit, int rank) {
        assertNotNull(fruit);
        assertTrue(rank > 0);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data.csv", numLinesToSkip = 1)
    @DisplayName("Test with CSV file")
    void testWithCsvFile(String name, int age, String email) {
        assertNotNull(name);
        assertTrue(age > 0);
        assertTrue(email.contains("@"));
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    @DisplayName("Test with method source")
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

    // =============================================================================
    // 重复测试
    // =============================================================================

    @RepeatedTest(5)
    @DisplayName("Repeated test")
    void repeatedTest(RepetitionInfo repetitionInfo) {
        System.out.println("Repetition #" + repetitionInfo.getCurrentRepetition());
        assertTrue(true);
    }

    // =============================================================================
    // 超时测试
    // =============================================================================

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    @DisplayName("Test with timeout")
    void testWithTimeout() throws InterruptedException {
        // 必须在 2 秒内完成
        Thread.sleep(1000);
    }

    // =============================================================================
    // 禁用测试
    // =============================================================================

    @Test
    @Disabled("Not implemented yet")
    @DisplayName("Disabled test")
    void notImplementedTest() {
        // 这个测试被跳过
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    @DisplayName("Test only on Linux")
    void testOnlyOnLinux() {
        // 只在 Linux 上运行
    }

    // =============================================================================
    // 嵌套测试
    // =============================================================================

    @Nested
    @DisplayName("Tests for addition")
    class AdditionTests {

        @Test
        @DisplayName("1 + 1 = 2")
        void addsPositiveNumbers() {
            assertEquals(2, calculator.add(1, 1));
        }

        @Test
        @DisplayName("-1 + -1 = -2")
        void addsNegativeNumbers() {
            assertEquals(-2, calculator.add(-1, -1));
        }
    }

    @Nested
    @DisplayName("Tests for division")
    class DivisionTests {

        @Test
        @DisplayName("10 / 2 = 5")
        void dividesPositiveNumbers() {
            assertEquals(5, calculator.divide(10, 2));
        }

        @Test
        @DisplayName("Division by zero throws exception")
        void throwsExceptionWhenDividingByZero() {
            assertThrows(ArithmeticException.class, () -> {
                calculator.divide(10, 0);
            });
        }
    }

    // =============================================================================
    // 假设 (Assumptions)
    // =============================================================================

    @Test
    @DisplayName("Test with assumption")
    void testWithAssumption() {
        assumeTrue(System.getProperty("os.name").contains("Linux"));
        // 只在 Linux 上继续执行
        assertEquals(0, calculator.divide(10, 5));
    }

    // =============================================================================
    // 测试异常属性
    // =============================================================================

    @Test
    @DisplayName("Exception with attributes")
    void exceptionWithAttributes() {
        InvalidInputException ex = assertThrows(InvalidInputException.class, () -> {
            validateInput("invalid");
        });

        assertEquals(400, ex.getErrorCode());
        assertEquals("Invalid input", ex.getMessage());
    }

    // =============================================================================
    // Given-When-Then 模板
    // =============================================================================

    @Test
    @DisplayName("Given-When-Then pattern")
    void givenWhenThenPattern() {
        // Given
        Calculator calculator = new Calculator();
        int num1 = 5;
        int num2 = 3;

        // When
        int result = calculator.add(num1, num2);

        // Then
        assertEquals(8, result);
    }

    // =============================================================================
    // 测试套件示例
    // =============================================================================

    /**
     * 创建测试套件：
     *
     * @Suite
     * @SuiteDisplayName("All Tests")
     * @SelectPackages("com.example.tests")
     * public class AllTests {
     * }
     */
}

// =============================================================================
// AssertJ 示例（推荐使用）
// =============================================================================

/**
 * 如果使用 AssertJ:
 *
 * import static org.assertj.core.api.Assertions.*;
 *
 * @Test
 * void assertjExample() {
 *     assertThat(user.getName()).isEqualTo("Alice");
 *     assertThat(user.getEmail()).contains("@");
 *     assertThat(user.getAge()).isGreaterThan(0);
 *
 *     // 链式断言
 *     assertThat(user)
 *         .isNotNull()
 *         .hasFieldOrProperty("name")
 *         .extracting("name")
 *         .isEqualTo("Alice");
 * }
 */

// =============================================================================
// Mockito 示例
// =============================================================================

/**
 * 如果使用 Mockito:
 *
 * import org.mockito.Mockito;
 * import static org.mockito.Mockito.*;
 *
 * @ExtendWith(MockitoExtension.class)
 * class MockitoExampleTest {
 *
 *     @Mock
 *     private List<String> mockList;
 *
 *     @Test
 *     void mockitoExample() {
 *         // 设置行为
 *         when(mockList.get(0)).thenReturn("first");
 *         when(mockList.size()).thenReturn(1);
 *
 *         // 使用 mock
 *         assertEquals("first", mockList.get(0));
 *         assertEquals(1, mockList.size());
 *
 *         // 验证调用
 *         verify(mockList).get(0);
 *         verify(mockList, times(1)).get(0);
 *         verify(mockList, never()).get(1);
 *     }
 *
 *     @Test
 *     void argumentCaptorExample() {
 *         mockList.add("Alice");
 *
 *         // 捕获参数
 *         ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
 *         verify(mockList).add(captor.capture());
 *
 *         assertEquals("Alice", captor.getValue());
 *     }
 * }
 */

// =============================================================================
// JUnit 5 配置 (junit-platform.properties)
// =============================================================================

/**
 * 在 src/test/resources 创建 junit-platform.properties:
 *
 * # 测试发现
 * junit.jupiter.testinstance.lifecycle.default = per_class
 *
 * # 显示名称生成器
 * junit.jupiter.displayname.generator.default = org.junit.jupiter.api.DisplayNameGenerator$ReplaceUnderscores
 *
 * # 并行执行
 * junit.jupiter.execution.parallel.enabled = true
 * junit.jupiter.execution.parallel.mode.default = concurrent
 */
