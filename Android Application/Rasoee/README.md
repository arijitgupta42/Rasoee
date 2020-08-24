## Information (Download [here](https://github.com/ameyalaad/Rasoee/releases/download/v1.0/rasoee.apk))
This is the android application "Rasoee" for the PyTorch Summer Hackathon 2020.

This application can recognize 308 types of food, using the camera, or the gallery. After identifying the food item, it uses [RecipePuppy](http://www.recipepuppy.com/about/api/)'s API in order to fetch up to 10 recipes and display them. 
Furthermore, ingredients about the recipe can be found by tapping on the respective recipe, along with a link to the recipe.

This application has been written in Kotlin, following the MVVM architecture. It uses the Moshi, Retrofit, Glide, and TorchScript modules. 
The Retrofit, Moshu, and Glide modules help to make the api request, parse the data, and get the thumbnail respectively.
The torchscript module is used to make predictions on the input image and contributes the most to the size of the apk.

The application can be downloaded at [rasoee.apk](https://github.com/ameyalaad/Rasoee/releases/download/v1.0/rasoee.apk)

### Attributions
<div>The app icon is made by <a href="https://www.flaticon.com/free-icon/breakfast_926255" title="photo3idea_studio">photo3idea_studio</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>
