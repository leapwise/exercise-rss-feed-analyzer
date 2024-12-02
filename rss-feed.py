# Luka Cvetko 2024

# web-app -> 2 objectives
# analyzes multiple RSS feeds to find overlapping topics (hot topics) among news articles

# /analyse/new -> input: two RSS feed URLs, output: overlap news?, store data - in mem, unique id

# /frequency/{id} -> endpoint -> input: id, output: 3 elements with most matches, news header, link

# og req - JAVA
    # Spring JPA H2 database running in memory (data will not be persistent across application restarts)
    # You are free to add / change any libraries which you might need to solve this exercise,
    # the only requirement is that we do not have to setup / install any external software to run this application.
    # maven

# req python based -> some tweaks since we aren't using java
    # 1. web-app -> flask or django
    # 2. no ext software - except pip
    # 3. in-memory - SQLAlchemy - ORM -> Spring JPA alt
    # 4. Sql-lite - database
    # 5. feedparser - parsing rss -> https://feedparser.readthedocs.io/en/latest/

# pip - > external software?


# libraries
import feedparser  # RSS feed parsing
import uuid  # unique id
import re
from flask import Flask, request, jsonify  # flask
from collections import OrderedDict
from collections import Counter
from model import db, Analysis, Entry, HotTopic  # Import models from models.py - malo change
import json
from flask import Response

rss = Flask(__name__)

# config - https://flask-sqlalchemy.readthedocs.io/en/stable/config/
rss.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///:memory:'  # in-memory database
rss.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False  # performance change

# init database
db.init_app(rss)

# tables
with rss.app_context():
    db.create_all()


# keyword extractor -> re -> could be better --> nlp based solution?
def keywords_extract(text):
    skip = {'the', 'and', 'is', 'to', 'in', 'for', 'of', 'a', 'with', 'on', 'by', 'an', 'be', 'as', 'at', 'from',
            'that', 'this', 'it', 'are', 'was', 'but', 'not', 'or', 'have', 'has', 'had', 'they', 'you', 'we',
            'he', 'she', 'him', 'her', 'his', 'their', 'our', 'can', 'will', 'do', 'does', 's', 'new', 'about',
            'year', 'before', 'after', 'out', 'would', 'could', 'should', 'ever', 'never', 'forever', 'says', 'how',
            'why'}

    words = re.findall(r'\w+', text.lower())

    keywords = [
        word for word in words
        if word not in skip and len(
            word) > 2]  # loop through words list, only return those >2 chars long, not in skip lists

    return keywords


# endpoint rss feeds - analyze

@rss.route('/analyse/new', methods=['POST'])
def analyse():
    d = request.get_json()
    url = d.get('urls')

    # urls >=2

    if len(url) < 2:  # ERROR check < 2 url provided
        return jsonify({'error': "Provide at least two RSS URL"}), 400

    keywords = []
    collection = []

    # for loop for RSS feed url
    for i in url:
        f = feedparser.parse(i)

        for result in f.entries:
            # title + link -> in-mem storage
            title = result.get('title', '')
            link = result.get('link', '')

            # new news
            news = Entry(title=title, link=link)

            db.session.add(news)

            collection.append(news)  # add news

            keyword = keywords_extract(title)
            keywords.extend(keyword)  # add title keywords to list

        # frequency of words -> hot topics

    k_count = Counter(keywords)

    # hot topics -> keywords > 1 -> problem: exclude common words -> solution fixed skip list set

    hot_topic = []
    for word, count, in k_count.items():
        if count > 1:  #  acceptable count?
            topic = HotTopic(topic=word, count=count)
            db.session.add(topic)
            hot_topic.append(topic)

    # store analysis data
    analysis_id = str(uuid.uuid4())  # unique id
    analysis = Analysis(id=analysis_id)
    analysis.entries.extend(collection)
    analysis.hot_topics.extend(hot_topic)
    db.session.add(analysis)
    db.session.commit()

    # return analysis ID
    return jsonify({'id': analysis_id}), 200


@rss.route('/frequency/<id>', methods=['GET'])
def freq(id):
    analysis = Analysis.query.filter_by(id=id).first()
    if not analysis:
        return jsonify({'error': 'Analysis ID not found'}), 404

    hot_topics = sorted(
        analysis.hot_topics,
        key=lambda ht: ht.count,  # descending order based on freq count (high to low), top 3 topics selected
        reverse=True
    )[:3]
    result = []

    for hot_topic in hot_topics:
        matching_entries = []
        for entry in analysis.entries:
            if hot_topic.topic in keywords_extract(entry.title):
                matching_entries.append({
                    'title': entry.title,
                    'link': entry.link
                })

        # preserve the key order and assign values correctly
        topic_data = OrderedDict()
        topic_data['Hot Topic'] = hot_topic.topic  # string
        topic_data['count'] = hot_topic.count  # int
        topic_data['news related to topic'] = matching_entries
        result.append(topic_data)

    response_json = json.dumps(result, ensure_ascii=False, indent=2)
    return Response(response_json, mimetype='application/json')


if __name__ == '__main__':
    rss.run(debug=True)
