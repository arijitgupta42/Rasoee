import torchvision.transforms as transforms
from PIL import image
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

	