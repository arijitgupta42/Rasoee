from django.shortcuts import render
from django.shortcuts import render
from .forms import ImageForm
import os
import torchvision.transforms as transforms
from PIL import Image
import torch

def preprocess(img):
	data_transforms = transforms.Compose([transforms.Resize(512),
        			  transforms.CenterCrop(448),
        			  transforms.ToTensor(),
        	          transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])])
	img = Image.open("./Img1.jpeg")
	img = data_transforms(img)
	return img

def run_model(img, path, class_names):
	model = torch.hub.load('pytorch/vision:v0.6.0', 'mobilenet_v2', pretrained=True)
	model.load_state_dict(torch.load(path))
	model.eval()
	output = model(img[None, ...])
	preds = torch.max(output, 1)
	text = class_names[preds[0]]
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
            #with open(filename2) as f:
            #    weights = f.readlines()
            text = run_model(img, filename2, lines)
            return render(request, 'upload.html', {'form': form, 'img_obj': img_obj, 'text': text})
    else:
        form = ImageForm()
    return render(request, 'upload.html', {'form': form})
