#!/usr/bin/python

import argparse
from geopy.distance import great_circle
import json
import math
import MySQLdb

connection = MySQLdb.connect("localhost", "root", "INSERT PASSWORD HERE", "INSERT DB NAME HERE")
connection.set_character_set('utf8')
cursor = connection.cursor(MySQLdb.cursors.DictCursor)
cursor.execute('SET NAMES utf8;')
cursor.execute('SET CHARACTER SET utf8;')
cursor.execute('SET character_set_connection=utf8;')

def lat_lng_box(lat, lng, side_length, max_iter):
    x_margin_guess = 0.05
    y_margin_guess = 0.05
    for i in xrange(max_iter):
        pt1 = (lat + x_margin_guess, lng)
        pt2 = (lat - x_margin_guess, lng)
        if great_circle(pt1, pt2).miles < side_length:
            x_margin_guess *= 1.5
        else:
            x_margin_guess *= .7
    for i in xrange(max_iter):
        pt1 = (lat, lng + y_margin_guess)
        pt2 = (lat, lng - y_margin_guess)
        if great_circle(pt1, pt2).miles < side_length:
            y_margin_guess *= 1.5
        else:
            y_margin_guess *= .7
    return (lat - x_margin_guess, lat + x_margin_guess, lng - y_margin_guess, lng + y_margin_guess)

def get_table_info(table, lat, lng, side_length, max_iter):
    minx, maxx, miny, maxy = lat_lng_box(lat, lng, side_length, max_iter)

    sql = "SELECT * FROM " + table + " WHERE lat < " + str(maxx) + " && lat > " + str(minx) + \
          " && lng < " + str(maxy) + " && lng > " + str(miny) + ";"

    cursor.execute(sql)
    query_output = cursor.fetchall()
    return query_output

def get_all_info(lat, lng, side_length, max_iter):
    info = {}
    cursor.execute("SHOW TABLES")
    table_names = cursor.fetchall()
    for table_name in table_names:
        table_name = table_name.values()[0]
        info[table_name] = list(get_table_info(table_name, lat, lng, side_length, max_iter))
    return info
