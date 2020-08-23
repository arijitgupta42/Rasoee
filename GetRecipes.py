import requests
import json

print("What dish do you want to search ingredients for")
inp = input()
req = "http://www.recipepuppy.com/api/?q={}".format(inp)
x = requests.get(req).json()

for y in x['results'][:5]:
    title = " ".join(y['title'].split())
    link = y['href']
    ingredients = y['ingredients'].split(',')
    print(title, link ,ingredients,'\n')