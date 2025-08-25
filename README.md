# Tone

A Discord bot that analyzes the sentiment of chat messages in real-time. Built to help communities understand their conversation dynamics and catch potential toxicity before it escalates.

## Add to Your Server

**[Install Tone Bot](https://discord.com/oauth2/authorize?client_id=1407744855840657492&permissions=68608&integration_type=0&scope=bot)**

## Why I built this

Managing online communities is tough. Moderators can catch obvious rule violations, but subtle toxicity and negative atmosphere shifts often go unnoticed until it's too late. I wanted to build something that could help detect these patterns automatically.

Tone processes every message through sentiment analysis and tracks emotional trends over time. It's like having a mood monitor for your Discord server.

## What it does

- **Real-time sentiment analysis** of Discord messages using Hugging Face models
- **Tracks sentiment trends** to identify when conversations turn negative
- **Identifies problematic users** through sentiment pattern analysis  
- **REST API** for custom integrations and dashboards
- **Historical data storage** for long-term community health insights

## Bot Commands

After adding the bot to your server, use these commands:

- `!tone help` - Show available commands
- `!tone stats` - Display server sentiment statistics
- `!tone analyze [text]` - Analyze sentiment of any text
- `!tone leaderboard` - Show user sentiment rankings
- `!tone vibe` - Check current server mood

## Demo Instructions

### Quick Demo
1. **Add the bot** using the installation link above
2. **Test analysis**: `!tone analyze I love this Discord server`
3. **Check server stats**: `!tone stats`
4. **View leaderboard**: `!tone leaderboard`

### Extended Demo
1. **Have multiple users chat** to build sentiment history
2. **Try different sentiment types**:
   - Positive: `!tone analyze This is amazing`
   - Negative: `!tone analyze This is terrible`
   - Neutral: `!tone analyze The weather is cloudy today`
3. **Watch real-time tracking** as the bot processes regular messages
4. **Check mood**: `!tone vibe` to see current server atmosphere

## Tech stack

- **Java 17** with **Spring Boot 3.2** for the backend
- **PostgreSQL** for data persistence (H2 for local dev)
- **JDA 5.0** for Discord API integration
- **Hugging Face API** for sentiment analysis
- **Docker** for containerization and deployment

## Live demo

Currently running on AWS EC2: http://54.165.3.54:8081

Test the sentiment analysis:
```bash
curl -X POST http://54.165.3.54:8081/api/v1/tone/analyze \
  -H "Content-Type: application/json" \
  -d '{"text": "This is an amazing project!"}'
```

## Getting started

1. **Clone the repo**
```bash
git clone https://github.com/korirussell/tone.git
cd tone
```

2. **Set up environment variables**
```bash
cp env.example .env
# Edit .env with your API keys
```

3. **Get your API keys**
   - Discord bot token: [Discord Developer Portal](https://discord.com/developers/applications)
   - Hugging Face API key: [HuggingFace.co](https://huggingface.co) (free tier available)

4. **Run locally**
```bash
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run
```

The application will start on port 8081 with an in-memory H2 database.

## API endpoints

**Analyze sentiment**
```bash
POST /api/v1/tone/analyze
Content-Type: application/json

{
  "text": "Your message here"
}
```

**Health check**
```bash
GET /actuator/health
```

**Debug configuration**
```bash
GET /api/v1/tone/debug/config
```

## Discord setup

1. Create a new application at the [Discord Developer Portal](https://discord.com/developers/applications)
2. Create a bot and copy the token
3. Add the token to your `.env` file
4. Invite the bot to your server with these permissions:
   - Read Messages
   - Read Message History
   - Send Messages (for alerts)

## Deployment

I've deployed this on AWS EC2 using Docker. Full deployment instructions are in [DEPLOYMENT.md](DEPLOYMENT.md).

**Quick deployment with Docker:**
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## Project structure

```
src/main/java/com/tonediscord/tone/
├── controller/     # REST API endpoints
├── service/        # Business logic and external API calls
├── entity/         # Database models
├── repository/     # Data access layer
└── dto/           # Data transfer objects
```

## What I learned

This project taught me:
- Integrating with external APIs (Discord and Hugging Face)
- Real-time data processing and storage
- Container orchestration with Docker Compose
- AWS deployment and security group configuration
- Building robust error handling for external service dependencies

## Future improvements

- Fine-tune ML models specifically for Discord language patterns
- Add a React dashboard for data visualization
- Implement webhook notifications for external systems
- Add support for multiple Discord servers
- Create sentiment trend alerting system

## Contributing

Issues and pull requests welcome. Particularly interested in:
- Performance optimizations for high-message-volume servers
- Additional sentiment analysis models
- Better Discord event handling

---

Built by Kori Russell • [Live Demo](http://54.165.3.54:8081/actuator/health) 
