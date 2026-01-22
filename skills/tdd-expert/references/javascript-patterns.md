# JavaScript/TypeScript TDD 模式与最佳实践

## 测试框架选择

### Jest (推荐)

**优点**：
- 零配置，开箱即用
- 内置断言库和 Mock
- 快速的并行测试执行
- 快照测试
- 良好的 TypeScript 支持

**基本结构**：
```javascript
describe('Calculator', () => {
  test('adds two numbers', () => {
    expect(1 + 1).toBe(2);
  });

  test('throws error for invalid input', () => {
    expect(() => parseNumber('invalid')).toThrow();
  });
});
```

### Vitest

**优点**：
- 与 Vite 深度集成
- Jest 兼容的 API
- 更快的 ESM 支持
- 开箱即用的 TypeScript/JSX

**基本结构**：
```javascript
import { test, expect } from 'vitest';

test('adds two numbers', () => {
  expect(add(1, 2)).toBe(3);
});
```

### Mocha + Chai

**优点**：
- 灵活，可组合断言库
- 不强制特定风格
- 大量插件支持

**基本结构**：
```javascript
import { expect } from 'chai';

describe('Calculator', () => {
  it('should add two numbers', () => {
    expect(add(1, 2)).to.equal(3);
  });
});
```

## Jest 核心模式

### 1. describe 和 test 块

**组织测试套件**：

```javascript
describe('User', () => {
  describe('authentication', () => {
    test('logs in with valid credentials', () => {
      // 测试登录
    });

    test('rejects invalid credentials', () => {
      // 测试拒绝登录
    });
  });

  describe('profile', () => {
    test('returns user profile', () => {
      // 测试获取资料
    });
  });
});
```

**使用 test.only 调试**：

```javascript
test.only('this test will run', () => {
  // 只运行这个测试
});

test('this test will be skipped', () => {
  // 这个测试会被跳过
});
```

**跳过测试**：

```javascript
test.skip('this test is skipped', () => {
  // 这个测试被跳过
});

// 或使用 xtest
xtest('this test is also skipped', () => {
  // 这个测试被跳过
});
```

### 2. Before/After 钩子

**测试生命周期钩子**：

```javascript
describe('Database Tests', () => {
  let db;

  beforeAll(() => {
    // 所有测试之前运行一次
    db = new Database();
    db.connect();
  });

  afterAll(() => {
    // 所有测试之后运行一次
    db.disconnect();
  });

  beforeEach(() => {
    // 每个测试之前运行
    db.cleanup();
    db.seedTestData();
  });

  afterEach(() => {
    // 每个测试之后运行
    db.clear();
  });

  test('creates user', () => {
    db.createUser({ name: 'Alice' });
    expect(db.getUserCount()).toBe(1);
  });

  test('deletes user', () => {
    const user = db.createUser({ name: 'Bob' });
    db.deleteUser(user.id);
    expect(db.getUserCount()).toBe(0);
  });
});
```

### 3. 断言 (Expectations)

**常用 matchers**：

```javascript
// 相等性
expect(1 + 1).toBe(2);           // 严格相等 (===)
expect({ name: 'Alice' }).toEqual({ name: 'Alice' });  // 深度相等
expect([1, 2, 3]).toEqual([1, 2, 3]);

// 真值判断
expect(value).toBeTruthy();
expect(value).toBeFalsy();
expect(value).toBeNull();
expect(value).toBeUndefined();
expect(value).toBeDefined();

// 数字比较
expect(10).toBeGreaterThan(5);
expect(5).toBeLessThan(10);
expect(5).toBeGreaterThanOrEqual(5);
expect(0.1 + 0.2).toBeCloseTo(0.3, 5);  // 浮点数

// 字符串
expect('hello world').toContain('hello');
expect('hello').toMatch(/ell/);

// 数组
expect([1, 2, 3]).toContain(2);
expect([1, 2, 3]).toHaveLength(3);

// 对象
expect(obj).toHaveProperty('name');
expect(obj).toHaveProperty('name', 'Alice');
expect(obj).toMatchObject({ name: 'Alice' });

// 异常
expect(() => riskyOperation()).toThrow();
expect(() => riskyOperation()).toThrow(Error);
expect(() => riskyOperation()).toThrow('Invalid input');
expect(() => riskyOperation()).toThrow(/invalid/i);
```

**异步断言**：

```javascript
// Promise
test('async test', async () => {
  const result = await fetchData();
  expect(result).toBe('data');
});

// .resolves
test('promise resolves', () => {
  return expect(fetchData()).resolves.toBe('data');
});

// .rejects
test('promise rejects', () => {
  return expect(invalidOperation()).rejects.toThrow('Error');
});
```

### 4. Mock 函数

**基本 Mock**：

```javascript
test('mock function', () => {
  const mockFn = jest.fn();

  mockFn('arg1', 'arg2');

  // 验证调用
  expect(mockFn).toHaveBeenCalled();
  expect(mockFn).toHaveBeenCalledTimes(1);
  expect(mockFn).toHaveBeenCalledWith('arg1', 'arg2');
  expect(mockFn).toHaveBeenLastCalledWith('arg1', 'arg2');

  // 检查调用参数
  expect(mockFn.mock.calls).toEqual([['arg1', 'arg2']]);
  expect(mockFn.mock.calls[0][0]).toBe('arg1');

  // 检查返回值
  expect(mockFn.mock.results[0].value).toBeUndefined();
});
```

**返回值**：

```javascript
const mockFn = jest.fn();

mockFn.mockReturnValue(42);
expect(mockFn()).toBe(42);

mockFn.mockReturnValueOnce(1).mockReturnValueOnce(2);
expect(mockFn()).toBe(1);
expect(mockFn()).toBe(2);
expect(mockFn()).toBe(42);  // 之后返回 42
```

**实现**：

```javascript
const mockFn = jest.fn((a, b) => a + b);
expect(mockFn(1, 2)).toBe(3);
expect(mockFn(5, 10)).toBe(15);
```

### 5. Mock 模块

**Mock 整个模块**：

```javascript
// math.js
export const add = (a, b) => a + b;
export const subtract = (a, b) => a - b;

// math.test.js
import { add, subtract } from './math';

jest.mock('./math', () => ({
  add: jest.fn(() => 42),
  subtract: jest.fn(() => 0),
}));

test('uses mocked add', () => {
  expect(add(1, 2)).toBe(42);
  expect(subtract(5, 3)).toBe(0);
});
```

**Mock 部分功能**：

```javascript
import * as math from './math';

jest.spyOn(math, 'add').mockReturnValue(42);

test('spies on add', () => {
  expect(math.add(1, 2)).toBe(42);
  expect(math.add).toHaveBeenCalledWith(1, 2);
});

// 清理
math.add.mockRestore();
```

**Mock 外部库**：

```javascript
// axios.js
import axios from 'axios';

jest.mock('axios');

test('fetches user data', async () => {
  axios.get.mockResolvedValue({ data: { name: 'Alice' } });

  const user = await fetchUser(1);
  expect(user.name).toBe('Alice');
  expect(axios.get).toHaveBeenCalledWith('/users/1');
});
```

### 6. 快照测试

**基本快照**：

```javascript
test('component snapshot', () => {
  const component = render(<MyComponent />);
  expect(component).toMatchSnapshot();
});

// 内联快照
test('inline snapshot', () => {
  const data = processData();
  expect(data).toMatchInlineSnapshot(`
    {
      "id": 1,
      "name": "Alice",
    }
  `);
});
```

**更新快照**：

```bash
# 更新快照
jest --updateSnapshot

# 或使用 CI 环境变量
CI=true jest
```

### 7. 测试异步代码

**测试 Promise**：

```javascript
test('async/await', async () => {
  const result = await fetchData();
  expect(result).toBe('data');
});

test('promise .then', () => {
  return fetchData().then(result => {
    expect(result).toBe('data');
  });
});

test('promise resolves', () => {
  return expect(fetchData()).resolves.toBe('data');
});

test('promise rejects', () => {
  return expect(invalidOperation()).rejects.toThrow('Error');
});
```

**测试回调**：

```javascript
test('callback', done => {
  fetchDataCallback((data) => {
    expect(data).toBe('data');
    done();  // 必须调用 done()
  });
});

test('callback with error', done => {
  fetchDataCallback((error, data) => {
    expect(error).toBeNull();
    expect(data).toBe('data');
    done();
  });
});
```

**测试定时器**：

```javascript
jest.useFakeTimers();

test('timer callback', () => {
  const callback = jest.fn();

  setTimeout(callback, 1000);

  expect(callback).not.toHaveBeenCalled();

  jest.advanceTimersByTime(1000);
  // 或 jest.runAllTimers();
  // 或 jest.runOnlyPendingTimers();

  expect(callback).toHaveBeenCalledTimes(1);
});

jest.useRealTimers();
```

### 8. 参数化测试

**使用 test.each**：

```javascript
test.each([
  [1, 2, 3],
  [0, 0, 0],
  [-1, 1, 0],
  [100, 200, 300],
])('add(%i, %i) = %i', (a, b, expected) => {
  expect(add(a, b)).toBe(expected);
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
```

### 9. 测试 DOM/组件

**React Testing Library**：

```javascript
import { render, screen, fireEvent, waitFor } from '@testing-library/react';

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
```

### 10. 测试自定义 Hook

```javascript
import { renderHook, act } from '@testing-library/react-hooks';

test('useCounter hook', () => {
  const { result } = renderHook(() => useCounter());

  expect(result.current.count).toBe(0);

  act(() => {
    result.current.increment();
  });

  expect(result.current.count).toBe(1);
});
```

## TypeScript 特定模式

### 类型安全的测试

```typescript
import { Calculator } from './Calculator';

describe('Calculator', () => {
  let calc: Calculator;

  beforeEach(() => {
    calc = new Calculator();
  });

  test('adds numbers', () => {
    const result: number = calc.add(1, 2);
    expect(result).toBe(3);
  });
});
```

### Mock 类型

```typescript
// 保留类型信息的 Mock
const mockFn = jest.fn<(a: number, b: number) => number>();

mockFn.mockImplementation((a, b) => a + b);

const result: number = mockFn(1, 2);
```

### 测试类型错误

```typescript
// 使用 @ts-expect-error 验证类型错误
test('type error', () => {
  // @ts-expect-error - string 不能赋值给 number
  const value: number = 'string';
});
```

## 常见场景模式

### 场景 1: 测试 Express/Fastify API

```javascript
import request from 'supertest';
import app from './app';

describe('POST /users', () => {
  test('creates user with valid data', async () => {
    const response = await request(app)
      .post('/users')
      .send({ name: 'Alice', email: 'alice@example.com' })
      .expect(201);

    expect(response.body).toHaveProperty('id');
    expect(response.body.name).toBe('Alice');
  });

  test('rejects invalid email', async () => {
    const response = await request(app)
      .post('/users')
      .send({ name: 'Alice', email: 'invalid' })
      .expect(400);

    expect(response.body.error).toContain('email');
  });
});
```

### 场景 2: 测试数据库操作

```javascript
import { Client } from 'pg';

describe('UserRepository', () => {
  let client: Client;

  beforeAll(async () => {
    client = new Client({ database: 'test_db' });
    await client.connect();
    await client.query('CREATE TEMPORARY TABLE users (id SERIAL, name TEXT)');
  });

  afterAll(async () => {
    await client.end();
  });

  beforeEach(async () => {
    await client.query('TRUNCATE TABLE users');
  });

  test('saves user', async () => {
    await UserRepository.save(client, { name: 'Alice' });

    const result = await client.query('SELECT * FROM users');
    expect(result.rows).toHaveLength(1);
    expect(result.rows[0].name).toBe('Alice');
  });
});
```

### 场景 3: 测试文件系统

```javascript
import fs from 'fs/promises';
import os from 'os';
import path from 'path';

describe('FileProcessor', () => {
  let tempDir: string;

  beforeEach(async () => {
    tempDir = await fs.mkdtemp(path.join(os.tmpdir(), 'test-'));
  });

  afterEach(async () => {
    await fs.rm(tempDir, { recursive: true, force: true });
  });

  test('processes file', async () => {
    const filePath = path.join(tempDir, 'test.txt');
    await fs.writeFile(filePath, 'Hello, World!');

    await FileProcessor.process(filePath);

    const content = await fs.readFile(filePath, 'utf-8');
    expect(content).toBe('PROCESSED: Hello, World!');
  });
});
```

### 场景 4: 测试事件发射器

```javascript
import { EventEmitter } from 'events';

describe('EventEmitter', () => {
  test('emits event', (done) => {
    const emitter = new EventEmitter();

    emitter.on('data', (data) => {
      expect(data).toBe('test data');
      done();
    });

    emitter.emit('data', 'test data');
  });

  test('emits event with async handler', async () => {
    const emitter = new EventEmitter();
    const promise = new Promise(resolve => {
      emitter.on('data', resolve);
    });

    emitter.emit('data', 'test data');

    await expect(promise).resolves.toBe('test data');
  });
});
```

### 场景 5: 测试 Class

```javascript
class Stack {
  constructor() {
    this.items = [];
  }

  push(item) {
    this.items.push(item);
  }

  pop() {
    if (this.isEmpty()) {
      throw new Error('Stack is empty');
    }
    return this.items.pop();
  }

  isEmpty() {
    return this.items.length === 0;
  }
}

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
```

## Jest 配置

### jest.config.js

```javascript
module.exports = {
  // 测试环境
  testEnvironment: 'node',

  // 测试文件匹配模式
  testMatch: [
    '**/__tests__/**/*.js',
    '**/?(*.)+(spec|test).js'
  ],

  // 覆盖率收集
  collectCoverageFrom: [
    'src/**/*.js',
    '!src/**/*.test.js',
    '!src/**/*.spec.js',
  ],

  // 覆盖率阈值
  coverageThreshold: {
    global: {
      branches: 80,
      functions: 80,
      lines: 80,
      statements: 80,
    },
  },

  // 模块路径别名
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1',
  },

  // Setup 文件
  setupFilesAfterEnv: ['<rootDir>/jest.setup.js'],

  // 转换器
  transform: {
    '^.+\\.tsx?$': 'ts-jest',
    '^.+\\.jsx?$': 'babel-jest',
  },
};
```

### package.json 脚本

```json
{
  "scripts": {
    "test": "jest",
    "test:watch": "jest --watch",
    "test:coverage": "jest --coverage",
    "test:debug": "node --inspect-brk node_modules/.bin/jest --runInBand"
  }
}
```

## 最佳实践总结

### ✅ Do

1. **使用 describe 组织相关测试**
   ```javascript
   describe('UserService', () => {
     describe('create', () => {
       test('creates valid user', () => {});
       test('rejects invalid email', () => {});
     });
   });
   ```

2. **使用描述性测试名称**
   ```javascript
   test('returns 404 when user not found', () => {});
   ```

3. **测试行为而非实现**
   ```javascript
   // 好
   test('calculates total price', () => {
     expect(cart.getTotal()).toBe(100);
   });

   // 不好
   test('uses internal calculation method', () => {
     expect(cart._calculate()).toBe(100);
   });
   ```

4. **保持测试独立**
   ```javascript
   test('creates user', () => {
     // 每个测试自己准备数据
     const user = createTestUser();
     expect(user.isValid()).toBe(true);
   });
   ```

5. **使用 beforeEach 清理状态**
   ```javascript
   beforeEach(() => {
     // 重置状态
   });
   ```

### ❌ Don't

1. **不要在测试中写逻辑**
   ```javascript
   // 不好
   test('dynamic assertions', () => {
     const result = calculate();
     if (result > 0) {
       expect(result).toBe(expected);
     }
   });
   ```

2. **不要依赖测试顺序**
   ```javascript
   // 不好
   let globalState;
   test('first', () => { globalState = 1; });
   test('second', () => { expect(globalState).toBe(1); });
   ```

3. **不要过度 Mock**
   ```javascript
   // 不好 - Mock 了所有东西
   const mockA = jest.fn();
   const mockB = jest.fn();
   const mockC = jest.fn();
   // 没有测试真实逻辑
   ```

4. **不要忽略异步错误**
   ```javascript
   // 不好 - 没有处理 Promise rejection
   test('async test', async () => {
   await operationThatThrows();
   });

   // 好
   test('async test', async () => {
   await expect(operationThatThrows()).rejects.toThrow();
   });
   ```

5. **不要在测试中吞异常**
   ```javascript
   // 不好
   test('exception', () => {
     try {
       riskyOperation();
     } catch (e) {
       // 忽略
     }
   });
   ```
