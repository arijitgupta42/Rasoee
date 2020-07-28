from django.shortcuts import render
from django.shortcuts import render
from .forms import ImageForm
from shortcuts import preprocess, run_model
import os

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
