"""文章爬虫 - 爬取英语新闻/文章"""
import requests
from bs4 import BeautifulSoup
from fake_useragent import UserAgent
from loguru import logger
import time
import sys
sys.path.append('..')
from utils.db import insert_articles

ua = UserAgent()

def crawl_bbc_learning_english():
    """爬取 BBC Learning English 文章"""
    url = "https://www.bbc.co.uk/learningenglish/english/features/news-report"
    headers = {"User-Agent": ua.random}

    try:
        resp = requests.get(url, headers=headers, timeout=15)
        soup = BeautifulSoup(resp.text, 'lxml')

        articles = []
        for item in soup.select('.course-content-item')[:10]:
            title_elem = item.select_one('.course-content-item__title')
            summary_elem = item.select_one('.course-content-item__description')
            link_elem = item.select_one('a')

            if title_elem:
                title = title_elem.get_text(strip=True)
                summary = summary_elem.get_text(strip=True) if summary_elem else ""
                link = link_elem.get('href', '') if link_elem else ""

                # 计算难度（根据文章长度估算）
                word_count = len(summary.split()) if summary else 0
                difficulty = min(5, max(1, word_count // 50))

                articles.append((
                    title,
                    "BBC Learning English",
                    "BBC",
                    f"<p>{summary}</p><p><em>Full article available at: {link}</em></p>",
                    summary,
                    difficulty,
                    word_count,
                    "新闻"
                ))

        if articles:
            insert_articles(articles)
            logger.info(f"Inserted {len(articles)} articles from BBC")
        return articles
    except Exception as e:
        logger.error(f"Error crawling BBC: {e}")
        return []

def crawl_simple_wikipedia():
    """爬取 Simple English Wikipedia 特色文章"""
    url = "https://simple.wikipedia.org/wiki/Wikipedia:Very_good_articles"
    headers = {"User-Agent": ua.random}

    try:
        resp = requests.get(url, headers=headers, timeout=15)
        soup = BeautifulSoup(resp.text, 'lxml')

        articles = []
        for item in soup.select('.mw-parser-output ul li a')[:10]:
            title = item.get_text(strip=True)
            href = item.get('href', '')
            if href and title:
                full_url = f"https://simple.wikipedia.org{href}"
                articles.append(fetch_wikipedia_article(title, full_url))
                time.sleep(1)

        valid_articles = [a for a in articles if a]
        if valid_articles:
            insert_articles(valid_articles)
            logger.info(f"Inserted {len(valid_articles)} articles from Wikipedia")
        return valid_articles
    except Exception as e:
        logger.error(f"Error crawling Wikipedia: {e}")
        return []

def fetch_wikipedia_article(title: str, url: str) -> tuple | None:
    """获取单篇 Wikipedia 文章内容"""
    try:
        headers = {"User-Agent": ua.random}
        resp = requests.get(url, headers=headers, timeout=15)
        soup = BeautifulSoup(resp.text, 'lxml')

        content_div = soup.select_one('.mw-parser-output')
        if not content_div:
            return None

        paragraphs = content_div.select('p')[:5]
        text = ' '.join([p.get_text(strip=True) for p in paragraphs if len(p.get_text(strip=True)) > 50])
        word_count = len(text.split())
        difficulty = min(5, max(1, word_count // 60))

        return (
            title,
            "Wikipedia",
            "Simple Wikipedia",
            f"<p>{text}</p>",
            text[:200] + "..." if len(text) > 200 else text,
            difficulty,
            word_count,
            "百科"
        )
    except Exception as e:
        logger.error(f"Error fetching article '{title}': {e}")
        return None

if __name__ == "__main__":
    logger.info("Starting article crawler...")
    crawl_bbc_learning_english()
    crawl_simple_wikipedia()
