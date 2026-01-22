/**
 * Jest 测试模板
 *
 * 使用说明：
 * 1. 复制此文件到你的测试目录
 * 2. 重命名为 <module_name>.test.js 或 <module_name>.spec.js
 * 3. 根据需要修改导入和测试
 */

const { yourFunction, YourClass } = require('./yourModule');

// =============================================================================
// 基本测试结构
// =============================================================================

describe('YourModule', () => {
  test('basic test', () => {
    expect(1 + 1).toBe(2);
  });
});

// =============================================================================
// Before/After 钩子
// =============================================================================

describe('Lifecycle hooks', () => {
  let instance;

  beforeAll(() => {
    // 所有测试之前运行一次
    instance = new YourClass();
  });

  afterAll(() => {
    // 所有测试之后运行一次
    instance.cleanup();
  });

  beforeEach(() => {
    // 每个测试之前运行
    instance.reset();
  });

  afterEach(() => {
    // 每个测试之后运行
    instance.clear();
  });

  test('first test', () => {
    expect(instance.value).toBe(0);
  });

  test('second test', () => {
    expect(instance.value).toBe(0); // 重置后的值
  });
});

// =============================================================================
// 常用断言 (Matchers)
// =============================================================================

describe('Common assertions', () => {
  test('equality', () => {
    expect(1 + 1).toBe(2); // 严格相等 (===)
    expect({ name: 'Alice' }).toEqual({ name: 'Alice' }); // 深度相等
  });

  test('truthiness', () => {
    expect(value).toBeTruthy();
    expect(value).toBeFalsy();
    expect(value).toBeNull();
    expect(value).toBeUndefined();
    expect(value).toBeDefined();
  });

  test('numbers', () => {
    expect(10).toBeGreaterThan(5);
    expect(5).toBeLessThan(10);
    expect(0.1 + 0.2).toBeCloseTo(0.3); // 浮点数
  });

  test('strings', () => {
    expect('hello world').toContain('hello');
    expect('hello').toMatch(/ell/);
  });

  test('arrays', () => {
    expect([1, 2, 3]).toContain(2);
    expect([1, 2, 3]).toHaveLength(3);
  });

  test('objects', () => {
    expect(obj).toHaveProperty('name');
    expect(obj).toHaveProperty('name', 'Alice');
    expect(obj).toMatchObject({ name: 'Alice' });
  });

  test('exceptions', () => {
    expect(() => riskyOperation()).toThrow();
    expect(() => riskyOperation()).toThrow(Error);
    expect(() => riskyOperation()).toThrow('Invalid input');
    expect(() => riskyOperation()).toThrow(/invalid/i);
  });
});

// =============================================================================
// 异步测试
// =============================================================================

describe('Async tests', () => {
  // 使用 async/await
  test('async/await', async () => {
    const result = await fetchData();
    expect(result).toBe('data');
  });

  // 使用 Promise .then
  test('promise then', () => {
    return fetchData().then(result => {
      expect(result).toBe('data');
    });
  });

  // 使用 .resolves
  test('promise resolves', () => {
    return expect(fetchData()).resolves.toBe('data');
  });

  // 使用 .rejects
  test('promise rejects', () => {
    return expect(invalidOperation()).rejects.toThrow('Error');
  });

  // 测试回调
  test('callback', done => {
    fetchDataCallback((data) => {
      expect(data).toBe('data');
      done(); // 必须调用 done()
    });
  });
});

// =============================================================================
// Mock 函数
// =============================================================================

describe('Mock functions', () => {
  test('basic mock', () => {
    const mockFn = jest.fn();
    mockFn('arg1', 'arg2');

    // 验证调用
    expect(mockFn).toHaveBeenCalled();
    expect(mockFn).toHaveBeenCalledTimes(1);
    expect(mockFn).toHaveBeenCalledWith('arg1', 'arg2');
    expect(mockFn).toHaveBeenLastCalledWith('arg1', 'arg2');
  });

  test('mock return value', () => {
    const mockFn = jest.fn();
    mockFn.mockReturnValue(42);

    expect(mockFn()).toBe(42);

    // 不同调用返回不同值
    mockFn.mockReturnValueOnce(1).mockReturnValueOnce(2);
    expect(mockFn()).toBe(1);
    expect(mockFn()).toBe(2);
    expect(mockFn()).toBe(42); // 之后返回 42
  });

  test('mock implementation', () => {
    const mockFn = jest.fn((a, b) => a + b);
    expect(mockFn(1, 2)).toBe(3);
    expect(mockFn(5, 10)).toBe(15);
  });
});

// =============================================================================
// Mock 模块
// =============================================================================

describe('Mocking modules', () => {
  // Mock 整个模块
  beforeEach(() => {
    jest.doMock('./externalModule', () => ({
      fetchData: jest.fn(() => Promise.resolve('mocked data')),
    }));
  });

  afterEach(() => {
    jest.dontMock('./externalModule');
  });

  test('uses mocked module', async () => {
    const { fetchData } = require('./externalModule');
    const data = await fetchData();
    expect(data).toBe('mocked data');
  });
});

// 使用 jest.spyOn
describe('Spies', () => {
  test('spy on method', () => {
    const obj = {
      method: () => 'real value',
    };

    const spy = jest.spyOn(obj, 'method').mockReturnValue('mocked value');

    expect(obj.method()).toBe('mocked value');
    expect(spy).toHaveBeenCalled();

    spy.mockRestore(); // 恢复原方法
    expect(obj.method()).toBe('real value');
  });
});

// =============================================================================
// 参数化测试
// =============================================================================

describe.each([
  [1, 2, 3],
  [0, 0, 0],
  [-1, 1, 0],
  [100, 200, 300],
])('add(%i, %i) = %i', (a, b, expected) => {
  test(`returns ${expected}`, () => {
    expect(add(a, b)).toBe(expected);
  });
});

// 或使用模板字符串
test.each`
  a    | b    | expected
  ${1} | ${2} | ${3}
  ${0} | ${0} | ${0}
  ${-1}| ${1} | ${0}
`('add($a, $b) = $expected', ({ a, b, expected }) => {
  expect(add(a, b)).toBe(expected);
});

// =============================================================================
// 快照测试
// =============================================================================

describe('Snapshot tests', () => {
  test('component snapshot', () => {
    const component = render(<MyComponent />);
    expect(component).toMatchSnapshot();
  });

  test('inline snapshot', () => {
    const data = processData();
    expect(data).toMatchInlineSnapshot(`
      {
        "id": 1,
        "name": "Alice",
      }
    `);
  });
});

// =============================================================================
// 测试类
// =============================================================================

describe('Stack', () => {
  let stack;

  beforeEach(() => {
    stack = new Stack();
  });

  test('pushes and pops items', () => {
    stack.push('first');
    stack.push('second');

    expect(stack.pop()).toBe('second');
    expect(stack.pop()).toBe('first');
  });

  test('pop throws error when empty', () => {
    expect(() => stack.pop()).toThrow('Stack is empty');
  });

  test('isEmpty returns true for empty stack', () => {
    expect(stack.isEmpty()).toBe(true);

    stack.push('item');
    expect(stack.isEmpty()).toBe(false);
  });
});

// =============================================================================
// 跳过和只运行测试
// =============================================================================

describe('Skipping tests', () => {
  test.skip('this test is skipped', () => {
    // 这个测试被跳过
  });

  xtest('also skipped', () => {
    // 这个也被跳过
  });

  test.only('this test will run exclusively', () => {
    // 只运行这个测试
  });

  test('this will not run because of .only above', () => {
    // 不会运行
  });
});

// =============================================================================
// 条件测试
// =============================================================================

describe('Conditional tests', () => {
  // 只在特定条件下运行
  test.ifSystem('linux').only('runs on linux', () => {
    // 只在 Linux 上运行
  });

  test.concurrent('can run concurrently', async () => {
    // 并发运行
  });
});

// =============================================================================
// 测试 DOM/组件 (React Testing Library)
// =============================================================================

describe('Component tests', () => {
  test('renders user profile', () => {
    render(<UserProfile name="Alice" />);

    expect(screen.getByText('Alice')).toBeInTheDocument();
    expect(screen.getByRole('heading')).toHaveTextContent('Alice');
  });

  test('button click', () => {
    const handleClick = jest.fn();
    render(<Button onClick={handleClick}>Click me</Button>);

    fireEvent.click(screen.getByText('Click me'));

    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  test('async data loading', async () => {
    render(<UserProfile userId={1} />);

    expect(screen.getByText('Loading...')).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText('Alice')).toBeInTheDocument();
    });
  });
});

// =============================================================================
// 定时器测试
// =============================================================================

describe('Timer tests', () => {
  beforeEach(() => {
    jest.useFakeTimers();
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  test('timer callback', () => {
    const callback = jest.fn();

    setTimeout(callback, 1000);

    expect(callback).not.toHaveBeenCalled();

    jest.advanceTimersByTime(1000);
    // 或 jest.runAllTimers();
    // 或 jest.runOnlyPendingTimers();

    expect(callback).toHaveBeenCalledTimes(1);
  });
});

// =============================================================================
// 测试配置 (jest.config.js)
// =============================================================================

// 在项目根目录创建 jest.config.js:
//
// module.exports = {
//   // 测试环境
//   testEnvironment: 'node',
//
//   // 测试文件匹配模式
//   testMatch: [
//     '**/__tests__/**/*.js',
//     '**/?(*.)+(spec|test).js'
//   ],
//
//   // 覆盖率收集
//   collectCoverageFrom: [
//     'src/**/*.js',
//     '!src/**/*.test.js',
//     '!src/**/*.spec.js',
//   ],
//
//   // 覆盖率阈值
//   coverageThreshold: {
//     global: {
//       branches: 80,
//       functions: 80,
//       lines: 80,
//       statements: 80,
//     },
//   },
//
//   // 模块路径别名
//   moduleNameMapper: {
//     '^@/(.*)$': '<rootDir>/src/$1',
//   },
//
//   // Setup 文件
//   setupFilesAfterEnv: ['<rootDir>/jest.setup.js'],
// };

// =============================================================================
// Given-When-Then 模板
// =============================================================================

describe('Given-When-Then pattern', () => {
  test('calculator adds two numbers', () => {
    // Given
    const calculator = new Calculator();
    const num1 = 5;
    const num2 = 3;

    // When
    const result = calculator.add(num1, num2);

    // Then
    expect(result).toBe(8);
  });
});
