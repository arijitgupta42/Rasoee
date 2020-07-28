# Rasoee
***The website for the app which identifies the food in an uploaded photograph***

## Python setup
You will need to download and install the following python packages.
1. Pytorch : PyTorch is the machine learning library that was used for this website.
``` 
pip install pytorch 
```
or on your anaconda prompt using 
```
conda install -c pytorch pytorch
```

2. Torchvision : It will be used to load models and important datasets. 
You can install it in the command line prompt using
```
pip install torchvision
```
or on your anaconda prompt using 
```
conda install -c pytorch torchvision
```

3. Pillow : It will be used to interact with image files and for preprocessing.
You can install it in the command line prompt using
```
pip install Pillow
```
or on your anaconda prompt using 
```
conda install -c anaconda pillow
```

## Django setup
The entire website was built using django. Django can be very easily installed on your system using the command line. Type the following in the command line
```
py -m pip install Django
```
You can check whether django was succefully installed or not by runnning the ```django-admin``` command on your cli. If django was installed correctly then this should give you a list of all commands.

## Website setup
Download the code in the project and make sure that you have installed django and all the important python packages mentioned above.

Open up a command line interface and then navigate to folder where the project was downloaded.

Run the following commands in your command line to make sure that there are no unwanted changes in the project
```
python manage.py makemigrations
python manage.py migrate
```
The result of the following commands should ideally produce no changes.

To host the website on your system so that it can be accessed locally, run the following command on your cli.
```
python manage.py runserver
```

The website will now be accessible locally. Open any web browser and enter the address given by the previous command, this will bring you to the home of the website.

Upload photographs and have fun
