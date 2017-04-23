from __future__ import unicode_literals
from django.shortcuts import render
from django.http import HttpResponse
import json

# Create your views here.
def geteventdata(request):
    testjson = { 'foo': 'bar', 'event' : 'location'}

    jsonObject = json.dumps(testjson)
    
    return HttpResponse(jsonObject)
