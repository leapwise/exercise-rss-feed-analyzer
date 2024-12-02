from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

class Analysis(db.Model):  #  first API call
    __tablename__ = 'analysis'
    id = db.Column(db.String, primary_key=True)
    entries = db.relationship('Entry', secondary='analysis_entries', backref='analyses')
    hot_topics = db.relationship('HotTopic', secondary='analysis_hot_topics', backref='analyses')

class Entry(db.Model):  # RSS feed news
    __tablename__ = 'entry'
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String)
    link = db.Column(db.String)

class HotTopic(db.Model):  # hot topic after analysis
    __tablename__ = 'hot_topic'
    id = db.Column(db.Integer, primary_key=True)
    topic = db.Column(db.String)
    count = db.Column(db.Integer)

# many-many -> analysis and news(entry)
analysis_entries = db.Table('analysis_entries',
    db.Column('analysis_id', db.String, db.ForeignKey('analysis.id'), primary_key=True),
    db.Column('entry_id', db.Integer, db.ForeignKey('entry.id'), primary_key=True)
)
# many-many -> analysis and hot topic
analysis_hot_topics = db.Table('analysis_hot_topics',
    db.Column('analysis_id', db.String, db.ForeignKey('analysis.id'), primary_key=True),
    db.Column('hot_topic_id', db.Integer, db.ForeignKey('hot_topic.id'), primary_key=True)
)
