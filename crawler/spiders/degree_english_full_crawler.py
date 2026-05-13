
#!/usr/bin/env python3
"""
成都文理学院 学位英语 完整词汇爬虫
====================================
从 Dictionary API 爬取 600+ 学位英语单词的详细信息：
音标(美式/英式)、词性、英文释义、例句等
"""

import requests
import json
import time
import os
import sys
import concurrent.futures
from collections import OrderedDict

API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/{}"
OUTPUT_FILE = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 
                           "data", "degree_english_full.json")
JAVA_OUTPUT = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
                           "data", "degree_english_words.java.txt")

# 学位英语完整词表 (word -> Chinese definition)
WORDS = {
    "abandon": "放弃，抛弃", "ability": "能力，才能", "abroad": "在国外，到国外",
    "absence": "缺席，不在", "absolute": "绝对的，完全的", "absorb": "吸收，吸引",
    "abstract": "抽象的，摘要", "abundant": "丰富的，充裕的", "academic": "学术的，学院的",
    "accelerate": "加速，促进", "accept": "接受，承认", "access": "进入，通道",
    "accommodation": "住宿，膳宿", "accompany": "陪伴，伴随", "accomplish": "完成，实现",
    "account": "账户，说明", "accurate": "准确的，精确的", "accuse": "指控，指责",
    "achieve": "达到，取得", "acknowledge": "承认，确认", "acquire": "获得，习得",
    "adapt": "适应，改编", "adequate": "足够的，适当的", "adjust": "调整，适应",
    "admire": "钦佩，赞赏", "admit": "承认，准许进入", "adopt": "采用，收养",
    "advance": "前进，进步", "advantage": "优势，有利条件", "affair": "事务，事件",
    "affect": "影响，感动", "afford": "负担得起", "agreement": "协议，同意",
    "aid": "援助，帮助", "aim": "目标，瞄准", "alarm": "警报，惊恐",
    "alternative": "替代的，选择", "amaze": "使惊奇", "amount": "数量，总额",
    "analyze": "分析", "background": "背景", "balance": "平衡，余额",
    "ban": "禁止，禁令", "base": "基础，基地", "basic": "基本的，基础的",
    "basis": "基础，根据", "bear": "忍受，承担", "behave": "表现，行为",
    "belief": "信念，信仰", "belong": "属于", "benefit": "利益，好处",
    "blame": "责备，责怪", "blank": "空白的，空白处", "block": "街区，块，阻塞",
    "board": "板，董事会", "bore": "使厌烦", "bother": "打扰，烦恼",
    "bound": "一定的，界限", "brand": "品牌，商标", "brief": "简短的，摘要",
    "broad": "宽的，广泛的", "budget": "预算", "burden": "负担，重担",
    "burst": "爆发，破裂", "bury": "埋葬，掩埋", "capable": "有能力的",
    "capacity": "容量，能力", "capture": "捕获，俘获", "career": "职业，生涯",
    "celebrate": "庆祝", "challenge": "挑战", "character": "性格，角色",
    "charge": "收费，指控", "charity": "慈善，慈善机构", "chief": "主要的，首领",
    "circumstance": "环境，情况", "claim": "声称，要求", "classic": "经典的",
    "climate": "气候", "coach": "教练，辅导", "collapse": "倒塌，崩溃",
    "combine": "结合，联合", "comfort": "舒适，安慰", "command": "命令，指挥",
    "comment": "评论，意见", "commercial": "商业的，广告", "commission": "委员会，佣金",
    "commit": "犯（罪），承诺", "communicate": "交流，沟通", "community": "社区，团体",
    "compare": "比较，对比", "compete": "竞争，比赛", "complain": "抱怨，投诉",
    "complex": "复杂的，综合体", "concentrate": "集中，专注", "concept": "概念，观念",
    "concern": "关心，涉及", "conclude": "总结，得出结论", "condition": "条件，状况",
    "conduct": "行为，进行", "conference": "会议，讨论会", "confidence": "信心，信任",
    "confirm": "确认，证实", "conflict": "冲突，矛盾", "connect": "连接，联系",
    "conscious": "有意识的，清醒的", "consequence": "结果，后果", "conservative": "保守的",
    "consider": "考虑，认为", "consist": "由...组成", "constant": "不断的，恒定的",
    "construct": "建造，构建", "consume": "消费，消耗", "contact": "接触，联系",
    "contain": "包含，容纳", "content": "内容，满足的", "context": "上下文，背景",
    "continue": "继续，持续", "contract": "合同，收缩", "contribute": "贡献，捐献",
    "convenient": "方便的，便利的", "convince": "说服，使信服", "cooperate": "合作，协作",
    "cope": "应对，处理", "create": "创造，创建", "credit": "信用，学分",
    "crime": "犯罪，罪行", "crisis": "危机", "critical": "批评的，关键的",
    "crucial": "至关重要的", "cultivate": "培养，耕作", "culture": "文化",
    "curious": "好奇的", "current": "当前的，电流", "damage": "损害，破坏",
    "debate": "辩论，争论", "debt": "债务", "decade": "十年",
    "decline": "下降，拒绝", "decrease": "减少，降低", "defeat": "击败，战胜",
    "defend": "防御，辩护", "define": "定义，明确", "degree": "程度，学位",
    "delay": "延迟，耽搁", "deliver": "递送，发表", "demand": "要求，需求",
    "demonstrate": "展示，证明", "deny": "否认，拒绝", "depend": "依靠，取决于",
    "describe": "描述，形容", "deserve": "值得，应得", "design": "设计，图案",
    "desire": "渴望，愿望", "despite": "尽管，不管", "destroy": "破坏，毁灭",
    "detail": "细节，详情", "detect": "发现，察觉", "determine": "决定，确定",
    "develop": "发展，开发", "device": "设备，装置", "devote": "致力于，奉献",
    "digital": "数字的", "disappear": "消失，失踪", "discover": "发现",
    "discuss": "讨论", "disease": "疾病", "dismiss": "解雇，解散",
    "display": "展示，显示", "distinguish": "区分，辨别", "distribute": "分配，分发",
    "district": "地区，区域", "document": "文件，文档", "domestic": "国内的，家庭的",
    "donate": "捐赠", "doubt": "怀疑，疑问", "dramatic": "戏剧性的，巨大的",
    "economic": "经济的", "educate": "教育", "effective": "有效的",
    "efficient": "高效的", "effort": "努力，尽力", "element": "元素，要素",
    "eliminate": "消除，淘汰", "embrace": "拥抱，包含", "emerge": "出现，浮现",
    "emotion": "情感，情绪", "emphasis": "强调，重点", "employ": "雇用，使用",
    "enable": "使能够", "encounter": "遇到，遭遇", "encourage": "鼓励，激励",
    "engage": "从事，订婚", "enormous": "巨大的，庞大的", "ensure": "确保，保证",
    "enterprise": "企业，事业", "entertain": "娱乐，招待", "enthusiasm": "热情，热心",
    "entire": "全部的，整个的", "environment": "环境", "equipment": "设备，装备",
    "essential": "必要的，本质的", "establish": "建立，设立", "estimate": "估计，估算",
    "evaluate": "评估，评价", "evidence": "证据", "evolve": "进化，发展",
    "examine": "检查，考试", "excellent": "优秀的，极好的", "exchange": "交换，兑换",
    "exist": "存在，生存", "expand": "扩展，膨胀", "expect": "期望，预期",
    "expense": "费用，开销", "experiment": "实验，试验", "expert": "专家，行家",
    "explain": "解释，说明", "explore": "探索，探究", "export": "出口，输出",
    "express": "表达，快递", "extend": "延伸，扩展", "extraordinary": "非凡的，特别的",
    "extreme": "极端的，极度的", "facility": "设施，设备", "factor": "因素，要素",
    "familiar": "熟悉的", "fashion": "时尚，方式", "feature": "特征，特色",
    "figure": "数字，人物", "finance": "金融，财政", "flexible": "灵活的，柔韧的",
    "focus": "焦点，集中", "forecast": "预报，预测", "formal": "正式的",
    "former": "前者，以前的", "fortune": "财富，运气", "foundation": "基础，基金会",
    "frequent": "频繁的", "function": "功能，作用", "fund": "基金，资金",
    "fundamental": "基本的，根本的", "gain": "获得，增加", "gap": "差距，缺口",
    "generate": "产生，生成", "generous": "慷慨的，大方的", "genuine": "真正的，真诚的",
    "global": "全球的", "goal": "目标", "govern": "治理，统治",
    "gradual": "逐渐的", "grant": "授予，拨款", "guarantee": "保证，担保",
    "handle": "处理，把手", "harmony": "和谐，协调", "hesitate": "犹豫，迟疑",
    "highlight": "突出，强调", "host": "主人，主办", "household": "家庭，家喻户晓的",
    "identify": "识别，确认", "ignore": "忽视，不理睬", "illustrate": "说明，阐明",
    "image": "形象，图像", "imagine": "想象，设想", "immediate": "立即的，直接的",
    "impact": "影响，冲击", "implement": "实施，执行", "imply": "暗示，意味着",
    "impose": "强加，征收", "impress": "给...留下印象", "improve": "改善，提高",
    "incident": "事件，事故", "include": "包括，包含", "income": "收入，收益",
    "increase": "增加，增长", "indicate": "表明，指示", "individual": "个人的，个人",
    "industry": "工业，行业", "influence": "影响，势力", "inform": "通知，告知",
    "initial": "最初的，首字母", "initiative": "主动性，倡议", "innocent": "无辜的，天真的",
    "innovation": "创新，革新", "inspire": "激励，启发", "instance": "例子，实例",
    "institution": "机构，制度", "insurance": "保险", "intellectual": "智力的，知识分子",
    "intelligence": "智力，情报", "intend": "打算，意图", "intense": "强烈的，紧张的",
    "interpret": "解释，口译", "invest": "投资", "investigate": "调查，研究",
    "involve": "包含，涉及", "isolate": "隔离，孤立", "issue": "问题，发行",
    "journal": "期刊，日记", "journey": "旅程，旅行", "justice": "正义，司法",
    "justify": "证明...正当", "keen": "热切的，敏锐的", "lack": "缺乏，缺少",
    "landscape": "风景，景观", "launch": "发射，发起", "leading": "领先的，主要的",
    "legal": "法律的，合法的", "liberal": "自由的，开明的", "likely": "可能的",
    "limit": "限制，界限", "link": "联系，连接", "literature": "文学，文献",
    "locate": "位于，定位", "logic": "逻辑", "loyal": "忠诚的",
    "maintain": "维持，保持", "major": "主要的，专业", "manage": "管理，设法",
    "massive": "大量的，巨大的", "material": "材料，物质的", "measure": "测量，措施",
    "medium": "中等的，媒介", "mental": "精神的，心理的", "mention": "提到，提及",
    "method": "方法，方式", "military": "军事的，军队", "minimum": "最小的，最小值",
    "minor": "较小的，次要的", "minority": "少数，少数民族", "mission": "使命，任务",
    "model": "模型，模特", "moderate": "适度的，温和的", "modern": "现代的",
    "modify": "修改，调整", "monitor": "监控，班长", "moral": "道德的，寓意",
    "motivate": "激励，激发", "mutual": "相互的，共同的", "negative": "消极的，否定的",
    "negotiate": "谈判，协商", "network": "网络", "nevertheless": "然而，不过",
    "normal": "正常的，标准的", "notion": "概念，观念", "numerous": "许多的，大量的",
    "object": "物体，目标，反对", "objective": "客观的，目标", "observe": "观察，遵守",
    "obtain": "获得，得到", "obvious": "明显的", "occasion": "场合，时机",
    "occupy": "占据，使忙于", "operate": "操作，经营", "opinion": "意见，看法",
    "opponent": "对手，反对者", "opportunity": "机会", "oppose": "反对，对抗",
    "option": "选择，选项", "ordinary": "普通的，平常的", "organize": "组织，安排",
    "original": "原始的，原创的", "outcome": "结果，成果", "outline": "大纲，概述",
    "overcome": "克服，战胜", "overseas": "海外的，国外的", "pace": "步伐，速度",
    "participate": "参加，参与", "particular": "特别的，具体的", "passion": "激情，热情",
    "patient": "耐心的，病人", "pattern": "模式，图案", "perform": "表现，执行",
    "permanent": "永久的，固定的", "permit": "允许，许可", "phenomenon": "现象",
    "physical": "身体的，物理的", "policy": "政策，方针", "political": "政治的",
    "popular": "流行的，受欢迎的", "portion": "部分，一份", "position": "位置，职位",
    "positive": "积极的，正面的", "possess": "拥有，具有", "potential": "潜在的，潜力",
    "practical": "实际的，实用的", "precious": "珍贵的，宝贵的", "predict": "预测，预言",
    "prefer": "偏爱，更喜欢", "prepare": "准备", "present": "现在的，呈现",
    "preserve": "保护，保存", "pressure": "压力，压迫", "pretend": "假装，装作",
    "prevent": "防止，阻止", "previous": "以前的，先前的", "principle": "原则，原理",
    "priority": "优先，优先权", "private": "私人的，私有的", "procedure": "程序，步骤",
    "process": "过程，处理", "produce": "生产，产生", "profession": "职业，专业",
    "profit": "利润，收益", "progress": "进步，进展", "project": "项目",
    "promise": "承诺，许诺", "promote": "促进，提升", "proper": "适当的，正确的",
    "property": "财产，特性", "propose": "提议，建议", "protect": "保护，防护",
    "prove": "证明，证实", "provide": "提供，供给", "publish": "出版，发表",
    "purchase": "购买", "pursue": "追求，追赶", "quality": "质量，品质",
    "quantity": "数量，大量", "range": "范围，排列", "rapid": "迅速的，快速的",
    "rate": "率，费率，评价", "react": "反应，回应", "realistic": "现实的，实际的",
    "realize": "意识到，实现", "reasonable": "合理的，公道的", "recognize": "认识，认出",
    "recommend": "推荐，建议", "recover": "恢复，康复", "reduce": "减少，降低",
    "reflect": "反映，反射", "reform": "改革，改良", "regard": "认为，看待",
    "region": "地区，区域", "register": "注册，登记", "regular": "定期的，规则的",
    "reject": "拒绝，排斥", "relate": "联系，关联", "release": "释放，发布",
    "relevant": "相关的", "relief": "缓解，救济", "rely": "依赖，依靠",
    "remain": "保持，剩余", "remark": "评论，备注", "remote": "遥远的，远程的",
    "remove": "移除，删除", "replace": "替代，替换", "represent": "代表，表示",
    "reputation": "名声，声誉", "request": "请求，要求", "require": "需要，要求",
    "research": "研究，调查", "reserve": "保留，预订", "resist": "抵抗，抵制",
    "resolve": "解决，决心", "resource": "资源", "respond": "回应，回答",
    "responsible": "负责的，有责任的", "restore": "恢复，修复", "restrict": "限制，约束",
    "result": "结果，导致", "reveal": "揭示，透露", "revenue": "收入，税收",
    "review": "复习，审查", "revolution": "革命", "reward": "奖励，回报",
    "risk": "风险，冒险", "role": "角色，作用", "rural": "农村的",
    "sacrifice": "牺牲，献祭", "sample": "样本，样品", "satisfy": "满足，使满意",
    "scale": "规模，比例", "schedule": "日程，时间表", "scheme": "计划，方案",
    "scientific": "科学的", "secure": "安全的，获得", "seek": "寻求，寻找",
    "select": "选择，挑选", "senior": "高级的，年长的", "sensitive": "敏感的",
    "separate": "分开的，分离", "sequence": "顺序，序列", "series": "系列，连续",
    "serve": "服务，招待", "settle": "解决，定居", "severe": "严重的，严厉的",
    "shelter": "庇护所，遮蔽", "shift": "转移，轮班", "signal": "信号，标志",
    "significance": "重要性，意义", "similar": "相似的，类似的", "situation": "情况，形势",
    "skill": "技能，技巧", "social": "社会的，社交的", "software": "软件",
    "solution": "解决方案", "source": "来源，出处", "specific": "具体的，特定的",
    "stable": "稳定的", "standard": "标准", "status": "地位，状态",
    "strategy": "策略，战略", "strength": "力量，优势", "stress": "压力，强调",
    "structure": "结构，构造", "struggle": "奋斗，斗争", "submit": "提交，服从",
    "succeed": "成功，继承", "suffer": "遭受，受苦", "sufficient": "足够的，充分的",
    "suggest": "建议，暗示", "suitable": "合适的，适当的", "supply": "供应，供给",
    "support": "支持，支撑", "suppose": "假设，认为", "surface": "表面，外表",
    "surround": "包围，围绕", "survive": "生存，幸存", "suspect": "怀疑，嫌疑犯",
    "suspend": "暂停，悬挂", "sustain": "维持，支撑", "symbol": "象征，符号",
    "sympathy": "同情，赞同", "system": "系统，体系", "target": "目标，靶子",
    "technique": "技术，技巧", "technology": "技术，科技", "temporary": "暂时的，临时的",
    "tend": "倾向，趋向", "tension": "紧张，张力", "theme": "主题，题目",
    "theory": "理论，学说", "therefore": "因此，所以", "thorough": "彻底的，全面的",
    "threat": "威胁，恐吓", "thrive": "繁荣，兴旺", "throughout": "贯穿，遍及",
    "tolerate": "容忍，忍受", "topic": "话题，主题", "tough": "艰难的，坚强的",
    "track": "轨道，跟踪", "tradition": "传统", "transfer": "转移，转让",
    "transform": "转变，改造", "transport": "运输，交通", "treasure": "财富，珍宝",
    "treat": "对待，治疗", "trend": "趋势，潮流", "trigger": "触发，引发",
    "ultimate": "最终的，根本的", "undergo": "经历，遭受", "undertake": "承担，从事",
    "unique": "独特的，唯一的", "universal": "普遍的，通用的", "update": "更新，升级",
    "urban": "城市的，都市的", "urge": "催促，强烈要求", "utilize": "利用，使用",
    "valid": "有效的，合法的", "value": "价值，重视", "variety": "多样性，种类",
    "various": "各种各样的", "version": "版本，说法", "victim": "受害者，牺牲品",
    "vision": "视力，愿景", "vital": "至关重要的", "volume": "体积，音量",
    "volunteer": "志愿者，自愿", "vulnerable": "脆弱的，易受伤的", "wealth": "财富，丰富",
    "welfare": "福利，幸福", "widespread": "广泛的，普遍的", "witness": "目击者，见证",
    "worthwhile": "值得的", "yield": "屈服，产出", "zone": "区域，地带",
    # 补充学位英语核心词
    "appreciate": "感激；欣赏；升值", "associate": "联想，联系", "available": "可用的，可获得的",
    "attitude": "态度，看法", "attract": "吸引", "audience": "观众，听众",
    "authority": "权威，当局", "award": "奖，授予", "aware": "意识到的",
    "barrier": "障碍，屏障", "battle": "战斗，战役", "billion": "十亿",
    "biology": "生物学", "bond": "纽带，债券", "bonus": "奖金，红利",
    "carbon": "碳", "category": "类别，种类", "cell": "细胞，牢房",
    "chain": "链条，连锁", "channel": "频道，渠道", "chapter": "章，回",
    "chemistry": "化学", "chip": "芯片，碎片", "citizen": "公民，市民",
    "civil": "公民的，民用的", "clinic": "诊所", "code": "代码，密码",
    "colleague": "同事", "collection": "收藏，收集", "column": "专栏，栏目",
    "combination": "组合，结合", "committee": "委员会", "commodity": "商品，货物",
    "compensation": "补偿，赔偿", "complaint": "投诉，抱怨", "component": "组成部分",
    "compound": "复合物，院子", "comprehensive": "全面的，综合的", "comprise": "包含，由...组成",
    "concrete": "具体的，混凝土", "conductor": "指挥，导体", "confident": "自信的",
    "confuse": "混淆", "congress": "国会，代表大会", "constitution": "宪法，章程",
    "consumer": "消费者", "contemporary": "当代的", "convention": "惯例，大会",
    "conviction": "定罪，坚信", "coordinate": "协调，坐标", "core": "核心，要点",
    "corporate": "公司的，法人的", "correspond": "对应，通信", "council": "理事会，委员会",
    "counter": "柜台，反击", "county": "县，郡", "coup": "政变",
    "court": "法庭，球场", "craft": "工艺，手艺", "criminal": "犯罪的，罪犯",
    "crowd": "人群，拥挤", "crown": "王冠，皇冠", "currency": "货币，通货",
    "curve": "曲线，弯曲", "cycle": "循环，周期", "database": "数据库",
    "deadline": "截止日期", "dean": "院长，系主任", "defendant": "被告",
    "defense": "防御，辩护", "deficit": "赤字，不足", "delegate": "代表，委派",
    "democracy": "民主", "department": "部门，系", "deposit": "存款，押金",
    "depression": "抑郁，萧条", "derive": "源自，获得", "dimension": "维度，尺寸",
    "discipline": "纪律，学科", "discount": "折扣", "disorder": "混乱，失调",
    "dispute": "争论，纠纷", "dissolve": "溶解，解散", "diverse": "多样的，不同的",
    "division": "部门，分割", "dominant": "主导的，占优势的", "draft": "草案，草稿",
    "drift": "漂流，大意", "drought": "干旱", "duration": "持续时间",
    "dynamic": "动态的，充满活力的", "ease": "缓解，轻松", "edit": "编辑",
    "elderly": "老年人，年长的", "election": "选举", "electronics": "电子学",
    "embassy": "大使馆", "emergency": "紧急情况", "emission": "排放，散发",
    "empire": "帝国", "enforce": "执行，强制", "enhance": "增强，提高",
    "episode": "插曲，一集", "equation": "方程式，等式", "equivalent": "等价的，等同物",
    "estate": "房产，庄园", "ethnic": "民族的，种族的", "exception": "例外",
    "exclusive": "独家的，排他的", "executive": "行政的，高管", "expectation": "期望，期待",
    "explosion": "爆炸，激增", "extent": "程度，范围", "fabric": "织物，结构",
    "faculty": "才能，学院", "fade": "褪色，消退", "failure": "失败",
    "federal": "联邦的", "feedback": "反馈", "festival": "节日",
    "fiber": "纤维", "fiction": "小说，虚构", "filter": "过滤器，筛选",
    "flame": "火焰，热情", "fleet": "舰队，车队", "fluid": "流体，流动的",
    "folk": "民间的，人们", "forever": "永远", "formation": "形成，编队",
    "formula": "公式，方案", "fraction": "分数，部分", "framework": "框架，结构",
    "frontier": "前沿，边境", "fuel": "燃料，刺激", "fulfill": "实现，履行",
    "gallery": "画廊，美术馆", "gene": "基因", "gesture": "手势，姿态",
    "grain": "谷物，颗粒", "grasp": "抓住，理解", "gravity": "重力，严重性",
    "greenhouse": "温室", "gross": "总的，毛的", "guideline": "指南，指导方针",
    "habitat": "栖息地", "halt": "停止，暂停", "harbor": "港口，庇护",
    "harsh": "严厉的，严酷的", "haul": "拖，拉", "headquarters": "总部",
    "hearing": "听力，听证会", "heritage": "遗产，传统", "horizon": "地平线，眼界",
    "humanity": "人类，人性", "humor": "幽默", "hunger": "饥饿，渴望",
    "ideal": "理想的，理想", "identical": "相同的，同一的", "ignorance": "无知",
    "immigrant": "移民", "implication": "含义，暗示", "impose": "强加，征收",
    "impulse": "冲动，脉冲", "incentive": "激励，动力", "index": "索引，指数",
    "indication": "指示，迹象", "inevitable": "不可避免的", "infant": "婴儿",
    "infection": "感染，传染", "inflation": "通货膨胀", "ingredient": "成分，原料",
    "inhabitant": "居民", "inherent": "固有的，内在的", "injury": "伤害，受伤",
    "insight": "洞察力，见解", "inspection": "检查，视察", "installation": "安装，设施",
    "instrument": "工具，乐器", "integration": "整合，一体化", "intention": "意图，目的",
    "interaction": "互动，相互作用", "interference": "干涉，干扰", "interval": "间隔，中场休息",
    "invasion": "入侵，侵略", "invention": "发明", "investment": "投资",
    "invitation": "邀请", "involvement": "参与，卷入", "jealous": "嫉妒的",
    "jury": "陪审团", "kingdom": "王国", "label": "标签，标注",
    "laboratory": "实验室", "laptop": "笔记本电脑", "laughter": "笑声，笑",
    "laundry": "洗衣，洗衣店", "leader": "领导者", "lease": "租约，租赁",
    "legacy": "遗产，遗留", "legislation": "立法，法规", "liability": "责任，负债",
    "lifetime": "一生，终身", "likelihood": "可能性", "limitation": "限制，局限",
    "literacy": "读写能力", "loan": "贷款，借出", "lobby": "大厅，游说",
    "luxury": "奢侈，奢侈品", "magnetic": "磁性的，有吸引力的", "magnificent": "壮丽的，宏伟的",
    "mainstream": "主流", "manufacturer": "制造商", "margin": "边缘，利润",
    "mechanism": "机制，机械", "membership": "会员资格", "merchant": "商人，商业的",
    "mercy": "仁慈，宽恕", "migration": "迁移，移民", "mill": "工厂，磨坊",
    "ministry": "部，部门", "miracle": "奇迹", "molecule": "分子",
    "monopoly": "垄断", "morality": "道德，品行", "mortgage": "抵押贷款",
    "motion": "运动，动议", "motive": "动机，目的", "municipal": "市政的",
    "myth": "神话，误解", "narrative": "叙述，故事", "nationality": "国籍",
    "navigation": "导航，航行", "necessity": "必要性，必需品", "neighborhood": "邻里，社区",
    "nomination": "提名，任命", "nonprofit": "非营利", "norm": "规范，标准",
    "nuclear": "核的，原子能的", "obligation": "义务，责任", "occupation": "职业，占领",
    "offense": "冒犯，犯罪", "operator": "操作员，经营者", "optimistic": "乐观的",
    "organic": "有机的", "organizational": "组织的", "ownership": "所有权",
    "oxygen": "氧气", "panel": "面板，小组", "parallel": "平行的，平行",
    "parameter": "参数，界限", "parliament": "议会，国会", "partnership": "合伙，合作关系",
    "passive": "被动的，消极的", "peak": "顶峰，峰值", "penalty": "惩罚，罚款",
    "pension": "养老金，退休金", "perception": "感知，看法", "phase": "阶段，相位",
    "philosopher": "哲学家", "platform": "平台", "pleasure": "快乐，乐趣",
    "pledge": "承诺，抵押", "plot": "情节，阴谋", "poetry": "诗歌",
    "poll": "民意调查", "pollution": "污染", "portfolio": "投资组合，文件夹",
    "portrait": "肖像，描绘", "pose": "姿势，造成", "possession": "拥有，财产",
    "poverty": "贫穷", "preference": "偏好，偏爱", "prejudice": "偏见，歧视",
    "premium": "保险费，优质的", "prescription": "处方，药方", "presence": "存在，出席",
    "president": "总统，主席", "primarily": "主要地，首先", "prime": "首要的，最好的",
    "prison": "监狱", "privilege": "特权，荣幸", "probe": "探针，调查",
    "profile": "简介，轮廓", "promotion": "晋升，推广", "proportion": "比例，部分",
    "prosecutor": "检察官", "prospect": "前景，展望", "protein": "蛋白质",
    "protocol": "协议，礼仪", "province": "省，领域", "provision": "规定，供应",
    "psychology": "心理学", "publication": "出版，出版物", "pulse": "脉搏，脉冲",
    "punishment": "惩罚", "quest": "探索，追求", "quote": "引用，报价",
    "racial": "种族的", "radical": "激进的，根本的", "rally": "集会，拉力赛",
    "ranking": "排名，等级", "ratio": "比率，比例", "raw": "生的，原始的",
    "reader": "读者", "realm": "领域，王国", "rebel": "反叛，造反",
    "recession": "衰退，经济衰退", "recognition": "认可，识别", "recovery": "恢复，复苏",
    "recruit": "招募，招聘", "reference": "参考，引用", "regime": "政权，制度",
    "regulation": "规则，管理", "reinforce": "加强，增援", "religion": "宗教",
    "rely": "依赖，依靠", "remain": "保持，剩余", "renewable": "可再生的",
    "replacement": "替换，替代", "representation": "代表，陈述", "republican": "共和党的",
    "reservation": "预订，保留", "residence": "居住，住所", "resistance": "抵抗，阻力",
    "resolution": "决议，决心", "retail": "零售", "retirement": "退休",
    "reverse": "反向，逆转", "rhythm": "节奏，韵律", "rival": "竞争对手",
    "robot": "机器人", "rocket": "火箭", "route": "路线，路径",
    "royal": "皇家的，王室的", "ruin": "毁灭，废墟", "sake": "缘故，目的",
    "satellite": "卫星", "scandal": "丑闻，流言蜚语", "scenario": "场景，方案",
    "scholar": "学者", "scope": "范围", "scrutiny": "仔细审查",
    "sector": "部门，领域", "semester": "学期", "seminar": "研讨会",
    "sensation": "感觉，轰动", "sentiment": "情绪，感情", "session": "会议，学期",
    "settlement": "解决，定居点", "shame": "羞耻，遗憾", "shape": "形状，塑造",
    "shareholder": "股东", "sight": "视力，景象", "signature": "签名",
    "significance": "重要性，意义", "silence": "沉默，寂静", "simplicity": "简单，朴素",
    "slavery": "奴隶制", "slice": "切片，部分", "slogan": "口号，标语",
    "slot": "位置，槽", "solar": "太阳的", "sole": "唯一的，鞋底",
    "solidarity": "团结", "span": "跨度，一段时间", "specialist": "专家",
    "species": "物种", "spectrum": "光谱，范围", "sphere": "领域，球体",
    "sponsor": "赞助者，赞助", "spot": "地点，斑点", "stability": "稳定性",
    "stadium": "体育场", "stake": "利害关系，股份", "statistics": "统计学",
    "stem": "茎，源于", "stock": "股票，库存", "storage": "存储，仓库",
    "strain": "压力，拉紧", "stream": "溪流，流", "strike": "罢工，打击",
    "studio": "工作室，录音室", "subsidy": "补贴，津贴", "substantial": "大量的，实质的",
    "substitute": "替代品，替代", "suicide": "自杀", "summit": "峰会，山顶",
    "supervision": "监督，管理", "supplement": "补充，增刊", "surgeon": "外科医生",
    "surgery": "外科手术", "surplus": "过剩，盈余", "surveillance": "监视，监管",
    "survey": "调查，测量", "survival": "生存，幸存", "sustainable": "可持续的",
    "switch": "开关，转换", "tactic": "策略，战术", "tale": "故事，传说",
    "tank": "坦克，水箱", "tap": "水龙头，轻敲", "tax": "税，征税",
    "telescope": "望远镜", "temple": "寺庙，太阳穴", "territory": "领土，领域",
    "terror": "恐怖，恐怖行为", "textile": "纺织品", "therapy": "治疗，疗法",
    "thereby": "因此，从而", "thread": "线，线索", "threshold": "门槛，临界值",
    "tissue": "组织，纸巾", "toll": "通行费，伤亡人数", "tone": "语气，音调",
    "tournament": "锦标赛", "trail": "小径，追踪", "trait": "特征，特性",
    "transaction": "交易", "transition": "过渡，转变", "transmission": "传输，传播",
    "trauma": "创伤", "treaty": "条约，协议", "trial": "审判，试验",
    "tribe": "部落", "troop": "军队，部队", "tunnel": "隧道",
    "turnover": "营业额，人员流动", "tutor": "导师，家教", "twin": "双胞胎",
    "universe": "宇宙", "utility": "公用事业，效用", "vacuum": "真空",
    "venture": "冒险，企业", "venue": "场地，地点", "verdict": "裁决，判决",
    "vessel": "船只，容器", "veteran": "老兵，经验丰富的", "via": "通过，经由",
    "virtue": "美德，优点", "virus": "病毒", "visible": "可见的",
    "vitamin": "维生素", "vocabulary": "词汇", "voter": "选民",
    "wage": "工资，发动", "wave": "波浪，挥动", "weapon": "武器",
    "wheat": "小麦", "wildlife": "野生动物", "wisdom": "智慧，明智",
    "withdraw": "撤回，取款", "workforce": "劳动力", "workshop": "研讨会，车间",
    "worldwide": "世界范围的", "worship": "崇拜，敬拜", "wrap": "包裹，缠绕",
}

def crawl_word(word):
    """从Dictionary API爬取单词详情"""
    try:
        resp = requests.get(API_URL.format(word), timeout=8)
        if resp.status_code != 200:
            return None
        data = resp.json()[0]
        
        # 提取音标
        phonetic_us = ""
        phonetic_uk = ""
        for p in data.get("phonetics", []):
            audio = str(p.get("audio", "")).lower()
            if not phonetic_us and ("us" in audio or "us" in p.get("text", "")):
                phonetic_us = p.get("text", "")
            if not phonetic_uk and ("uk" in audio or "uk" in p.get("text", "")):
                phonetic_uk = p.get("text", "")
        if not phonetic_us and data.get("phonetics"):
            phonetic_us = data["phonetics"][0].get("text", "")
        
        # 提取含义
        meanings = data.get("meanings", [])
        pos = meanings[0].get("partOfSpeech", "") if meanings else ""
        defs = meanings[0].get("definitions", []) if meanings else []
        definition = defs[0].get("definition", "") if defs else ""
        example = defs[0].get("example", "") if defs else ""
        
        return {
            "word": word,
            "phonetic_us": phonetic_us.replace("/", "").strip() if phonetic_us else "",
            "phonetic_uk": phonetic_uk.replace("/", "").strip() if phonetic_uk else "",
            "pos": pos,
            "definition": definition,
            "example": example,
        }
    except:
        return None

def crawl_batch(words_dict, max_workers=5, delay=0.3):
    """批量爬取单词"""
    results = {}
    total = len(words_dict)
    completed = 0
    
    # 先尝试加载已有数据
    if os.path.exists(OUTPUT_FILE):
        with open(OUTPUT_FILE, 'r') as f:
            results = json.load(f)
        print(f"Loaded {len(results)} existing results")
    
    remaining = {k: v for k, v in words_dict.items() if k not in results}
    print(f"Need to crawl: {len(remaining)} words")
    
    words_list = list(remaining.keys())
    batch_size = 20
    
    for i in range(0, len(words_list), batch_size):
        batch = words_list[i:i+batch_size]
        with concurrent.futures.ThreadPoolExecutor(max_workers=max_workers) as executor:
            futures = {executor.submit(crawl_word, w): w for w in batch}
            for future in concurrent.futures.as_completed(futures):
                word = futures[future]
                try:
                    data = future.result()
                    if data:
                        # Format phonetics
                        if data["phonetic_us"]:
                            data["phonetic_us"] = "/" + data["phonetic_us"] + "/"
                        if data["phonetic_uk"]:
                            data["phonetic_uk"] = "/" + data["phonetic_uk"] + "/"
                        results[word] = {
                            **data,
                            "def_cn": words_dict[word]
                        }
                except:
                    pass
                completed += 1
        
        # 保存进度
        with open(OUTPUT_FILE, 'w') as f:
            json.dump(results, f, ensure_ascii=False, indent=2)
        print(f"Progress: {len(results)}/{total} ({(len(results)/total)*100:.1f}%)")
        time.sleep(delay)
    
    return results

def generate_java_code(results):
    """生成Java DataInitializer代码"""
    lines = []
    for word, data in results.items():
        def_cn = data.get("def_cn", word)
        def_en = data.get("definition", "")
        phonetic_us = data.get("phonetic_us", "")
        phonetic_uk = data.get("phonetic_uk", "")
        pos = data.get("pos", "")
        example = data.get("example", "")
        
        # Clean up for Java string
        def_en = def_en.replace('"', '\\"').replace('\n', ' ')
        example = example.replace('"', '\\"').replace('\n', ' ')
        
        # Determine difficulty and category
        length = len(word)
        level = 1 if length <= 5 else (2 if length <= 8 else 3)
        category = "学位英语"
        
        line = f'createWord("{word}", "{phonetic_us}", "{phonetic_uk}", "{pos}", "{def_cn}",\n'
        line += f'        "{def_en}", "{example}",\n'
        line += f'        "", {level}, "{category}")'
        lines.append(line)
    
    code = ",\n".join(lines)
    
    with open(JAVA_OUTPUT, 'w') as f:
        f.write(f"// 学位英语完整词汇 (from Dictionary API crawl)\n")
        f.write(f"// Total: {len(results)} words\n\n")
        f.write(code)
    
    print(f"Java code saved to: {JAVA_OUTPUT}")
    return code

if __name__ == "__main__":
    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument("--sample", type=int, default=0, help="Crawl N sample words (0=all)")
    parser.add_argument("--generate-java", action="store_true", help="Generate Java code from results")
    parser.add_argument("--stats", action="store_true", help="Show stats only")
    args = parser.parse_args()
    
    if args.stats:
        if os.path.exists(OUTPUT_FILE):
            with open(OUTPUT_FILE) as f:
                data = json.load(f)
            print(f"Total words crawled: {len(data)}")
            print(f"With phonetics: {sum(1 for d in data.values() if d.get('phonetic_us'))}")
            print(f"With examples: {sum(1 for d in data.values() if d.get('example'))}")
            print(f"JSON file: {OUTPUT_FILE}")
        else:
            print("No data yet. Run crawl first.")
        sys.exit(0)
    
    if args.generate_java:
        if not os.path.exists(OUTPUT_FILE):
            print("No data file. Run crawl first!")
            sys.exit(1)
        with open(OUTPUT_FILE) as f:
            data = json.load(f)
        generate_java_code(data)
        sys.exit(0)
    
    # Crawl
    target_words = WORDS
    if args.sample > 0:
        target_words = dict(list(WORDS.items())[:args.sample])
    
    print(f"Starting crawl: {len(target_words)} words")
    print(f"API: {API_URL}")
    print(f"Output: {OUTPUT_FILE}")
    print()
    
    results = crawl_batch(target_words)
    print(f"\nDone! Crawled {len(results)} words")
