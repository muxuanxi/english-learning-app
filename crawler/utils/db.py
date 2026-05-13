"""数据库连接工具"""
import pymysql
from contextlib import contextmanager

DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': 'root',
    'database': 'english_learn',
    'charset': 'utf8mb4',
    'cursorclass': pymysql.cursors.DictCursor
}

@contextmanager
def get_connection():
    conn = pymysql.connect(**DB_CONFIG)
    try:
        yield conn
    finally:
        conn.close()

def insert_words(words: list):
    """批量插入单词"""
    sql = """INSERT INTO words (word, phonetic_us, phonetic_uk, part_of_speech,
             definition_cn, definition_en, example_sentence, example_translation,
             difficulty_level, category) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
             ON DUPLICATE KEY UPDATE definition_cn=VALUES(definition_cn)"""
    with get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.executemany(sql, words)
        conn.commit()

def insert_articles(articles: list):
    """批量插入文章"""
    sql = """INSERT INTO articles (title, author, source, content_html, summary,
             difficulty_level, word_count, category) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)"""
    with get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.executemany(sql, articles)
        conn.commit()
