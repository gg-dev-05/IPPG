
import flask
from flask import request as rq
from flask import send_file
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
from PIL import Image
import requests
import time
from io import BytesIO
import urllib.request
import sys
import base64

app = flask.Flask(__name__)

@app.route('/', methods=['GET', 'POST'])
def handle_request():
	return "connected"

@app.route('/app', methods=['POST'])
def func():
	req = rq.get_json()
	username = req['username']
	
	url = "https://www.instagram.com/" + username + "/"
	options = webdriver.ChromeOptions()
	options.add_argument('headless')
	options.add_argument('--ignore-certificate-errors')
	options.add_argument('--ignore-ssl-errors')
	print("Opening Webpage", end = " ")
	browser = webdriver.Chrome(ChromeDriverManager().install(), options = options)
	print("Web Page Opened")



	browser.get(url)
	print("Locating Image", end = " ")
	image = browser.find_elements_by_class_name("_6q-tv")
	print("Located Image")

	if(image == []):
		image = browser.find_elements_by_class_name("be6sR")

	if(image == []):
		return "Not"

	else:
		print(" Opening Image ")
		for img in image:
			url = img.get_attribute("src")
			print(url)
			# response = requests.get(url)
			# out = Image.open(BytesIO(response.content))
			# out.show()
			image_of_user = "{}.jpg".format(username)
			urllib.request.urlretrieve(url, image_of_user)
			with open(image_of_user,"rb") as imageFile:
				str = base64.b64encode(imageFile.read())
				return str




app.run(host="0.0.0.0", port=5000, debug=True)