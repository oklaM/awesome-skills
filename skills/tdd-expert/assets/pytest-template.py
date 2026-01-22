"""
Pytest 测试模板

使用说明：
1. 复制此文件到你的测试目录
2. 重命名为 test_<module_name>.py
3. 根据需要修改导入和测试
"""

import pytest
from unittest.mock import Mock, patch
from your_module import YourClass, your_function


# =============================================================================
# Fixture 定义
# =============================================================================

@pytest.fixture
def instance():
    """创建一个测试用的实例"""
    return YourClass()


@pytest.fixture
def fresh_instance():
    """每个测试都获得一个新的实例"""
    return YourClass()


@pytest.fixture
def database():
    """数据库 fixture - 带 setup/teardown"""
    db = Database()
    db.connect()
    yield db
    db.cleanup()


@pytest.fixture(scope="module")
def shared_resource():
    """模块级别的共享资源"""
    resource = ExpensiveResource()
    resource.initialize()
    yield resource
    resource.cleanup()


# =============================================================================
# 参数化测试模板
# =============================================================================

@pytest.mark.parametrize("input,expected", [
    (1, 2),
    (2, 4),
    (3, 6),
    (10, 20),
])
def test_function_with_various_inputs(input, expected):
    """参数化测试示例"""
    assert your_function(input) == expected


# =============================================================================
# 异常测试模板
# =============================================================================

def test_raises_exception():
    """测试抛出异常"""
    with pytest.raises(ValueError) as exc_info:
        your_function("invalid")

    assert "invalid" in str(exc_info.value)


def test_raises_specific_exception():
    """测试特定的异常类型"""
    with pytest.raises(ValueError, match="Invalid value"):
        your_function("invalid")


# =============================================================================
# Mock 测试模板
# =============================================================================

def test_with_mock():
    """使用 Mock 的测试"""
    mock_obj = Mock()
    mock_obj.method.return_value = 42

    result = your_function(mock_obj)

    assert result == 42
    mock_obj.method.assert_called_once()


def test_with_patch():
    """使用 patch 的测试"""
    with patch('your_module.external_dependency') as mock_dep:
        mock_dep.return_value = "mocked value"

        result = your_function()

        assert result == "mocked value"
        mock_dep.assert_called_once()


# =============================================================================
# 异步测试模板
# =============================================================================

@pytest.mark.asyncio
async def test_async_function():
    """异步函数测试"""
    result = await async_function()
    assert result == expected


@pytest.mark.asyncio
async def test_async_with_fixture(async_client):
    """使用 fixture 的异步测试"""
    response = await async_client.get("/api/data")
    assert response.status_code == 200


# =============================================================================
# 测试类模板
# =============================================================================

class TestYourClass:
    """测试类示例 - 类名以 Test 开头"""

    @pytest.fixture(autouse=True)
    def setup(self):
        """自动应用到每个测试的 fixture"""
        self.instance = YourClass()

    def test_method_one(self):
        """测试方法命名: test_<description>"""
        result = self.instance.method_one()
        assert result == expected

    def test_method_two_with_exception(self):
        """测试异常"""
        with pytest.raises(ValueError):
            self.instance.method_two("invalid")


# =============================================================================
# Given-When-Then 模板
# =============================================================================

def test_given_when_then():
    """Given-When-Then 结构示例"""

    # Given (Arrange) - 准备测试数据
    calculator = Calculator()
    num1 = 5
    num2 = 3

    # When (Act) - 执行被测试的行为
    result = calculator.add(num1, num2)

    # Then (Assert) - 验证结果
    assert result == 8


# =============================================================================
# 跳过和标记测试
# =============================================================================

@pytest.mark.slow
def test_slow_operation():
    """标记为慢速测试"""
    import time
    time.sleep(5)
    assert True


@pytest.mark.skip(reason="Not implemented yet")
def test_not_ready():
    """跳过此测试"""
    assert False


@pytest.mark.skipif(sys.version_info < (3, 8), reason="Requires Python 3.8+")
def test_python38_feature():
    """条件跳过"""
    assert True


@pytest.mark.xfail(reason="Known bug")
def test_known_failure():
    """预期失败但不会中断测试"""
    assert 1 == 2


# =============================================================================
# 临时文件和目录测试
# =============================================================================

def test_with_tmp_path(tmp_path):
    """使用临时目录"""
    test_file = tmp_path / "test.txt"
    test_file.write_text("Hello, World!")

    content = test_file.read_text()
    assert content == "Hello, World!"


def test_with_tmpdir(tmpdir):
    """使用 tmpdir (旧版 API)"""
    test_file = tmpdir.join("test.txt")
    test_file.write("Hello, World!")

    content = test_file.read()
    assert content == "Hello, World!"


# =============================================================================
# 对比快照测试 (需要 pytest-icdiff 或其他插件)
# =============================================================================

def test_snapshot(snapshot):
    """快照测试示例"""
    result = your_function()
    assert result == snapshot


# =============================================================================
# 测试前后钩子
# =============================================================================

def setup_module(module):
    """模块级 setup - 在所有测试之前运行"""
    pass


def teardown_module(module):
    """模块级 teardown - 在所有测试之后运行"""
    pass


def setup_function(function):
    """函数级 setup - 在每个测试函数之前运行"""
    pass


def teardown_function(function):
    """函数级 teardown - 在每个测试函数之后运行"""
    pass


# =============================================================================
# 组合 fixtures
# =============================================================================

@pytest.fixture
def config():
    return {"host": "localhost", "port": 5432}


@pytest.fixture
def database(config):
    """依赖另一个 fixture"""
    return Database(config["host"], config["port"])


def test_with_multiple_fixtures(database, instance):
    """使用多个 fixtures"""
    result = instance.query(database)
    assert result is not None


# =============================================================================
# Monkeypatch 测试
# =============================================================================

def test_with_monkeypatch_env(monkeypatch):
    """使用 monkeypatch 修改环境变量"""
    monkeypatch.setenv("API_KEY", "test-key")
    assert os.getenv("API_KEY") == "test-key"


def test_with_monkeypatch_attribute(monkeypatch):
    """使用 monkeypatch 修改对象属性"""
    monkeypatch.setattr(obj, "attribute", value)
    assert obj.attribute == value


# =============================================================================
# Caplog 和 Capfd 测试
# =============================================================================

def test_logging(caplog):
    """测试日志输出"""
    import logging

    with caplog.at_level(logging.INFO):
        log_something()

    assert "Something" in caplog.text


def test_stdout(capsys):
    """测试标准输出"""
    print("Hello, World!")

    captured = capsys.readouterr()
    assert captured.out == "Hello, World!\n"


# =============================================================================
# Test configuration (pytest.ini)
# =============================================================================
"""
在你的项目根目录创建 pytest.ini:

[pytest]
testpaths = tests
python_files = test_*.py
python_classes = Test*
python_functions = test_*
addopts =
    -v
    --strict-markers
    --tb=short
    --cov=your_module
    --cov-report=html
    --cov-report=term-missing

markers =
    slow: marks tests as slow (deselect with '-m "not slow"')
    integration: marks tests as integration tests
    unit: marks tests as unit tests
"""
