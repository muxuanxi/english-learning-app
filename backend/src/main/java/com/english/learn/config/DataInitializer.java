package com.english.learn.config;

import com.english.learn.model.*;
import com.english.learn.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final WordRepository wordRepo;
    private final GrammarLessonRepository grammarRepo;
    private final ArticleRepository articleRepo;
    private final ListeningMaterialRepository listeningRepo;
    private final QuizRepository quizRepo;
    private final QuizQuestionRepository quizQuestionRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepo.count() > 0) return;
        log.info("=== Initializing sample data ===");

        // Demo user (password: demo123)
        User demo = User.builder()
                .username("demo")
                .email("demo@englishmaster.com")
                .password(passwordEncoder.encode("demo123"))
                .nickname("学习达人")
                .level(User.Level.INTERMEDIATE)
                .dailyGoal(30)
                .build();
        userRepo.save(demo);

        // Words - 80 vocabulary words
        List<Word> words = List.of(
                // ===== 基础动词 (Basic Verbs) =====
                createWord("abandon", "/əˈbæn.dən/", "/əˈbæn.dən/", "v.", "放弃，抛弃",
                        "To give up or leave completely",
                        "They had to abandon the project due to lack of funding.",
                        "由于缺乏资金，他们不得不放弃这个项目。", 2, "动词"),
                createWord("acquire", "/əˈkwaɪər/", "/əˈkwaɪə/", "v.", "获得，习得",
                        "To gain or obtain something",
                        "It takes years to acquire fluency in a foreign language.",
                        "掌握一门外语需要多年的时间。", 3, "动词"),
                createWord("advocate", "/ˈæd.və.keɪt/", "/ˈæd.və.keɪt/", "v.", "提倡，拥护",
                        "To publicly support or recommend",
                        "Many experts advocate for more sustainable energy policies.",
                        "许多专家提倡更可持续的能源政策。", 3, "动词"),
                createWord("alleviate", "/əˈliː.vi.eɪt/", "/əˈliː.vi.eɪt/", "v.", "减轻，缓解",
                        "To make suffering or a problem less severe",
                        "The medicine helped alleviate her chronic pain.",
                        "这种药帮助缓解了她的慢性疼痛。", 4, "动词"),
                createWord("articulate", "/ɑːrˈtɪk.jə.leɪt/", "/ɑːˈtɪk.jə.leɪt/", "v.", "清晰表达",
                        "To express ideas clearly and effectively",
                        "She was able to articulate her vision for the company.",
                        "她能够清晰地表达她对公司的愿景。", 3, "动词"),

                // ===== 常用形容词 (Common Adjectives) =====
                createWord("benevolent", "/bəˈnev.əl.ənt/", "/bəˈnev.əl.ənt/", "adj.", "仁慈的，慈善的",
                        "Well-meaning and kindly",
                        "The benevolent donor contributed millions to education.",
                        "这位仁慈的捐赠者为教育捐赠了数百万。", 4, "形容词"),
                createWord("concise", "/kənˈsaɪs/", "/kənˈsaɪs/", "adj.", "简洁的，简明的",
                        "Giving information clearly and in few words",
                        "Please keep your report concise and to the point.",
                        "请保持你的报告简洁明了。", 3, "形容词"),
                createWord("controversial", "/ˌkɑːn.trəˈvɜːr.ʃəl/", "/ˌkɒn.trəˈvɜː.ʃəl/", "adj.", "有争议的",
                        "Giving rise to public disagreement",
                        "The controversial policy sparked nationwide protests.",
                        "这项有争议的政策引发了全国范围的抗议。", 3, "形容词"),
                createWord("courageous", "/kəˈreɪ.dʒəs/", "/kəˈreɪ.dʒəs/", "adj.", "勇敢的，有胆量的",
                        "Not deterred by danger or pain; brave",
                        "The firefighters made a courageous rescue attempt.",
                        "消防员进行了勇敢的救援尝试。", 2, "形容词"),
                createWord("credible", "/ˈkred.ə.bəl/", "/ˈkred.ə.bəl/", "adj.", "可信的，可靠的",
                        "Able to be believed; convincing",
                        "We need credible evidence before making accusations.",
                        "在提出指控之前我们需要可靠的证据。", 3, "形容词"),
                createWord("crucial", "/ˈkruː.ʃəl/", "/ˈkruː.ʃəl/", "adj.", "至关重要的",
                        "Decisive or critical, especially in the success or failure of something",
                        "Communication is crucial for a healthy relationship.",
                        "沟通对健康的关系至关重要。", 2, "形容词"),
                createWord("diverse", "/daɪˈvɜːrs/", "/daɪˈvɜːs/", "adj.", "多样的，多元的",
                        "Showing a great deal of variety",
                        "The university attracts students from diverse cultural backgrounds.",
                        "这所大学吸引了来自不同文化背景的学生。", 2, "形容词"),
                createWord("dominant", "/ˈdɑː.mə.nənt/", "/ˈdɒm.ɪ.nənt/", "adj.", "占优势的，主导的",
                        "Most important, powerful, or influential",
                        "English is the dominant language of international business.",
                        "英语是国际商务的主要语言。", 2, "形容词"),

                // ===== 学术词汇 (Academic Words) =====
                createWord("hypothesis", "/haɪˈpɑː.θə.sɪs/", "/haɪˈpɒθ.ə.sɪs/", "n.", "假设，假说",
                        "A proposed explanation based on limited evidence",
                        "The scientist tested her hypothesis through experiments.",
                        "科学家通过实验验证了她的假设。", 4, "学术词汇"),
                createWord("phenomenon", "/fɪˈnɑː.mə.nɑːn/", "/fɪˈnɒm.ɪ.nən/", "n.", "现象",
                        "A fact or event that can be observed",
                        "Global warming is a complex phenomenon with multiple causes.",
                        "全球变暖是一个有多种原因的复杂现象。", 3, "学术词汇"),
                createWord("paradigm", "/ˈper.ə.daɪm/", "/ˈpær.ə.daɪm/", "n.", "范式，模式",
                        "A typical example or pattern of something",
                        "The internet created a new paradigm for communication.",
                        "互联网为通信创造了新的范式。", 5, "学术词汇"),
                createWord("empirical", "/ɪmˈpɪr.ɪ.kəl/", "/ɪmˈpɪr.ɪ.kəl/", "adj.", "经验主义的",
                        "Based on observation or experience rather than theory",
                        "The research provides empirical evidence for the theory.",
                        "这项研究为该理论提供了经验证据。", 5, "学术词汇"),
                createWord("theoretical", "/ˌθiː.əˈret.ɪ.kəl/", "/ˌθɪəˈret.ɪ.kəl/", "adj.", "理论的",
                        "Concerned with theories rather than practical application",
                        "The course covers both theoretical and practical aspects.",
                        "这门课程涵盖理论和实践两个方面。", 4, "学术词汇"),
                createWord("synthesis", "/ˈsɪn.θə.sɪs/", "/ˈsɪn.θə.sɪs/", "n.", "综合，合成",
                        "The combination of ideas to form a theory or system",
                        "The essay presents a synthesis of different viewpoints.",
                        "这篇文章呈现了不同观点的综合。", 4, "学术词汇"),
                createWord("correlation", "/ˌkɔːr.əˈleɪ.ʃən/", "/ˌkɒr.əˈleɪ.ʃən/", "n.", "相关性",
                        "A mutual relationship between two or more things",
                        "There is a strong correlation between education and income.",
                        "教育和收入之间有很强的相关性。", 4, "学术词汇"),

                // ===== 商务词汇 (Business Words) =====
                createWord("revenue", "/ˈrev.ə.nuː/", "/ˈrev.ən.juː/", "n.", "收入，营收",
                        "Income generated from business activities",
                        "The company's annual revenue exceeded 10 billion dollars.",
                        "该公司的年收入超过了100亿美元。", 3, "商务"),
                createWord("stakeholder", "/ˈsteɪkˌhoʊl.dər/", "/ˈsteɪkˌhəʊl.də/", "n.", "利益相关者",
                        "A person with an interest in an organization",
                        "All stakeholders were invited to the strategic meeting.",
                        "所有利益相关者都被邀请参加了战略会议。", 3, "商务"),
                createWord("entrepreneur", "/ˌɑːn.trə.prəˈnɜːr/", "/ˌɒn.trə.prəˈnɜː/", "n.", "企业家",
                        "A person who starts and runs a business",
                        "She is a successful entrepreneur who founded three startups.",
                        "她是一位成功的企业家，创办了三家初创公司。", 3, "商务"),
                createWord("benchmark", "/ˈbentʃ.mɑːrk/", "/ˈbentʃ.mɑːk/", "n.", "基准，标杆",
                        "A standard against which things may be compared",
                        "We use industry benchmarks to measure our performance.",
                        "我们使用行业基准来衡量我们的业绩。", 3, "商务"),
                createWord("logistics", "/ləˈdʒɪs.tɪks/", "/ləˈdʒɪs.tɪks/", "n.", "物流，后勤",
                        "The detailed organization of a complex operation",
                        "Efficient logistics are essential for online retail.",
                        "高效的物流对在线零售至关重要。", 3, "商务"),
                createWord("feasibility", "/ˌfiː.zəˈbɪl.ə.ti/", "/ˌfiː.zəˈbɪl.ə.ti/", "n.", "可行性",
                        "The state of being possible or practical",
                        "We need to conduct a feasibility study before investing.",
                        "在投资之前我们需要进行可行性研究。", 3, "商务"),

                // ===== 高级形容词 (Advanced Adjectives) =====
                createWord("inevitable", "/ɪnˈev.ɪ.tə.bəl/", "/ɪnˈev.ɪ.tə.bəl/", "adj.", "不可避免的",
                        "Certain to happen; unavoidable",
                        "Change is an inevitable part of life.",
                        "变化是生活中不可避免的一部分。", 3, "高级词汇"),
                createWord("lucrative", "/ˈluː.krə.tɪv/", "/ˈluː.krə.tɪv/", "adj.", "有利可图的，赚钱的",
                        "Producing a great deal of profit",
                        "She landed a lucrative contract with a major corporation.",
                        "她与一家大公司签订了一份利润丰厚的合同。", 3, "高级词汇"),
                createWord("obsolete", "/ˌɑːb.səˈliːt/", "/ˌɒb.səˈliːt/", "adj.", "过时的，废弃的",
                        "No longer produced or used; out of date",
                        "Many traditional skills have become obsolete with automation.",
                        "许多传统技能随着自动化变得过时了。", 4, "高级词汇"),
                createWord("spontaneous", "/spɑːnˈteɪ.ni.əs/", "/spɒnˈteɪ.ni.əs/", "adj.", "自发的，自然的",
                        "Performed or occurring as a result of a sudden impulse",
                        "We made a spontaneous decision to go on a road trip.",
                        "我们临时决定去自驾游。", 3, "高级词汇"),
                createWord("vulnerable", "/ˈvʌl.nər.ə.bəl/", "/ˈvʌl.nər.ə.bəl/", "adj.", "脆弱的，易受伤的",
                        "Susceptible to physical or emotional harm",
                        "Children are especially vulnerable to online threats.",
                        "儿童特别容易受到在线威胁的影响。", 3, "高级词汇"),

                // ===== 进阶动词 (Advanced Verbs) =====
                createWord("deteriorate", "/dɪˈtɪr.i.ə.reɪt/", "/dɪˈtɪə.ri.ə.reɪt/", "v.", "恶化，退化",
                        "To become progressively worse",
                        "His health began to deteriorate after the accident.",
                        "事故后他的健康状况开始恶化。", 4, "动词"),
                createWord("exacerbate", "/ɪɡˈzæs.ər.beɪt/", "/ɪɡˈzæs.ə.beɪt/", "v.", "加剧，使恶化",
                        "To make a problem or situation worse",
                        "The drought exacerbated the food shortage crisis.",
                        "干旱加剧了粮食短缺危机。", 5, "动词"),
                createWord("facilitate", "/fəˈsɪl.ɪ.teɪt/", "/fəˈsɪl.ɪ.teɪt/", "v.", "促进，使便利",
                        "To make an action or process easier",
                        "Technology can facilitate faster communication globally.",
                        "技术可以促进全球范围内更快的沟通。", 4, "动词"),
                createWord("implement", "/ˈɪm.plɪ.ment/", "/ˈɪm.plɪ.ment/", "v.", "实施，执行",
                        "To put a decision or plan into effect",
                        "The government plans to implement new environmental regulations.",
                        "政府计划实施新的环境法规。", 3, "动词"),
                createWord("scrutinize", "/ˈskruː.tɪ.naɪz/", "/ˈskruː.tɪ.naɪz/", "v.", "仔细审查",
                        "To examine or inspect closely and thoroughly",
                        "The auditor will scrutinize all financial records.",
                        "审计员将仔细审查所有财务记录。", 4, "动词"),

                // ===== 常用名词 (Common Nouns) =====
                createWord("consensus", "/kənˈsen.səs/", "/kənˈsen.səs/", "n.", "共识，一致意见",
                        "A general agreement among a group",
                        "The committee reached a consensus on the budget proposal.",
                        "委员会就预算提案达成了共识。", 3, "名词"),
                createWord("dilemma", "/daɪˈlem.ə/", "/daɪˈlem.ə/", "n.", "困境，两难",
                        "A situation requiring a difficult choice",
                        "She faced a dilemma between career and family.",
                        "她面临事业和家庭之间的两难选择。", 3, "名词"),
                createWord("infrastructure", "/ˈɪn.frəˌstrʌk.tʃər/", "/ˈɪn.frəˌstrʌk.tʃə/", "n.", "基础设施",
                        "The basic physical systems of a country or organization",
                        "The city invested heavily in transportation infrastructure.",
                        "该市在交通基础设施上投入了大量资金。", 3, "名词"),
                createWord("momentum", "/moʊˈmen.təm/", "/məˈmen.təm/", "n.", "势头，动力",
                        "The impetus gained by a moving object or course of events",
                        "The project has gained momentum and is now ahead of schedule.",
                        "该项目势头正盛，目前已提前完成进度。", 2, "名词"),
                createWord("precedent", "/ˈpres.ə.dent/", "/ˈpres.ɪ.dənt/", "n.", "先例，前例",
                        "An earlier event regarded as an example for future situations",
                        "The court's decision set a legal precedent for similar cases.",
                        "法院的裁决为类似案件设立了法律先例。", 4, "名词"),
                createWord("resilience", "/rɪˈzɪl.jəns/", "/rɪˈzɪl.jəns/", "n.", "韧性，恢复力",
                        "The capacity to recover quickly from difficulties",
                        "Her resilience in the face of adversity is inspiring.",
                        "她在逆境中展现的韧性令人鼓舞。", 3, "名词"),

                // ===== 原词 + 派生词 (Word Families) =====
                createWord("innovate", "/ˈɪn.ə.veɪt/", "/ˈɪn.əʊ.veɪt/", "v.", "创新，革新",
                        "To introduce something new or different",
                        "Companies must constantly innovate to stay competitive.",
                        "公司必须不断创新以保持竞争力。", 2, "动词"),
                createWord("innovation", "/ˌɪn.əˈveɪ.ʃən/", "/ˌɪn.əˈveɪ.ʃən/", "n.", "创新，革新",
                        "The act of introducing something new",
                        "Technological innovation drives economic growth.",
                        "技术创新推动经济增长。", 2, "名词"),
                createWord("innovative", "/ˈɪn.ə.veɪ.tɪv/", "/ˈɪn.ə.və.tɪv/", "adj.", "创新的",
                        "Featuring new methods; advanced and original",
                        "The startup developed an innovative solution to the problem.",
                        "这家初创公司开发了一个创新的解决方案。", 2, "形容词"),
                createWord("collaborate", "/kəˈlæb.ə.reɪt/", "/kəˈlæb.ə.reɪt/", "v.", "合作，协作",
                        "Work jointly on an activity or project",
                        "The two departments will collaborate on the new project.",
                        "两个部门将在这个新项目上合作。", 2, "动词"),
                createWord("collaboration", "/kəˌlæb.əˈreɪ.ʃən/", "/kəˌlæb.əˈreɪ.ʃən/", "n.", "合作，协作",
                        "The action of working with someone to produce something",
                        "International collaboration is key to solving climate change.",
                        "国际合作是解决气候变化的关键。", 2, "名词"),

                // ===== 日常生活 (Daily Life) =====
                createWord("indispensable", "/ˌɪn.dɪˈspen.sə.bəl/", "/ˌɪn.dɪˈspen.sə.bəl/", "adj.", "不可或缺的",
                        "Absolutely necessary; essential",
                        "The internet has become indispensable in daily life.",
                        "互联网已成为日常生活中不可或缺的部分。", 3, "日常"),
                createWord("procrastinate", "/proʊˈkræs.tɪ.neɪt/", "/prəˈkræs.tɪ.neɪt/", "v.", "拖延",
                        "To delay or postpone action",
                        "Stop procrastinating and start working on your assignment!",
                        "别再拖延了，开始做你的作业吧！", 3, "日常"),
                createWord("commute", "/kəˈmjuːt/", "/kəˈmjuːt/", "v.", "通勤，上下班往返",
                        "To travel regularly between home and work",
                        "She commutes two hours every day to work in the city.",
                        "她每天通勤两小时去市区上班。", 1, "日常"),
                createWord("budget", "/ˈbʌdʒ.ɪt/", "/ˈbʌdʒ.ɪt/", "n.", "预算",
                        "An estimate of income and expenditure for a period",
                        "We need to stick to a strict budget this month.",
                        "这个月我们需要严格遵守预算。", 1, "日常"),
                createWord("cuisine", "/kwɪˈziːn/", "/kwɪˈziːn/", "n.", "菜肴，烹饪",
                        "A style or method of cooking characteristic of a region",
                        "Italian cuisine is loved all over the world.",
                        "意大利菜肴深受全世界喜爱。", 2, "日常"),

                // ===== 情感心理 (Emotions & Psychology) =====
                createWord("nostalgia", "/nɑːˈstæl.dʒə/", "/nɒˈstæl.dʒə/", "n.", "怀旧，乡愁",
                        "A sentimental longing for the past",
                        "The old song filled her with a sense of nostalgia.",
                        "这首老歌让她充满了怀旧之情。", 4, "情感"),
                createWord("empathy", "/ˈem.pə.θi/", "/ˈem.pə.θi/", "n.", "同理心，共情",
                        "The ability to understand others' feelings",
                        "Good leaders show empathy towards their team members.",
                        "好的领导者对团队成员表现出同理心。", 3, "情感"),
                createWord("introvert", "/ˈɪn.trə.vɜːrt/", "/ˈɪn.trə.vɜːt/", "n.", "内向的人",
                        "A person who is predominantly concerned with their own thoughts",
                        "As an introvert, she prefers small gatherings over large parties.",
                        "作为一个内向的人，她更喜欢小型聚会而不是大型派对。", 3, "情感"),

                // ===== 科技词汇 (Tech Words) =====
                createWord("algorithm", "/ˈæl.ɡə.rɪð.əm/", "/ˈæl.ɡə.rɪð.əm/", "n.", "算法",
                        "A set of rules for solving a problem in a finite number of steps",
                        "Search engines use complex algorithms to rank web pages.",
                        "搜索引擎使用复杂的算法对网页进行排名。", 3, "科技"),
                createWord("cybersecurity", "/ˌsaɪ.bər.sɪˈkjʊr.ə.ti/", "/ˌsaɪ.bə.sɪˈkjʊə.rə.ti/", "n.", "网络安全",
                        "The practice of protecting systems and networks from digital attacks",
                        "Cybersecurity threats are increasing rapidly worldwide.",
                        "网络安全威胁在全球范围内迅速增长。", 3, "科技"),
                createWord("artificial intelligence", "/ˌɑːr.tɪˈfɪʃ.əl ɪnˈtel.ɪ.dʒəns/", "/ˌɑː.tɪˈfɪʃ.əl ɪnˈtel.ɪ.dʒəns/", "n.", "人工智能",
                        "The simulation of human intelligence by machines",
                        "Artificial intelligence is transforming every industry.",
                        "人工智能正在改变每一个行业。", 3, "科技"),

                // ===== 原词保留 (Original Words - keep for compatibility) =====
                createWord("ubiquitous", "/juːˈbɪk.wɪ.təs/", "/juːˈbɪk.wɪ.təs/", "adj.", "无处不在的，普遍存在的",
                        "Present, appearing, or found everywhere",
                        "Smartphones have become ubiquitous in modern society.",
                        "智能手机在现代社会已无处不在。", 4, "高级词汇"),
                createWord("eloquent", "/ˈel.ə.kwənt/", "/ˈel.ə.kwənt/", "adj.", "雄辩的，有口才的",
                        "Fluent or persuasive in speaking or writing",
                        "She gave an eloquent speech at the graduation ceremony.",
                        "她在毕业典礼上发表了雄辩的演讲。", 3, "形容词"),
                createWord("meticulous", "/məˈtɪk.jə.ləs/", "/məˈtɪk.jə.ləs/", "adj.", "一丝不苟的，细致的",
                        "Showing great attention to detail",
                        "The meticulous craftsman spent hours perfecting each detail.",
                        "这位细致的工匠花了数小时完善每一个细节。", 3, "形容词"),
                createWord("ephemeral", "/ɪˈfem.ər.əl/", "/ɪˈfem.ər.əl/", "adj.", "短暂的，转瞬即逝的",
                        "Lasting for a very short time",
                        "The beauty of cherry blossoms is ephemeral but breathtaking.",
                        "樱花之美短暂却令人叹为观止。", 4, "高级词汇"),
                createWord("resilient", "/rɪˈzɪl.jənt/", "/rɪˈzɪl.jənt/", "adj.", "有韧性的，能迅速恢复的",
                        "Able to recover quickly from difficult conditions",
                        "Children are often more resilient than adults give them credit for.",
                        "孩子们往往比成年人想象的更坚强。", 3, "形容词"),
                createWord("pragmatic", "/præɡˈmæt.ɪk/", "/præɡˈmæt.ɪk/", "adj.", "务实的，实用的",
                        "Dealing with things sensibly and realistically",
                        "We need a pragmatic approach to solve this problem.",
                        "我们需要务实的方法来解决这个问题。", 3, "形容词"),
                createWord("ambiguous", "/æmˈbɪɡ.ju.əs/", "/æmˈbɪɡ.ju.əs/", "adj.", "模糊的，模棱两可的",
                        "Open to more than one interpretation",
                        "The contract contains several ambiguous clauses.",
                        "该合同包含几个模棱两可的条款。", 2, "形容词"),
                createWord("sustainable", "/səˈsteɪ.nə.bəl/", "/səˈsteɪ.nə.bəl/", "adj.", "可持续的",
                        "Able to be maintained at a certain rate or level",
                        "We should adopt more sustainable living habits.",
                        "我们应该养成更可持续的生活习惯。", 2, "形容词"),
                createWord("profound", "/prəˈfaʊnd/", "/prəˈfaʊnd/", "adj.", "深刻的，深远的",
                        "Very great or intense; having deep insight",
                        "The book had a profound impact on my life.",
                        "这本书对我的生活产生了深远的影响。", 3, "形容词"),
                createWord("diligent", "/ˈdɪl.ɪ.dʒənt/", "/ˈdɪl.ɪ.dʒənt/", "adj.", "勤奋的，勤勉的",
                        "Having or showing care in one's work",
                        "She is a diligent student who always completes homework on time.",
                        "她是一个勤奋的学生，总是按时完成作业。", 2, "形容词"),
                createWord("comprehend", "/ˌkɑːm.prɪˈhend/", "/ˌkɒm.prɪˈhend/", "v.", "理解，领悟",
                        "Grasp mentally; understand",
                        "It takes time to fully comprehend the complexity.",
                        "完全理解情况的复杂性需要时间。", 2, "动词"),
                createWord("negotiate", "/nɪˈɡoʊ.ʃi.eɪt/", "/nɪˈɡəʊ.ʃi.eɪt/", "v.", "谈判，协商",
                        "Try to reach an agreement through discussion",
                        "We need to negotiate better terms for the contract.",
                        "我们需要为合同谈判更好的条款。", 2, "动词"),
                createWord("persevere", "/ˌpɜːr.səˈvɪr/", "/ˌpɜː.sɪˈvɪə/", "v.", "坚持不懈",
                        "Continue in a course of action despite difficulty",
                        "Despite all setbacks, she persevered and achieved her goal.",
                        "尽管遭遇挫折，她坚持不懈，最终实现了目标。", 3, "动词"),

                // ===== 学位英语-校园词汇 (Chengdu College Degree English) =====
                createWord("semester", "/səˈmes.tər/", "/sɪˈmes.tə/", "n.", "学期",
                        "A half-year term in a school or college",
                        "The new semester starts in September.",
                        "新学期九月开始。", 1, "学位英语-校园"),
                createWord("curriculum", "/kəˈrɪk.jə.ləm/", "/kəˈrɪk.jʊ.ləm/", "n.", "课程",
                        "The subjects comprising a course of study",
                        "English is part of the core curriculum.",
                        "英语是核心课程的一部分。", 2, "学位英语-校园"),
                createWord("attendance", "/əˈten.dəns/", "/əˈten.dəns/", "n.", "出勤，出席",
                        "The action of being present at a place or event",
                        "Regular attendance is required for this course.",
                        "这门课程要求定期出勤。", 1, "学位英语-校园"),
                createWord("assignment", "/əˈsaɪn.mənt/", "/əˈsaɪn.mənt/", "n.", "作业，任务",
                        "A task given to students as part of their studies",
                        "Please submit your assignment by Friday.",
                        "请在周五前提交作业。", 1, "学位英语-校园"),
                createWord("scholarship", "/ˈskɑː.lər.ʃɪp/", "/ˈskɒl.ə.ʃɪp/", "n.", "奖学金",
                        "A grant of financial aid for a student",
                        "She won a full scholarship to study abroad.",
                        "她获得了出国留学的全额奖学金。", 2, "学位英语-校园"),
                createWord("dormitory", "/ˈdɔːr.mə.tɔːr.i/", "/ˈdɔː.mɪ.tər.i/", "n.", "宿舍",
                        "A large bedroom for many people in a school",
                        "Most freshmen live in the dormitory on campus.",
                        "大多数新生住在校园宿舍。", 1, "学位英语-校园"),
                createWord("tuition", "/tuˈɪʃ.ən/", "/tjuˈɪʃ.ən/", "n.", "学费",
                        "A sum of money charged for teaching by a college",
                        "Tuition fees have increased this year.",
                        "今年学费上涨了。", 1, "学位英语-校园"),

                // ===== 学位英语-高频动词 =====
                createWord("accomplish", "/əˈkɑːm.plɪʃ/", "/əˈkʌm.plɪʃ/", "v.", "完成，实现",
                        "To achieve or complete successfully",
                        "She accomplished her goal of learning English.",
                        "她实现了学习英语的目标。", 2, "学位英语-动词"),
                createWord("demonstrate", "/ˈdem.ən.streɪt/", "/ˈdem.ən.streɪt/", "v.", "展示，证明",
                        "To show clearly and deliberately",
                        "The experiment demonstrates the theory.",
                        "这个实验证明了该理论。", 2, "学位英语-动词"),
                createWord("evaluate", "/ɪˈvæl.ju.eɪt/", "/ɪˈvæl.ju.eɪt/", "v.", "评估，评价",
                        "To judge or calculate the quality or importance",
                        "Teachers evaluate students based on their performance.",
                        "教师根据学生的表现来评估他们。", 2, "学位英语-动词"),
                createWord("identify", "/aɪˈden.tə.faɪ/", "/aɪˈden.tɪ.faɪ/", "v.", "识别，确认",
                        "To establish or indicate who or what something is",
                        "Can you identify the main idea of the passage?",
                        "你能识别文章的主要观点吗？", 2, "学位英语-动词"),
                createWord("indicate", "/ˈɪn.dɪ.keɪt/", "/ˈɪn.dɪ.keɪt/", "v.", "表明，指示",
                        "To point out or show",
                        "Research indicates that exercise improves memory.",
                        "研究表明运动能改善记忆力。", 2, "学位英语-动词"),
                createWord("involve", "/ɪnˈvɑːlv/", "/ɪnˈvɒlv/", "v.", "包含，涉及",
                        "To include as a necessary part",
                        "The job involves a lot of travel.",
                        "这份工作需要大量出差。", 2, "学位英语-动词"),
                createWord("maintain", "/meɪnˈteɪn/", "/meɪnˈteɪn/", "v.", "维持，保持",
                        "To cause to continue; keep in existence",
                        "It's important to maintain a healthy lifestyle.",
                        "保持健康的生活方式很重要。", 2, "学位英语-动词"),
                createWord("participate", "/pɑːrˈtɪs.ɪ.peɪt/", "/pɑːˈtɪs.ɪ.peɪt/", "v.", "参加，参与",
                        "To take part in an activity or event",
                        "All students are encouraged to participate in class.",
                        "鼓励所有学生参与课堂讨论。", 2, "学位英语-动词"),
                createWord("obtain", "/əbˈteɪn/", "/əbˈteɪn/", "v.", "获得，得到",
                        "To acquire or get possession of",
                        "You need to obtain a certificate before graduation.",
                        "毕业前你需要获得证书。", 2, "学位英语-动词"),
                createWord("appreciate", "/əˈpriː.ʃi.eɪt/", "/əˈpriː.ʃi.eɪt/", "v.", "感激；欣赏",
                        "To recognize the worth of; be grateful for",
                        "I really appreciate your help with my studies.",
                        "我非常感谢你在学习上对我的帮助。", 2, "学位英语-高频"),
                createWord("contribute", "/kənˈtrɪb.juːt/", "/kənˈtrɪb.juːt/", "v.", "贡献，捐献",
                        "To give in order to help achieve something",
                        "Everyone should contribute ideas to the discussion.",
                        "每个人都应该为讨论贡献想法。", 2, "学位英语-高频"),
                createWord("determine", "/dɪˈtɜːr.mɪn/", "/dɪˈtɜː.mɪn/", "v.", "决定，确定",
                        "To cause something to occur in a particular way",
                        "Your attitude will determine your success.",
                        "你的态度将决定你的成功。", 2, "学位英语-高频"),
                createWord("recommend", "/ˌrek.əˈmend/", "/ˌrek.əˈmend/", "v.", "推荐，建议",
                        "To suggest as being particularly suitable",
                        "I recommend reading this book for the exam.",
                        "我推荐你读这本书备考。", 1, "学位英语-高频"),

                // ===== 学位英语-形容词副词 =====
                createWord("significant", "/sɪɡˈnɪf.ə.kənt/", "/sɪɡˈnɪf.ɪ.kənt/", "adj.", "重要的，显著的",
                        "Sufficiently great or important",
                        "There has been a significant improvement in his grades.",
                        "他的成绩有了显著的提高。", 2, "学位英语-形容词"),
                createWord("sufficient", "/səˈfɪʃ.ənt/", "/səˈfɪʃ.ənt/", "adj.", "足够的，充分的",
                        "Enough; adequate",
                        "We don't have sufficient time to finish.",
                        "我们没有足够的时间来完成。", 2, "学位英语-形容词"),
                createWord("appropriate", "/əˈproʊ.pri.ət/", "/əˈprəʊ.pri.ət/", "adj.", "适当的，合适的",
                        "Suitable or proper in the circumstances",
                        "Please dress in appropriate clothing for the interview.",
                        "面试时请穿着得体的服装。", 2, "学位英语-形容词"),
                createWord("effective", "/ɪˈfek.tɪv/", "/ɪˈfek.tɪv/", "adj.", "有效的",
                        "Successful in producing a desired result",
                        "This is an effective method for learning vocabulary.",
                        "这是学习词汇的有效方法。", 2, "学位英语-形容词"),
                createWord("essential", "/ɪˈsen.ʃəl/", "/ɪˈsen.ʃəl/", "adj.", "必要的，本质的",
                        "Absolutely necessary; extremely important",
                        "Water is essential for life.",
                        "水是生命所必需的。", 2, "学位英语-形容词"),
                createWord("potential", "/poʊˈten.ʃəl/", "/pəˈten.ʃəl/", "adj./n.", "潜在的；潜力",
                        "Having capacity to develop in the future",
                        "She has great potential as a writer.",
                        "她作为作家有很大的潜力。", 2, "学位英语-形容词"),
                createWord("convenient", "/kənˈviː.ni.ənt/", "/kənˈviː.ni.ənt/", "adj.", "方便的，便利的",
                        "Fitting in well with a person's needs",
                        "Online learning is very convenient for working students.",
                        "在线学习对在职学生来说非常方便。", 1, "学位英语-高频"),
                createWord("practical", "/ˈpræk.tɪ.kəl/", "/ˈpræk.tɪ.kəl/", "adj.", "实际的，实用的",
                        "Concerned with actual use rather than theory",
                        "We need practical solutions, not just theories.",
                        "我们需要实际的解决方案，而不仅仅是理论。", 2, "学位英语-高频"),
                createWord("professional", "/prəˈfeʃ.ən.əl/", "/prəˈfeʃ.ən.əl/", "adj.", "专业的，职业的",
                        "Relating to a profession",
                        "She gave a very professional presentation.",
                        "她做了一个非常专业的演讲。", 2, "学位英语-高频"),
                createWord("specific", "/spəˈsɪf.ɪk/", "/spəˈsɪf.ɪk/", "adj.", "具体的，特定的",
                        "Clearly defined or identified",
                        "Can you give me a specific example?",
                        "你能给我一个具体的例子吗？", 2, "学位英语-高频"),
                createWord("economic", "/ˌek.əˈnɑː.mɪk/", "/ˌiː.kəˈnɒ.mɪk/", "adj.", "经济的",
                        "Relating to economics or the economy",
                        "The country faces serious economic challenges.",
                        "该国面临严重的经济挑战。", 2, "学位英语-高频"),
                createWord("individual", "/ˌɪn.dɪˈvɪdʒ.u.əl/", "/ˌɪn.dɪˈvɪdʒ.u.əl/", "adj./n.", "个人的；个人",
                        "Single; separate; a single human being",
                        "Each individual has unique learning needs.",
                        "每个人都有独特的学习需求。", 2, "学位英语-高频"),
                createWord("frequently", "/ˈfriː.kwənt.li/", "/ˈfriː.kwənt.li/", "adv.", "频繁地，经常",
                        "Regularly or habitually; often",
                        "She frequently visits the library after class.",
                        "她放学后经常去图书馆。", 1, "学位英语-副词"),
                createWord("gradually", "/ˈɡrædʒ.u.ə.li/", "/ˈɡrædʒ.u.ə.li/", "adv.", "逐渐地",
                        "In a gradual way; slowly",
                        "Her English improved gradually over the semester.",
                        "她的英语在整个学期中逐渐提高。", 1, "学位英语-副词"),
                createWord("relatively", "/ˈrel.ə.tɪv.li/", "/ˈrel.ə.tɪv.li/", "adv.", "相对地，比较地",
                        "In relation or proportion to something else",
                        "This task is relatively easy compared to the others.",
                        "与其他任务相比，这个任务相对容易。", 2, "学位英语-高频"),

                // ===== 学位英语-名词 =====
                createWord("circumstance", "/ˈsɜːr.kəm.stæns/", "/ˈsɜː.kəm.stɑːns/", "n.", "环境，情况",
                        "A fact or condition connected with an event",
                        "Under no circumstances should you open this door.",
                        "在任何情况下你都不应该打开这扇门。", 2, "学位英语-名词"),
                createWord("consequence", "/ˈkɑːn.sə.kwens/", "/ˈkɒn.sɪ.kwəns/", "n.", "结果，后果",
                        "A result or effect of an action",
                        "His actions had serious consequences.",
                        "他的行为导致了严重的后果。", 2, "学位英语-名词"),
                createWord("environment", "/ɪnˈvaɪ.rən.mənt/", "/ɪnˈvaɪ.rən.mənt/", "n.", "环境",
                        "The surroundings or conditions in which one lives",
                        "We must protect the environment.",
                        "我们必须保护环境。", 1, "学位英语-名词"),
                createWord("opportunity", "/ˌɑː.pərˈtuː.nə.ti/", "/ˌɒp.əˈtjuː.nə.ti/", "n.", "机会",
                        "A set of circumstances making something possible",
                        "College provides many opportunities for personal growth.",
                        "大学为个人成长提供了许多机会。", 1, "学位英语-名词"),
                createWord("responsibility", "/rɪˌspɑːn.səˈbɪl.ə.ti/", "/rɪˌspɒn.sɪˈbɪl.ə.ti/", "n.", "责任",
                        "The state of being accountable for something",
                        "It's your responsibility to complete the project.",
                        "按时完成项目是你的责任。", 1, "学位英语-名词"),
                createWord("achievement", "/əˈtʃiːv.mənt/", "/əˈtʃiːv.mənt/", "n.", "成就，成绩",
                        "A thing done successfully with effort",
                        "Getting into university was a great achievement.",
                        "考上大学是一个巨大的成就。", 1, "学位英语-名词"),
                createWord("communication", "/kəˌmjuː.nɪˈkeɪ.ʃən/", "/kəˌmjuː.nɪˈkeɪ.ʃən/", "n.", "沟通，交流",
                        "The exchanging of information",
                        "Good communication is key to teamwork.",
                        "良好的沟通是团队合作的关键。", 1, "学位英语-高频"),
                createWord("evidence", "/ˈev.ɪ.dəns/", "/ˈev.ɪ.dəns/", "n.", "证据",
                        "The available facts indicating whether a belief is true",
                        "There is no evidence to support this claim.",
                        "没有证据支持这个说法。", 2, "学位英语-高频"),
                createWord("knowledge", "/ˈnɑː.lɪdʒ/", "/ˈnɒl.ɪdʒ/", "n.", "知识",
                        "Facts, information, and skills acquired through experience",
                        "Knowledge is power in today's world.",
                        "知识就是力量。", 1, "学位英语-高频"),
                createWord("influence", "/ˈɪn.flu.əns/", "/ˈɪn.flu.əns/", "n./v.", "影响",
                        "The capacity to have an effect on someone",
                        "Parents have a strong influence on their children.",
                        "父母对孩子有很大的影响。", 2, "学位英语-高频"),
                createWord("tradition", "/trəˈdɪʃ.ən/", "/trəˈdɪʃ.ən/", "n.", "传统",
                        "Customs passed down through generations",
                        "It is a tradition to have a graduation ceremony.",
                        "举行毕业典礼是一种传统。", 2, "学位英语-高频"),
                createWord("variety", "/vəˈraɪ.ə.ti/", "/vəˈraɪ.ə.ti/", "n.", "多样性，种类",
                        "The quality of being different or diverse",
                        "The library offers a wide variety of books.",
                        "图书馆提供各种各样的书籍。", 2, "学位英语-高频"),

                // ===== 学位英语-阅读连接词 =====
                createWord("furthermore", "/ˌfɜːr.ðərˈmɔːr/", "/ˌfɜː.ðəˈmɔː/", "adv.", "此外，而且",
                        "In addition; besides",
                        "The plan is risky. Furthermore, it's expensive.",
                        "这个计划有风险，而且很昂贵。", 2, "学位英语-阅读"),
                createWord("nevertheless", "/ˌnev.ər.ðəˈles/", "/ˌnev.ə.ðəˈles/", "adv.", "然而，不过",
                        "In spite of that; notwithstanding",
                        "It was difficult. Nevertheless, she succeeded.",
                        "这很困难，然而她成功了。", 2, "学位英语-阅读"),
                createWord("therefore", "/ˈðer.fɔːr/", "/ˈðeə.fɔː/", "adv.", "因此，所以",
                        "For that reason; consequently",
                        "He didn't prepare, therefore he failed.",
                        "他没有准备，因此他失败了。", 1, "学位英语-阅读"),
                createWord("moreover", "/mɔːrˈoʊ.vər/", "/mɔːˈrəʊ.vər/", "adv.", "而且，此外",
                        "As a further matter; besides",
                        "The course is free. Moreover, it offers a certificate.",
                        "这门课程免费，而且提供证书。", 2, "学位英语-阅读"),

                // ===== 学位英语-听力场景 =====
                createWord("appointment", "/əˈpɔɪnt.mənt/", "/əˈpɔɪnt.mənt/", "n.", "约会，预约",
                        "An arrangement to meet at a particular time",
                        "I have a doctor's appointment at 3 PM.",
                        "我下午三点有个医生预约。", 1, "学位英语-听力"),
                createWord("reservation", "/ˌrez.ərˈveɪ.ʃən/", "/ˌrez.əˈveɪ.ʃən/", "n.", "预订，预约",
                        "An arrangement to have something held for you",
                        "I'd like to make a reservation for dinner.",
                        "我想预订晚餐。", 1, "学位英语-听力"),
                createWord("direction", "/dɪˈrek.ʃən/", "/dɪˈrek.ʃən/", "n.", "方向，指示",
                        "The course along which someone moves",
                        "Could you give me directions to the library?",
                        "你能告诉我去图书馆的路线吗？", 1, "学位英语-听力"),
                createWord("announcement", "/əˈnaʊns.mənt/", "/əˈnaʊns.mənt/", "n.", "公告，通知",
                        "A public and typically formal statement",
                        "The announcement was made over the school speaker.",
                        "公告是通过学校广播发布的。", 1, "学位英语-听力"),
                createWord("schedule", "/ˈskedʒ.uːl/", "/ˈʃed.juːl/", "n./v.", "日程，安排",
                        "A plan of things to be done",
                        "The exam is scheduled for next Monday.",
                        "考试安排在下周一。", 1, "学位英语-听力"),

                // ===== 学位英语-艺术体育（成都文理学院特色）=====
                createWord("creative", "/kriˈeɪ.tɪv/", "/kriˈeɪ.tɪv/", "adj.", "创造性的，有创意的",
                        "Relating to the use of imagination",
                        "The art students showed very creative work.",
                        "艺术系学生展示了非常有创意的作品。", 2, "学位英语-艺术"),
                createWord("performance", "/pərˈfɔːr.məns/", "/pəˈfɔː.məns/", "n.", "表演，表现",
                        "An act of presenting entertainment",
                        "The dance performance was amazing.",
                        "舞蹈表演非常精彩。", 1, "学位英语-艺术"),
                createWord("exhibition", "/ˌek.sɪˈbɪʃ.ən/", "/ˌek.sɪˈbɪʃ.ən/", "n.", "展览",
                        "A public display of works of art",
                        "The art exhibition opens this Saturday.",
                        "艺术展览本周六开幕。", 2, "学位英语-艺术"),
                createWord("inspiration", "/ˌɪn.spəˈreɪ.ʃən/", "/ˌɪn.spɪˈreɪ.ʃən/", "n.", "灵感",
                        "The process of being mentally stimulated",
                        "Nature is a great source of inspiration.",
                        "大自然是灵感的重要来源。", 2, "学位英语-艺术"),
                createWord("audience", "/ˈɑː.di.əns/", "/ˈɔː.di.əns/", "n.", "观众，听众",
                        "The assembled spectators at an event",
                        "The audience applauded at the end.",
                        "演出结束时观众鼓掌。", 1, "学位英语-艺术"),
                createWord("athlete", "/ˈæθ.liːt/", "/ˈæθ.liːt/", "n.", "运动员",
                        "A person proficient in sports",
                        "She trained hard to become a professional athlete.",
                        "她刻苦训练成为一名职业运动员。", 2, "学位英语-体育"),
                createWord("competition", "/ˌkɑːm.pəˈtɪʃ.ən/", "/ˌkɒm.pəˈtɪʃ.ən/", "n.", "竞赛，比赛",
                        "An event in which people compete",
                        "The competition attracted nationwide participants.",
                        "比赛吸引了全国各地的参与者。", 1, "学位英语-体育"),

                // ===== 学位英语-翻译词汇 =====
                createWord("culture", "/ˈkʌl.tʃər/", "/ˈkʌl.tʃə/", "n.", "文化",
                        "The ideas and customs of a people",
                        "Learning a language is also learning its culture.",
                        "学习语言也是学习其文化。", 1, "学位英语-翻译"),
                createWord("economy", "/ɪˈkɑː.nə.mi/", "/ɪˈkɒn.ə.mi/", "n.", "经济",
                        "The system of trade and industry",
                        "The global economy is recovering slowly.",
                        "全球经济正在缓慢复苏。", 2, "学位英语-翻译"),
                createWord("government", "/ˈɡʌv.ərn.mənt/", "/ˈɡʌv.ən.mənt/", "n.", "政府",
                        "The governing body of a nation",
                        "The government announced new policies.",
                        "政府宣布了新的政策。", 1, "学位英语-翻译"),
                createWord("technology", "/tekˈnɑː.lə.dʒi/", "/tekˈnɒl.ə.dʒi/", "n.", "技术，科技",
                        "The application of scientific knowledge",
                        "Technology is changing every aspect of our lives.",
                        "技术正在改变我们生活的方方面面。", 1, "学位英语-翻译"),
                createWord("development", "/dɪˈvel.əp.mənt/", "/dɪˈvel.əp.mənt/", "n.", "发展",
                        "The process of growing or improving",
                        "Education is key to personal development.",
                        "教育是个人发展的关键。", 1, "学位英语-翻译"),
                createWord("international", "/ˌɪn.tərˈnæʃ.ən.əl/", "/ˌɪn.təˈnæʃ.ən.əl/", "adj.", "国际的",
                        "Existing between nations",
                        "She wants to work for an international company.",
                        "她想在国际公司工作。", 1, "学位英语-翻译"),
                createWord("society", "/səˈsaɪ.ə.ti/", "/səˈsaɪ.ə.ti/", "n.", "社会",
                        "The community of people",
                        "We all have a role to play in society.",
                        "我们在社会中都有一份责任。", 1, "学位英语-翻译"),
                createWord("experience", "/ɪkˈspɪr.i.əns/", "/ɪkˈspɪə.ri.əns/", "n./v.", "经验，经历",
                        "Practical contact with facts or events",
                        "Studying abroad was a life-changing experience.",
                        "出国留学是一次改变人生的经历。", 1, "学位英语-翻译"),
                createWord("education", "/ˌedʒ.əˈkeɪ.ʃən/", "/ˌedʒ.uˈkeɪ.ʃən/", "n.", "教育",
                        "The process of systematic instruction",
                        "Education is the foundation of success.",
                        "教育是成功的基础。", 1, "学位英语-翻译"),
                createWord("population", "/ˌpɑː.pjəˈleɪ.ʃən/", "/ˌpɒp.jʊˈleɪ.ʃən/", "n.", "人口",
                        "All the inhabitants of a place",
                        "China has the largest population in the world.",
                        "中国拥有世界上最多的人口。", 1, "学位英语-翻译"),

                // ===== 学位英语-语法 =====
                createWord("despite", "/dɪˈspaɪt/", "/dɪˈspaɪt/", "prep.", "尽管，不管",
                        "Without being affected by",
                        "Despite the difficulty, she passed the exam.",
                        "尽管困难重重，她还是通过了考试。", 2, "学位英语-语法"),
                createWord("although", "/ɑːlˈðoʊ/", "/ɔːlˈðəʊ/", "conj.", "虽然，尽管",
                        "In spite of the fact that",
                        "Although he is young, he is very responsible.",
                        "虽然他很年轻，但他非常负责任。", 1, "学位英语-语法"),
                createWord("however", "/haʊˈev.ər/", "/haʊˈev.ə/", "adv.", "然而，但是",
                        "Used to introduce a contrast",
                        "The test was difficult. However, most passed.",
                        "考试很难，然而大多数学生都通过了。", 1, "学位英语-语法"),
                createWord("unless", "/ʌnˈles/", "/ʌnˈles/", "conj.", "除非，如果不",
                        "Except if",
                        "You won't pass unless you study harder.",
                        "除非你更努力学习，否则你无法通过。", 1, "学位英语-语法"),
                createWord("whether", "/ˈweð.ər/", "/ˈweð.ə/", "conj.", "是否",
                        "Expressing a doubt between alternatives",
                        "I'm not sure whether he will come.",
                        "我不确定他是否会来。", 1, "学位英语-语法")
        );
        wordRepo.saveAll(words);
        log.info("Inserted {} built-in words", words.size());

        // Load crawled degree English words from JSON
        List<Word> crawledWords = loadDegreeEnglishWords();
        if (!crawledWords.isEmpty()) {
            wordRepo.saveAll(crawledWords);
            log.info("Inserted {} degree English words from crawl", crawledWords.size());
        }

        // Grammar Lessons
        GrammarLesson g1 = GrammarLesson.builder()
                .title("一般现在时 vs 现在进行时")
                .category("时态")
                .description("掌握一般现在时和现在进行时的区别与用法")
                .contentHtml("<h2>一般现在时</h2><p><strong>结构：</strong>主语 + 动词原形（三单 + s/es）</p><p><strong>用法：</strong></p><ul><li>客观事实：<em>The sun rises in the east.</em></li><li>习惯动作：<em>I go to work by subway.</em></li></ul><h2>现在进行时</h2><p><strong>结构：</strong>am/is/are + 动词-ing</p><ul><li>此刻进行：<em>I am reading a book.</em></li><li>将来安排：<em>We are leaving tomorrow.</em></li></ul>")
                .difficultyLevel(1).sortOrder(1).build();
        GrammarLesson g2 = GrammarLesson.builder()
                .title("定语从句详解")
                .category("从句")
                .description("系统学习关系代词和关系副词的用法")
                .contentHtml("<h2>定语从句</h2><h3>关系代词</h3><ul><li><strong>who</strong> — 指人</li><li><strong>which</strong> — 指物</li><li><strong>that</strong> — 通用</li><li><strong>whose</strong> — 表所属</li></ul><h3>关系副词</h3><ul><li><strong>when</strong> — 时间</li><li><strong>where</strong> — 地点</li><li><strong>why</strong> — 原因</li></ul>")
                .difficultyLevel(2).sortOrder(2).build();
        GrammarLesson g3 = GrammarLesson.builder()
                .title("虚拟语气完全指南")
                .category("语气")
                .description("深入理解虚拟条件句及其各种形式")
                .contentHtml("<h2>虚拟语气</h2><h3>1. 与现在相反</h3><p>If + 过去式, would + 动词原形</p><p><em>If I were you, I would go.</em></p><h3>2. 与过去相反</h3><p>If + had + 过去分词, would have + 过去分词</p><p><em>If I had known, I would have come.</em></p>")
                .difficultyLevel(3).sortOrder(3).build();
        grammarRepo.saveAll(List.of(g1, g2, g3));
        log.info("Inserted 3 grammar lessons");

        // Articles
        Article a1 = Article.builder()
                .title("The Rise of AI in Healthcare")
                .author("Dr. Sarah Chen")
                .source("TechReview")
                .contentHtml("<p>AI is revolutionizing healthcare. From diagnostic algorithms detecting cancer with accuracy surpassing human radiologists, to personalized treatment plans based on genetic profiles.</p><p>Machine learning models now predict patient outcomes, optimize hospital workflows, and assist in surgeries. The future of medicine lies at the intersection of human expertise and AI.</p>")
                .summary("How AI is transforming healthcare from diagnostics to personalized medicine.")
                .difficultyLevel(4).wordCount(80).category("科技")
                .publishDate(LocalDate.of(2026, 1, 15)).build();
        Article a2 = Article.builder()
                .title("The Art of Effective Communication")
                .author("Michael Brown")
                .source("BusinessInsight")
                .contentHtml("<p>Effective communication is the cornerstone of professional success. Your ability to convey ideas clearly can make or break your career.</p><p>Top communicators listen actively, adapt to their audience, use storytelling, and read non-verbal cues. Developing these skills requires practice and self-awareness.</p>")
                .summary("Key principles for becoming a more effective communicator in professional settings.")
                .difficultyLevel(3).wordCount(70).category("商业")
                .publishDate(LocalDate.of(2026, 3, 10)).build();
        Article a3 = Article.builder()
                .title("Climate Change: What Individuals Can Do")
                .author("Emily Watson")
                .source("GreenEarth")
                .contentHtml("<p>While climate change requires systemic solutions, individual actions matter. From what we eat to how we travel—each choice contributes to our carbon footprint.</p><p>Reducing meat, using public transport, minimizing energy use, and supporting sustainable businesses are practical steps. Small actions, multiplied across millions, drive change.</p>")
                .summary("Practical steps individuals can take to combat climate change daily.")
                .difficultyLevel(2).wordCount(65).category("环境")
                .publishDate(LocalDate.of(2026, 4, 22)).build();
        articleRepo.saveAll(List.of(a1, a2, a3));
        log.info("Inserted 3 articles");

        // Listening
        ListeningMaterial lm1 = ListeningMaterial.builder()
                .title("Daily Conversation: Ordering at a Restaurant")
                .description("练习日常餐厅点餐对话，学习如何用英语点餐。")
                .audioUrl("/audio/restaurant.mp3")
                .transcript("Waiter: Good evening! Welcome to The Garden Bistro. Do you have a reservation?\nCustomer: Yes, under the name Johnson, for 7:30.\nWaiter: Right this way, please. Can I get you started with something to drink?\nCustomer: I'll have a glass of sparkling water, please.\nWaiter: Are you ready to order?\nCustomer: What do you recommend today?\nWaiter: Our grilled salmon is excellent.")
                .durationSeconds(180).difficultyLevel(1).category("日常生活").build();
        ListeningMaterial lm2 = ListeningMaterial.builder()
                .title("Business Meeting: Project Update")
                .description("练习商务会议场景听力，学习职场英语表达。")
                .audioUrl("/audio/business_meeting.mp3")
                .transcript("Manager: Let's review the quarterly results. The marketing campaign exceeded expectations with a 25% increase in engagement.\nTeam Lead: That's great news! The social media strategy really paid off.\nManager: However, we need to focus on customer retention for Q3. Any suggestions?\nAnalyst: I recommend we implement a loyalty program.")
                .durationSeconds(240).difficultyLevel(3).category("商务").build();
        listeningRepo.saveAll(List.of(lm1, lm2));
        log.info("Inserted 2 listening materials");

        // Quizzes - 成都文理学院 学位英语 模拟试卷
        createDegreeQuiz1();
        createDegreeQuiz2();
        createDegreeQuiz3();
        createDegreeQuiz4();
        createDegreeQuiz5();
        log.info("Created 5 degree English quiz papers");
    }

    private void createDegreeQuiz1() {
        Quiz quiz = Quiz.builder()
                .title("成都文理学院 学位英语 模拟试卷（一）")
                .description("基于四川省大学英语新三级考试大纲，涵盖词汇选择、语法判断和填空。适用于艺体类本专科学生。")
                .quizType(Quiz.QuizType.VOCABULARY)
                .difficultyLevel(2).totalQuestions(12).passScore(60).build();
        List<QuizQuestion> qs = List.of(
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("The company needs to find a ______ solution to this problem.")
                        .optionsJson("[\"practical\", \"practically\", \"practice\", \"practiced\"]")
                        .correctAnswer("practical").explanation("形容词 'practical' 修饰名词 'solution'。").score(10).sortOrder(1).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("She has made ______ progress in her English studies.")
                        .optionsJson("[\"significant\", \"significantly\", \"significance\", \"signify\"]")
                        .correctAnswer("significant").explanation("形容词 'significant' 修饰名词 'progress'。").score(10).sortOrder(2).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("The students are required to ______ the assignment by Friday.")
                        .optionsJson("[\"submit\", \"permit\", \"admit\", \"commit\"]")
                        .correctAnswer("submit").explanation("'submit the assignment' 意为'提交作业'。").score(10).sortOrder(3).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("______ the heavy rain, the football match continued.")
                        .optionsJson("[\"Despite\", \"Although\", \"Because\", \"Since\"]")
                        .correctAnswer("Despite").explanation("'Despite' 后接名词短语，'Although' 后接从句。").score(10).sortOrder(4).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("It is important to maintain a good ______ with your colleagues.")
                        .optionsJson("[\"relationship\", \"friendship\", \"membership\", \"leadership\"]")
                        .correctAnswer("relationship").explanation("'maintain a good relationship' 意为'保持良好的关系'。").score(10).sortOrder(5).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("The teacher asked us to ______ attention to the grammar rules.")
                        .optionsJson("[\"pay\", \"give\", \"make\", \"take\"]")
                        .correctAnswer("pay").explanation("固定搭配 'pay attention to' 意为'注意'。").score(10).sortOrder(6).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("More and more people are becoming aware ______ the importance of environmental protection.")
                        .optionsJson("[\"of\", \"to\", \"for\", \"with\"]")
                        .correctAnswer("of").explanation("固定搭配 'be aware of' 意为'意识到'。").score(10).sortOrder(7).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("I'm looking forward ______ hearing from you soon.")
                        .optionsJson("[\"to\", \"for\", \"at\", \"in\"]")
                        .correctAnswer("to").explanation("固定搭配 'look forward to + doing'。").score(10).sortOrder(8).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.TRUE_FALSE)
                        .questionText("The sentence 'She don't like coffee' is grammatically correct.")
                        .optionsJson("[\"True\", \"False\"]")
                        .correctAnswer("False").explanation("第三人称单数应用 'doesn't'，正确为 'She doesn't like coffee'。").score(10).sortOrder(9).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.TRUE_FALSE)
                        .questionText("'There are many informations in this book.' This sentence is correct.")
                        .optionsJson("[\"True\", \"False\"]")
                        .correctAnswer("False").explanation("'information' 是不可数名词。").score(10).sortOrder(10).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.FILL_BLANK)
                        .questionText("The past tense of 'begin' is ______.")
                        .optionsJson("[]").correctAnswer("began")
                        .explanation("'begin'(开始) 的过去式是 'began'。").score(10).sortOrder(11).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.FILL_BLANK)
                        .questionText("Complete the phrase: 'as ______ as possible' (表示'尽快')")
                        .optionsJson("[]").correctAnswer("soon")
                        .explanation("'as soon as possible' (ASAP) 意为'尽快'。").score(10).sortOrder(12).build()
        );
        quiz.setQuestions(qs);
        quizRepo.save(quiz);
    }

    private void createDegreeQuiz2() {
        Quiz quiz = Quiz.builder()
                .title("成都文理学院 学位英语 模拟试卷（二）")
                .description("重点测试学位英语核心词汇、语法结构和阅读理解能力。")
                .quizType(Quiz.QuizType.VOCABULARY)
                .difficultyLevel(2).totalQuestions(12).passScore(60).build();
        List<QuizQuestion> qs = List.of(
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("We should take full ______ of this opportunity to improve ourselves.")
                        .optionsJson("[\"advantage\", \"disadvantage\", \"advance\", \"adventure\"]")
                        .correctAnswer("advantage").explanation("'take advantage of' 意为'利用'。").score(10).sortOrder(1).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("The Great Wall is one of the most famous tourist ______ in China.")
                        .optionsJson("[\"attractions\", \"attentions\", \"attempts\", \"attitudes\"]")
                        .correctAnswer("attractions").explanation("'tourist attractions' 意为'旅游景点'。").score(10).sortOrder(2).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("He couldn't ______ the fact that he had failed the exam.")
                        .optionsJson("[\"accept\", \"except\", \"expect\", \"respect\"]")
                        .correctAnswer("accept").explanation("'accept the fact' 意为'接受事实'。").score(10).sortOrder(3).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("The development of technology has a great ______ on our daily lives.")
                        .optionsJson("[\"influence\", \"difference\", \"importance\", \"interest\"]")
                        .correctAnswer("influence").explanation("'have a great influence on' 意为'对...有很大影响'。").score(10).sortOrder(4).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("You'd better ______ your notes before the exam.")
                        .optionsJson("[\"review\", \"reviewing\", \"to review\", \"reviewed\"]")
                        .correctAnswer("review").explanation("'had better + 动词原形'。").score(10).sortOrder(5).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("She is ______ kind that everyone likes her.")
                        .optionsJson("[\"so\", \"such\", \"too\", \"very\"]")
                        .correctAnswer("so").explanation("'so + 形容词 + that' 句型。").score(10).sortOrder(6).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("The book ______ I borrowed from the library is very interesting.")
                        .optionsJson("[\"which\", \"who\", \"whom\", \"whose\"]")
                        .correctAnswer("which").explanation("定语从句，先行词是物用 'which'。").score(10).sortOrder(7).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("If I ______ you, I would study harder for the degree exam.")
                        .optionsJson("[\"were\", \"was\", \"am\", \"be\"]")
                        .correctAnswer("were").explanation("虚拟语气，be动词用 'were'。").score(10).sortOrder(8).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.TRUE_FALSE)
                        .questionText("The word 'information' can be used with 'many'.")
                        .optionsJson("[\"True\", \"False\"]")
                        .correctAnswer("False").explanation("'information' 是不可数名词，应用 'much'。").score(10).sortOrder(9).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.TRUE_FALSE)
                        .questionText("'Neither of the answers are correct.' This sentence is grammatically correct.")
                        .optionsJson("[\"True\", \"False\"]")
                        .correctAnswer("False").explanation("'Neither of...' 作主语谓语用单数，正确为 'is correct'。").score(10).sortOrder(10).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.FILL_BLANK)
                        .questionText("The comparative form of 'good' is ______.")
                        .optionsJson("[]").correctAnswer("better")
                        .explanation("'good' 的比较级是不规则变化 'better'。").score(10).sortOrder(11).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.FILL_BLANK)
                        .questionText("'Would you mind ______ the window?' (open)")
                        .optionsJson("[]").correctAnswer("opening")
                        .explanation("'Would you mind + doing' 是固定句型。").score(10).sortOrder(12).build()
        );
        quiz.setQuestions(qs);
        quizRepo.save(quiz);
    }

    private void createDegreeQuiz3() {
        Quiz quiz = Quiz.builder()
                .title("成都文理学院 学位英语 模拟试卷（三）")
                .description("学位英语考前冲刺模拟卷，重点考查词汇辨析、语法结构、固定搭配。")
                .quizType(Quiz.QuizType.VOCABULARY)
                .difficultyLevel(3).totalQuestions(10).passScore(60).build();
        List<QuizQuestion> qs = List.of(
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("The two countries have ______ an agreement on trade.")
                        .optionsJson("[\"reached\", \"arrived\", \"got\", \"made\"]")
                        .correctAnswer("reached").explanation("'reach an agreement' 意为'达成协议'。").score(10).sortOrder(1).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("It never ______ to me that he might be lying.")
                        .optionsJson("[\"occurred\", \"happened\", \"appeared\", \"showed\"]")
                        .correctAnswer("occurred").explanation("'It occurs to sb. that...' 意为'某人突然想到...'。").score(10).sortOrder(2).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("The professor suggested that every student ______ a research paper.")
                        .optionsJson("[\"write\", \"writes\", \"wrote\", \"written\"]")
                        .correctAnswer("write").explanation("'suggest' 引导的宾语从句用虚拟语气 '(should) + 动词原形'。").score(10).sortOrder(3).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("Not only the students but also the teacher ______ excited about the trip.")
                        .optionsJson("[\"is\", \"are\", \"was being\", \"were\"]")
                        .correctAnswer("is").explanation("就近原则，'the teacher' 是单数。" ).score(10).sortOrder(4).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("Hardly ______ the airport when the plane took off.")
                        .optionsJson("[\"had I reached\", \"I had reached\", \"did I reach\", \"I reached\"]")
                        .correctAnswer("had I reached").explanation("'Hardly...when...' 句型，主句用过去完成时且需倒装。").score(10).sortOrder(5).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("The number of students in this school ______ increased significantly.")
                        .optionsJson("[\"has\", \"have\", \"are\", \"were\"]")
                        .correctAnswer("has").explanation("'The number of...' 作主语，谓语用单数。").score(10).sortOrder(6).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.TRUE_FALSE)
                        .questionText("'Each of the students have a dictionary.' This sentence is correct.")
                        .optionsJson("[\"True\", \"False\"]")
                        .correctAnswer("False").explanation("'Each of...' 作主语谓语用单数，正确为 'has'。" ).score(10).sortOrder(7).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.FILL_BLANK)
                        .questionText("The past participle of 'choose' is ______.")
                        .optionsJson("[]").correctAnswer("chosen")
                        .explanation("'choose' 的过去分词是 'chosen'。").score(10).sortOrder(8).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.FILL_BLANK)
                        .questionText("He insisted that she ______ (go) with him.")
                        .optionsJson("[]").correctAnswer("go")
                        .explanation("'insist' 从句用虚拟语气 '(should) + 动词原形'。").score(10).sortOrder(9).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("______ his age, he did a very good job.")
                        .optionsJson("[\"Considering\", \"Considered\", \"To consider\", \"Being considered\"]")
                        .correctAnswer("Considering").explanation("'Considering' 作介词，意为'考虑到'。").score(10).sortOrder(10).build()
        );
        quiz.setQuestions(qs);
        quizRepo.save(quiz);
    }

    private void createDegreeQuiz4() {
        Quiz quiz = Quiz.builder()
                .title("成都文理学院 学位英语 高频词汇测试")
                .description("专门针对学位英语高频词汇的强化训练，涵盖校园生活、学术写作等场景。")
                .quizType(Quiz.QuizType.VOCABULARY)
                .difficultyLevel(1).totalQuestions(10).passScore(70).build();
        List<QuizQuestion> qs = List.of(
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("\"Semester\" 的意思是什么？")
                        .optionsJson("[\"学期\", \"考试\", \"课程\", \"毕业\"]")
                        .correctAnswer("学期").explanation("'Semester' 意为'学期'。").score(10).sortOrder(1).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("\"Appreciate\" 的意思是？")
                        .optionsJson("[\"感激；欣赏\", \"申请\", \"出现\", \"接近\"]")
                        .correctAnswer("感激；欣赏").explanation("'Appreciate' 意为'感激、欣赏'。").score(10).sortOrder(2).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("The opposite of \"sufficient\" is?")
                        .optionsJson("[\"insufficient\", \"efficient\", \"proficient\", \"deficient\"]")
                        .correctAnswer("insufficient").explanation("'sufficient'(足够的) 的反义词是 'insufficient'(不足的)。").score(10).sortOrder(3).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("\"Despite\" 和 \"Although\" 的区别是什么？")
                        .optionsJson("[\"Despite 后接名词，Although 后接从句\", \"两者完全相同\", \"Despite 后接从句\", \"Although 后接名词\"]")
                        .correctAnswer("Despite 后接名词，Although 后接从句").explanation("'Despite' 是介词后接名词，'Although' 是连词后接从句。").score(10).sortOrder(4).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("\"Environment\" 的正确中文翻译是？")
                        .optionsJson("[\"环境\", \"发展\", \"政府\", \"经历\"]")
                        .correctAnswer("环境").explanation("'Environment' 意为'环境'。").score(10).sortOrder(5).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("Which word means \"必要的\"?")
                        .optionsJson("[\"necessary\", \"possible\", \"terrible\", \"comfortable\"]")
                        .correctAnswer("necessary").explanation("'necessary' 意为'必要的'。").score(10).sortOrder(6).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("\"Therefore\" 的同义词是？")
                        .optionsJson("[\"consequently\", \"however\", \"moreover\", \"meanwhile\"]")
                        .correctAnswer("consequently").explanation("'Therefore' 和 'consequently' 都表示'因此'。").score(10).sortOrder(7).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.FILL_BLANK)
                        .questionText("\"Education is the key to success.\" 翻译：教育是成功的______。")
                        .optionsJson("[]").correctAnswer("关键")
                        .explanation("'the key to success' 意为'成功的关键'。").score(10).sortOrder(8).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.FILL_BLANK)
                        .questionText("\"In my opinion\" 的中文意思是：______。")
                        .optionsJson("[]").correctAnswer("在我看来")
                        .explanation("'In my opinion' 是表达个人观点的常用短语。").score(10).sortOrder(9).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("\"Knowledge is power.\" 翻译为中文是？")
                        .optionsJson("[\"知识就是力量\", \"力量就是知识\", \"知识改变命运\", \"知识创造财富\"]")
                        .correctAnswer("知识就是力量").explanation("'Knowledge is power.' 是培根的名言。").score(10).sortOrder(10).build()
        );
        quiz.setQuestions(qs);
        quizRepo.save(quiz);
    }

    private void createDegreeQuiz5() {
        Quiz quiz = Quiz.builder()
                .title("成都文理学院 学位英语 语法专项训练")
                .description("针对学位英语考试中常见的语法考点进行专项训练：时态、语态、从句、虚拟语气。")
                .quizType(Quiz.QuizType.GRAMMAR)
                .difficultyLevel(2).totalQuestions(10).passScore(60).build();
        List<QuizQuestion> qs = List.of(
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("By the time he arrives, we ______ for two hours.")
                        .optionsJson("[\"will have been waiting\", \"will wait\", \"are waiting\", \"have waited\"]")
                        .correctAnswer("will have been waiting").explanation("'By the time + 现在时'，主句用将来完成时。").score(10).sortOrder(1).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("The room needs ______ before the guests arrive.")
                        .optionsJson("[\"cleaning\", \"to clean\", \"cleaned\", \"being cleaned\"]")
                        .correctAnswer("cleaning").explanation("'need doing' 表示被动含义 (= need to be done)。").score(10).sortOrder(2).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("It is high time that we ______ action to protect the environment.")
                        .optionsJson("[\"took\", \"take\", \"taken\", \"taking\"]")
                        .correctAnswer("took").explanation("'It is (high) time that...' 从句用过去式（虚拟语气）。").score(10).sortOrder(3).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("Only when he returned home ______ that he had left his keys.")
                        .optionsJson("[\"did he realize\", \"he realized\", \"had he realized\", \"he had realized\"]")
                        .correctAnswer("did he realize").explanation("'Only + 状语' 位于句首时，主句部分倒装。").score(10).sortOrder(4).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("The reason ______ he was late was that he missed the bus.")
                        .optionsJson("[\"why\", \"which\", \"that\", \"for\"]")
                        .correctAnswer("why").explanation("'The reason why...' 是定语从句固定搭配。").score(10).sortOrder(5).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.TRUE_FALSE)
                        .questionText("'The book written by Mark Twain is worth reading.' uses passive voice correctly.")
                        .optionsJson("[\"True\", \"False\"]")
                        .correctAnswer("True").explanation("'written by...' 过去分词作后置定语，'be worth doing' 正确。").score(10).sortOrder(6).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.FILL_BLANK)
                        .questionText("I wish I ______ (know) the answer yesterday.")
                        .optionsJson("[]").correctAnswer("had known")
                        .explanation("'wish' 与过去事实相反，从句用过去完成时。").score(10).sortOrder(7).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.FILL_BLANK)
                        .questionText("If you had come earlier, you ______ (meet) him.")
                        .optionsJson("[]").correctAnswer("would have met")
                        .explanation("与过去事实相反的虚拟语气：If + had done, would have done。").score(10).sortOrder(8).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("He is one of the students who ______ always on time.")
                        .optionsJson("[\"are\", \"is\", \"was\", \"has been\"]")
                        .correctAnswer("are").explanation("'one of + 复数名词 + who' 定语从句谓语与复数名词一致。").score(10).sortOrder(9).build(),
                QuizQuestion.builder().quiz(quiz).questionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("So fast ______ that I couldn't catch up with him.")
                        .optionsJson("[\"did he run\", \"he ran\", \"he did run\", \"runs he\"]")
                        .correctAnswer("did he run").explanation("'So + 副词' 位于句首，主句部分倒装。").score(10).sortOrder(10).build()
        );
        quiz.setQuestions(qs);
        quizRepo.save(quiz);
    }

    private List<Word> loadDegreeEnglishWords() {
        try {
            ClassPathResource resource = new ClassPathResource("degree_english_words.json");
            InputStream is = resource.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Map<String, String>> data = mapper.readValue(is,
                    new TypeReference<Map<String, Map<String, String>>>() {});

            List<Word> words = new ArrayList<>();
            for (Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
                String wordName = entry.getKey();
                Map<String, String> details = entry.getValue();

                Word word = Word.builder()
                        .word(wordName)
                        .phoneticUs(details.getOrDefault("phonetic_us", ""))
                        .phoneticUk(details.getOrDefault("phonetic_uk", ""))
                        .partOfSpeech(details.getOrDefault("pos", ""))
                        .definitionCn(details.getOrDefault("def_cn", ""))
                        .definitionEn(details.getOrDefault("definition", ""))
                        .exampleSentence(details.getOrDefault("example", ""))
                        .exampleTranslation("")
                        .difficultyLevel(details.containsKey("def_cn") ? 2 : 3)
                        .category("学位英语")
                        .build();
                words.add(word);
            }
            return words;
        } catch (Exception e) {
            log.warn("Failed to load degree English words from JSON: {}", e.getMessage());
            return List.of();
        }
    }

    private Word createWord(String word, String phoneticUs, String phoneticUk, String pos,
                            String defCn, String defEn, String example, String exampleCn,
                            int level, String cat) {
        return Word.builder()
                .word(word).phoneticUs(phoneticUs).phoneticUk(phoneticUk)
                .partOfSpeech(pos)
                .definitionCn(defCn).definitionEn(defEn)
                .exampleSentence(example).exampleTranslation(exampleCn)
                .difficultyLevel(level).category(cat).build();
    }

}
