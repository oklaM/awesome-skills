---
name: tdd-expert
description: "TDD (Test-Driven Development) expert that guides through the red-green-refactor cycle. Use when: (1) Starting from scratch to implement new features with TDD methodology, (2) Adding tests to existing code bases to improve coverage and quality, (3) Reviewing and improving test quality and TDD practices. Supports Python (pytest, unittest), JavaScript/TypeScript (Jest, Vitest, Mocha), and Java (JUnit, TestNG) with expert-level guidance on test design principles, mocking, refactoring, and best practices."
---

# TDD Expert

Comprehensive TDD expert that guides through test-driven development using the red-green-refactor cycle. Provides complete testing solutions for new feature development, test addition, and code review across multiple programming languages.

## Quick Start

**Identify the task type**:
1. **New feature** - Implement from scratch using TDD
2. **Add tests** - Write tests for existing code
3. **Review/Improve** - Evaluate and enhance existing tests

**Check language/framework**:
- Python: pytest, unittest
- JavaScript/TypeScript: Jest, Vitest, Mocha
- Java: JUnit 5, TestNG

**Read the appropriate reference** for language-specific patterns and best practices.

## Task Decision Tree

```
Start
 ├─ New feature development?
 │   └─ → Go to "TDD for New Features"
 │
 ├─ Adding tests to existing code?
 │   └─ → Go to "Writing Tests for Existing Code"
 │
 └─ Reviewing/improving tests?
     └─ → Go to "Test Review and Improvement"
```

## Task 1: TDD for New Features

When implementing a feature from scratch, follow the red-green-refactor cycle:

### Step 1: Understand Requirements

Clarify the feature specification:
- What are the inputs and expected outputs?
- What are the edge cases?
- What constraints exist?
- What exceptions should be handled?

### Step 2: Plan Test Cases

Break down the feature into testable scenarios:
1. Start with the simplest case (happy path)
2. Add edge cases (empty, null, boundary values)
3. Add error cases (invalid inputs, exceptions)
4. Add integration scenarios if needed

### Step 3: Execute TDD Cycles

For each test case:

**RED** - Write a failing test:
- Create a test that clearly describes the desired behavior
- Run the test to confirm it fails
- Ensure the failure is expected (not a syntax/error)

**GREEN** - Make it pass:
- Write the minimal code to make the test pass
- Don't worry about perfection or cleanliness
- Run the test to confirm it passes
- Move to the next test or refactor

**REFACTOR** - Improve the code:
- Look for duplication and code smells
- Apply refactoring patterns (see [refactoring-guide.md](references/refactoring-guide.md))
- Run all tests to ensure nothing breaks
- Keep tests passing at all times

### Step 4: Expand Coverage

Progressively add more complex scenarios:
- Normal cases → Edge cases → Error cases → Integration
- Each addition follows its own red-green-refactor cycle

## Task 2: Writing Tests for Existing Code

When adding tests to existing code:

### Step 1: Analyze the Code

- Understand what the code does
- Identify public APIs and behaviors
- Note dependencies and external interactions
- Look for complex logic that needs testing

### Step 2: Prioritize Test Coverage

Start with:
1. **Critical business logic** - Core functionality
2. **Complex methods** - Hard to understand logic
3. **Error handling** - Exception paths
4. **Edge cases** - Boundary conditions

### Step 3: Write Characterization Tests

For unclear code, write characterization tests:
- Call the code with various inputs
- Record what it actually does
- Use tests to document current behavior
- Refactor once tests protect the behavior

### Step 4: Use Test Templates

Copy and adapt the appropriate template from `assets/`:
- Python: [pytest-template.py](assets/pytest-template.py)
- JavaScript: [jest-template.test.js](assets/jest-template.test.js)
- Java: [junit-template.java](assets/junit-template.java)

### Step 5: Apply Test Design Principles

Reference [test-design-principles.md](references/test-design-principles.md) for:
- Given-When-Then structure
- Test isolation and independence
- Descriptive test naming
- Appropriate use of mocks and test doubles

## Task 3: Test Review and Improvement

When reviewing existing tests:

### Step 1: Evaluate Test Quality

Check for:
- **Coverage** - Are all important behaviors tested?
- **Clarity** - Do tests clearly describe what they test?
- **Independence** - Do tests run in isolation?
- **Maintainability** - Are tests easy to understand and modify?

### Step 2: Identify Code Smells

Look for common test smells (see [refactoring-guide.md](references/refactoring-guide.md)):
- Duplicated test setup code
- Tests that are too large or complex
- Brittle tests that break easily
- Over-mocked tests that don't verify real behavior
- Tests with no assertions (green tests)

### Step 3: Recommend Improvements

Suggest specific improvements:
- Extract common setup into fixtures
- Use parameterized tests for similar cases
- Replace magic values with descriptive constants
- Improve test names to describe behavior
- Add missing edge case tests
- Remove unnecessary mocks

### Step 4: Validate with Principles

Ensure tests follow:
- **FIRST** principles (Fast, Independent, Repeatable, Self-Validating, Timely)
- **Given-When-Then** structure
- **Single responsibility** - one behavior per test
- **Behavior over implementation** - test outcomes, not internals

## Language-Specific Guidance

### Python

**Reference**: [python-patterns.md](references/python-patterns.md)

**Key patterns**:
- Use fixtures for setup/teardown
- Leverage parametrize for data-driven tests
- Use pytest.raises for exception testing
- Mock with unittest.mock or pytest-mock

**Common workflows**:
- Testing async code with pytest-asyncio
- Testing FastAPI/Flask endpoints
- Database testing with SQLAlchemy
- File I/O testing with tmp_path

### JavaScript/TypeScript

**Reference**: [javascript-patterns.md](references/javascript-patterns.md)

**Key patterns**:
- Use describe/test blocks for organization
- Mock functions and modules with Jest
- Test async code with async/await or .resolves/.rejects
- Use React Testing Library for components

**Common workflows**:
- Testing Express/Fastify APIs
- Testing React components
- Async/await testing patterns
- Timer and callback testing

### Java

**Reference**: [java-patterns.md](references/java-patterns.md)

**Key patterns**:
- Use @BeforeEach/@AfterEach for setup/teardown
- Parameterized tests with @ParameterizedTest
- Assertions with Assertions class or AssertJ
- Mocking with Mockito

**Common workflows**:
- Testing Spring Boot applications
- JPA repository testing
- REST controller testing
- Service layer unit testing

## TDD Best Practices

### DO ✓

1. **Start simple** - Begin with the easiest test case
2. **Small steps** - One test at a time, small refactorings
3. **Descriptive names** - Test names should explain the behavior
4. **Test behavior** - Focus on what, not how
5. **Keep tests fast** - Use test doubles for slow dependencies
6. **Run tests frequently** - After every change
7. **Refactor continuously** - Clean up code between tests

### DON'T ✗

1. **Don't skip tests** - Always write tests first
2. **Don't batch tests** - Write one test, not many at once
3. **Don't test internals** - Test public APIs, not private methods
4. **Don't ignore failing tests** - Fix or understand failures immediately
5. **Don't over-mock** - Mock only what's necessary
6. **Don't test trivial code** - Avoid testing getters/setters
7. **Don't rely on test order** - Tests should run independently

## When to Use References

### Load references based on task:

**test-design-principles.md** - Always useful for:
- Understanding TDD fundamentals
- Writing clean, maintainable tests
- Applying FIRST principles
- Avoiding common anti-patterns

**python-patterns.md** - For Python developers:
- Learning pytest fixtures and features
- Mocking strategies
- Testing async code
- Framework-specific patterns (Django, FastAPI)

**javascript-patterns.md** - For JS/TS developers:
- Jest/Vitest testing patterns
- React component testing
- Mocking modules and functions
- Async testing strategies

**java-patterns.md** - For Java developers:
- JUnit 5 assertions and lifecycle
- Parameterized testing
- Spring Boot test patterns
- Mockito usage

**refactoring-guide.md** - When improving code:
- Identifying code smells
- Choosing refactoring techniques
- Safely applying refactorings
- Maintaining test coverage during refactoring

## Using Templates

Templates in `assets/` provide starting points:

1. **Copy** the appropriate template to your project
2. **Rename** to match your module/function
3. **Adapt** imports and class/function names
4. **Fill in** test cases based on requirements
5. **Run** the tests to verify setup
6. **Iterate** adding more tests following TDD cycle

Templates include:
- Common test patterns (fixtures, hooks, lifecycle)
- Examples of assertions and matchers
- Mock/stub patterns
- Parameterized test examples
- Async test patterns

## Output Format

When providing TDD guidance:

1. **Start with test cases** - List what needs to be tested
2. **Show the red-green-refactor cycle** for each major test
3. **Provide complete code** - Both test and implementation
4. **Explain the reasoning** - Why this test, why this implementation
5. **Suggest next steps** - What to test next
6. **Include file structure** - Where to place tests

**Example output format**:

```markdown
## Test Case 1: [Description]

**RED - Write failing test**:
```python
def test_[function]_[scenario]():
    # Given
    ...

    # When
    result = function_under_test(...)

    # Then
    assert result == expected
```

**GREEN - Minimal implementation**:
```python
def function_under_test(...):
    return expected  # Simplest passing code
```

**REFACTOR - Improve**:
```python
def function_under_test(...):
    # Improved implementation
    ...
```

**Next**: Test Case 2 - [Next scenario]
```

## Common Scenarios

### Scenario: API Endpoint Testing

1. Test success path (200 status)
2. Test validation errors (400 status)
3. Test not found (404 status)
4. Test server errors (500 status)
5. Test authentication/authorization

### Scenario: Data Transformation

1. Test normal input
2. Test empty input
3. Test null/undefined input
4. Test boundary values
5. Test invalid data

### Scenario: State Management

1. Test initial state
2. Test state transitions
3. Test state persistence
4. Test state reset
5. Test concurrent access

## Resources

### references/

Detailed documentation loaded as needed:
- **test-design-principles.md** - Core TDD principles and patterns
- **python-patterns.md** - Python/pytest specific guidance
- **javascript-patterns.md** - JavaScript/TypeScript testing patterns
- **java-patterns.md** - Java/JUnit testing practices
- **refactoring-guide.md** - Code smells and refactoring techniques

### assets/

Ready-to-use test templates:
- **pytest-template.py** - Python pytest template with fixtures, mocks, async examples
- **jest-template.test.js** - Jest/JavaScript template with hooks, mocking, async patterns
- **junit-template.java** - JUnit 5 template with lifecycle, parameterized tests, assertions

Copy these templates to jumpstart test development.
