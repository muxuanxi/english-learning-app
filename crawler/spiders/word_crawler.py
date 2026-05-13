"""单词爬虫 - 从在线词典爬取英语单词"""
import requests
from bs4 import BeautifulSoup
from fake_useragent import UserAgent
from loguru import logger
import time
import sys
sys.path.append('..')
from utils.db import insert_words

ua = UserAgent()

# 常见英语单词列表（可根据需要扩展）
COMMON_WORDS = [
    "abandon", "abstract", "academic", "accelerate", "accomplish",
    "accumulate", "acknowledge", "acquire", "adequate", "advocate",
    "aggregate", "allocate", "alternative", "ambiguous", "analogy",
    "anticipate", "apparent", "arbitrary", "aspect", "assemble",
    "attribute", "authentic", "autonomous", "barrier", "benevolent",
]

def crawl_from_dictionaryapi(word: str) -> dict | None:
    """从免费词典API获取单词信息"""
    try:
        url = f"https://api.dictionaryapi.dev/api/v2/entries/en/{word}"
        resp = requests.get(url, timeout=10)
        if resp.status_code != 200:
            return None

        data = resp.json()[0]
        phonetic_us = ""
        phonetic_uk = ""
        for phonetic in data.get("phonetics", []):
            if "us" in str(phonetic.get("audio", "")).lower():
                phonetic_us = phonetic.get("text", "")
            elif "uk" in str(phonetic.get("audio", "")).lower():
                phonetic_uk = phonetic.get("text", "")

        meanings = data.get("meanings", [])
        part_of_speech = meanings[0].get("partOfSpeech", "") if meanings else ""
        definitions = meanings[0].get("definitions", []) if meanings else []

        definition_en = definitions[0].get("definition", "") if definitions else ""
        example = definitions[0].get("example", "") if definitions else ""

        return {
            "word": word,
            "phonetic_us": phonetic_us,
            "phonetic_uk": phonetic_uk,
            "pos": part_of_speech,
            "def_en": definition_en,
            "example": example,
        }
    except Exception as e:
        logger.error(f"Error crawling word '{word}': {e}")
        return None

def crawl_words_batch(word_list: list[str] = None):
    """批量爬取单词"""
    if word_list is None:
        word_list = COMMON_WORDS

    results = []
    for word in word_list:
        logger.info(f"Crawling: {word}")
        data = crawl_from_dictionaryapi(word)
        if data:
            results.append((
                data["word"],
                data.get("phonetic_us", ""),
                data.get("phonetic_uk", ""),
                data.get("pos", ""),
                "",  # definition_cn - 需要另外翻译
                data.get("def_en", ""),
                data.get("example", ""),
                "",  # example_translation
                2,   # difficulty_level
                "通用"  # category
            ))
        time.sleep(1)  # 礼貌延迟

    if results:
        insert_words(results)
        logger.info(f"Inserted {len(results)} words to database")

if __name__ == "__main__":
    crawl_words_batch()
