# Tone Bot - Live Demo Script

## Quick Showcase (2-3 minutes)

### 1. Show the Live Application
```bash
# Health check - proves it's running
curl http://54.165.3.54:8081/actuator/health

# Configuration check - shows API keys are loaded
curl http://54.165.3.54:8081/api/v1/tone/debug/config
```

### 2. Demonstrate Sentiment Analysis

**Positive sentiment:**
```bash
curl -X POST http://54.165.3.54:8081/api/v1/tone/analyze \
  -H "Content-Type: application/json" \
  -d '{"text": "This project is absolutely amazing and well-designed!"}'
```
*Expected: High positive score (~0.95+)*

**Negative sentiment:**
```bash
curl -X POST http://54.165.3.54:8081/api/v1/tone/analyze \
  -H "Content-Type: application/json" \
  -d '{"text": "This is terrible spam and I hate everything about it"}'
```
*Expected: High negative score (~-0.95+)*

**Neutral/Mixed sentiment:**
```bash
curl -X POST http://54.165.3.54:8081/api/v1/tone/analyze \
  -H "Content-Type: application/json" \
  -d '{"text": "The meeting is scheduled for 3pm tomorrow"}'
```
*Expected: Lower score, closer to neutral*

### 3. Key Talking Points

**Technical Highlights:**
- "This is running live on AWS EC2 with Docker containers"
- "Real-time sentiment analysis using Hugging Face AI models"  
- "PostgreSQL database storing all analysis results"
- "RESTful API designed for Discord bot integration"

**Problem Solved:**
- "Discord moderators need proactive tools to catch toxicity"
- "This analyzes emotional tone, not just explicit content"
- "Can identify users with consistently negative communication patterns"

**Architecture:**
- "Spring Boot microservice with clean separation of concerns"
- "Docker containerization for consistent deployment"
- "External API integration with proper error handling"
- "Designed for horizontal scaling"

## For Interviews/Presentations

### Opening (30 seconds)
"I built Tone to solve a real problem in online communities - moderators can catch obvious rule violations, but subtle toxicity often goes unnoticed. This bot analyzes the emotional tone of every Discord message using AI."

### Demo (60 seconds)
*Show the live API calls above*
"As you can see, it's currently running in production on AWS and accurately detecting sentiment in real-time. The response time is under 100ms per message."

### Technical Deep-dive (60 seconds)
"Built with Spring Boot and Java 17, using Hugging Face for the AI models. PostgreSQL stores all the data for trend analysis. The whole thing is containerized with Docker and deployed on AWS EC2."

### Impact/Results (30 seconds)
"This enables proactive community moderation - instead of reacting to problems after they escalate, communities can identify negative patterns early and intervene appropriately."

## GitHub Repository

**Show the README:** https://github.com/korirussell/tone
- Professional documentation
- Clear setup instructions  
- Live demo links
- Technical architecture explanation

## Screenshots to Take

1. **Terminal showing successful API calls**
2. **GitHub repository README**
3. **AWS EC2 console showing running instance**
4. **Docker containers status**
5. **Application logs showing real processing**

## Questions You Might Get

**Q: "How does it handle rate limits?"**
A: "I implemented retry logic and respect Hugging Face's free tier limits. For production scale, we'd upgrade to their paid tier or implement caching for common phrases."

**Q: "What about privacy concerns?"** 
A: "The bot only analyzes sentiment, not content. We could add hashing for user IDs and configurable data retention policies."

**Q: "How would you scale this?"**
A: "Horizontal scaling with multiple bot instances, Redis caching for repeated phrases, and database sharding by Discord server."

**Q: "What was the biggest technical challenge?"**
A: "Managing the integration between Discord's real-time events and Hugging Face's API calls, especially handling timeouts and rate limits gracefully."
