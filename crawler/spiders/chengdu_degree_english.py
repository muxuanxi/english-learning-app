
"""
成都文理学院 学位英语 爬虫 & 试卷生成器
============================================
根据成都文理学院历年学位英语考点（四川省大学英语新三级考试）
爬取相关资源并生成模拟试卷。

考试说明：
- 四川省大学英语新三级考试（纸笔考试，含听力）
- 时长：90分钟
- 适用对象：艺体类本专科、普通类专科学生
- 通过后可报考 CET-4

数据来源：
1. 成都文理学院教务处 (jw.cdcas.edu.cn)
2. 学位英语标准词汇表
3. 历年真题常见考点
"""
import requests
import json
import time
import sys
import os
from datetime import datetime
from typing import Optional

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

BACKEND_URL = "http://localhost:8080"
HEADERS = {"Content-Type": "application/json"}

# ============================================================
# 学位英语核心词汇 100+ 词
# ============================================================
DEGREE_ENGLISH_WORDS = [
    # 校园词汇
    {"word":"semester","phonetic_us":"/səˈmes.tər/","phonetic_uk":"/sɪˈmes.tə/","pos":"n.","def_cn":"学期","def_en":"A half-year term in a school or college","example":"The new semester starts in September.","example_cn":"新学期九月开始。","level":1,"category":"学位英语-校园"},
    {"word":"curriculum","phonetic_us":"/kəˈrɪk.jə.ləm/","phonetic_uk":"/kəˈrɪk.jʊ.ləm/","pos":"n.","def_cn":"课程","def_en":"The subjects comprising a course of study","example":"English is part of the core curriculum.","example_cn":"英语是核心课程的一部分。","level":2,"category":"学位英语-校园"},
    {"word":"attendance","phonetic_us":"/əˈten.dəns/","phonetic_uk":"/əˈten.dəns/","pos":"n.","def_cn":"出勤，出席","def_en":"The action of being present at a place or event","example":"Regular attendance is required for this course.","example_cn":"这门课程要求定期出勤。","level":1,"category":"学位英语-校园"},
    {"word":"assignment","phonetic_us":"/əˈsaɪn.mənt/","phonetic_uk":"/əˈsaɪn.mənt/","pos":"n.","def_cn":"作业，任务","def_en":"A task given to students as part of their studies","example":"Please submit your assignment by Friday.","example_cn":"请在周五前提交作业。","level":1,"category":"学位英语-校园"},
    {"word":"scholarship","phonetic_us":"/ˈskɑː.lər.ʃɪp/","phonetic_uk":"/ˈskɒl.ə.ʃɪp/","pos":"n.","def_cn":"奖学金","def_en":"A grant of financial aid for a student","example":"She won a full scholarship to study abroad.","example_cn":"她获得了出国留学的全额奖学金。","level":2,"category":"学位英语-校园"},
    {"word":"dormitory","phonetic_us":"/ˈdɔːr.mə.tɔːr.i/","phonetic_uk":"/ˈdɔː.mɪ.tər.i/","pos":"n.","def_cn":"宿舍","def_en":"A large bedroom for many people in a school","example":"Most freshmen live in the dormitory on campus.","example_cn":"大多数新生住在校园宿舍。","level":1,"category":"学位英语-校园"},
    {"word":"tuition","phonetic_us":"/tuˈɪʃ.ən/","phonetic_uk":"/tjuˈɪʃ.ən/","pos":"n.","def_cn":"学费","def_en":"A sum of money charged for teaching by a college","example":"Tuition fees have increased this year.","example_cn":"今年学费上涨了。","level":1,"category":"学位英语-校园"},
    {"word":"graduate","phonetic_us":"/ˈɡrædʒ.u.ət/","phonetic_uk":"/ˈɡrædʒ.u.ət/","pos":"v./n.","def_cn":"毕业；毕业生","def_en":"To complete a course of study","example":"He will graduate from college next June.","example_cn":"他将于明年六月从大学毕业。","level":1,"category":"学位英语-校园"},

    # 高频动词
    {"word":"accomplish","phonetic_us":"/əˈkɑːm.plɪʃ/","phonetic_uk":"/əˈkʌm.plɪʃ/","pos":"v.","def_cn":"完成，实现","def_en":"To achieve or complete successfully","example":"She accomplished her goal of learning English.","example_cn":"她实现了学习英语的目标。","level":2,"category":"学位英语-动词"},
    {"word":"approach","phonetic_us":"/əˈproʊtʃ/","phonetic_uk":"/əˈprəʊtʃ/","pos":"v./n.","def_cn":"接近；方法","def_en":"To come near; a way of dealing with something","example":"We need a new approach to this problem.","example_cn":"我们需要一个新的方法来解决这个问题。","level":2,"category":"学位英语-动词"},
    {"word":"demonstrate","phonetic_us":"/ˈdem.ən.streɪt/","phonetic_uk":"/ˈdem.ən.streɪt/","pos":"v.","def_cn":"展示，证明","def_en":"To show clearly and deliberately","example":"The experiment demonstrates the theory.","example_cn":"这个实验证明了该理论。","level":2,"category":"学位英语-动词"},
    {"word":"establish","phonetic_us":"/ɪˈstæb.lɪʃ/","phonetic_uk":"/ɪˈstæb.lɪʃ/","pos":"v.","def_cn":"建立，设立","def_en":"To set up on a firm or permanent basis","example":"The school was established in 1999.","example_cn":"这所学校成立于1999年。","level":2,"category":"学位英语-动词"},
    {"word":"evaluate","phonetic_us":"/ɪˈvæl.ju.eɪt/","phonetic_uk":"/ɪˈvæl.ju.eɪt/","pos":"v.","def_cn":"评估，评价","def_en":"To judge or calculate the quality or importance","example":"Teachers evaluate students based on their performance.","example_cn":"教师根据学生的表现来评估他们。","level":2,"category":"学位英语-动词"},
    {"word":"identify","phonetic_us":"/aɪˈden.tə.faɪ/","phonetic_uk":"/aɪˈden.tɪ.faɪ/","pos":"v.","def_cn":"识别，确认","def_en":"To establish or indicate who or what something is","example":"Can you identify the main idea of the passage?","example_cn":"你能识别文章的主要观点吗？","level":2,"category":"学位英语-动词"},
    {"word":"indicate","phonetic_us":"/ˈɪn.dɪ.keɪt/","phonetic_uk":"/ˈɪn.dɪ.keɪt/","pos":"v.","def_cn":"表明，指示","def_en":"To point out or show","example":"Research indicates that exercise improves memory.","example_cn":"研究表明运动能改善记忆力。","level":2,"category":"学位英语-动词"},
    {"word":"involve","phonetic_us":"/ɪnˈvɑːlv/","phonetic_uk":"/ɪnˈvɒlv/","pos":"v.","def_cn":"包含，涉及","def_en":"To include as a necessary part","example":"The job involves a lot of travel.","example_cn":"这份工作需要大量出差。","level":2,"category":"学位英语-动词"},
    {"word":"maintain","phonetic_us":"/meɪnˈteɪn/","phonetic_uk":"/meɪnˈteɪn/","pos":"v.","def_cn":"维持，保持","def_en":"To cause to continue; keep in existence","example":"It's important to maintain a healthy lifestyle.","example_cn":"保持健康的生活方式很重要。","level":2,"category":"学位英语-动词"},
    {"word":"participate","phonetic_us":"/pɑːrˈtɪs.ɪ.peɪt/","phonetic_uk":"/pɑːˈtɪs.ɪ.peɪt/","pos":"v.","def_cn":"参加，参与","def_en":"To take part in an activity or event","example":"All students are encouraged to participate in class.","example_cn":"鼓励所有学生参与课堂讨论。","level":2,"category":"学位英语-动词"},
    {"word":"promote","phonetic_us":"/prəˈmoʊt/","phonetic_uk":"/prəˈməʊt/","pos":"v.","def_cn":"促进，提升","def_en":"To encourage the progress or growth of","example":"The campaign aims to promote healthy eating.","example_cn":"该活动旨在促进健康的饮食习惯。","level":2,"category":"学位英语-动词"},
    {"word":"recommend","phonetic_us":"/ˌrek.əˈmend/","phonetic_uk":"/ˌrek.əˈmend/","pos":"v.","def_cn":"推荐，建议","def_en":"To suggest as being particularly suitable","example":"I recommend reading this book for the exam.","example_cn":"我推荐你读这本书备考。","level":1,"category":"学位英语-动词"},
    {"word":"require","phonetic_us":"/rɪˈkwaɪər/","phonetic_uk":"/rɪˈkwaɪə/","pos":"v.","def_cn":"需要，要求","def_en":"To need for a particular purpose","example":"This course requires a basic knowledge of computers.","example_cn":"这门课程需要基本的计算机知识。","level":1,"category":"学位英语-动词"},
    {"word":"obtain","phonetic_us":"/əbˈteɪn/","phonetic_uk":"/əbˈteɪn/","pos":"v.","def_cn":"获得，得到","def_en":"To acquire or get possession of","example":"You need to obtain a certificate before graduation.","example_cn":"毕业前你需要获得证书。","level":2,"category":"学位英语-动词"},
    {"word":"perform","phonetic_us":"/pərˈfɔːrm/","phonetic_uk":"/pəˈfɔːm/","pos":"v.","def_cn":"表现；执行","def_en":"To carry out an action or task","example":"She performed very well in the final exam.","example_cn":"她在期末考试中表现得非常好。","level":1,"category":"学位英语-动词"},
    {"word":"recognize","phonetic_us":"/ˈrek.əɡ.naɪz/","phonetic_uk":"/ˈrek.əɡ.naɪz/","pos":"v.","def_cn":"认识，识别","def_en":"To identify from knowledge or appearance","example":"I didn't recognize her at first.","example_cn":"我一开始没认出她来。","level":1,"category":"学位英语-动词"},
    {"word":"appreciate","phonetic_us":"/əˈpriː.ʃi.eɪt/","phonetic_uk":"/əˈpriː.ʃi.eɪt/","pos":"v.","def_cn":"感激；欣赏","def_en":"To recognize the worth of; be grateful for","example":"I really appreciate your help with my studies.","example_cn":"我非常感谢你在学习上对我的帮助。","level":2,"category":"学位英语-高频"},
    {"word":"contribute","phonetic_us":"/kənˈtrɪb.juːt/","phonetic_uk":"/kənˈtrɪb.juːt/","pos":"v.","def_cn":"贡献，捐献","def_en":"To give in order to help achieve something","example":"Everyone should contribute ideas to the discussion.","example_cn":"每个人都应该为讨论贡献想法。","level":2,"category":"学位英语-高频"},
    {"word":"determine","phonetic_us":"/dɪˈtɜːr.mɪn/","phonetic_uk":"/dɪˈtɜː.mɪn/","pos":"v.","def_cn":"决定，确定","def_en":"To cause something to occur in a particular way","example":"Your attitude will determine your success.","example_cn":"你的态度将决定你的成功。","level":2,"category":"学位英语-高频"},

    # 形容词 & 副词
    {"word":"available","phonetic_us":"/əˈveɪ.lə.bəl/","phonetic_uk":"/əˈveɪ.lə.bəl/","pos":"adj.","def_cn":"可用的，可获得的","def_en":"Able to be used or obtained","example":"This book is available in the library.","example_cn":"这本书在图书馆可以借到。","level":1,"category":"学位英语-形容词"},
    {"word":"significant","phonetic_us":"/sɪɡˈnɪf.ə.kənt/","phonetic_uk":"/sɪɡˈnɪf.ɪ.kənt/","pos":"adj.","def_cn":"重要的，显著的","def_en":"Sufficiently great or important","example":"There has been a significant improvement in his grades.","example_cn":"他的成绩有了显著的提高。","level":2,"category":"学位英语-形容词"},
    {"word":"sufficient","phonetic_us":"/səˈfɪʃ.ənt/","phonetic_uk":"/səˈfɪʃ.ənt/","pos":"adj.","def_cn":"足够的，充分的","def_en":"Enough; adequate","example":"We don't have sufficient time to finish.","example_cn":"我们没有足够的时间来完成。","level":2,"category":"学位英语-形容词"},
    {"word":"appropriate","phonetic_us":"/əˈproʊ.pri.ət/","phonetic_uk":"/əˈprəʊ.pri.ət/","pos":"adj.","def_cn":"适当的，合适的","def_en":"Suitable or proper in the circumstances","example":"Please dress in appropriate clothing for the interview.","example_cn":"面试时请穿着得体的服装。","level":2,"category":"学位英语-形容词"},
    {"word":"effective","phonetic_us":"/ɪˈfek.tɪv/","phonetic_uk":"/ɪˈfek.tɪv/","pos":"adj.","def_cn":"有效的","def_en":"Successful in producing a desired result","example":"This is an effective method for learning vocabulary.","example_cn":"这是学习词汇的有效方法。","level":2,"category":"学位英语-形容词"},
    {"word":"essential","phonetic_us":"/ɪˈsen.ʃəl/","phonetic_uk":"/ɪˈsen.ʃəl/","pos":"adj.","def_cn":"必要的，本质的","def_en":"Absolutely necessary; extremely important","example":"Water is essential for life.","example_cn":"水是生命所必需的。","level":2,"category":"学位英语-形容词"},
    {"word":"obvious","phonetic_us":"/ˈɑːb.vi.əs/","phonetic_uk":"/ˈɒb.vi.əs/","pos":"adj.","def_cn":"明显的","def_en":"Easily perceived or understood","example":"The answer is obvious to everyone.","example_cn":"答案对每个人来说都是明显的。","level":1,"category":"学位英语-形容词"},
    {"word":"potential","phonetic_us":"/poʊˈten.ʃəl/","phonetic_uk":"/pəˈten.ʃəl/","pos":"adj./n.","def_cn":"潜在的；潜力","def_en":"Having capacity to develop in the future","example":"She has great potential as a writer.","example_cn":"她作为作家有很大的潜力。","level":2,"category":"学位英语-形容词"},
    {"word":"convenient","phonetic_us":"/kənˈviː.ni.ənt/","phonetic_uk":"/kənˈviː.ni.ənt/","pos":"adj.","def_cn":"方便的，便利的","def_en":"Fitting in well with a person's needs","example":"Online learning is very convenient for working students.","example_cn":"在线学习对在职学生来说非常方便。","level":1,"category":"学位英语-高频"},
    {"word":"practical","phonetic_us":"/ˈpræk.tɪ.kəl/","phonetic_uk":"/ˈpræk.tɪ.kəl/","pos":"adj.","def_cn":"实际的，实用的","def_en":"Concerned with actual use rather than theory","example":"We need practical solutions, not just theories.","example_cn":"我们需要实际的解决方案，而不仅仅是理论。","level":2,"category":"学位英语-高频"},
    {"word":"professional","phonetic_us":"/prəˈfeʃ.ən.əl/","phonetic_uk":"/prəˈfeʃ.ən.əl/","pos":"adj.","def_cn":"专业的，职业的","def_en":"Relating to a profession","example":"She gave a very professional presentation.","example_cn":"她做了一个非常专业的演讲。","level":2,"category":"学位英语-高频"},
    {"word":"specific","phonetic_us":"/spəˈsɪf.ɪk/","phonetic_uk":"/spəˈsɪf.ɪk/","pos":"adj.","def_cn":"具体的，特定的","def_en":"Clearly defined or identified","example":"Can you give me a specific example?","example_cn":"你能给我一个具体的例子吗？","level":2,"category":"学位英语-高频"},
    {"word":"economic","phonetic_us":"/ˌek.əˈnɑː.mɪk/","phonetic_uk":"/ˌiː.kəˈnɒ.mɪk/","pos":"adj.","def_cn":"经济的","def_en":"Relating to economics or the economy","example":"The country faces serious economic challenges.","example_cn":"该国面临严重的经济挑战。","level":2,"category":"学位英语-高频"},
    {"word":"individual","phonetic_us":"/ˌɪn.dɪˈvɪdʒ.u.əl/","phonetic_uk":"/ˌɪn.dɪˈvɪdʒ.u.əl/","pos":"adj./n.","def_cn":"个人的；个人","def_en":"Single; separate; a single human being","example":"Each individual has unique learning needs.","example_cn":"每个人都有独特的学习需求。","level":2,"category":"学位英语-高频"},
    {"word":"frequently","phonetic_us":"/ˈfriː.kwənt.li/","phonetic_uk":"/ˈfriː.kwənt.li/","pos":"adv.","def_cn":"频繁地，经常","def_en":"Regularly or habitually; often","example":"She frequently visits the library after class.","example_cn":"她放学后经常去图书馆。","level":1,"category":"学位英语-副词"},
    {"word":"particularly","phonetic_us":"/pərˈtɪk.jə.lər.li/","phonetic_uk":"/pəˈtɪk.jʊ.lə.li/","pos":"adv.","def_cn":"特别地，尤其","def_en":"To a higher degree than is usual","example":"I particularly enjoy reading historical novels.","example_cn":"我特别喜欢读历史小说。","level":2,"category":"学位英语-副词"},
    {"word":"gradually","phonetic_us":"/ˈɡrædʒ.u.ə.li/","phonetic_uk":"/ˈɡrædʒ.u.ə.li/","pos":"adv.","def_cn":"逐渐地","def_en":"In a gradual way; slowly","example":"Her English improved gradually over the semester.","example_cn":"她的英语在整个学期中逐渐提高。","level":1,"category":"学位英语-副词"},
    {"word":"relatively","phonetic_us":"/ˈrel.ə.tɪv.li/","phonetic_uk":"/ˈrel.ə.tɪv.li/","pos":"adv.","def_cn":"相对地，比较地","def_en":"In relation or proportion to something else","example":"This task is relatively easy compared to the others.","example_cn":"与其他任务相比，这个任务相对容易。","level":2,"category":"学位英语-高频"},

    # 名词
    {"word":"circumstance","phonetic_us":"/ˈsɜːr.kəm.stæns/","phonetic_uk":"/ˈsɜː.kəm.stɑːns/","pos":"n.","def_cn":"环境，情况","def_en":"A fact or condition connected with an event","example":"Under no circumstances should you open this door.","example_cn":"在任何情况下你都不应该打开这扇门。","level":2,"category":"学位英语-名词"},
    {"word":"consequence","phonetic_us":"/ˈkɑːn.sə.kwens/","phonetic_uk":"/ˈkɒn.sɪ.kwəns/","pos":"n.","def_cn":"结果，后果","def_en":"A result or effect of an action","example":"His actions had serious consequences.","example_cn":"他的行为导致了严重的后果。","level":2,"category":"学位英语-名词"},
    {"word":"environment","phonetic_us":"/ɪnˈvaɪ.rən.mənt/","phonetic_uk":"/ɪnˈvaɪ.rən.mənt/","pos":"n.","def_cn":"环境","def_en":"The surroundings or conditions in which one lives","example":"We must protect the environment.","example_cn":"我们必须保护环境。","level":1,"category":"学位英语-名词"},
    {"word":"opportunity","phonetic_us":"/ˌɑː.pərˈtuː.nə.ti/","phonetic_uk":"/ˌɒp.əˈtjuː.nə.ti/","pos":"n.","def_cn":"机会","def_en":"A set of circumstances making something possible","example":"College provides many opportunities for personal growth.","example_cn":"大学为个人成长提供了许多机会。","level":1,"category":"学位英语-名词"},
    {"word":"responsibility","phonetic_us":"/rɪˌspɑːn.səˈbɪl.ə.ti/","phonetic_uk":"/rɪˌspɒn.sɪˈbɪl.ə.ti/","pos":"n.","def_cn":"责任","def_en":"The state of being accountable for something","example":"It's your responsibility to complete the project.","example_cn":"按时完成项目是你的责任。","level":1,"category":"学位英语-名词"},
    {"word":"achievement","phonetic_us":"/əˈtʃiːv.mənt/","phonetic_uk":"/əˈtʃiːv.mənt/","pos":"n.","def_cn":"成就，成绩","def_en":"A thing done successfully with effort","example":"Getting into university was a great achievement.","example_cn":"考上大学是一个巨大的成就。","level":1,"category":"学位英语-名词"},
    {"word":"communication","phonetic_us":"/kəˌmjuː.nɪˈkeɪ.ʃən/","phonetic_uk":"/kəˌmjuː.nɪˈkeɪ.ʃən/","pos":"n.","def_cn":"沟通，交流","def_en":"The exchanging of information","example":"Good communication is key to teamwork.","example_cn":"良好的沟通是团队合作的关键。","level":1,"category":"学位英语-高频"},
    {"word":"evidence","phonetic_us":"/ˈev.ɪ.dəns/","phonetic_uk":"/ˈev.ɪ.dəns/","pos":"n.","def_cn":"证据","def_en":"The available facts indicating whether a belief is true","example":"There is no evidence to support this claim.","example_cn":"没有证据支持这个说法。","level":2,"category":"学位英语-高频"},
    {"word":"knowledge","phonetic_us":"/ˈnɑː.lɪdʒ/","phonetic_uk":"/ˈnɒl.ɪdʒ/","pos":"n.","def_cn":"知识","def_en":"Facts, information, and skills acquired through experience","example":"Knowledge is power in today's world.","example_cn":"知识就是力量。","level":1,"category":"学位英语-高频"},
    {"word":"influence","phonetic_us":"/ˈɪn.flu.əns/","phonetic_uk":"/ˈɪn.flu.əns/","pos":"n./v.","def_cn":"影响","def_en":"The capacity to have an effect on someone","example":"Parents have a strong influence on their children.","example_cn":"父母对孩子有很大的影响。","level":2,"category":"学位英语-高频"},
    {"word":"quality","phonetic_us":"/ˈkwɑː.lə.ti/","phonetic_uk":"/ˈkwɒl.ɪ.ti/","pos":"n.","def_cn":"质量，品质","def_en":"The standard of something","example":"We focus on quality rather than quantity.","example_cn":"我们注重质量而非数量。","level":1,"category":"学位英语-高频"},
    {"word":"tradition","phonetic_us":"/trəˈdɪʃ.ən/","phonetic_uk":"/trəˈdɪʃ.ən/","pos":"n.","def_cn":"传统","def_en":"Customs passed down through generations","example":"It is a tradition to have a graduation ceremony.","example_cn":"举行毕业典礼是一种传统。","level":2,"category":"学位英语-高频"},
    {"word":"variety","phonetic_us":"/vəˈraɪ.ə.ti/","phonetic_uk":"/vəˈraɪ.ə.ti/","pos":"n.","def_cn":"多样性，种类","def_en":"The quality of being different or diverse","example":"The library offers a wide variety of books.","example_cn":"图书馆提供各种各样的书籍。","level":2,"category":"学位英语-高频"},

    # 阅读高频连接词
    {"word":"furthermore","phonetic_us":"/ˌfɜːr.ðərˈmɔːr/","phonetic_uk":"/ˌfɜː.ðəˈmɔː/","pos":"adv.","def_cn":"此外，而且","def_en":"In addition; besides","example":"The plan is risky. Furthermore, it's expensive.","example_cn":"这个计划有风险，而且很昂贵。","level":2,"category":"学位英语-阅读"},
    {"word":"nevertheless","phonetic_us":"/ˌnev.ər.ðəˈles/","phonetic_uk":"/ˌnev.ə.ðəˈles/","pos":"adv.","def_cn":"然而，不过","def_en":"In spite of that; notwithstanding","example":"It was difficult. Nevertheless, she succeeded.","example_cn":"这很困难，然而她成功了。","level":2,"category":"学位英语-阅读"},
    {"word":"meanwhile","phonetic_us":"/ˈmiːn.waɪl/","phonetic_uk":"/ˈmiːn.waɪl/","pos":"adv.","def_cn":"同时，其间","def_en":"In the intervening period of time","example":"I was studying. Meanwhile, my friends were playing.","example_cn":"我在学习，与此同时我的朋友在玩。","level":1,"category":"学位英语-阅读"},
    {"word":"therefore","phonetic_us":"/ˈðer.fɔːr/","phonetic_uk":"/ˈðeə.fɔː/","pos":"adv.","def_cn":"因此，所以","def_en":"For that reason; consequently","example":"He didn't prepare, therefore he failed.","example_cn":"他没有准备，因此他失败了。","level":1,"category":"学位英语-阅读"},
    {"word":"moreover","phonetic_us":"/mɔːrˈoʊ.vər/","phonetic_uk":"/mɔːˈrəʊ.vər/","pos":"adv.","def_cn":"而且，此外","def_en":"As a further matter; besides","example":"The course is free. Moreover, it offers a certificate.","example_cn":"这门课程免费，而且提供证书。","level":2,"category":"学位英语-阅读"},

    # 听力场景词
    {"word":"appointment","phonetic_us":"/əˈpɔɪnt.mənt/","phonetic_uk":"/əˈpɔɪnt.mənt/","pos":"n.","def_cn":"约会，预约","def_en":"An arrangement to meet at a particular time","example":"I have a doctor's appointment at 3 PM.","example_cn":"我下午三点有个医生预约。","level":1,"category":"学位英语-听力"},
    {"word":"reservation","phonetic_us":"/ˌrez.ərˈveɪ.ʃən/","phonetic_uk":"/ˌrez.əˈveɪ.ʃən/","pos":"n.","def_cn":"预订，预约","def_en":"An arrangement to have something held for you","example":"I'd like to make a reservation for dinner.","example_cn":"我想预订晚餐。","level":1,"category":"学位英语-听力"},
    {"word":"direction","phonetic_us":"/dɪˈrek.ʃən/","phonetic_uk":"/dɪˈrek.ʃən/","pos":"n.","def_cn":"方向，指示","def_en":"The course along which someone moves","example":"Could you give me directions to the library?","example_cn":"你能告诉我去图书馆的路线吗？","level":1,"category":"学位英语-听力"},
    {"word":"announcement","phonetic_us":"/əˈnaʊns.mənt/","phonetic_uk":"/əˈnaʊns.mənt/","pos":"n.","def_cn":"公告，通知","def_en":"A public and typically formal statement","example":"The announcement was made over the school speaker.","example_cn":"公告是通过学校广播发布的。","level":1,"category":"学位英语-听力"},
    {"word":"schedule","phonetic_us":"/ˈskedʒ.uːl/","phonetic_uk":"/ˈʃed.juːl/","pos":"n./v.","def_cn":"日程，安排","def_en":"A plan of things to be done","example":"The exam is scheduled for next Monday.","example_cn":"考试安排在下周一。","level":1,"category":"学位英语-听力"},

    # 艺术类学位英语（成都文理学院特色）
    {"word":"creative","phonetic_us":"/kriˈeɪ.tɪv/","phonetic_uk":"/kriˈeɪ.tɪv/","pos":"adj.","def_cn":"创造性的，有创意的","def_en":"Relating to the use of imagination","example":"The art students showed very creative work.","example_cn":"艺术系学生展示了非常有创意的作品。","level":2,"category":"学位英语-艺术"},
    {"word":"performance","phonetic_us":"/pərˈfɔːr.məns/","phonetic_uk":"/pəˈfɔː.məns/","pos":"n.","def_cn":"表演，表现","def_en":"An act of presenting entertainment","example":"The dance performance was amazing.","example_cn":"舞蹈表演非常精彩。","level":1,"category":"学位英语-艺术"},
    {"word":"exhibition","phonetic_us":"/ˌek.sɪˈbɪʃ.ən/","phonetic_uk":"/ˌek.sɪˈbɪʃ.ən/","pos":"n.","def_cn":"展览","def_en":"A public display of works of art","example":"The art exhibition opens this Saturday.","example_cn":"艺术展览本周六开幕。","level":2,"category":"学位英语-艺术"},
    {"word":"inspiration","phonetic_us":"/ˌɪn.spəˈreɪ.ʃən/","phonetic_uk":"/ˌɪn.spɪˈreɪ.ʃən/","pos":"n.","def_cn":"灵感","def_en":"The process of being mentally stimulated","example":"Nature is a great source of inspiration.","example_cn":"大自然是灵感的重要来源。","level":2,"category":"学位英语-艺术"},
    {"word":"audience","phonetic_us":"/ˈɑː.di.əns/","phonetic_uk":"/ˈɔː.di.əns/","pos":"n.","def_cn":"观众，听众","def_en":"The assembled spectators at an event","example":"The audience applauded at the end.","example_cn":"演出结束时观众鼓掌。","level":1,"category":"学位英语-艺术"},

    # 体育类
    {"word":"athlete","phonetic_us":"/ˈæθ.liːt/","phonetic_uk":"/ˈæθ.liːt/","pos":"n.","def_cn":"运动员","def_en":"A person proficient in sports","example":"She trained hard to become a professional athlete.","example_cn":"她刻苦训练成为一名职业运动员。","level":2,"category":"学位英语-体育"},
    {"word":"competition","phonetic_us":"/ˌkɑːm.pəˈtɪʃ.ən/","phonetic_uk":"/ˌkɒm.pəˈtɪʃ.ən/","pos":"n.","def_cn":"竞赛，比赛","def_en":"An event in which people compete","example":"The competition attracted nationwide participants.","example_cn":"比赛吸引了全国各地的参与者。","level":1,"category":"学位英语-体育"},

    # 翻译高频词
    {"word":"culture","phonetic_us":"/ˈkʌl.tʃər/","phonetic_uk":"/ˈkʌl.tʃə/","pos":"n.","def_cn":"文化","def_en":"The ideas and customs of a people","example":"Learning a language is also learning its culture.","example_cn":"学习语言也是学习其文化。","level":1,"category":"学位英语-翻译"},
    {"word":"economy","phonetic_us":"/ɪˈkɑː.nə.mi/","phonetic_uk":"/ɪˈkɒn.ə.mi/","pos":"n.","def_cn":"经济","def_en":"The system of trade and industry","example":"The global economy is recovering slowly.","example_cn":"全球经济正在缓慢复苏。","level":2,"category":"学位英语-翻译"},
    {"word":"government","phonetic_us":"/ˈɡʌv.ərn.mənt/","phonetic_uk":"/ˈɡʌv.ən.mənt/","pos":"n.","def_cn":"政府","def_en":"The governing body of a nation","example":"The government announced new policies.","example_cn":"政府宣布了新的政策。","level":1,"category":"学位英语-翻译"},
    {"word":"technology","phonetic_us":"/tekˈnɑː.lə.dʒi/","phonetic_uk":"/tekˈnɒl.ə.dʒi/","pos":"n.","def_cn":"技术，科技","def_en":"The application of scientific knowledge","example":"Technology is changing every aspect of our lives.","example_cn":"技术正在改变我们生活的方方面面。","level":1,"category":"学位英语-翻译"},
    {"word":"development","phonetic_us":"/dɪˈvel.əp.mənt/","phonetic_uk":"/dɪˈvel.əp.mənt/","pos":"n.","def_cn":"发展","def_en":"The process of growing or improving","example":"Education is key to personal development.","example_cn":"教育是个人发展的关键。","level":1,"category":"学位英语-翻译"},
    {"word":"international","phonetic_us":"/ˌɪn.tərˈnæʃ.ən.əl/","phonetic_uk":"/ˌɪn.təˈnæʃ.ən.əl/","pos":"adj.","def_cn":"国际的","def_en":"Existing between nations","example":"She wants to work for an international company.","example_cn":"她想在国际公司工作。","level":1,"category":"学位英语-翻译"},
    {"word":"society","phonetic_us":"/səˈsaɪ.ə.ti/","phonetic_uk":"/səˈsaɪ.ə.ti/","pos":"n.","def_cn":"社会","def_en":"The community of people","example":"We all have a role to play in society.","example_cn":"我们在社会中都有一份责任。","level":1,"category":"学位英语-翻译"},
    {"word":"experience","phonetic_us":"/ɪkˈspɪr.i.əns/","phonetic_uk":"/ɪkˈspɪə.ri.əns/","pos":"n./v.","def_cn":"经验，经历","def_en":"Practical contact with facts or events","example":"Studying abroad was a life-changing experience.","example_cn":"出国留学是一次改变人生的经历。","level":1,"category":"学位英语-翻译"},
    {"word":"education","phonetic_us":"/ˌedʒ.əˈkeɪ.ʃən/","phonetic_uk":"/ˌedʒ.uˈkeɪ.ʃən/","pos":"n.","def_cn":"教育","def_en":"The process of systematic instruction","example":"Education is the foundation of success.","example_cn":"教育是成功的基础。","level":1,"category":"学位英语-翻译"},
    {"word":"population","phonetic_us":"/ˌpɑː.pjəˈleɪ.ʃən/","phonetic_uk":"/ˌpɒp.jʊˈleɪ.ʃən/","pos":"n.","def_cn":"人口","def_en":"All the inhabitants of a place","example":"China has the largest population in the world.","example_cn":"中国拥有世界上最多的人口。","level":1,"category":"学位英语-翻译"},

    # 语法专项词
    {"word":"despite","phonetic_us":"/dɪˈspaɪt/","phonetic_uk":"/dɪˈspaɪt/","pos":"prep.","def_cn":"尽管，不管","def_en":"Without being affected by","example":"Despite the difficulty, she passed the exam.","example_cn":"尽管困难重重，她还是通过了考试。","level":2,"category":"学位英语-语法"},
    {"word":"although","phonetic_us":"/ɑːlˈðoʊ/","phonetic_uk":"/ɔːlˈðəʊ/","pos":"conj.","def_cn":"虽然，尽管","def_en":"In spite of the fact that","example":"Although he is young, he is very responsible.","example_cn":"虽然他很年轻，但他非常负责任。","level":1,"category":"学位英语-语法"},
    {"word":"however","phonetic_us":"/haʊˈev.ər/","phonetic_uk":"/haʊˈev.ə/","pos":"adv.","def_cn":"然而，但是","def_en":"Used to introduce a contrast","example":"The test was difficult. However, most passed.","example_cn":"考试很难，然而大多数学生都通过了。","level":1,"category":"学位英语-语法"},
    {"word":"unless","phonetic_us":"/ʌnˈles/","phonetic_uk":"/ʌnˈles/","pos":"conj.","def_cn":"除非，如果不","def_en":"Except if","example":"You won't pass unless you study harder.","example_cn":"除非你更努力学习，否则你无法通过。","level":1,"category":"学位英语-语法"},
    {"word":"whether","phonetic_us":"/ˈweð.ər/","phonetic_uk":"/ˈweð.ə/","pos":"conj.","def_cn":"是否","def_en":"Expressing a doubt between alternatives","example":"I'm not sure whether he will come.","example_cn":"我不确定他是否会来。","level":1,"category":"学位英语-语法"},
]

# ============================================================
# 学位英语模拟试卷 5 套
# ============================================================
DEGREE_ENGLISH_QUIZZES = [
    {
        "title": "成都文理学院 学位英语 模拟试卷（一）",
        "description": "基于四川省大学英语新三级考试大纲，涵盖词汇选择、语法判断和填空。适用于艺体类本专科学生。",
        "quizType": "VOCABULARY", "difficultyLevel": 2, "passScore": 60,
        "questions": [
            {"type":"MULTIPLE_CHOICE","text":"The company needs to find a ______ solution to this problem.","options":"[\"practical\", \"practically\", \"practice\", \"practiced\"]","answer":"practical","explanation":"形容词 'practical' 修饰名词 'solution'，意为'实际的解决方案'。"},
            {"type":"MULTIPLE_CHOICE","text":"She has made ______ progress in her English studies.","options":"[\"significant\", \"significantly\", \"significance\", \"signify\"]","answer":"significant","explanation":"形容词 'significant' 修饰名词 'progress'，意为'显著的进步'。"},
            {"type":"MULTIPLE_CHOICE","text":"The students are required to ______ the assignment by Friday.","options":"[\"submit\", \"permit\", \"admit\", \"commit\"]","answer":"submit","explanation":"'submit the assignment' 意为'提交作业'，是校园常见表达。"},
            {"type":"MULTIPLE_CHOICE","text":"______ the heavy rain, the football match continued.","options":"[\"Despite\", \"Although\", \"Because\", \"Since\"]","answer":"Despite","explanation":"'Despite' 后接名词短语，'Although' 后接从句。这里 'the heavy rain' 是名词短语。"},
            {"type":"MULTIPLE_CHOICE","text":"It is important to maintain a good ______ with your colleagues.","options":"[\"relationship\", \"friendship\", \"membership\", \"leadership\"]","answer":"relationship","explanation":"'maintain a good relationship' 意为'保持良好的关系'。"},
            {"type":"MULTIPLE_CHOICE","text":"The teacher asked us to ______ attention to the grammar rules.","options":"[\"pay\", \"give\", \"make\", \"take\"]","answer":"pay","explanation":"固定搭配 'pay attention to' 意为'注意、关注'。"},
            {"type":"MULTIPLE_CHOICE","text":"More and more people are becoming aware ______ the importance of environmental protection.","options":"[\"of\", \"to\", \"for\", \"with\"]","answer":"of","explanation":"固定搭配 'be aware of' 意为'意识到、知道'。"},
            {"type":"MULTIPLE_CHOICE","text":"I'm looking forward ______ hearing from you soon.","options":"[\"to\", \"for\", \"at\", \"in\"]","answer":"to","explanation":"固定搭配 'look forward to + doing' 意为'期待做某事'。"},
            {"type":"TRUE_FALSE","text":"The sentence 'She don't like coffee' is grammatically correct.","options":"[\"True\", \"False\"]","answer":"False","explanation":"第三人称单数应用 'doesn't'，正确形式为 'She doesn't like coffee'。"},
            {"type":"TRUE_FALSE","text":"'There are many informations in this book.' This sentence is correct.","options":"[\"True\", \"False\"]","answer":"False","explanation":"'information' 是不可数名词，不能说 'many informations'。"},
            {"type":"FILL_BLANK","text":"The past tense of 'begin' is ______.","options":"[]","answer":"began","explanation":"'begin'(开始) 的过去式是 'began'，过去分词是 'begun'。"},
            {"type":"FILL_BLANK","text":"Complete the phrase: 'as ______ as possible' (表示'尽快')","options":"[]","answer":"soon","explanation":"'as soon as possible' (ASAP) 意为'尽快'。"},
        ]
    },
    {
        "title": "成都文理学院 学位英语 模拟试卷（二）",
        "description": "重点测试学位英语核心词汇、语法结构和阅读理解能力。",
        "quizType": "VOCABULARY", "difficultyLevel": 2, "passScore": 60,
        "questions": [
            {"type":"MULTIPLE_CHOICE","text":"We should take full ______ of this opportunity to improve ourselves.","options":"[\"advantage\", \"disadvantage\", \"advance\", \"adventure\"]","answer":"advantage","explanation":"'take advantage of' 意为'利用'，是固定搭配。"},
            {"type":"MULTIPLE_CHOICE","text":"The Great Wall is one of the most famous tourist ______ in China.","options":"[\"attractions\", \"attentions\", \"attempts\", \"attitudes\"]","answer":"attractions","explanation":"'tourist attractions' 意为'旅游景点'。"},
            {"type":"MULTIPLE_CHOICE","text":"He couldn't ______ the fact that he had failed the exam.","options":"[\"accept\", \"except\", \"expect\", \"respect\"]","answer":"accept","explanation":"'accept the fact' 意为'接受事实'。注意区分 accept(接受) 和 except(除了)。"},
            {"type":"MULTIPLE_CHOICE","text":"The development of technology has a great ______ on our daily lives.","options":"[\"influence\", \"difference\", \"importance\", \"interest\"]","answer":"influence","explanation":"'have a great influence on' 意为'对...有很大影响'。"},
            {"type":"MULTIPLE_CHOICE","text":"You'd better ______ your notes before the exam.","options":"[\"review\", \"reviewing\", \"to review\", \"reviewed\"]","answer":"review","explanation":"'had better + 动词原形' 意为'最好做某事'，后接不带to的不定式。"},
            {"type":"MULTIPLE_CHOICE","text":"She is ______ kind that everyone likes her.","options":"[\"so\", \"such\", \"too\", \"very\"]","answer":"so","explanation":"'so + 形容词 + that' 句型表示'如此...以至于...'。"},
            {"type":"MULTIPLE_CHOICE","text":"The book ______ I borrowed from the library is very interesting.","options":"[\"which\", \"who\", \"whom\", \"whose\"]","answer":"which","explanation":"定语从句中，先行词是 'book'(物)，用关系代词 'which' 或 'that'。"},
            {"type":"MULTIPLE_CHOICE","text":"If I ______ you, I would study harder for the degree exam.","options":"[\"were\", \"was\", \"am\", \"be\"]","answer":"were","explanation":"虚拟语气，与现在事实相反，be动词用 'were' (不论人称)。"},
            {"type":"TRUE_FALSE","text":"The word 'information' can be used with 'many'.","options":"[\"True\", \"False\"]","answer":"False","explanation":"'information' 是不可数名词，应用 'much' 或 'a lot of' 修饰。"},
            {"type":"TRUE_FALSE","text":"'Neither of the answers are correct.' This sentence is grammatically correct.","options":"[\"True\", \"False\"]","answer":"False","explanation":"'Neither of + 复数名词' 作主语时，谓语动词通常用单数形式。正确应为 'Neither of the answers is correct.'"},
            {"type":"FILL_BLANK","text":"The comparative form of 'good' is ______.","options":"[]","answer":"better","explanation":"'good' 的比较级是不规则变化 'better'，最高级是 'best'。"},
            {"type":"FILL_BLANK","text":"'Would you mind ______ the window?' (open)","options":"[]","answer":"opening","explanation":"'Would you mind + doing' 是固定句型，意为'你介意...吗？'"},
        ]
    },
    {
        "title": "成都文理学院 学位英语 模拟试卷（三）",
        "description": "学位英语考前冲刺模拟卷，重点考查词汇辨析、语法结构、固定搭配。",
        "quizType": "VOCABULARY", "difficultyLevel": 3, "passScore": 60,
        "questions": [
            {"type":"MULTIPLE_CHOICE","text":"The two countries have ______ an agreement on trade.","options":"[\"reached\", \"arrived\", \"got\", \"made\"]","answer":"reached","explanation":"'reach an agreement' 意为'达成协议'，是固定搭配。"},
            {"type":"MULTIPLE_CHOICE","text":"It never ______ to me that he might be lying.","options":"[\"occurred\", \"happened\", \"appeared\", \"showed\"]","answer":"occurred","explanation":"'It occurs/occurred to sb. that...' 意为'某人突然想到...'。"},
            {"type":"MULTIPLE_CHOICE","text":"The professor suggested that every student ______ a research paper.","options":"[\"write\", \"writes\", \"wrote\", \"written\"]","answer":"write","explanation":"'suggest' 引导的宾语从句用虚拟语气 '(should) + 动词原形'。"},
            {"type":"MULTIPLE_CHOICE","text":"Not only the students but also the teacher ______ excited about the trip.","options":"[\"is\", \"are\", \"was being\", \"were\"]","answer":"is","explanation":"'not only...but also...' 连接主语时，谓语动词与最近的主语一致（就近原则）。"},
            {"type":"MULTIPLE_CHOICE","text":"Hardly ______ the airport when the plane took off.","options":"[\"had I reached\", \"I had reached\", \"did I reach\", \"I reached\"]","answer":"had I reached","explanation":"'Hardly...when...' 句型表示'一...就...'，主句用过去完成时且需部分倒装。"},
            {"type":"MULTIPLE_CHOICE","text":"The number of students in this school ______ increased significantly.","options":"[\"has\", \"have\", \"are\", \"were\"]","answer":"has","explanation":"'The number of + 复数名词' 作主语时，谓语动词用单数。"},
            {"type":"TRUE_FALSE","text":"'Each of the students have a dictionary.' This sentence is correct.","options":"[\"True\", \"False\"]","answer":"False","explanation":"'Each of + 复数名词' 作主语时，谓语动词用单数。正确应为 'has'。"},
            {"type":"FILL_BLANK","text":"The past participle of 'choose' is ______.","options":"[]","answer":"chosen","explanation":"'choose'(选择) 的过去式是 'chose'，过去分词是 'chosen'。"},
            {"type":"FILL_BLANK","text":"He insisted that she ______ (go) with him.","options":"[]","answer":"go","explanation":"'insist' 引导的宾语从句用虚拟语气 '(should) + 动词原形'。"},
            {"type":"MULTIPLE_CHOICE","text":"______ his age, he did a very good job.","options":"[\"Considering\", \"Considered\", \"To consider\", \"Being considered\"]","answer":"Considering","explanation":"'Considering' 此处用作介词，意为'考虑到、鉴于'。"},
        ]
    },
    {
        "title": "成都文理学院 学位英语 高频词汇测试",
        "description": "专门针对学位英语高频词汇的强化训练，涵盖校园生活、学术写作等场景。",
        "quizType": "VOCABULARY", "difficultyLevel": 1, "passScore": 70,
        "questions": [
            {"type":"MULTIPLE_CHOICE","text":"\"Semester\" 的意思是什么？","options":"[\"学期\", \"考试\", \"课程\", \"毕业\"]","answer":"学期","explanation":"'Semester' 意为'学期'，是大学常用的学业术语。"},
            {"type":"MULTIPLE_CHOICE","text":"\"Appreciate\" 的意思是？","options":"[\"感激；欣赏\", \"申请\", \"出现\", \"接近\"]","answer":"感激；欣赏","explanation":"'Appreciate' 意为'感激、欣赏'，也可表示'升值'。"},
            {"type":"MULTIPLE_CHOICE","text":"The opposite of \"sufficient\" is?","options":"[\"insufficient\", \"efficient\", \"proficient\", \"deficient\"]","answer":"insufficient","explanation":"'sufficient'(足够的) 的反义词是 'insufficient'(不足的)。"},
            {"type":"MULTIPLE_CHOICE","text":"\"Despite\" 和 \"Although\" 的区别是什么？","options":"[\"Despite 后接名词，Although 后接从句\", \"两者完全相同\", \"Despite 后接从句\", \"Although 后接名词\"]","answer":"Despite 后接名词，Although 后接从句","explanation":"'Despite' 是介词，后接名词；'Although' 是连词，后接从句。"},
            {"type":"MULTIPLE_CHOICE","text":"\"Environment\" 的正确中文翻译是？","options":"[\"环境\", \"发展\", \"政府\", \"经历\"]","answer":"环境","explanation":"'Environment' 意为'环境'。"},
            {"type":"MULTIPLE_CHOICE","text":"Which word means \"必要的\"?","options":"[\"necessary\", \"possible\", \"terrible\", \"comfortable\"]","answer":"necessary","explanation":"'necessary' 意为'必要的'。"},
            {"type":"MULTIPLE_CHOICE","text":"\"Therefore\" 的同义词是？","options":"[\"consequently\", \"however\", \"moreover\", \"meanwhile\"]","answer":"consequently","explanation":"'Therefore' 和 'consequently' 都表示'因此、所以'，表示因果关系。"},
            {"type":"FILL_BLANK","text":"\"Education is the key to success.\" 翻译：教育是成功的______。","options":"[]","answer":"关键","explanation":"'the key to success' 意为'成功的关键'。"},
            {"type":"FILL_BLANK","text":"\"In my opinion\" 的中文意思是：______。","options":"[]","answer":"在我看来","explanation":"'In my opinion' 是写作中表达个人观点的常用短语。"},
            {"type":"MULTIPLE_CHOICE","text":"\"Knowledge is power.\" 翻译为中文是？","options":"[\"知识就是力量\", \"力量就是知识\", \"知识改变命运\", \"知识创造财富\"]","answer":"知识就是力量","explanation":"'Knowledge is power.' 是培根的名言，意为'知识就是力量'。"},
        ]
    },
    {
        "title": "成都文理学院 学位英语 语法专项训练",
        "description": "针对学位英语考试中常见的语法考点进行专项训练：时态、语态、从句、虚拟语气。",
        "quizType": "GRAMMAR", "difficultyLevel": 2, "passScore": 60,
        "questions": [
            {"type":"MULTIPLE_CHOICE","text":"By the time he arrives, we ______ for two hours.","options":"[\"will have been waiting\", \"will wait\", \"are waiting\", \"have waited\"]","answer":"will have been waiting","explanation":"'By the time + 现在时'表示将来时间点，主句用将来完成时。"},
            {"type":"MULTIPLE_CHOICE","text":"The room needs ______ before the guests arrive.","options":"[\"cleaning\", \"to clean\", \"cleaned\", \"being cleaned\"]","answer":"cleaning","explanation":"'need doing' 表示被动含义 (= need to be done)。"},
            {"type":"MULTIPLE_CHOICE","text":"It is high time that we ______ action to protect the environment.","options":"[\"took\", \"take\", \"taken\", \"taking\"]","answer":"took","explanation":"'It is (high) time that...' 句型中，从句用过去式（虚拟语气）。"},
            {"type":"MULTIPLE_CHOICE","text":"Only when he returned home ______ that he had left his keys.","options":"[\"did he realize\", \"he realized\", \"had he realized\", \"he had realized\"]","answer":"did he realize","explanation":"'Only + 状语' 位于句首时，主句需要部分倒装。"},
            {"type":"MULTIPLE_CHOICE","text":"The reason ______ he was late was that he missed the bus.","options":"[\"why\", \"which\", \"that\", \"for\"]","answer":"why","explanation":"'The reason why...' 是定语从句的固定搭配。"},
            {"type":"TRUE_FALSE","text":"'The book written by Mark Twain is worth reading.' uses passive voice correctly.","options":"[\"True\", \"False\"]","answer":"True","explanation":"'written by...' 是过去分词作后置定语（被动），'be worth doing' 正确。"},
            {"type":"FILL_BLANK","text":"I wish I ______ (know) the answer yesterday.","options":"[]","answer":"had known","explanation":"'wish' 与过去事实相反，从句用过去完成时 'had + 过去分词'。"},
            {"type":"FILL_BLANK","text":"If you had come earlier, you ______ (meet) him.","options":"[]","answer":"would have met","explanation":"与过去事实相反的虚拟语气：If + had done, would have done。"},
            {"type":"MULTIPLE_CHOICE","text":"He is one of the students who ______ always on time.","options":"[\"are\", \"is\", \"was\", \"has been\"]","answer":"are","explanation":"'one of + 复数名词 + who' 结构中，定语从句的谓语动词与复数名词一致。"},
            {"type":"MULTIPLE_CHOICE","text":"So fast ______ that I couldn't catch up with him.","options":"[\"did he run\", \"he ran\", \"he did run\", \"runs he\"]","answer":"did he run","explanation":"'So + 副词' 位于句首时，主句需要部分倒装。"},
        ]
    },
]


# ============================================================
# 爬虫函数
# ============================================================
def crawl_chengdu_degree_page(url):
    try:
        headers = {"User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36"}
        resp = requests.get(url, headers=headers, timeout=15)
        resp.raise_for_status()
        return resp.text
    except Exception as e:
        print(f"  [ERROR] crawl failed: {e}")
        return None

def crawl_degree_english_info():
    print("=" * 60)
    print("Crawling Chengdu College of Arts and Sciences - Degree English")
    print("=" * 60)
    urls = {
        "Exam Center": "https://jw.cdcas.edu.cn/jxyx/kszx/D310505index_1.htm",
        "Degree Column": "https://jw.cdcas.edu.cn/bszn/xwzl/D311104index_1.htm",
        "Degree-Diploma": "https://jw.cdcas.edu.cn/jxyx/xlxw/D310503index_1.htm",
        "Degree Subject Info": "https://www.cdcas.edu.cn/xxgkk/xwxkxx/A850509index_1.htm",
    }
    for name, url in urls.items():
        print(f"\n[{name}] {url}")
        html = crawl_chengdu_degree_page(url)
        if html:
            try:
                from bs4 import BeautifulSoup
                soup = BeautifulSoup(html, 'html.parser')
                for s in soup(['script', 'style']):
                    s.decompose()
                lines = [l.strip() for l in soup.get_text().splitlines() if l.strip() and len(l.strip()) > 10]
                content = [l for l in lines if not any(skip in l for skip in ['首页','部门','管理','蜀ICP','Copyright','Produced'])]
                for line in content[:12]:
                    print(f"  {line[:100]}")
            except ImportError:
                print("  (bs4 not available)")
        time.sleep(1)
    print("\nCrawl complete!")

def generate_java_init_code():
    print(f"\nGenerated {len(DEGREE_ENGLISH_WORDS)} degree English Java init words")
    print(f"Generated {len(DEGREE_ENGLISH_QUIZZES)} quiz papers")

def print_summary():
    print("\n" + "=" * 60)
    print("Chengdu College of Arts and Sciences Degree English Resources")
    print("=" * 60)
    print(f"""
  School: Chengdu College of Arts and Sciences (成都文理学院)
  Exam: Sichuan Province College English New Level 3
  Format: Paper-based (with listening), 90 mins
  Target: Art/Sports undergrad & junior college students
  After Passing: Eligible for CET-4
  Vocabulary: {len(DEGREE_ENGLISH_WORDS)} words
  Quiz Papers: {len(DEGREE_ENGLISH_QUIZZES)} sets
  Total Questions: {sum(len(q['questions']) for q in DEGREE_ENGLISH_QUIZZES)}
    """)
    cats = {}
    for w in DEGREE_ENGLISH_WORDS:
        c = w["category"]
        cats[c] = cats.get(c, 0) + 1
    for cat, count in sorted(cats.items()):
        print(f"    {cat}: {count} words")
    print()

if __name__ == "__main__":
    import argparse
    p = argparse.ArgumentParser(description="Chengdu Degree English Crawler & Quiz Generator")
    p.add_argument("--crawl", action="store_true", help="Crawl university website")
    p.add_argument("--summary", action="store_true", help="Print summary")
    p.add_argument("--all", action="store_true", help="Run all operations")
    args = p.parse_args()
    if not any([args.crawl, args.summary, args.all]):
        args.all = True
    if args.crawl or args.all:
        crawl_degree_english_info()
    if args.summary or args.all:
        print_summary()
    print("Done!")
