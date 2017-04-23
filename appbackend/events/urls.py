from django.conf.urls import url

from . import views

urlpatterns = {
    url(r'^appdata/$', views.geteventdata),
}
