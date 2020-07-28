import torchvision.transforms as transforms
from PIL import Image
import torch
import torch.nn as nn
from django.utils.translation import gettext as _

def preprocess(img):
	data_transforms = transforms.Compose([transforms.Resize(512),
        			  transforms.CenterCrop(448),
        			  transforms.ToTensor(),
        	          transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])])
	image = Image.open(img)
	image = data_transforms(image)
	return image

def run_model(img, path, class_names):
	preds = []
	device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
	model = torch.hub.load('pytorch/vision:v0.6.0', 'mobilenet_v2', pretrained=True)
	ftrs = model.classifier[1].in_features
	model.classifier[1] = nn.Linear(ftrs, 184)
	model = model.to(device)
	model.load_state_dict(torch.load(path, map_location=torch.device('cpu')))
	#model.load_state_dict(torch.load("./Model Weights/food_classification_mobilenetv2_80%.pth", map_location=torch.device('cpu')))
	model.eval()
	output = model(img[None, ...])
	_,preds = torch.max(output, 1)
	print(preds[0])
	text = class_names[preds[0]]
	#text = 'hi'
	return text