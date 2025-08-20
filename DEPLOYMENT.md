# 🚀 Tone Bot - AWS Deployment Guide

## Quick Deploy to AWS EC2 (Free Tier)

### **Step 1: Launch EC2 Instance**

1. **Go to AWS Console** → EC2 → Launch Instance
2. **Choose AMI**: Amazon Linux 2023 (Free tier eligible)
3. **Instance Type**: t2.micro (Free tier)
4. **Key Pair**: Create new or use existing
5. **Security Group**: 
   - SSH (22) - Your IP
   - HTTP (80) - Anywhere
   - Custom TCP (8081) - Anywhere (for API access)
6. **Storage**: 8GB (Free tier)
7. **Launch Instance**

### **Step 2: Connect to Instance**

```bash
# SSH into your instance
ssh -i your-key.pem ec2-user@your-ec2-ip

# Run deployment script
curl -O https://raw.githubusercontent.com/yourusername/tone/main/deploy.sh
chmod +x deploy.sh
./deploy.sh
```

### **Step 3: Upload Your Project**

```bash
# From your local machine, copy project to EC2
scp -i your-key.pem -r . ec2-user@your-ec2-ip:/home/ec2-user/tone-bot/

# Or use git clone (if repo is public)
git clone https://github.com/yourusername/tone.git /home/ec2-user/tone-bot
```

### **Step 4: Configure Environment**

```bash
# SSH into EC2 and create .env file
ssh -i your-key.pem ec2-user@your-ec2-ip
cd /home/ec2-user/tone-bot

# Create .env file
cat > .env << EOF
DISCORD_BOT_TOKEN=your_actual_discord_token
HUGGINGFACE_API_KEY=your_actual_huggingface_key
POSTGRES_PASSWORD=SecurePassword123!
SPRING_PROFILES_ACTIVE=production
EOF
```

### **Step 5: Deploy with Docker**

```bash
# Start the application
docker-compose up -d

# Check if it's running
docker-compose ps
docker-compose logs tone-bot

# Your API will be available at:
# http://your-ec2-ip:8081/api/v1/tone/debug/config
```

### **Step 6: Configure Discord Bot**

1. **Get your Discord bot token** from [Discord Developer Portal](https://discord.com/developers/applications)
2. **Set bot permissions**: Read Messages, Send Messages, Read Message History
3. **Invite bot to your server** with the OAuth2 URL generator
4. **Test bot**: Send `!tone help` in your Discord server

### **🔒 Security Enhancements (Optional)**

```bash
# Set up SSL with Let's Encrypt
sudo yum install certbot -y

# Configure reverse proxy with nginx
sudo yum install nginx -y

# Set up automatic backups
crontab -e
# Add: 0 2 * * * docker exec tone-postgres pg_dump -U toneuser tonedb > /backup/tone-$(date +\%Y\%m\%d).sql
```

### **📊 Monitoring**

```bash
# Check application health
curl http://your-ec2-ip:8081/actuator/health

# View logs
docker-compose logs -f tone-bot

# Monitor resource usage
docker stats
```

### **🔄 Updates & Maintenance**

```bash
# Pull latest changes
git pull origin main

# Rebuild and restart
docker-compose down
docker-compose up -d --build

# Database backup
docker exec tone-postgres pg_dump -U toneuser tonedb > backup.sql
```

### **💰 Cost Optimization**

- **t2.micro**: Free for 12 months (750 hours/month)
- **8GB EBS**: Free tier includes 30GB
- **Data Transfer**: 1GB outbound free per month
- **Total Monthly Cost**: $0 for first year with light usage

### **🎯 Production Checklist**

- [ ] EC2 instance launched and secured
- [ ] Docker and Docker Compose installed
- [ ] Application deployed and running
- [ ] Environment variables configured
- [ ] Discord bot connected and responding
- [ ] API endpoints accessible
- [ ] Database persisting data
- [ ] Logs being generated properly
- [ ] Security groups configured
- [ ] Backups configured (optional)

### **🆘 Troubleshooting**

**Port 8081 not accessible:**
```bash
# Check security group allows port 8081
# Check if application is running: docker-compose ps
```

**Discord bot not responding:**
```bash
# Check token is valid
curl http://localhost:8081/api/v1/tone/debug/config
```

**Database issues:**
```bash
# Check PostgreSQL logs
docker-compose logs postgres
```

---

**🎉 Congratulations!** Your Tone bot is now live on AWS and ready to analyze Discord sentiment at scale!

**Public API URL**: `http://your-ec2-ip:8081/api/v1/tone/`

**Add this to your resume/portfolio**: 
- ✅ AWS EC2 deployment
- ✅ Docker containerization  
- ✅ Production database
- ✅ RESTful API design
- ✅ Real-time Discord integration
