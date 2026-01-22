# TDD 测试设计原则

## 核心原则

### 1. 测试驱动开发循环

**红-绿-重构** 循环是 TDD 的核心：

1. **红 (RED)**: 编写一个失败的测试
   - 测试应该描述你想要的行为
   - 测试必须失败（确认功能尚未实现）
   - 失败原因清晰，不是语法错误

2. **绿 (GREEN)**: 编写最简单的代码让测试通过
   - 不求完美，只求测试通过
   - 可以写"硬编码"的返回值
   - 目标是快速回到可工作状态

3. **重构 (REFACTOR)**: 改进代码质量
   - 消除重复
   - 提高可读性
   - 保持测试通过
   - 小步重构，频繁运行测试

### 2. Given-When-Then 模式

每个测试应该遵循三段式结构：

```python
def test_add_two_numbers():
    # Given (Arrange) - 准备测试数据和环境
    calculator = Calculator()
    num1 = 5
    num2 = 3

    # When (Act) - 执行被测试的行为
    result = calculator.add(num1, num2)

    # Then (Assert) - 验证结果
    assert result == 8
```

**优点**：
- 清晰分离准备、执行、验证
- 易于理解和维护
- 统一的结构提高可读性

### 3. 测试命名规范

**使用描述性名称**，清晰表达测试意图：

```python
# 好的测试名称
def test_calculator_adds_two_positive_numbers()
def test_calculator_returns_zero_when_adding_zero()
def test_calculator_throws_exception_for_negative_numbers()

# 不好的测试名称
def test_add()
def test1()
def test_calculator()
```

**模式**: `test_[单元]_[期望行为]_[条件/场景]`

### 4. 测试隔离性

每个测试应该：
- **独立运行**: 不依赖其他测试的执行顺序
- **独立设置**: 每个测试自己准备环境
- **独立清理**: 每个测试后恢复干净状态

```python
# 使用 fixture 确保隔离
@pytest.fixture
def clean_database():
    db = Database()
    db.connect()
    yield db
    db.cleanup()  # 每次测试后清理

def test_save_user(clean_database):
    user = User(name="Alice")
    clean_database.save(user)
    assert clean_database.count() == 1

def test_save_multiple_users(clean_database):
    # 这个测试不受上一个测试影响
    user1 = User(name="Bob")
    user2 = User(name="Carol")
    clean_database.save(user1)
    clean_database.save(user2)
    assert clean_database.count() == 2
```

### 5. 单一行为原则

**每个测试只验证一个行为**：

```python
# 不好 - 测试多个行为
def test_calculator_operations():
    calc = Calculator()
    assert calc.add(2, 3) == 5
    assert calc.subtract(5, 3) == 2
    assert calc.multiply(2, 3) == 6

# 好 - 每个测试一个行为
def test_calculator_adds_numbers():
    assert Calculator().add(2, 3) == 5

def test_calculator_subtracts_numbers():
    assert Calculator().subtract(5, 3) == 2

def test_calculator_multiplies_numbers():
    assert Calculator().multiply(2, 3) == 6
```

### 6. 测试覆盖的层次

按优先级覆盖：

1. **正常路径** (Happy Path)
   - 最常见的使用场景
   - 预期的成功流程

2. **边界条件** (Edge Cases)
   - 零值、空值、最小/最大值
   - 集合的空/单元素/多元素

3. **异常情况** (Error Cases)
   - 无效输入
   - 资源不可用
   - 权限问题

4. **集成点** (Integration Points)
   - 外部依赖的交互
   - API 调用

### 7. AAA 模式变体

**Arrange-Act-Assert** 的标准应用：

```python
def test_user_registration_with_valid_email():
    # Arrange - 准备
    user_service = UserService()
    valid_email = "user@example.com"

    # Act - 执行
    result = user_service.register(valid_email)

    # Assert - 验证
    assert result.success is True
    assert result.user.email == valid_email
    assert result.user.id is not None
```

### 8. 避免测试实现细节

**测试行为，不是实现**：

```python
# 不好 - 测试内部实现细节
def test_user_uses_internal_list():
    user = User()
    assert isinstance(user._notifications, list)  # 测试私有字段
    assert len(user._notifications) == 0

# 好 - 测试公共行为
def test_user_has_no_initial_notifications():
    user = User()
    assert user.get_notification_count() == 0
    assert user.get_notifications() == []
```

### 9. 使用测试替身 (Test Doubles)

根据场景选择合适的替身：

**Test Double 类型**：

1. **Dummy**: 仅用于填充参数，不使用
   ```python
   user = User(name="Alice")  # address 是可选的
   ```

2. **Stub**: 提供预设的响应
   ```python
   class StubEmailService:
       def send(self, email):
           return True  # 总是返回成功
   ```

3. **Mock**: 验证交互行为
   ```python
   mock_service = Mock()
   user_service.send_notification(mock_service, "Hello")
   mock_service.send.assert_called_once_with("Hello")
   ```

4. **Fake**: 简化的工作实现
   ```python
   class InMemoryRepository:
       def __init__(self):
           self._data = []
       def save(self, item):
           self._data.append(item)
   ```

5. **Spy**: 记录调用信息
   ```python
   class SpyEmailService:
       def __init__(self):
           self.calls = []
       def send(self, email):
           self.calls.append(email)
   ```

### 10. 参数化测试

用数据驱动减少重复代码：

```python
@pytest.mark.parametrize("a,b,expected", [
    (0, 0, 0),
    (1, 1, 2),
    (-1, 1, 0),
    (100, 200, 300),
])
def test_add_various_numbers(a, b, expected):
    assert Calculator().add(a, b) == expected
```

### 11. 测试的可读性

**让测试像文档一样易读**：

```python
# 好的测试 - 自我解释
def test_authenticated_user_can_access_profile():
    user = authenticate(username="alice", password="correct_password")
    profile = user.get_profile()
    assert profile is not None

# 不好的测试 - 需要注释理解
def test_user():
    # 测试用户登录后能访问个人资料
    u = User().login("alice", "pass")
    assert u.profile() != None
```

### 12. FIRST 原则

良好测试的 **FIRST** 属性：

- **F**ast (快速): 测试应该快速运行
- **I**ndependent (独立): 测试之间互不依赖
- **R**epeatable (可重复): 在任何环境下都能重复
- **S**elf-Validating (自我验证): 测试有明确的通过/失败结果
- **T**imely (及时): 在生产代码之前编写测试

### 13. 测试覆盖率误区

**覆盖率不是目标**，质量才是：

- 100% 覆盖率 ≠ 0 bug
- 覆盖率工具检查代码执行，不检查断言
- 关注测试质量，而非覆盖率数字

**有效测试**：
```python
def test_calculator_division():
    # 这行被执行，但测试没有验证
    Calculator().divide(10, 2)
    # 覆盖率会增加，但测试毫无意义
```

**改进**：
```python
def test_calculator_division():
    result = Calculator().divide(10, 2)
    assert result == 5  # 实际验证结果
```

### 14. 避免逻辑 branching

测试代码不应该有复杂的逻辑：

```python
# 不好 - 测试中有条件逻辑
def test_calculation():
    result = calculate(value)
    if result > 0:
        assert result == expected_positive
    else:
        assert result == expected_negative

# 好 - 拆分成独立测试
def test_calculation_with_positive_value():
    assert calculate(positive_value) == expected_positive

def test_calculation_with_negative_value():
    assert calculate(negative_value) == expected_negative
```

### 15. 测试数据构建模式

**使用 Builder 或 Factory 模式**：

```python
# 使用测试数据构建器
class UserBuilder:
    def __init__(self):
        self.name = "Default User"
        self.email = "default@example.com"
        self.age = 25

    def with_name(self, name):
        self.name = name
        return self

    def with_email(self, email):
        self.email = email
        return self

    def build(self):
        return User(self.name, self.email, self.age)

# 使用构建器创建测试数据
def test_user_can_update_email():
    user = UserBuilder().with_name("Alice").build()
    user.update_email("newemail@example.com")
    assert user.email == "newemail@example.com"
```

## 测试反模式 (Anti-Patterns)

### ❌ 避免这些

1. **Green Tests** (没有断言的测试)
   ```python
   def test_something():
       calculate(5, 3)  # 没有断言，总是通过
   ```

2. **The Inspector** (检查实现细节)
   ```python
   def test_internal_state():
       obj = MyClass()
       assert obj._internal_var == 5  # 测试私有变量
   ```

3. **The Giant** (一个测试做太多事)
   ```python
   def test_complete_workflow():
       # 100 行代码，测试整个系统
   ```

4. **The Mockery** (过度 Mock)
   ```python
   def test_calculation():
       mock_calculator = Mock()
       mock_calculator.add.return_value = 5
       # Mock 太多，测试了 Mock 而非业务逻辑
   ```

5. **The Flaky** (不稳定的测试)
   ```python
   def test_something():
       result = api_call()  # 依赖外部服务
       assert result is not None  # 有时失败
   ```

## 总结

良好的 TDD 实践需要：

✅ 遵循红-绿-重构循环
✅ 测试行为而非实现
✅ 保持测试简单清晰
✅ 每个测试验证一个行为
✅ 使用 Given-When-Then 结构
✅ 确保测试独立可重复
✅ 关注测试质量而非覆盖率

❌ 避免测试实现细节
❌ 避免测试中的复杂逻辑
❌ 避免过度使用 Mock
❌ 避免不稳定的测试
