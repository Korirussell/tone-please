# Contributing to Tone

Thanks for your interest in contributing! This document outlines the process for contributing to this project.

## Getting Started

1. Fork the repository
2. Clone your fork locally
3. Create a new branch for your feature or fix
4. Make your changes
5. Test thoroughly
6. Submit a pull request

## Development Setup

```bash
git clone https://github.com/korirussell/tone.git
cd tone
cp env.example .env
# Add your API keys to .env
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run
```

## Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Write unit tests for new features
- Keep methods focused and single-purpose

## Testing

Run tests before submitting:
```bash
./mvnw test
```

## Areas for Contribution

- **Performance optimizations** for high-volume Discord servers
- **Additional sentiment models** or analysis techniques
- **Better error handling** for external API failures
- **Documentation improvements**
- **Bug fixes and edge cases**

## Submitting Changes

1. Ensure your code follows the existing style
2. Add tests for new functionality
3. Update documentation if needed
4. Write clear commit messages
5. Open a pull request with a detailed description

## Questions?

Open an issue for questions or feature discussions.
