from django.db import models

# Create your models here.
class Driver(models.Model):
    name = models.CharField(max_length = 99)
    photo = models.CharField(max_length = 999)
    dorm = models.CharField(max_length = 99, blank=True)
    major = models.CharField(max_length = 99, blank=True)
    car_model_url = models.CharField(max_length = 99)
    phone = models.CharField(max_length = 99)
    distance = models.DecimalField(max_digits = 4, decimal_places = 3)
    dest_distance = models.DecimalField(max_digits = 4, decimal_places = 3)
    is_visible = models.BooleanField()


class RideRequest(models.Model):
    latitude = models.DecimalField(max_digits = 8, decimal_places = 5)
    longitude = models.DecimalField(max_digits = 8, decimal_places = 5)
    user = models.ForeignKey('Driver')
