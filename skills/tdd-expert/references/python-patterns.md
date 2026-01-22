# Python TDD 模式与最佳实践

## 测试框架选择

### pytest (推荐)

**优点**：
- 简洁优雅的语法
- 强大的 fixture 系统
- 参数化测试支持
- 丰富的插件生态

**基本结构**：
```python
import pytest

def test_simple_assertion():
    assert 1 + 1 == 2

def test_with_exception():
    with pytest.raises(ValueError):
        int("not a number")
```

### unittest

**优点**：
- Python 标准库内置
- 类似 JUnit 的结构
- 适合从 Java 转过来的开发者

**基本结构**：
```python
import unittest

class TestCalculator(unittest.TestCase):
    def test_add(self):
        self.assertEqual(1 + 1, 2)

    def test_exception(self):
        with self.assertRaises(ValueError):
            int("not a number")
```

## pytest 核心模式

### 1. Fixture 模式

**Fixture** 是 pytest 的依赖注入系统，用于管理测试资源和共享状态。

```python
import pytest

@pytest.fixture
def database():
    """Setup 和 teardown 模式"""
    db = Database()
    db.connect()
    yield db  # 测试使用这个对象
    db.cleanup()  # 清理

def test_save_user(database):
    user = User(name="Alice")
    database.save(user)
    assert database.count() == 1
```

**Fixture 作用域**：

```python
@pytest.fixture(scope="function")  # 每个测试函数一次（默认）
def temp_file():
    ...

@pytest.fixture(scope="class")  # 每个测试类一次
def class_resource():
    ...

@pytest.fixture(scope="module")  # 每个模块一次
def module_resource():
    ...

@pytest.fixture(scope="session")  # 整个测试会话一次
def database():
    ...
```

**Fixture 参数化**：

```python
@pytest.fixture(params=["mysql", "postgresql", "sqlite"])
def db_connection(request):
    """为每个数据库类型运行一次测试"""
    return Database(request.param)

def test_query(db_connection):
    # 这个测试会运行 3 次，每次使用不同的数据库
    result = db_connection.query("SELECT 1")
    assert result is not None
```

**Fixture 使用其他 Fixture**：

```python
@pytest.fixture
def config():
    return {"host": "localhost", "port": 5432}

@pytest.fixture
def database(config):
    """依赖 config fixture"""
    return Database(config["host"], config["port"])

def test_database_connection(database):
    assert database.is_connected()
```

### 2. 参数化测试

**单参数**：

```python
@pytest.mark.parametrize("input,expected", [
    (2, 4),
    (3, 9),
    (10, 100),
])
def test_square(input, expected):
    assert square(input) == expected
```

**多参数**：

```python
@pytest.mark.parametrize("a,b,expected", [
    (1, 2, 3),
    (0, 0, 0),
    (-1, 1, 0),
])
def test_add(a, b, expected):
    assert Calculator().add(a, b) == expected
```

**参数组合**：

```python
@pytest.mark.parametrize("x", [1, 2])
@pytest.mark.parametrize("y", [10, 20])
def test_multiply(x, y):
    # 会运行 4 次: (1,10), (1,20), (2,10), (2,20)
    assert Calculator().multiply(x, y) == x * y
```

### 3. 异常测试

```python
def test_raises_exception():
    with pytest.raises(ValueError) as exc_info:
        raise ValueError("Invalid value")

    assert str(exc_info.value) == "Invalid value"
    assert exc_info.type == ValueError

# 检查异常属性
def test_exception_with_attribute():
    with pytest.raises(CustomError) as exc_info:
        raise CustomError(code=404, message="Not found")

    assert exc_info.value.code == 404
    assert exc_info.value.message == "Not found"
```

### 4. 警告测试

```python
import warnings

def test_warning():
    with warnings.catch_warnings(record=True) as w:
        warnings.simplefilter("always")
        function_that_issues_warning()
        assert len(w) == 1
        assert issubclass(w[0].category, DeprecationWarning)
        assert "deprecated" in str(w[0].message).lower()
```

### 5. 标记 (Marks)

**自定义标记**：

```python
@pytest.mark.slow
def test_slow_operation():
    time.sleep(5)

@pytest.mark.integration
def test_database_integration():
    ...

# 运行特定标记的测试
# pytest -m slow
# pytest -m "not slow"
```

**pytest.ini 配置**：

```ini
[pytest]
markers =
    slow: marks tests as slow
    integration: marks tests as integration tests
    unit: marks tests as unit tests
```

**内置标记**：

```python
@pytest.mark.skip(reason="Not implemented yet")
def test_not_ready():
    ...

@pytest.mark.skipif(sys.version_info < (3, 8), reason="Requires Python 3.8+")
def test_python38_feature():
    ...

@pytest.mark.xfail(reason="Known bug")
def test_known_failure():
    assert 1 == 2  # 预期失败，但测试不会中断
```

### 6. 临时文件和目录

```python
def test_with_tmp_path(tmp_path):
    """tmp_path 是 pytest 内置 fixture"""
    file = tmp_path / "test.txt"
    file.write_text("content")
    assert file.read_text() == "content"

def test_with_tmpdir(tmpdir):
    """tmpdir 是 py.path.local 对象（旧版）"""
    file = tmpdir.join("test.txt")
    file.write("content")
    assert file.read() == "content"
```

### 7. Mock 和 Patch

**使用 unittest.mock**：

```python
from unittest.mock import Mock, patch, call

def test_basic_mock():
    mock = Mock()
    mock.method("arg1", "arg2")

    # 验证调用
    mock.method.assert_called_once_with("arg1", "arg2")
    assert mock.method.call_count == 1

def test_mock_return_value():
    mock = Mock()
    mock.method.return_value = 42

    result = mock.method()
    assert result == 42

def test_mock_side_effect():
    mock = Mock()
    mock.method.side_effect = [1, 2, 3]  # 多次调用返回不同值

    assert mock.method() == 1
    assert mock.method() == 2
    assert mock.method() == 3

def test_mock_side_effect_exception():
    mock = Mock()
    mock.method.side_effect = ValueError("Invalid")

    with pytest.raises(ValueError):
        mock.method()
```

**Patch 模式**：

```python
from unittest.mock import patch

def test_patch_class():
    """Patch 一个类"""
    with patch('module.ClassName') as mock_class:
        instance = mock_class.return_value
        instance.method.return_value = 42

        result = function_that_uses_ClassName()

        mock_class.assert_called_once()
        instance.method.assert_called_once()

def test_patch_decorator():
    """使用 decorator patch"""
    @patch('module.external_api_call')
    def test_external_dependency(mock_api):
        mock_api.return_value = {"status": "ok"}
        result = my_function()
        assert result["status"] == "ok"

# patch 完整路径
# 正确: patch('myapp.services.api_call')
# 错误: patch('api_call')  # patch 的是测试文件中的引用
```

**Mock 自动规约 (autospec)**：

```python
def test_autospec():
    """autospec 确保 mock 的 API 与真实对象匹配"""
    with patch('module.RealClass', autospec=True):
        # 如果调用 RealClass 不存在的方法，会报错
        pass
```

### 8. Monkeypatch

```python
def test_monkeypatch_env(monkeypatch):
    """monkeypatch 是 pytest 内置 fixture"""
    monkeypatch.setenv("API_KEY", "test-key")
    assert os.getenv("API_KEY") == "test-key"

def test_monkeypatch_attribute(monkeypatch):
    monkeypatch.setattr(obj, "attribute", value)
    assert obj.attribute == value

def test_monkeypatch_function(monkeypatch):
    def fake_function():
        return 42

    monkeypatch.setattr(module, "real_function", fake_function)
    assert module.real_function() == 42
```

### 9. 测试生成器

```python
def pytest_generate_tests(metafunc):
    """动态生成测试"""
    if "input" in metafunc.fixturenames:
        metafunc.parametrize("input", [1, 2, 3])

def test_dynamic(input):
    assert input > 0
```

### 10. 测试类组织

```python
class TestCalculator:
    """测试类以 Test 开头"""

    @pytest.fixture(autouse=True)
    def setup(self):
        """autouse=True 让每个测试自动使用这个 fixture"""
        self.calc = Calculator()

    def test_add_positive_numbers(self):
        assert self.calc.add(2, 3) == 5

    def test_add_negative_numbers(self):
        assert self.calc.add(-2, -3) == -5

    def test_add_zero(self):
        assert self.calc.add(5, 0) == 5
```

## 常见场景模式

### 场景 1: 测试 REST API

```python
import pytest
from fastapi.testclient import TestClient

@pytest.fixture
def client():
    """测试客户端"""
    from myapp import app
    return TestClient(app)

def test_create_user(client):
    response = client.post(
        "/users",
        json={"name": "Alice", "email": "alice@example.com"}
    )
    assert response.status_code == 201
    data = response.json()
    assert data["name"] == "Alice"
    assert "id" in data

def test_create_user_invalid_email(client):
    response = client.post(
        "/users",
        json={"name": "Alice", "email": "invalid"}
    )
    assert response.status_code == 422

@pytest.mark.parametrize("user_data,expected_status", [
    ({"name": "Bob", "email": "bob@example.com"}, 201),
    ({"name": "", "email": "test@example.com"}, 422),
    ({"email": "test@example.com"}, 422),  # 缺少 name
])
def test_create_user_validation(client, user_data, expected_status):
    response = client.post("/users", json=user_data)
    assert response.status_code == expected_status
```

### 场景 2: 测试数据库操作

```python
import pytest
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

@pytest.fixture(scope="function")
def db_session():
    """内存数据库，每个测试独立"""
    engine = create_engine("sqlite:///:memory:")
    Session = sessionmaker(bind=engine)
    Base.metadata.create_all(engine)

    session = Session()
    yield session
    session.close()

def test_create_user(db_session):
    user = User(name="Alice", email="alice@example.com")
    db_session.add(user)
    db_session.commit()

    retrieved = db_session.query(User).filter_by(name="Alice").first()
    assert retrieved is not None
    assert retrieved.email == "alice@example.com"

def test_user_not_found(db_session):
    user = db_session.query(User).filter_by(name="NonExistent").first()
    assert user is None
```

### 场景 3: 测试异步代码

```python
import pytest

@pytest.mark.asyncio
async def test_async_function():
    result = await async_function()
    assert result == expected

@pytest.mark.asyncio
async def test_async_with_fixture(async_client):
    response = await async_client.get("/api/data")
    assert response.status_code == 200
```

### 场景 4: 测试文件 I/O

```python
def test_file_operations(tmp_path):
    """使用 tmp_path fixture"""
    test_file = tmp_path / "test.txt"

    # 写入
    test_file.write_text("Hello, World!")

    # 读取
    content = test_file.read_text()
    assert content == "Hello, World!"

    # 验证存在
    assert test_file.exists()
    assert test_file.is_file()
```

### 场景 5: 测试日期时间

```python
import pytest
from freezegun import freeze_time

def test_with_frozen_time():
    """使用 freezegun 冻结时间"""
    with freeze_time("2024-01-01 12:00:00"):
        assert datetime.now() == datetime(2024, 1, 1, 12, 0, 0)

@freeze_time("2024-01-01")
def test_decorator_frozen_time():
    assert datetime.now().year == 2024

# 或者使用 mocker fixture (pytest-mock)
def test_with_mocker(mocker):
    frozen_datetime = datetime(2024, 1, 1, 12, 0, 0)
    mocker.patch('module.datetime').now.return_value = frozen_datetime
```

### 场景 6: 测试私有方法

```python
def test_private_method():
    """不推荐测试私有方法，但如果需要："""
    obj = MyClass()
    # 方式 1: 直接访问
    result = obj._private_method()

    # 方式 2: 使用公有接口
    result = obj.public_interface_that_uses_private_method()

    # 优先测试公共行为
```

## pytest 插件推荐

### pytest-cov (覆盖率)

```bash
# 安装
pip install pytest-cov

# 运行测试并生成覆盖率报告
pytest --cov=myapp tests/

# 生成 HTML 报告
pytest --cov=myapp --cov-report=html tests/
```

### pytest-mock (增强 mock)

```python
def test_with mocker_fixture(mocker):
    """mocker 是 pytest-mock 提供的 fixture"""
    mock = mocker.patch('module.function')
    mock.return_value = 42

    result = module.function()
    assert result == 42

    mock.assert_called_once()
```

### pytest-asyncio (异步测试)

```python
import pytest

@pytest.mark.asyncio
async def test_async_function():
    result = await async_operation()
    assert result == expected
```

### pytest-django (Django 测试)

```python
import pytest
from django.test import Client

@pytest.fixture
def client():
    return Client()

def test_homepage(client):
    response = client.get('/')
    assert response.status_code == 200
```

### pytest-freezegun (时间冻结)

```python
@pytest.mark.freeze_time("2024-01-01")
def test_frozen_time():
    assert datetime.now().year == 2024
```

## pytest 配置

### pytest.ini

```ini
[pytest]
# 测试文件匹配模式
python_files = test_*.py *_test.py
# 测试类匹配模式
python_classes = Test*
# 测试函数匹配模式
python_functions = test_*

# 默认命令行选项
addopts =
    -v
    --strict-markers
    --tb=short

# 标记定义
markers =
    slow: marks tests as slow
    integration: marks tests as integration tests
    unit: marks tests as unit tests

# 测试路径
testpaths = tests

# 最小覆盖率
[coverage:run]
source = myapp
omit =
    */tests/*
    */venv/*
```

### pyproject.toml

```toml
[tool.pytest.ini_options]
testpaths = ["tests"]
python_files = ["test_*.py"]
python_classes = ["Test*"]
python_functions = ["test_*"]
addopts = "-v --strict-markers"

[tool.pytest.ini_options.markers]
slow = "marks tests as slow"
integration = "marks tests as integration tests"
unit = "marks tests as unit tests"
```

## 最佳实践总结

### ✅ Do

1. **使用 fixture 管理测试资源**
   ```python
   @pytest.fixture
   def resource():
       yield Resource()
       resource.cleanup()
   ```

2. **参数化测试减少重复**
   ```python
   @pytest.mark.parametrize("input,expected", [...])
   def test_something(input, expected):
       assert func(input) == expected
   ```

3. **使用描述性测试名称**
   ```python
   def test_calculator_returns_zero_when_adding_zero():
       ...
   ```

4. **保持测试简单**
   ```python
   def test_single_behavior():
       assert single_assertion
   ```

5. **使用 Given-When-Then 结构**
   ```python
   def test_behavior():
       # Given
       setup()
       # When
       result = action()
       # Then
       assert result
   ```

### ❌ Don't

1. **不要在测试中写逻辑**
   ```python
   # 不好
   def test_with_logic():
       result = calculate()
       if result > 0:
           assert result == expected
   ```

2. **不要测试私有方法**
   ```python
   # 不好
   def test_private():
       obj._private_method()
   ```

3. **不要依赖测试顺序**
   ```python
   # 不好
   def test_step_one():
       global.state = 1

   def test_step_two():
       assert global.state == 1
   ```

4. **不要在测试中处理异常**
   ```python
   # 不好
   def test_exception_handling():
       try:
           risky_operation()
       except Exception:
           pass  # 吞掉异常
   ```

5. **不要过度 Mock**
   ```python
   # 不好 - Mock 了所有东西
   def test_over_mocked(mocker):
       mock_a = mocker.patch('module.a')
       mock_b = mocker.patch('module.b')
       mock_c = mocker.patch('module.c')
       # 没有测试真实逻辑
   ```

## 调试测试

### 使用 pdb

```python
def test_with_debugger():
    import pdb; pdb.set_trace()
    result = function_to_debug()
    assert result == expected
```

### pytest pdb 选项

```bash
# 在失败时进入 pdb
pytest --pdb

# 在开始时就进入 pdb
pytest --trace
```

### 只运行失败的测试

```bash
# 运行上次失败的测试
pytest --lf

# 先运行失败的，再运行其他
pytest --ff
```

### 显示打印输出

```bash
# 显示 print 输出
pytest -s

# 只在失败时显示输出
pytest --capture=no
```
