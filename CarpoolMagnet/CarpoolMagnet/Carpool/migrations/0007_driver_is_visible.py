# -*- coding: utf-8 -*-
# Generated by Django 1.11 on 2017-04-23 16:41
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('Carpool', '0006_auto_20170423_1528'),
    ]

    operations = [
        migrations.AddField(
            model_name='driver',
            name='is_visible',
            field=models.BooleanField(default=0),
            preserve_default=False,
        ),
    ]