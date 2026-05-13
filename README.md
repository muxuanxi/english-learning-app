# 🎓 English Learning App

An adult English learning website with 518 vocabulary words, 5 exam papers, pronunciation, translation, and more.

[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy?repo=https://github.com/muxuanxi/english-learning-app)

## Features

- 📝 **Vocabulary** - 518 words with flashcard mode, US/UK pronunciation
- 📋 **Degree English Quizzes** - 5 exam papers (54 questions) based on Chengdu College of Arts and Sciences
- 🎤 **Pronunciation** - US & UK accents via Web Speech API
- 🌐 **Translation** - EN↔ZH translation tool
- 🔍 **Global Search** - Search across words, articles, grammar
- 📖 **Reading** - English articles with difficulty levels
- 🎧 **Listening** - Listening comprehension practice
- 📚 **Grammar** - Structured grammar lessons
- 📊 **Progress Tracking** - Learning statistics and daily check-in
- 🔐 **JWT Auth** - User registration and login

## Tech Stack

- **Backend**: Spring Boot 3.2, JPA/Hibernate, Spring Security, JWT
- **Frontend**: Vue 3 (Composition API), Vite, Pinia, Vue Router
- **Crawler**: Python (BeautifulSoup, requests, Dictionary API)
- **Database**: H2 (dev), MySQL (production)

## Quick Start

### Backend
```bash
cd backend
./mvnw spring-boot:run
# Runs on http://localhost:8080
```

### Frontend
```bash
cd frontend
npm install
npm run dev
# Runs on http://localhost:5173
```

### Crawler
```bash
cd crawler
pip install -r requirements.txt
python spiders/degree_english_full_crawler.py
```

## Deploy to Render

1. Fork this repo to your GitHub
2. Click the "Deploy to Render" button above
3. Render will auto-detect the Dockerfile and deploy
4. Your app will be live at `https://english-learning-app.onrender.com`

## Deploy Locally (Docker)
```bash
cd frontend && npm run build
cp -r dist/* ../backend/src/main/resources/static/
cd ../backend && mvn package -DskipTests
docker build -t english-learning-app .
docker run -p 8080:8080 english-learning-app
```

## Project Structure

```
├── backend/              # Spring Boot API
│   ├── controller/       # REST endpoints
│   ├── model/            # JPA entities
│   ├── service/          # Business logic
│   └── resources/        # Config + word data
├── frontend/             # Vue 3 SPA
│   ├── views/            # 14 pages
│   ├── utils/            # Speech + Translate
│   └── store/            # Pinia stores
├── crawler/              # Python crawlers
│   └── spiders/          # Word/article crawlers
└── database/             # MySQL init script
```
