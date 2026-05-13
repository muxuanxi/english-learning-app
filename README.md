# 📚 EnglishMaster - 成人英语学习平台

全栈英语学习网站，为成人用户提供词汇、语法、阅读、听力、测验、打卡等全方位学习功能。

## 🛠 技术栈

| 层级 | 技术 |
|------|------|
| **前端** | Vue 3 + Vite + Pinia + Vue Router + ECharts |
| **后端** | Spring Boot 3.2 + Spring Security + JPA + JWT |
| **数据库** | MySQL 8.0 |
| **爬虫** | Python (BeautifulSoup + Requests) |

## 📁 项目结构

```
hahhaha/
├── frontend/                    # Vue.js 前端
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   │   ├── Home.vue        # 首页（Hero + 功能介绍 + 每日词汇）
│   │   │   ├── Vocabulary.vue  # 词汇学习（闪卡模式 + 搜索）
│   │   │   ├── Grammar.vue     # 语法课程列表
│   │   │   ├── GrammarDetail.vue # 语法课程详情
│   │   │   ├── Reading.vue     # 阅读文章列表
│   │   │   ├── ReadingDetail.vue # 文章阅读详情
│   │   │   ├── Listening.vue   # 听力训练（音频播放 + 原文）
│   │   │   ├── Quiz.vue        # 测验列表
│   │   │   ├── QuizPlay.vue    # 答题页面（计时 + 即时反馈）
│   │   │   ├── Dashboard.vue   # 学习仪表盘（统计 + 打卡日历）
│   │   │   ├── Login.vue       # 登录
│   │   │   └── Register.vue    # 注册
│   │   ├── components/         # 公共组件
│   │   │   └── Navbar.vue      # 导航栏
│   │   ├── router/             # 路由配置
│   │   ├── store/              # Pinia 状态管理
│   │   │   ├── auth.js         # 认证状态
│   │   │   └── learning.js     # 学习数据状态
│   │   ├── api/                # API 请求封装
│   │   └── assets/css/         # 样式文件
│   │       └── main.css        # 全局设计系统
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
│
├── backend/                     # Spring Boot 后端
│   └── src/main/java/com/english/learn/
│       ├── EnglishLearnApplication.java
│       ├── model/              # JPA 实体类 (8个表)
│       ├── repository/         # JPA Repository
│       ├── service/            # 业务逻辑层
│       ├── controller/         # REST API 控制器
│       ├── dto/                # 数据传输对象
│       ├── security/           # JWT 认证 + 过滤器
│       └── config/             # Security + CORS 配置
│
├── crawler/                     # Python 爬虫
│   ├── spiders/
│   │   ├── word_crawler.py     # 单词爬虫（Dictionary API）
│   │   └── article_crawler.py  # 文章爬虫（BBC + Wikipedia）
│   ├── utils/db.py             # 数据库连接工具
│   └── requirements.txt
│
└── database/
    └── init.sql                 # 数据库初始化（9张表 + 示例数据）
```

## 🚀 快速开始

### 1. 数据库

```bash
mysql -u root -p < database/init.sql
```

### 2. 后端

```bash
cd backend
# 修改 src/main/resources/application.yml 中的数据库连接信息
mvn spring-boot:run
# 运行在 http://localhost:8080
```

### 3. 前端

```bash
cd frontend
npm install
npm run dev
# 运行在 http://localhost:5173
```

### 4. 爬虫（可选）

```bash
cd crawler
pip install -r requirements.txt
python spiders/word_crawler.py
python spiders/article_crawler.py
```

## ✨ 核心功能

- 📝 **词汇学习** — 闪卡模式 + 艾宾浩斯记忆曲线
- 📖 **语法课程** — 分难度分级的系统语法讲解
- 📰 **阅读训练** — 多分类原版英文文章
- 🎧 **听力训练** — 音频播放 + 原文对照
- ✅ **在线测验** — 选择题/填空/判断 + 即时评分
- 📊 **学习仪表盘** — 数据统计 + 可视化
- 🔥 **每日打卡** — 日历视图 + 连续天数
- 🔐 **JWT 认证** — 注册/登录/权限控制

## 🔌 API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录 |
| GET | `/api/auth/me` | 获取当前用户 |
| GET | `/api/words?level=&random=&limit=` | 获取词汇 |
| GET | `/api/words/search?q=` | 搜索词汇 |
| POST | `/api/words/{id}/study` | 记录学习 |
| GET | `/api/grammar?category=&level=` | 获取语法课程 |
| GET | `/api/grammar/{id}` | 语法详情 |
| GET | `/api/articles?category=&level=` | 获取文章 |
| GET | `/api/articles/{id}` | 文章详情 |
| GET | `/api/quizzes?type=` | 获取测验 |
| GET | `/api/quizzes/{id}` | 测验详情(含题目) |
| POST | `/api/quizzes/{id}/submit` | 提交测验 |
| GET | `/api/stats` | 学习统计 |
| POST | `/api/stats/checkin` | 每日打卡 |
