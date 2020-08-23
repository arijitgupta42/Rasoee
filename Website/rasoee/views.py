from django.shortcuts import render
from django.shortcuts import render
from .forms import ImageForm
from .forms import SearchForm
import os
import pandas as pd
import numpy as np
import urllib.request
import urllib.parse
import requests
import json
import torchvision.transforms as transforms
from PIL import Image
import torch
import torch.nn as nn
from django.utils.translation import gettext as _

def preprocess(img):
  data_transforms = transforms.Compose([transforms.Resize(256),
                transforms.CenterCrop(224),
                transforms.ToTensor(),
                    transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])])
  image = Image.open(img)
  image = data_transforms(image)
  return image

def run_model(img, path, class_names):
  preds = []
  device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
  model = torch.load(path, map_location=torch.device('cpu'))
  model.eval()
  output = model(img[None, ...])
  _,preds = torch.max(output, 1)
  print(preds[0])
  text = class_names[preds[0]]
  #text = 'hi'
  return text

def get_ingredients(inp):
    req = "http://www.recipepuppy.com/api/?q={}".format(inp)
    try:
      x = requests.get(req).json()
    except:
      return ['We found no recipes matching your needs']
    y = x['results'][0]
    title = " ".join(y['title'].split())
    link = y['href']
    ingredients = y['ingredients'].split(',')
    return ingredients, link

def suggest(cuisine, description):
  #cuisine = input() # drowdown input instead of input()
  
  path = os.path.dirname(os.path.dirname(os.path.abspath(__file__))) + '\\rasoee'
  cwd = os.getcwd()
  if (cuisine.lower() == 'indian'):
    filename = '/app/rasoee/data/indian_dishes.csv'
    df = pd.read_csv(filename, encoding='cp1252')
  elif (cuisine.lower() == 'thai'):
    filename = '/app/rasoee/data/thai_dishes.csv'
    df = pd.read_csv(filename, encoding='cp1252')
  elif (cuisine.lower() == 'french'):
    filename = '/app/rasoee/data/french_dishes.csv'
    df = pd.read_csv(filename, encoding='cp1252')
  elif (cuisine.lower() == 'chinese'):
    filename = '/app/rasoee/data/chinese_dishes.csv'
    df = pd.read_csv(filename, encoding='cp1252')
  elif (cuisine.lower() == 'american'):
    filename = '/app/rasoee/data/american_dishes.csv'
    df = pd.read_csv(filename, encoding='cp1252')
  elif (cuisine.lower() == 'italian'):
    filename = '/app/rasoee/data/italian_dishes.csv'
    df = pd.read_csv(filename, encoding='cp1252')
  elif (cuisine.lower() == 'lebanese'):
    filename = '/app/rasoee/data/lebanese_dishes.csv'
    df = pd.read_csv(filename, encoding='cp1252')

  df = df.fillna("None")
  df.head(10)

  text_file_name = '/app/rasoee/Keywords.txt'
  text_file = open(text_file_name, "r")
  lines = text_file.read().split('\n')
  lines = [w.lower() for w in lines]

 #print("What sort of dish would you like to eat?\n")

  userinp = description.split() # replace input() with the variable
  size = len(userinp) 
  idx_list = [idx + 1 for idx, val in
              enumerate(userinp) if val == 'or'] 

  try:
    res = [userinp[i: j] for i, j in
          zip([0] + idx_list, idx_list + 
          ([size] if idx_list[-1] != size else []))] 
  except:
    res = [userinp]
    
  queries = []
  for x in res:
    query = [w.lower() for w in x if w.lower() in lines]
    queries.append(query)

  req_dishes = []
  
  for query in queries:
    try:
      set_dishes = np.unique(list(df[df['summary'] == query[0]]['title']))
      for q in query:
        dishes = list(np.unique(df[df['summary'] == q]['title']))
        set_dishes = [x for x in set_dishes if x in dishes]
      
      req_dishes+=set_dishes
    except:
      return ['We found no dishes in our collection matching your needs']
  h = ' or '.join(['+'.join(x) for x in queries])
  #print(("\nWe got {} types of dish requirement(s), {}, and found {} dishes that match your description").format(len(queries), h, len(req_dishes)))
  return req_dishes



def image_upload_view(request):
    """Process images uploaded by users"""
    if request.method == 'POST':
        form = ImageForm(request.POST, request.FILES)
        if form.is_valid():
            form.save()
            
            # Get the current instance object to display in the template
            img_obj = form.instance
            img = preprocess(img_obj.image)
            path = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
            path += "\\rasoee"
            filename1 = ('/app/rasoee/classes.txt')
            filename2 = ('/app/rasoee/model.pth')
            with open(filename1) as f:
                lines = f.readlines()
            #with open(filename2) as f:
            #    weights = f.readlines()
            text = run_model(img, filename2, lines)
            query_string = "https://www.youtube.com/results?search_query=" + "how to make " + text 
            try:
              ingredients, link = get_ingredients(text)
              return render(request, 'upload.html', {'form': form, 'img_obj': img_obj, 'text': text, 'video_link': query_string, 'ingredients': ingredients, 'recipe_link': link})
            except:
              ingredients = get_ingredients(text)
              return render(request, 'upload.html', {'form': form, 'img_obj': img_obj, 'text': text, 'video_link': query_string, 'ingredients': ingredients})
            
      
            
    else:
        form = ImageForm()
    return render(request, 'upload.html', {'form': form})

def suggest_view(request):
    if request.method == 'POST':
        form = SearchForm(request.POST)
        if form.is_valid():
            cuisine = form.cleaned_data.get("cuisine")
            description = form.cleaned_data.get("description")
            suggestions = suggest(cuisine, description)
            return render(request, 'results.html', {'form': form, 'suggestions': suggestions})
    else:
        form = SearchForm()
    return render(request, 'search.html', {'form': form})

def home_view(request):
  return render(request, 'home.html', {})
    

