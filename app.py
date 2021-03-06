
import flask
from flask import request as rq
from flask import send_file
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
from PIL import Image
import requests
import time
import os
from io import BytesIO
import urllib.request
import sys
import base64
import re



app = flask.Flask(__name__)

#to check the connection
@app.route('/', methods=['GET'])
def handle_request():
	return "connected"

#these are the loading strings which are shown on the user screen while webdriver is running
@app.route('/loading', methods=['GET'])
def loader():
	loadingStrings = [
		"Sending Data to Server",
		"Searching in saved Files",
		"Running Flask App",
		"Accessing DataBase",
		"Verifying Username",
		"Accessing Instagram",
		"Loading Webpage",
		"Searching Username",
		"Searching For Image",
		"Getting Image",
		"Bringing Image from Server",
		"Downloading Image",
		"Please Wait"
	]
	loading = ",".join(loadingStrings)
	return loading

@app.route('/dots', methods=['GET'])
def dots():
	loadingStrings = [
		".",
		"..",
		"...",
		"....",
		"....."
	]
	dots = ",".join(loadingStrings)
	return dots

#this function deletes the username from the users.txt and usernames.txt files
def updateFunc(username):
	# req = rq.get_json()
	# username = req['username']
	username1 = "{}\n".format(username)
	with open('users.txt','r+') as fin:
		with open('out.txt','w+') as fout:
			for line in fin:
				fout.write(line.replace(username1,""))

	os.rename("users.txt", 'temp')
	os.rename("out.txt", "users.txt")
	os.rename('temp', "out.txt")

	with open('usernames.txt', 'r+') as fin:
		with open('out.txt', 'w+') as fout:
			for line in fin:
				name,var = line.partition("=")[::2]
				if(name == username+" "):
					fout.write("")
				else:
					fout.write(line)

	os.rename("usernames.txt", 'temp')
	os.rename("out.txt", "usernames.txt")
	os.rename('temp', "out.txt")

	os.remove('out.txt')


#the main method
@app.route('/app', methods=['POST'])
def func():



	req = rq.get_json()
	username = req['username']
	update = req['update']
	if(update):
		updateFunc(username)

	username1 = "{}\n".format(username)
	isSaved = False

	# username1 = "{}".format(username)
	if(not update):
		with open('users.txt') as f:
			result = re.findall(username1, f.read(), flags=re.IGNORECASE)
			if(len(result) > 0):
				isSaved = True

	if(isSaved):
		print("Sending From Saved Data")
		obj = {}
		with open("usernames.txt") as myfile:
			for line in myfile:
				name,var = line.partition("=")[::2]
				if name.strip() == username:
					return var.replace(" ","")
				# 		myvars = {}
# with open("namelist.txt") as myfile:
#     for line in myfile:
#         name, var = line.partition("=")[::2]
#         myvars[name.strip()] = float(var)

	else:
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
					stri = base64.b64encode(imageFile.read())

					file = open("usernames.txt","a+")
					data = "{} = {}".format(username,stri)
					file.write(data)
					file.write("\n")
					file.close()
					file = open("users.txt","a+")
					data = "{}".format(username)
					file.write(data)
					file.write("\n")
					file.close()

					return stri





app.run(host="0.0.0.0", port=5000, debug=True,threaded=True)
