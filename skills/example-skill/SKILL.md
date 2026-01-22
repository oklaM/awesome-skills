---
name: example-skill
description: 当需要演示技能结构或创建新技能模板时使用此技能
category: development
version: 1.0.0
---

# Example Skill

## 用途
这是一个示例技能，用于展示 Claude Code 技能的标准结构和格式。你可以基于这个模板创建自己的技能。

## 使用方法
1. 复制 `skills/example-skill` 目录
2. 重命名为你的技能名称（使用 kebab-case）
3. 编辑 SKILL.md 文件，填写技能的具体内容
4. 在 `.claude-plugin/plugin.json` 中注册你的新技能
5. 在 `marketplace.json` 中添加技能信息

## 指令
当用户调用此技能时：
1. 首先确认用户的具体需求
2. 根据需求提供相应的帮助或示例
3. 如果是创建新技能，引导用户按照标准结构进行

## 技能结构模板

```markdown
---
name: your-skill-name
description: 简短描述技能的功能
category: development|productivity|documentation|testing|devops|data|security
version: 1.0.0
---

# 技能标题

## 用途
详细描述这个技能的用途和使用场景

## 使用方法
1. 第一步
2. 第二步
3. 第三步

## 指令
具体的技能指令内容，告诉 Claude 如何执行这个技能

## 示例
提供实际使用示例
```

## 示例

### 示例 1：创建新技能
用户：我想创建一个代码格式化的技能
Claude：我来帮你创建一个代码格式化技能。首先，让我们创建技能目录...

### 示例 2：理解技能结构
用户：SKILL.md 的 frontmatter 部分是什么？
Claude：frontmatter 部分（---包围的 YAML）包含了技能的元数据...
