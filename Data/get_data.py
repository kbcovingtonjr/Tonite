#!/usr/bin/python

import argparse
import json
import MySQLdb
import time
import urllib

defaults = ["activities", "restaurants", "bars", "parks"]

parser = argparse.ArgumentParser()
parser.add_argument("-q", "--queries", nargs="*", type=str, default=defaults)
parser.add_argument("-l", "--location", nargs=1, type=str, default="boulder")
parser.add_argument("-k", "--key", nargs=1, type=str, required=True)
args = parser.parse_args()

URL_PREFIX = "https://maps.googleapis.com/maps/api/place/textsearch/json?"

keyfile = open(str(args.key[0]), 'r')
API_KEY = keyfile.read().rstrip('\n')

INVALID = "INVALID_REQUEST"

connection = MySQLdb.connect("localhost", "root", "INSERT PASSWORD HERE", "INSERT DB NAME HERE")
connection.set_character_set('utf8')
cursor = connection.cursor()
cursor.execute('SET NAMES utf8;')
cursor.execute('SET CHARACTER SET utf8;')
cursor.execute('SET character_set_connection=utf8;')

def Get_JSON(url):
    status = INVALID
    backoff = 2
    for i in xrange(5):
        response = urllib.urlopen(url)
        content = response.read()
        json_obj = json.loads(content.replace("\n",""))

        if json_obj['status'] != INVALID:
            break
        else:
            time.sleep(backoff)
            backoff = backoff * 2
    return json_obj

def Get_Pages(url, max_depth):
    pages = []
    for i in xrange(max_depth):
        page = Get_JSON(url)
        pages.append(page)
        if 'next_page_token' in page.keys():
            url = URL_PREFIX + "pagetoken=" + str(page['next_page_token']) + "&key=" + API_KEY
        else:
            break;
    return pages

def main():
    for query in args.queries:
        query_pages = Get_Pages(URL_PREFIX + "query=" + args.location.replace(' ', '+') + query + "&key=" + API_KEY, 5)
        for page in query_pages:
            for result in page['results']:
                name = result['name']
                city = str(args.location)
                lat = float(result['geometry']['location']['lat'])
                lng = float(result['geometry']['location']['lng'])
                try:
                    rating = float(result['rating'])
                except KeyError:
                    rating = None
                sql = "INSERT INTO " + query + " (name,city,lat,lng,rating) VALUES (%s,%s,%s,%s,%s)"
                cursor.execute(sql,(name,city,lat,lng,rating))
    connection.commit()
    connection.close()

main()
