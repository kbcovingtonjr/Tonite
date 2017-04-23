# -*- coding: utf-8 -*-
# Generated by Django 1.11 on 2017-04-23 15:28
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('Carpool', '0005_auto_20170423_1522'),
    ]

    operations = [
        migrations.AddField(
            model_name='driver',
            name='dest_distance',
            field=models.DecimalField(decimal_places=3, default=1, max_digits=4),
            preserve_default=False,
        ),
        migrations.AddField(
            model_name='driver',
            name='distance',
            field=models.DecimalField(decimal_places=3, default=1, max_digits=4),
            preserve_default=False,
        ),
    ]
