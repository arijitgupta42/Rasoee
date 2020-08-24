import warnings
import os
from tqdm import tqdm
from PIL import Image
import split_folders

size = 512, 512

warnings.filterwarnings("ignore")

split_folders.ratio('./dataset_old', output="./dataset", seed=1337, ratio=(.8, .1, .1)) # default values


for folder in os.listdir('./dataset'):
    for directories in tqdm(os.listdir('./dataset/' + folder)):
        for file in os.listdir('./dataset/' + folder + '/' + directories):
            img = './dataset/' + folder + '/' + directories + '/' + file
            # print(img)
            im = Image.open(img)
            try:
                rgb_im = im.convert('RGB')
                if im.size != size:
                    # print(im.size)
                    im_resized = im.resize(size, Image.ANTIALIAS)
                    rgb_im = im_resized.convert('RGB')
                    try:
                        rgb_im.save('./dataset/' + folder + '/' +
                                    directories + '/' + file[:-4] + '.jpg', dpi=(72, 72))
                    except:
                        os.makedirs('./dataset/' + folder + '/' + directories)
                        rgb_im.save('./dataset/' + folder + '/' +
                                    directories + '/' + file[:-4] + '.jpg', dpi=(72, 72))
            except:
                im.close()
                os.remove(img)

            # im_resized.show()
