from django.shortcuts import render
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
import json
from .models import *

# Create your views here.
@csrf_exempt
def index(request):
    print(request.POST)
    drivers = Driver.objects.all()
    returnList = []
    for driver in drivers:
        returnList.append({'name': driver.name, 'photo': driver.photo, 'dorm': driver.dorm, 'major': driver.major, 'phone': driver.phone, 'model': driver.car_model_url, 'dist': float(driver.distance), 'dest_dist': float(driver.dest_distance), 'visible': driver.is_visible})
    response = HttpResponse(json.dumps(returnList))
    response['Access-Control-Allow-Origin'] = "*"
    return response
