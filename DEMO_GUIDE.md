# Tone Bot Demo Guide

This guide provides step-by-step instructions for demonstrating the Tone Discord bot's capabilities.

## Installation

**Add the bot to your Discord server:**
[Install Tone Bot](https://discord.com/oauth2/authorize?client_id=1407744855840657492&permissions=68608&integration_type=0&scope=bot)

## Basic Demo Script (5 minutes)

### Step 1: Introduction
"This is Tone, a Discord bot I built that analyzes message sentiment in real-time using AI. It helps track the mood of conversations and identify trends."

### Step 2: Test Commands
```
!tone help
```
Show available commands and explain functionality.

### Step 3: Live Analysis
```
!tone analyze I absolutely love this Discord server and everyone here is amazing
```
Demonstrate real-time sentiment analysis with positive text.

```
!tone analyze This is the worst thing I have ever seen in my entire life
```
Show negative sentiment detection.

```
!tone analyze The weather is cloudy today with a chance of rain
```
Display neutral sentiment classification.

### Step 4: Server Statistics
```
!tone stats
```
Show current server sentiment statistics (requires message history).

### Step 5: User Rankings
```
!tone leaderboard
```
Display user sentiment rankings (demonstrates data persistence).

### Step 6: Mood Check
```
!tone vibe
```
Check overall server atmosphere.

## Extended Demo Script (10-15 minutes)

### Setup Phase
1. **Invite multiple users** to participate in chat
2. **Generate conversation** with mixed sentiment messages
3. **Let the bot process** several messages naturally

### Analysis Phase
1. **Real-time processing**: Show how the bot analyzes messages as they appear
2. **Historical data**: Use `!tone stats` to show accumulated data
3. **User comparison**: Use `!tone leaderboard` to compare sentiment patterns
4. **Trend analysis**: Use `!tone vibe` to assess current mood

### Technical Showcase
1. **API Integration**: Explain Hugging Face AI integration
2. **Data Persistence**: Show how PostgreSQL stores sentiment history
3. **Spring Boot Architecture**: Discuss scalable backend design
4. **AWS Deployment**: Mention cloud hosting capabilities

## Demo Tips

### Best Practices
- **Start with explanation** of the problem the bot solves
- **Use varied test messages** to show different sentiment levels
- **Encourage participation** from audience members
- **Explain technical decisions** briefly but clearly

### Common Questions
- **"How accurate is it?"** - Explain AI model limitations and training
- **"Can it detect sarcasm?"** - Discuss current limitations
- **"How much data does it store?"** - Explain privacy considerations
- **"Can it work in multiple servers?"** - Confirm scalability

### Troubleshooting
- **Bot not responding**: Check bot permissions and online status
- **No statistics**: Requires message history to build data
- **Commands not working**: Ensure proper command syntax with `!tone`

## Sample Conversation Flow

```
User 1: Hey everyone, how's it going?
User 2: Great! Just finished my project presentation
User 3: That's awesome! I'm really excited about this new feature
Bot: [Processes messages in background]

Demo: !tone stats
Bot: [Shows positive trend in recent messages]

Demo: !tone analyze I'm having the worst day ever
Bot: Sentiment: NEGATIVE (Score: 0.89)

Demo: !tone vibe  
Bot: [Shows current server mood assessment]
```

## Post-Demo Discussion Points

- **Real-world applications**: Community moderation, mental health awareness
- **Technical challenges**: Rate limiting, API integration, real-time processing
- **Future enhancements**: Dashboard, machine learning improvements, webhook integrations
- **Development process**: From concept to deployment on AWS

## Resources

- **GitHub Repository**: [Link to your repo]
- **Live Demo**: http://54.165.3.54:8081/actuator/health
- **Installation Link**: https://discord.com/oauth2/authorize?client_id=1407744855840657492&permissions=68608&integration_type=0&scope=bot
