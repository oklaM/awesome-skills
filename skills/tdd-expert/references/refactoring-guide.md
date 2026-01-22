# TDD 重构指导

## 重构原则

### 何时重构

在 TDD 循环中，重构是在**绿阶段**之后进行的：

1. ✅ 测试通过后
2. ✅ 发现代码重复
3. ✅ 识别代码异味
4. ✅ 需要提高可读性
5. ❌ 测试失败时（先让测试通过）

### 重构的安全网

**测试保护重构**：

```python
# 1. 确保所有测试通过（绿状态）
pytest tests/  # ✅ All passed

# 2. 进行小步重构
# 修改代码...

# 3. 立即运行测试
pytest tests/  # 应该仍然 ✅ All passed

# 4. 如果失败，立即回滚或修复
# 不要继续重构
```

## 代码异味识别

### 1. 重复代码 (Duplicated Code)

**症状**：
- 相同或相似的代码出现在多处
- Copy-paste 编程

**重构方法**：
- **提取方法** (Extract Method)
- **提取类** (Extract Class)
- **模板方法模式** (Template Method)

**示例**：

```python
# 重构前 - 重复代码
def process_user_a(user):
    if user.age >= 18:
        print("Adult")
        send_email(user)
        update_database(user)
    else:
        print("Minor")

def process_user_b(user):
    if user.age >= 18:
        print("Adult")  # 重复
        send_email(user)
        update_database(user)
    else:
        print("Minor")
```

```python
# 重构后 - 提取方法
def process_adult_user(user):
    print("Adult")
    send_email(user)
    update_database(user)

def process_user(user):
    if user.age >= 18:
        process_adult_user(user)
    else:
        print("Minor")
```

### 2. 过长方法 (Long Method)

**症状**：
- 方法超过 20-30 行
- 难以理解
- 做了多件事

**重构方法**：
- **提取方法** (Extract Method)
- **用查询替代临时变量** (Replace Temp with Query)
- **引入参数对象** (Introduce Parameter Object)

**示例**：

```python
# 重构前 - 100+ 行的方法
def calculate_order_total(order):
    subtotal = 0
    for item in order.items:
        price = item.price
        quantity = item.quantity
        discount = item.discount
        item_total = price * quantity * (1 - discount)
        subtotal += item_total

    tax = subtotal * 0.1
    shipping = 0
    if order.country == "US":
        shipping = 5.99
    elif order.country == "CA":
        shipping = 7.99
    else:
        shipping = 15.99

    if order.priority == "express":
        shipping *= 1.5

    total = subtotal + tax + shipping
    return total
```

```python
# 重构后 - 分解为小方法
def calculate_order_total(order):
    subtotal = calculate_subtotal(order)
    tax = calculate_tax(subtotal)
    shipping = calculate_shipping(order)
    return subtotal + tax + shipping

def calculate_subtotal(order):
    return sum(
        item.price * item.quantity * (1 - item.discount)
        for item in order.items
    )

def calculate_tax(subtotal):
    return subtotal * 0.1

def calculate_shipping(order):
    base_shipping = get_base_shipping(order.country)
    multiplier = 1.5 if order.priority == "express" else 1.0
    return base_shipping * multiplier

def get_base_shipping(country):
    rates = {"US": 5.99, "CA": 7.99}
    return rates.get(country, 15.99)
```

### 3. 过大类 (Large Class)

**症状**：
- 类有太多字段
- 类有太多方法
- 类做了太多事

**重构方法**：
- **提取类** (Extract Class)
- **提取子类** (Extract Subclass)
- **提取接口** (Extract Interface)

**示例**：

```python
# 重构前 - 做太多事的类
class User:
    def __init__(self, name, email, password):
        self.name = name
        self.email = email
        self.password = password

    def validate_password(self, password):
        return self.password == password

    def send_email(self, subject, body):
        # 发邮件逻辑
        pass

    def save_to_database(self):
        # 数据库逻辑
        pass

    def generate_report(self):
        # 报告生成逻辑
        pass
```

```python
# 重构后 - 单一职责
class User:
    def __init__(self, name, email, password):
        self.name = name
        self.email = email
        self.password = password

    def validate_password(self, password):
        return self.password == password

class EmailService:
    def send(self, user, subject, body):
        # 发邮件逻辑
        pass

class UserRepository:
    def save(self, user):
        # 数据库逻辑
        pass

class ReportGenerator:
    def generate(self, user):
        # 报告生成逻辑
        pass
```

### 4. 过长参数列表 (Long Parameter List)

**症状**：
- 方法参数超过 3-4 个
- 难以理解和维护

**重构方法**：
- **引入参数对象** (Introduce Parameter Object)
- **保留整个对象** (Preserve Whole Object)

**示例**：

```python
# 重构前
def create_user(name, email, age, address, city, country, postal_code):
    user = User()
    user.name = name
    user.email = email
    user.age = age
    user.address = address
    user.city = city
    user.country = country
    user.postal_code = postal_code
    return user

# 调用
create_user("Alice", "alice@example.com", 25,
            "123 Main St", "NYC", "US", "10001")
```

```python
# 重构后 - 使用参数对象
@dataclass
class UserDTO:
    name: str
    email: str
    age: int
    address: str
    city: str
    country: str
    postal_code: str

def create_user(user_dto: UserDTO):
    user = User()
    user.name = user_dto.name
    user.email = user_dto.email
    user.age = user_dto.age
    # ...
    return user

# 调用
dto = UserDTO(
    name="Alice",
    email="alice@example.com",
    age=25,
    address="123 Main St",
    city="NYC",
    country="US",
    postal_code="10001"
)
create_user(dto)
```

### 5. 发散式变化 (Divergent Change)

**症状**：
- 一个类因多种原因而变化
- 修改一个 bug 需要改多个类
- 添加一个功能需要改多个方法

**重构方法**：
- **提取类** (Extract Class)
- 单一职责原则

### 6. 霰弹式修改 (Shotgun Surgery)

**症状**：
- 每次修改都需要改多个类/方法
- 相关逻辑分散在各处

**重构方法**：
- **移动方法** (Move Method)
- **内联类** (Inline Class)

### 7. 依恋情结 (Feature Envy)

**症状**：
- 方法频繁访问另一个对象的数据
- 方法放在错误的类中

**重构方法**：
- **移动方法** (Move Method)
- **提取方法** (Extract Method)

**示例**：

```python
# 重构前 - Order 类过度使用 PriceCalculator 的数据
class Order:
    def calculate_total(self, calculator):
        base = calculator.base_price
        tax = calculator.tax_rate
        discount = calculator.discount
        return (base + base * tax) * (1 - discount)
```

```python
# 重构后 - 移动方法到 PriceCalculator
class PriceCalculator:
    def __init__(self, base_price, tax_rate, discount):
        self.base_price = base_price
        self.tax_rate = tax_rate
        self.discount = discount

    def calculate_total(self):
        return (self.base_price + self.base_price * self.tax_rate) * (1 - self.discount)

class Order:
    def __init__(self, calculator):
        self.calculator = calculator

    def calculate_total(self):
        return self.calculator.calculate_total()
```

### 8. 数据泥团 (Data Clumps)

**症状**：
- 多个参数总是一起出现
- 多个字段总是一起使用

**重构方法**：
- **提取类** (Extract Class)
- **引入参数对象** (Introduce Parameter Object)

**示例**：

```python
# 重构前 - 数据总是一起出现
def book_flight(from_city, to_city, departure_date, return_date):
    pass

def check_availability(from_city, to_city, departure_date, return_date):
    pass

def cancel_booking(from_city, to_city, departure_date, return_date):
    pass
```

```python
# 重构后 - 提取为对象
@dataclass
class FlightItinerary:
    from_city: str
    to_city: str
    departure_date: datetime
    return_date: datetime

def book_flight(itinerary: FlightItinerary):
    pass

def check_availability(itinerary: FlightItinerary):
    pass

def cancel_booking(itinerary: FlightItinerary):
    pass
```

### 9. 基本类型偏执 (Primitive Obsession)

**症状**：
- 过度使用基本类型（字符串、数字）
- 应该是对象的值变成了基本类型

**重构方法**：
- **以对象替代基本类型** (Replace Data Value with Object)
- **以类替代类型码** (Replace Type Code with Class)
- **以子类替代类型码** (Replace Type Code with Subclasses)
- **以状态/策略模式替代类型码** (Replace Type Code with State/Strategy)

**示例**：

```python
# 重构前 - 使用字符串和数字表示类型
class User:
    def __init__(self, name, user_type):
        self.name = name
        self.user_type = user_type  # "admin", "user", "guest"

    def get_permissions(self):
        if self.user_type == "admin":
            return ["read", "write", "delete"]
        elif self.user_type == "user":
            return ["read", "write"]
        else:
            return ["read"]
```

```python
# 重构后 - 使用枚举和多态
from enum import Enum
from abc import ABC, abstractmethod

class UserType(Enum):
    ADMIN = "admin"
    REGULAR = "user"
    GUEST = "guest"

class PermissionStrategy(ABC):
    @abstractmethod
    def get_permissions(self):
        pass

class AdminPermissions(PermissionStrategy):
    def get_permissions(self):
        return ["read", "write", "delete"]

class RegularPermissions(PermissionStrategy):
    def get_permissions(self):
        return ["read", "write"]

class GuestPermissions(PermissionStrategy):
    def get_permissions(self):
        return ["read"]

class User:
    def __init__(self, name, permission_strategy):
        self.name = name
        self.permission_strategy = permission_strategy

    def get_permissions(self):
        return self.permission_strategy.get_permissions()
```

### 10. 临时字段 (Temporary Field)

**症状**：
- 某些字段只在特定情况下使用
- 字段有时为 None

**重构方法**：
- **提取类** (Extract Class)
- **引入 Null 对象** (Introduce Null Object)

### 11. 被拒绝的遗赠 (Refused Bequest)

**症状**：
- 子类不需要父类的某些方法
- 继承层次不合理

**重构方法**：
- **组合优于继承** (Composition over Inheritance)
- **委托** (Replace Inheritance with Delegation)

### 12. 过多的注释 (Comments)

**症状**：
- 代码有大量注释解释"做什么"
- 注释比代码还多

**重构方法**：
- **提取方法** (Extract Method) - 用方法名代替注释
- **引入断言** (Introduce Assertion)

**示例**：

```python
# 重构前 - 依赖注释解释
def process_order(order):
    # 检查库存
    if order.quantity > inventory.quantity:
        return False

    # 计算折扣
    discount = 0
    if order.customer.is_vip:
        discount = 0.1  # VIP 客户 10% 折扣
    elif order.total > 100:
        discount = 0.05  # 超过 100 美元 5% 折扣

    # 应用折扣
    total = order.total * (1 - discount)

    # 保存订单
    save_to_database(order)
    return True
```

```python
# 重构后 - 用方法名代替注释
def process_order(order):
    if not has_sufficient_inventory(order):
        return False

    discount = calculate_discount(order)
    total = apply_discount(order.total, discount)
    save_to_database(order)
    return True

def has_sufficient_inventory(order):
    return order.quantity <= inventory.quantity

def calculate_discount(order):
    if order.customer.is_vip:
        return 0.1  # VIP discount
    if order.total > 100:
        return 0.05  # Volume discount
    return 0.0

def apply_discount(total, discount):
    return total * (1 - discount)
```

## 重构技巧

### 1. 提取方法 (Extract Method)

**目的**：将一段代码提取到独立的方法中

**步骤**：
1. 创建新方法，命名为解释它做的事
2. 复制代码到新方法
3. 在原位置调用新方法
4. 运行测试
5. 如果需要，处理变量作用域

**示例**：

```python
# Before
def print_invoice(order):
    print(f"Total: {order.total}")
    print(f"Tax: {order.tax}")
    print(f"Shipping: {order.shipping}")
    print(f"Grand Total: {order.total + order.tax + order.shipping}")

# After
def print_invoice(order):
    print_totals(order)
    print_grand_total(order)

def print_totals(order):
    print(f"Total: {order.total}")
    print(f"Tax: {order.tax}")
    print(f"Shipping: {order.shipping}")

def print_grand_total(order):
    grand_total = order.total + order.tax + order.shipping
    print(f"Grand Total: {grand_total}")
```

### 2. 内联方法 (Inline Method)

**目的**：当一个方法的方法体和它的名称一样清晰时，移除该方法

**示例**：

```python
# Before
def get_rating(driver):
    return driver.more_than_five_late_deliveries() ? 2 : 1

def more_than_five_late_deliveries(driver):
    return driver.number_of_late_deliveries > 5

# After
def get_rating(driver):
    return driver.number_of_late_deliveries > 5 ? 2 : 1
```

### 3. 提取变量 (Extract Variable)

**目的**：让表达式更易读

**示例**：

```python
# Before
def price(order):
    return order.quantity * order.item_price -
           max(0, order.quantity - 500) * order.item_price * 0.05 +
           min(order.quantity * order.item_price * 0.1, 100.0)

# After
def price(order):
    base_price = order.quantity * order.item_price
    quantity_discount = max(0, order.quantity - 500) * order.item_price * 0.05
    shipping = min(base_price * 0.1, 100.0)
    return base_price - quantity_discount + shipping
```

### 4. 内联变量 (Inline Variable)

**目的**：当变量只被使用一次，且不影响可读性时移除它

**示例**：

```python
# Before
def hasPermission(user, permission):
    return user.permissions.contains(permission)

def is_admin(user):
    is_admin = user.role == "admin"
    return is_admin

# After
def is_admin(user):
    return user.role == "admin"
```

### 5. 用查询替代临时变量 (Replace Temp with Query)

**目的**：将临时变量替换为方法调用

**示例**：

```python
# Before
def calculate_price(order):
    base_price = order.quantity * order.item_price
    if base_price > 1000:
        return base_price * 0.9
    else:
        return base_price

# After
def calculate_price(order):
    if base_price() > 1000:
        return base_price() * 0.9
    else:
        return base_price()

def base_price():
    return order.quantity * order.item_price
```

### 6. 引入解释性变量 (Introduce Explaining Variable)

**目的**：将复杂表达式分解为有意义的变量

**示例**：

```python
# Before
if (platform.toUpperCase().indexOf("MAC") > -1) &&
   (browser.toUpperCase().indexOf("IE") > -1) &&
   wasInitialized() && resize > 0:
    # do something

# After
def is_mac_platform():
    return platform.toUpperCase().indexOf("MAC") > -1

def is_ie_browser():
    return browser.toUpperCase().indexOf("IE") > -1

def was_resized():
    return resize > 0

if is_mac_platform() and is_ie_browser() and wasInitialized() and was_resized():
    # do something
```

### 7. 分解条件 (Decompose Conditional)

**目的**：将复杂的 if/else 分解为独立方法

**示例**：

```python
# Before
def calculate_pay_amount(employee):
    if employee.is_dead:
        result = dead_amount()
    elif employee.is_separated:
        result = separated_amount()
    elif employee.is_retired:
        result = retired_amount()
    else:
        result = normal_pay_amount()
    return result

# After
def calculate_pay_amount(employee):
    if employee.is_dead:
        return dead_amount()
    if employee.is_separated:
        return separated_amount()
    if employee.is_retired:
        return retired_amount()
    return normal_pay_amount()
```

### 8. 合并条件 (Consolidate Conditional)

**目的**：将多个条件检查合并为一个

**示例**：

```python
# Before
def is_eligible_for_disability(employee):
    if employee.seniority < 2:
        return False
    if employee.months_disabled > 12:
        return False
    if employee.is_part_time:
        return False
    return True

# After
def is_eligible_for_disability(employee):
    return (employee.seniority >= 2 and
            employee.months_disabled <= 12 and
            not employee.is_part_time)
```

### 9. 用多态替代条件 (Replace Conditional with Polymorphism)

**目的**：使用多态消除复杂的条件语句

**示例**：

```python
# Before
class Employee:
    def get_pay_amount(self):
        if self.type == "ENGINEER":
            return self.salary
        elif self.type == "MANAGER":
            return self.salary + self.bonus
        elif self.type == "SALESMAN":
            return self.salary + self.commission
```

```python
# After
from abc import ABC, abstractmethod

class Employee(ABC):
    @abstractmethod
    def get_pay_amount(self):
        pass

class Engineer(Employee):
    def get_pay_amount(self):
        return self.salary

class Manager(Employee):
    def get_pay_amount(self):
        return self.salary + self.bonus

class Salesman(Employee):
    def get_pay_amount(self):
        return self.salary + self.commission
```

### 10. 用卫语句替代嵌套条件 (Replace Nested Conditional with Guard Clauses)

**目的**：使用卫语句简化嵌套的 if 语句

**示例**：

```python
# Before
def get_pay_amount(employee):
    result = 0
    if employee.is_dead:
        result = dead_amount()
    else:
        if employee.is_separated:
            result = separated_amount()
        else:
            if employee.is_retired:
                result = retired_amount()
            else:
                result = normal_pay_amount()
    return result
```

```python
# After
def get_pay_amount(employee):
    if employee.is_dead:
        return dead_amount()
    if employee.is_separated:
        return separated_amount()
    if employee.is_retired:
        return retired_amount()
    return normal_pay_amount()
```

## 重构的节奏

### 小步前进

```python
# 1. 运行测试（全部通过）✅
# 2. 进行一次小重构
# 3. 运行测试（全部通过）✅
# 4. 提交代码
# 5. 重复
```

### 重构检查清单

- [ ] 所有测试通过
- [ ] 代码更易读
- [ ] 消除了重复
- [ ] 保持了行为不变
- [ ] 没有降低性能（除非有明确目标）

### 重构的坏味道

- ⚠️ 重构时同时添加新功能
- ⚠️ 在测试失败时继续重构
- ⚠️ 一次重构太多
- ⚠️ 没有测试保护就重构
- ⚠️ 重构后不运行测试

## 总结

重构的核心原则：

1. **小步前进** - 每次只做一个小的改变
2. **测试保护** - 依赖测试确保行为不变
3. **持续改进** - 定期重构，不要积累技术债务
4. **理解后再改** - 理解代码的意图再重构
5. **保持简单** - KISS 原则，避免过度设计

记住：重构不是终点，而是持续改进代码质量的过程。
