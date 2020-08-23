from django import forms
from .models import Image


#cuisine_choices = ['Indian', 'Thai', 'French', 'Chinese', 'American', 'Italian', 'Lebanese']
cuisine_choices = (
				('indian', 'Indian'),
				('thai', 'Thai'),
				('french', 'French'),
				('chinese', 'Chinese'),
				('american', 'American'),
				('italian', 'Italian'),
				('lebanese', 'Lebanese')
				)

class ImageForm(forms.ModelForm):
    """Form for the image model"""
    class Meta:
        model = Image
        fields = ('title', 'image')

class SearchForm(forms.Form):
	#cuisine = forms.CharField(label='Select a cuisine', widget=forms.Select(cuisine_choices))
	cuisine = forms.ChoiceField(choices = cuisine_choices) 
	description = forms.CharField(label='Describe the dish you would like to eat')

class HomeForm(forms.Form):
	name = forms.CharField(label='Enter your name please')