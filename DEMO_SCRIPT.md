# 🎬 Tone Bot - Portfolio Demo Video Script

## **Demo Duration: 3-4 minutes**

---

### **🎬 INTRO (30 seconds)**

**[Screen: Your IDE with the project open]**

> "Hi! I'm [Your Name], and I want to show you **Tone** - a production-ready Discord sentiment analysis bot I built using Spring Boot and AI. This project demonstrates full-stack development, cloud deployment, and real-time AI integration."

**[Quick montage of code files, showing the project structure]**

---

### **🏗️ ARCHITECTURE OVERVIEW (45 seconds)**

**[Screen: Draw.io or whiteboard diagram]**

> "Here's the architecture: **Spring Boot** backend with **JDA** for Discord integration, **Hugging Face AI** for sentiment analysis, **PostgreSQL** database for persistence, all containerized with **Docker** and deployed on **AWS EC2**."

**[Point to each component]**
- Discord Bot (JDA)
- Spring Boot API
- Hugging Face AI
- PostgreSQL Database
- AWS EC2 Deployment

---

### **🔬 CODE WALKTHROUGH (60 seconds)**

**[Screen: IDE showing key code files]**

> "Let me show you the key components:"

**1. Sentiment Service (15s)**
```java
// Show SentimentAnalysisService.java
```
> "The sentiment service integrates with Hugging Face's AI models, with retry logic and error handling for production reliability."

**2. Discord Integration (15s)**
```java
// Show DiscordBotService.java  
```
> "The Discord service uses JDA to listen for messages, process them asynchronously, and respond with commands like `!tone stats`."

**3. REST API (15s)**
```java
// Show ToneController.java
```
> "A full REST API provides endpoints for analytics, leaderboards, and real-time sentiment data."

**4. Database Layer (15s)**
```java
// Show entity classes and repositories
```
> "JPA entities and repositories handle data persistence with optimized queries for performance."

---

### **🎮 LIVE DEMO (90 seconds)**

**[Screen: Split between Discord and API testing]**

**1. API Testing (30s)**
```bash
# Show terminal/Postman
curl -X POST localhost:8081/api/v1/tone/analyze \
  -d '{"text": "I love this project!"}'
# Response: {"label":"POSITIVE","score":0.99}
```

> "The API correctly identifies positive sentiment with 99% confidence."

**2. Discord Bot Demo (60s)**

**[Screen: Discord server]**

> "Now let's see it in action on Discord:"

```
User: Hey everyone! This new update is amazing! 🎉
Bot: *silently processes sentiment*

User: !tone stats
Bot: 📊 Server Vibe Stats 📊
     Total Messages: 1,247
     Average Sentiment: 0.34 (Good vibes 👍)
     Active Users: 23

User: !tone leaderboard
Bot: 🏆 Vibe Leaderboard 🏆
     😈 Meanest Members:
     1. ToxicUser - -0.45 sentiment
     😇 Nicest Members:  
     1. PositiveVibes - 0.78 sentiment

User: !tone analyze "This project sucks!"
Bot: 😡 Sentiment Analysis 😡
     Sentiment: negative (-0.89)
     Vibe: Very negative vibes 💀
```

---

### **☁️ CLOUD DEPLOYMENT (30 seconds)**

**[Screen: AWS Console showing EC2 instance]**

> "The entire application is deployed on AWS EC2 using Docker Compose, with PostgreSQL for data persistence and proper logging for production monitoring."

**[Show terminal]**
```bash
docker-compose ps
# Show running containers
curl http://your-ec2-ip:8081/actuator/health
# {"status":"UP"}
```

---

### **📊 TECHNICAL HIGHLIGHTS (30 seconds)**

**[Screen: Code/Terminal/Diagrams]**

> "Key technical achievements:"
- ✅ **99%+ sentiment accuracy** using AI
- ✅ **Real-time processing** with async message handling  
- ✅ **RESTful API design** with proper error handling
- ✅ **Production deployment** with Docker and AWS
- ✅ **Database optimization** with JPA and connection pooling
- ✅ **Monitoring & health checks** built-in

---

### **🚀 CLOSING (15 seconds)**

**[Screen: GitHub repo or portfolio site]**

> "This project showcases full-stack development, AI integration, cloud deployment, and production-ready coding practices. All code is available on my GitHub, and the bot is live and analyzing sentiment right now!"

**[Show final stats or live API call]**

> "Thanks for watching! Feel free to reach out with any questions."

---

## **🎥 Production Tips**

### **Recording Setup:**
- **Screen Resolution**: 1920x1080 minimum
- **Recording Software**: OBS Studio (free) or Loom
- **Audio**: Clear microphone, eliminate background noise
- **Lighting**: Good lighting for face cam (optional)

### **Editing Checklist:**
- [ ] Smooth transitions between screens
- [ ] Highlight mouse clicks and keystrokes
- [ ] Add subtle background music (optional)
- [ ] Include captions/subtitles
- [ ] Export in 1080p MP4

### **Demo Data Prep:**
- [ ] Clean Discord server with good example messages
- [ ] Pre-populate database with interesting sentiment data
- [ ] Test all commands work smoothly
- [ ] Prepare positive and negative example texts
- [ ] Have backup plans if live demo fails

### **Portfolio Integration:**
- [ ] Upload to YouTube/Vimeo
- [ ] Embed in portfolio website
- [ ] Link from GitHub README
- [ ] Include in resume/LinkedIn
- [ ] Share on social media with #coding hashtags

---

## **🎯 Key Messages to Convey:**

1. **Technical Depth**: "This isn't just a tutorial project - it's production-ready code"
2. **Problem Solving**: "Real-world applications for community management"
3. **Scalability**: "Designed to handle high-traffic Discord servers"
4. **Best Practices**: "Proper error handling, logging, and monitoring"
5. **Innovation**: "Creative use of AI for social applications"

---

**🎬 Break a leg! This demo will definitely impress potential employers and show off your skills! 🌟**
