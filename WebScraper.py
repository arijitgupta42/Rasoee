from bing_image_downloader import downloader

file = open('Dishes.txt', 'r')
queries = file.read().splitlines()

for query in queries:
    downloader.download(query.lower()+' images', limit=1000,
                        adult_filter_off=True, force_replace=False)
