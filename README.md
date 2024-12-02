# Exercise

Implement a hot topic analysis for RSS feeds.

## Specification
Your application should expose two HTTP endpoints:

### API Definition: 

```
/analyse/new
```

### API Input:

This API endpoint should take at least two RSS URLs as a parameter (more are possible) e.g.:

https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&output=rss

### API Response:

For each request executed against the API endpoint you should return an unique identifier, which will be the input for the second API endpoint.

### Workflow:

When the the API is being called, your code should do a HTTP request to fetch the RSS feeds.
Your code should then analyse the entries in this feed and find potential hot topics --> are there any overlaps between the news.

### Example:

RSS Feed one contains following news:
To Democrats, Donald Trump Is No Longer a Laughing Matter
Burundi military sites attacked, 12 insurgents killed
San Bernardino divers return to lake seeking electronic evidence

RSS Feed two contains following news:
Attacks on Military Camps in Burundi Kill Eight
Saudi Women to Vote for First Time
Platini Dealt Further Blow in FIFA Presidency Bid

Your analysis should return that there are news related to Burundi in both feeds.
The analysed data should be stored within a data store and referenced by an unique identifier (see API response).

### API Definition: 

```
/frequency/{id}
```

### API Input:

This API endpoint takes an id as input

### API Output:

Returns the three elements with the most matches, additinally the orignal news header and the link to the whole news text should be displayed.

### Workflow:

When this API is being called, you will read the analysis data stored in the database by using the given id parameter
Return the top three results as a json object ordered by their frequency of occurrence


# Instalation

## Initial setup
### 1. Clone repository
```
git clone https://github.com/Windtwist/rss-feed-analyzer.git
cd rss-feed-analyzer
```
### 2. Create virtual environment
```
python -m venv venv
venv\Scripts\activate
```
### 3. Install requirements.txt
```
pip install -r requirements.txt
```

## Running application

### Start the server
```
python rss-feed.py
```
The application will start and run on http://localhost:5000.

### To test application run
```
python test.api.py
```
You can change the url listed here to other rss feeds
```
data = {
    'urls': [
        'https://www.yahoo.com/news/rss',
        'https://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml'
    ]
}
```
Expected output
```
{'id': 'a31149b8-6064-4981-9371-7e674dd7f9b1'}
```
### Retrieving hot topics (/frequency/id)

URL: http://localhost:5000/frequency/id
- Replace <id> with the analysis ID obtained from the previous step

Expected output
Hot topic with associated count and related news - 3 topics listed
```
[
    {
        "Hot Topic": "biden",
        "count": 10,
        "news related to topic": [
            {
                "title": "Washington DC reacts to President Biden pardoning son Hunter in shock decision",
                "link": "https://www.yahoo.com/news/president-biden-pardons-son-hunter-021000547.html"
            },
```
### File Structure
Ensure your project directory contains the following files:

- app.py: The main application file containing the Flask app and endpoints.
- models.py: Contains the SQLAlchemy models for the database.
- requirements.txt: Lists all the dependencies required for the application.

- Luka Cvetko 2024


