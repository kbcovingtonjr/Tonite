# -*- coding: utf-8 -*-
# Generated by Django 1.11 on 2017-04-23 15:22
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('Carpool', '0004_driver_phone'),
    ]

    operations = [
        migrations.AlterField(
            model_name='driver',
            name='dorm',
            field=models.CharField(blank=True, max_length=99),
        ),
        migrations.AlterField(
            model_name='driver',
            name='major',
            field=models.CharField(blank=True, max_length=99),
        ),
        migrations.AlterField(
            model_name='driver',
            name='photo',
            field=models.CharField(max_length=999),
        ),
    ]