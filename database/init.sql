-- ============================================
-- 英语学习网站数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS english_learn DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE english_learn;

-- -------------------------------------------
-- 用户表
-- -------------------------------------------
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    avatar_url VARCHAR(500),
    level ENUM('BEGINNER','ELEMENTARY','INTERMEDIATE','UPPER_INTERMEDIATE','ADVANCED') DEFAULT 'BEGINNER',
    daily_goal INT DEFAULT 30 COMMENT '每日学习目标(单词数)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_username (username)
) ENGINE=InnoDB;

-- -------------------------------------------
-- 词汇表
-- -------------------------------------------
CREATE TABLE words (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(100) NOT NULL,
    phonetic_us VARCHAR(100) COMMENT '美式音标',
    phonetic_uk VARCHAR(100) COMMENT '英式音标',
    part_of_speech VARCHAR(50) COMMENT '词性',
    definition_cn TEXT NOT NULL COMMENT '中文释义',
    definition_en TEXT COMMENT '英文释义',
    example_sentence TEXT,
    example_translation TEXT,
    audio_url VARCHAR(500),
    difficulty_level TINYINT DEFAULT 1 COMMENT '难度 1-5',
    category VARCHAR(100) COMMENT '分类标签',
    frequency_rank INT COMMENT '使用频率排名',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_word (word),
    INDEX idx_difficulty (difficulty_level),
    INDEX idx_category (category)
) ENGINE=InnoDB;

-- -------------------------------------------
-- 语法课程表
-- -------------------------------------------
CREATE TABLE grammar_lessons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    category VARCHAR(100) COMMENT '语法分类',
    description TEXT,
    content_html TEXT NOT NULL COMMENT '课程内容(HTML)',
    difficulty_level TINYINT DEFAULT 1,
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_difficulty (difficulty_level)
) ENGINE=InnoDB;

-- -------------------------------------------
-- 阅读文章表
-- -------------------------------------------
CREATE TABLE articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    author VARCHAR(100),
    source VARCHAR(200) COMMENT '文章来源',
    content_html TEXT NOT NULL,
    summary TEXT,
    difficulty_level TINYINT DEFAULT 3,
    word_count INT DEFAULT 0,
    category VARCHAR(100) COMMENT '科技/文化/新闻/文学',
    cover_image_url VARCHAR(500),
    publish_date DATE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_difficulty (difficulty_level),
    FULLTEXT INDEX ft_content (title, content_html)
) ENGINE=InnoDB;

-- -------------------------------------------
-- 听力资源表
-- -------------------------------------------
CREATE TABLE listening_materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    description TEXT,
    audio_url VARCHAR(500) NOT NULL,
    transcript TEXT COMMENT '听力原文',
    subtitles_json JSON COMMENT '字幕时间轴',
    duration_seconds INT,
    difficulty_level TINYINT DEFAULT 2,
    category VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_difficulty (difficulty_level)
) ENGINE=InnoDB;

-- -------------------------------------------
-- 测验表
-- -------------------------------------------
CREATE TABLE quizzes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    quiz_type ENUM('VOCABULARY','GRAMMAR','READING','LISTENING','COMPREHENSIVE') NOT NULL,
    difficulty_level TINYINT DEFAULT 2,
    time_limit_minutes INT DEFAULT 0 COMMENT '0=不限时',
    total_questions INT DEFAULT 0,
    pass_score INT DEFAULT 60,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_type (quiz_type),
    INDEX idx_difficulty (difficulty_level)
) ENGINE=InnoDB;

-- -------------------------------------------
-- 测验题目表
-- -------------------------------------------
CREATE TABLE quiz_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    question_type ENUM('MULTIPLE_CHOICE','FILL_BLANK','TRUE_FALSE','MATCHING','WRITING') NOT NULL,
    question_text TEXT NOT NULL,
    options_json JSON COMMENT '选项列表',
    correct_answer TEXT NOT NULL,
    explanation TEXT COMMENT '答案解析',
    score INT DEFAULT 10,
    sort_order INT DEFAULT 0,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    INDEX idx_quiz (quiz_id)
) ENGINE=InnoDB;

-- -------------------------------------------
-- 用户学习记录表（含艾宾浩斯记忆曲线）
-- -------------------------------------------
CREATE TABLE user_learning_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    record_type ENUM('WORD','GRAMMAR','READING','LISTENING','QUIZ') NOT NULL,
    target_id BIGINT COMMENT '关联的学习内容ID',
    is_mastered BOOLEAN DEFAULT FALSE COMMENT '是否已掌握',
    study_count INT DEFAULT 1 COMMENT '学习次数',
    last_study_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    next_review_at DATETIME COMMENT '下次复习时间(艾宾浩斯)',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_type (user_id, record_type),
    INDEX idx_next_review (next_review_at)
) ENGINE=InnoDB;

-- -------------------------------------------
-- 用户打卡表
-- -------------------------------------------
CREATE TABLE user_checkins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    checkin_date DATE NOT NULL,
    word_count INT DEFAULT 0,
    study_minutes INT DEFAULT 0,
    quiz_score_avg DECIMAL(5,2),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_date (user_id, checkin_date),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB;

-- -------------------------------------------
-- 用户测验记录表
-- -------------------------------------------
CREATE TABLE user_quiz_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quiz_id BIGINT NOT NULL,
    score INT NOT NULL,
    total_score INT NOT NULL,
    time_spent_seconds INT,
    answers_json JSON COMMENT '用户答案',
    completed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    INDEX idx_user_quiz (user_id, quiz_id)
) ENGINE=InnoDB;

-- -------------------------------------------
-- 插入示例数据
-- -------------------------------------------
INSERT INTO words (word, phonetic_us, phonetic_uk, part_of_speech, definition_cn, definition_en, example_sentence, example_translation, difficulty_level, category) VALUES
('ubiquitous', '/juːˈbɪk.wɪ.təs/', '/juːˈbɪk.wɪ.təs/', 'adj.', '无处不在的，普遍存在的', 'Present, appearing, or found everywhere', 'Smartphones have become ubiquitous in modern society.', '智能手机在现代社会已无处不在。', 4, '高级词汇'),
('eloquent', '/ˈel.ə.kwənt/', '/ˈel.ə.kwənt/', 'adj.', '雄辩的，有口才的', 'Fluent or persuasive in speaking or writing', 'She gave an eloquent speech at the graduation ceremony.', '她在毕业典礼上发表了雄辩的演讲。', 3, '形容词'),
('meticulous', '/məˈtɪk.jə.ləs/', '/məˈtɪk.jə.ləs/', 'adj.', '一丝不苟的，细致的', 'Showing great attention to detail', 'The meticulous craftsman spent hours perfecting each detail.', '这位细致的工匠花了数小时完善每一个细节。', 3, '形容词'),
('ephemeral', '/ɪˈfem.ər.əl/', '/ɪˈfem.ər.əl/', 'adj.', '短暂的，转瞬即逝的', 'Lasting for a very short time', 'The beauty of cherry blossoms is ephemeral but breathtaking.', '樱花之美短暂却令人叹为观止。', 4, '高级词汇'),
('resilient', '/rɪˈzɪl.jənt/', '/rɪˈzɪl.jənt/', 'adj.', '有韧性的，能迅速恢复的', 'Able to withstand or recover quickly from difficult conditions', 'Children are often more resilient than adults give them credit for.', '孩子们往往比成年人想象的更坚强。', 3, '形容词'),
('pragmatic', '/præɡˈmæt.ɪk/', '/præɡˈmæt.ɪk/', 'adj.', '务实的，实用的', 'Dealing with things sensibly and realistically', 'We need a pragmatic approach to solve this budget problem.', '我们需要务实的方法来解决这个预算问题。', 3, '形容词'),
('ambiguous', '/æmˈbɪɡ.ju.əs/', '/æmˈbɪɡ.ju.əs/', 'adj.', '模糊的，模棱两可的', 'Open to more than one interpretation', 'The contract contains several ambiguous clauses.', '该合同包含几个模棱两可的条款。', 2, '形容词'),
('innovate', '/ˈɪn.ə.veɪt/', '/ˈɪn.ə.veɪt/', 'v.', '创新，革新', 'Make changes in something established by introducing new methods', 'Companies must constantly innovate to stay competitive.', '公司必须不断创新以保持竞争力。', 2, '动词'),
('collaborate', '/kəˈlæb.ə.reɪt/', '/kəˈlæb.ə.reɪt/', 'v.', '合作，协作', 'Work jointly on an activity or project', 'The two departments will collaborate on the new project.', '两个部门将在这个新项目上合作。', 2, '动词'),
('sustainable', '/səˈsteɪ.nə.bəl/', '/səˈsteɪ.nə.bəl/', 'adj.', '可持续的', 'Able to be maintained at a certain rate or level', 'We should adopt more sustainable living habits.', '我们应该养成更可持续的生活习惯。', 2, '形容词'),
('profound', '/prəˈfaʊnd/', '/prəˈfaʊnd/', 'adj.', '深刻的，深远的', 'Very great or intense; having deep insight', 'The book had a profound impact on my understanding of life.', '这本书对我对生活的理解产生了深远的影响。', 3, '形容词'),
('diligent', '/ˈdɪl.ɪ.dʒənt/', '/ˈdɪl.ɪ.dʒənt/', 'adj.', '勤奋的，勤勉的', 'Having or showing care in one\'s work or duties', 'She is a diligent student who always completes her homework on time.', '她是一个勤奋的学生，总是按时完成作业。', 2, '形容词'),
('comprehend', '/ˌkɑːm.prɪˈhend/', '/ˌkɒm.prɪˈhend/', 'v.', '理解，领悟', 'Grasp mentally; understand', 'It takes time to fully comprehend the complexity of the situation.', '完全理解情况的复杂性需要时间。', 2, '动词'),
('negotiate', '/nɪˈɡoʊ.ʃi.eɪt/', '/nɪˈɡəʊ.ʃi.eɪt/', 'v.', '谈判，协商', 'Try to reach an agreement through discussion', 'We need to negotiate better terms for the contract.', '我们需要为合同谈判更好的条款。', 2, '动词'),
('persevere', '/ˌpɜːr.səˈvɪr/', '/ˌpɜː.sɪˈvɪə/', 'v.', '坚持不懈', 'Continue in a course of action despite difficulty', 'Despite all setbacks, she persevered and achieved her goal.', '尽管遭遇挫折，她坚持不懈，最终实现了目标。', 3, '动词');

INSERT INTO grammar_lessons (title, category, description, content_html, difficulty_level, sort_order) VALUES
('一般现在时 vs 现在进行时', '时态', '掌握一般现在时和现在进行时的区别与用法',
'<h2>一般现在时 (Simple Present)</h2><p><strong>结构：</strong>主语 + 动词原形（第三人称单数 + s/es）</p><p><strong>用法：</strong></p><ul><li>表达客观事实和真理：<em>The sun rises in the east.</em></li><li>表达习惯性动作：<em>I go to work by subway every day.</em></li><li>表达状态：<em>She lives in Shanghai.</em></li></ul><h2>现在进行时 (Present Continuous)</h2><p><strong>结构：</strong>主语 + am/is/are + 动词-ing</p><p><strong>用法：</strong></p><ul><li>表达此刻正在进行的动作：<em>I am reading a book right now.</em></li><li>表达现阶段持续的动作：<em>He is working on a project these days.</em></li><li>表达将来安排好的事：<em>We are leaving for London tomorrow.</em></li></ul>',
1, 1),
('定语从句详解', '从句', '系统学习关系代词和关系副词的用法',
'<h2>定语从句 (Relative Clauses)</h2><p>定语从句用来修饰名词或代词，由关系词引导。</p><h3>关系代词</h3><ul><li><strong>who</strong> — 指人：<em>The man who is standing there is my boss.</em></li><li><strong>whom</strong> — 指人（宾格）：<em>The person whom you met is my colleague.</em></li><li><strong>which</strong> — 指物：<em>The book which I bought yesterday is fascinating.</em></li><li><strong>that</strong> — 指人或物：<em>This is the best movie that I have ever seen.</em></li><li><strong>whose</strong> — 表所属：<em>The girl whose father is a doctor studies hard.</em></li></ul><h3>关系副词</h3><ul><li><strong>when</strong> — 时间：<em>I still remember the day when we first met.</em></li><li><strong>where</strong> — 地点：<em>This is the city where I was born.</em></li><li><strong>why</strong> — 原因：<em>That is the reason why I was late.</em></li></ul>',
2, 2),
('虚拟语气完全指南', '语气', '深入理解虚拟条件句及其各种形式',
'<h2>虚拟语气 (Subjunctive Mood)</h2><h3>1. 与现在事实相反</h3><p><strong>结构：</strong>If + 主语 + 过去式, 主语 + would/could/might + 动词原形</p><p><em>If I were you, I would accept the offer.</em></p><h3>2. 与过去事实相反</h3><p><strong>结构：</strong>If + 主语 + had + 过去分词, 主语 + would/could/might + have + 过去分词</p><p><em>If I had studied harder, I would have passed the exam.</em></p><h3>3. 与将来事实可能相反</h3><p><strong>结构：</strong>If + 主语 + should/were to + 动词原形, 主语 + would/could/might + 动词原形</p><p><em>If it should rain tomorrow, we would cancel the picnic.</em></p>',
3, 3),
('被动语态全面解析', '语态', '学习各种时态下的被动语态构成和用法',
'<h2>被动语态 (Passive Voice)</h2><p><strong>基本结构：</strong>be + 过去分词</p><h3>各时态被动语态</h3><ul><li>一般现在时：am/is/are + done</li><li>一般过去时：was/were + done</li><li>现在完成时：have/has been + done</li><li>将来时：will be + done</li><li>情态动词：can/must/should + be + done</li></ul><h3>使用场景</h3><ul><li>不知道或不需要指出动作执行者：<em>The window was broken.</em></li><li>强调动作的承受者：<em>The new policy was announced yesterday.</em></li><li>正式文体和学术写作：<em>It is believed that...</em></li></ul>',
2, 4);

INSERT INTO articles (title, author, source, content_html, summary, difficulty_level, word_count, category) VALUES
('The Rise of Artificial Intelligence in Healthcare', 'Dr. Sarah Chen', 'TechReview',
'<p>Artificial intelligence is revolutionizing the healthcare industry in unprecedented ways. From diagnostic algorithms that can detect cancer in medical images with greater accuracy than human radiologists, to personalized treatment plans based on a patient''s genetic profile, AI is transforming every aspect of medicine.</p><p>Machine learning models are now being used to predict patient outcomes, optimize hospital workflows, and even assist in surgical procedures. The integration of AI into healthcare promises not only to improve patient care but also to reduce costs significantly.</p><p>However, challenges remain. Issues of data privacy, algorithmic bias, and the need for regulatory frameworks are critical concerns that must be addressed as AI continues to permeate the healthcare sector. The future of medicine lies at the intersection of human expertise and artificial intelligence, creating a partnership that could fundamentally change how we approach health and disease.</p>',
'An overview of how artificial intelligence is transforming healthcare, from diagnostics to personalized medicine.',
4, 210, '科技'),
('The Art of Effective Communication', 'Michael Brown', 'BusinessInsight',
'<p>Effective communication is the cornerstone of professional success. Whether you are leading a team meeting, presenting to clients, or simply collaborating with colleagues, your ability to convey ideas clearly and persuasively can make or break your career.</p><p>Research shows that the most effective communicators share several key traits: they listen actively, they adapt their message to their audience, they use storytelling to make their points memorable, and they are skilled at reading non-verbal cues. In fact, studies suggest that non-verbal communication accounts for up to 55% of how we perceive a message.</p><p>Developing these skills requires deliberate practice and self-awareness. Start by seeking feedback from trusted colleagues, recording and reviewing your presentations, and consciously applying communication frameworks in your daily interactions. Over time, effective communication becomes not just a skill, but a habit that opens doors to new opportunities.</p>',
'Learn the key principles and practical techniques for becoming a more effective communicator in professional settings.',
3, 200, '商业'),
('Climate Change: What Individuals Can Do', 'Emily Watson', 'GreenEarth',
'<p>While climate change is a global problem requiring systemic solutions, individual actions collectively make a significant difference. The choices we make every day—from what we eat to how we travel—contribute to our carbon footprint.</p><p>Reducing meat consumption, using public transportation, minimizing energy use at home, and supporting sustainable businesses are practical steps anyone can take. Moreover, voting for environmentally conscious policies and educating others amplifies your impact beyond personal lifestyle changes.</p><p>The most important thing is to start somewhere. You don''t need to be perfect; consistent small actions, multiplied across millions of people, can drive the change our planet needs.</p>',
'Practical steps individuals can take to combat climate change in their daily lives.',
2, 150, '环境');

INSERT INTO quizzes (title, description, quiz_type, difficulty_level, total_questions, pass_score) VALUES
('基础词汇测试 - 第一单元', '测试你对基础英语词汇的掌握程度', 'VOCABULARY', 1, 10, 60),
('语法综合测试', '涵盖时态、从句、虚拟语气等语法知识点', 'GRAMMAR', 2, 15, 60),
('商务英语阅读理解', '阅读商务文章并回答相关问题', 'READING', 3, 5, 60);

INSERT INTO quiz_questions (quiz_id, question_type, question_text, options_json, correct_answer, explanation, score) VALUES
(1, 'MULTIPLE_CHOICE', '"Ubiquitous" 的意思是什么？', '["罕见的", "无处不在的", "珍贵的", "复杂的"]', '无处不在的', '"Ubiquitous" 意为"无处不在的，普遍存在的"，形容某物到处都能看到。', 10),
(1, 'MULTIPLE_CHOICE', '选择正确的词语填入空格：She is a very ______ worker, always paying attention to every detail.', '["careless", "meticulous", "lazy", "arrogant"]', 'meticulous', '"Meticulous" 意为"细致的，一丝不苟的"，最适合形容关注每个细节的工作者。', 10),
(1, 'MULTIPLE_CHOICE', '"Ephemeral" 的意思是什么？', '["永恒的", "短暂的", "重要的", "美丽的"]', '短暂的', '"Ephemeral" 意为"短暂的，转瞬即逝的"，形容持续时间极短。', 10),
(1, 'MULTIPLE_CHOICE', '"Pragmatic" 的反义词是？', '["practical", "idealistic", "realistic", "sensible"]', 'idealistic', '"Pragmatic" 意为"务实的"，其反义词是 "idealistic"（理想主义的）。', 10),
(2, 'MULTIPLE_CHOICE', '选择正确的时态：By the time he arrives, we ______ for two hours.', '["will wait", "will have been waiting", "are waiting", "have waited"]', 'will have been waiting', 'By the time 引导的时间状语从句，主句用将来完成时或将来完成进行时。', 10),
(2, 'MULTIPLE_CHOICE', '"The person ______ you spoke is my manager." 填入正确的关系词：', '["which", "to whom", "who", "what"]', 'to whom', 'speak to sb. 为固定搭配，关系代词指人且作介词宾语时用whom。', 10),
(2, 'MULTIPLE_CHOICE', 'If I ______ you, I would apply for that position.', '["am", "was", "were", "be"]', 'were', '与现在事实相反的虚拟语气中，be动词一律用were。', 10);
