import requests

url = 'http://localhost:5000'

# Data for the POST request
data = {
    'urls': [
        'https://www.yahoo.com/news/rss',
        'https://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml'
    ]
}

response = requests.post(f'{url}/analyse/new', json=data)
print(response.json())