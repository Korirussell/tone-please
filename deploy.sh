#!/bin/bash

# Tone Discord Bot - AWS EC2 Deployment Script
# This script deploys the Tone bot to AWS EC2

echo "🚀 Starting Tone Bot deployment to AWS EC2..."

# Update system packages
sudo yum update -y

# Install Docker
echo "📦 Installing Docker..."
sudo yum install docker -y
sudo service docker start
sudo usermod -a -G docker ec2-user

# Install Docker Compose
echo "📦 Installing Docker Compose..."
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Create application directory
echo "📁 Setting up application directory..."
mkdir -p /home/ec2-user/tone-bot
cd /home/ec2-user/tone-bot

# Clone the repository (you'll need to replace with your actual repo)
echo "📥 Cloning repository..."
# git clone https://github.com/yourusername/tone.git .

echo "⚙️ Setting up environment variables..."
echo "Please create your .env file with:"
echo "DISCORD_BOT_TOKEN=your_actual_token"
echo "HUGGINGFACE_API_KEY=your_actual_key"
echo "POSTGRES_PASSWORD=your_secure_password"

echo "🔧 To complete deployment:"
echo "1. Copy your project files to this directory"
echo "2. Create .env file with your tokens"
echo "3. Run: docker-compose up -d"

echo "✅ Deployment script completed!"
