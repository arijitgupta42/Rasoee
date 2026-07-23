<img align="left" width="64" height="64" src="https://github.com/arijitgupta42/Rasoee/blob/master/icon.svg" alt="Rasoee app icon">


# Rasoee
> **PyTorch Global Summer Hackathon 2020 Second Prize Winner** — Web/Mobile category
> ([announcement](https://pytorch.org/blog/announcing-the-winners-of-the-2020-global-pytorch-summer-hackathon/) · [write-up in the PyTorch publication](https://medium.com/pytorch/rasoee-a-picture-is-worth-a-thousand-ingredients-82576790a7bc))

Identifies 308 dishes from a photo and returns ingredients and a recipe link.
EfficientNet-B2 (9.2M params) fine-tuned to 91.3% on a dataset we scraped and cleaned
ourselves, then TorchScript-compiled for on-device inference in the Android app.

### Use Cases
1. The App will take images as input and output the name of the dish to the user. It will also list the ingredients to a recipe and the link to the original recipe.</br>
2. User can also choose from a list of cuisines and describe the taste and/or method of preparation of dish, and site will return matching dishes from pre existing list.

#### [List](./Dishes.txt) of 308 currently identifiable dishes

### Deployments
1. The website can be found [here](https://ag42homeserver.tail51dcd7.ts.net:8443/)
2. The Mobile App can be downloaded from [here](https://github.com/ameyalaad/Rasoee/releases/download/v1.0/rasoee.apk)


### Resources
1. https://www.kaggle.com/synysterjeet/food-classification (Food images divided by type of dish in 308 classes, noisy dataset scraped from the web)
2. https://www.tarladalal.com/RecipeCategories.aspx (List of dishes divided by cuisine with description based on taste and method of preparation)
