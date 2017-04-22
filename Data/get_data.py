#!/usr/bin/python

import argparse
import json
import urllib

defaults = ["activities", "restaurants", "bars", "parks"]

parser = argparse.ArgumentParser()
parser.add_argument("-q", "--queries", nargs="*", type=str, default=defaults, help="What to look for. \
                    ex: restaurants")
parser.add_argument("-l", "--location", nargs=1, type=str, default="boulder", help="Where to look. \
                    ex: boulder")
parser.add_argument("-k", "--key", nargs=1, type=str, required=True, help="Location of file containing \
                    the api key")
args = parser.parse_args()

URL_PREFIX = "https://maps.googleapis.com/maps/api/place/textsearch/json?query="

keyfile = open(str(args.key[0]), 'r')
APIKEY = keyfile.read().rstrip('\n')

OUTFILE = "data.json"

def Get_JSON(prefix, query, key):
    response = urllib.urlopen(prefix + query + "&key=" + key)
    content = response.read()
    return json.loads(content.replace("\n",""))

def main():
    for query in args.queries:
        out_file_name = args.location + "_" + query + ".json"
        outfile = open(out_file_name, 'w')
        outfile.write(json.dumps(Get_JSON(URL_PREFIX, args.location + "+" + query, APIKEY)))
        outfile.close()

main()
