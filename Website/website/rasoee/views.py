from django.shortcuts import render
from django.shortcuts import render
from .forms import ImageForm
import os
from torchvision import transforms, models
from PIL import Image
import torch
import torch.nn as nn
from django.utils.translation import gettext as _
from efficientnet_pytorch import EfficientNet


def preprocess(img):
    data_transforms = transforms.Compose([transforms.CenterCrop(224),
                                          transforms.ToTensor(),
                                          transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])])
    image = Image.open(img)
    image = data_transforms(image)
    return image


def run_model(img, path, class_names):
    preds = []
    model = EfficientNet.from_name('efficientnet-b2')
    ftrs = model._fc.in_features
    model._fc = nn.Linear(ftrs, 308)
    model.load_state_dict(torch.load(path, map_location=torch.device('cpu')))
    model.eval()
    output = model(img[None, ...])
    _, preds = torch.max(output, 1)
    print(preds[0])
    text = class_names[preds[0]]
    #text = 'hi'
    return text


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
            filename1 = os.path.join(path, 'classes.txt')
            filename2 = os.path.join(path, 'weights.pth')
            with open(filename1) as f:
                lines = f.readlines()
            # with open(filename2) as f:
            #    weights = f.readlines()
            text = run_model(img, filename2, lines)
            return render(request, 'upload.html', {'form': form, 'img_obj': img_obj, 'text': text})
    else:
        form = ImageForm()
    return render(request, 'upload.html', {'form': form})
