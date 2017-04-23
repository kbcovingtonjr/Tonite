from __future__ import unicode_literals
import db_fetch
from django.shortcuts import render
from django.http import HttpResponse
from django.http import HttpRequest
import json

# Create your views here.
def geteventdata(request):
    print("printing request.body and json.loads(request.body)")
    print(request.META)

    latitude = float(request.META['HTTP_LATITUDE'])
    longitude = float(request.META['HTTP_LONGITUDE'])

    testjson = db_fetch.get_all_info(latitude, longitude, 10, 10)

    jsonObject = json.dumps(testjson)
    
    return HttpResponse(jsonObject)
