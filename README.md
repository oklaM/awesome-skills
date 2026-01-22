# Awesome Claude Code Skills

A curated collection of skills for Claude Code - enhancing your AI-assisted development workflow.

## Quick Start

### Installation

```bash
/plugin marketplace add oklaM/awesome-skills
```

### Verify Installation

```bash
/context
```

## Included Skills

### Example Skill
- **Description**: A template skill demonstrating the standard structure
- **Category**: Development
- **Usage**: `/example-skill`

## Usage

1. Install the skills collection using the command above
2. Check loaded skills with `/context`
3. Invoke any skill using `/skill-name`

## Adding New Skills

1. Create a new directory in `skills/your-skill-name`
2. Add a `SKILL.md` file with the following structure:

```markdown
---
name: your-skill-name
description: Brief description
category: development|productivity|documentation|testing|devops
version: 1.0.0
---

# Your Skill Title

## Purpose
Describe what this skill does

## Instructions
How Claude should execute this skill
```

3. Update `.claude-plugin/plugin.json` to register the skill
4. Update `marketplace.json` to add it to the marketplace

## Repository Structure

```
awesome-skills/
├── .claude-plugin/
│   └── plugin.json          # Plugin configuration
├── skills/
│   ├── example-skill/
│   │   └── SKILL.md         # Skill definition
│   └── your-skill/
│       └── SKILL.md
├── marketplace.json          # Marketplace metadata
├── README.md
└── LICENSE
```

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a new skill in the `skills/` directory
3. Follow the standard skill structure
4. Update configuration files
5. Submit a pull request

## License

MIT License - see [LICENSE](LICENSE) for details

## Links

- [Claude Code Documentation](https://docs.claude.com)
- [Repository](https://github.com/oklaM/awesome-skills)
