# IPPG
Instagram Profile Picture Grabber

An android app to fetch the profile picture of a user from instagram.

<b>app.py</b> is the main python script to run the flask server which fetches the profile picture of the user.  
<b>usernames.txt</b> and <b>usernames1.txt</b> stores the profile picture of the user in a <i>base64</i> string.  
<b>users.txt</b> and <b>users1.txt</b> stores the names of the profiles whos profiles pictures are saved in usernames.txt and usernames1.txt.  
<b>eraser.py</b> script removes the images of the users from the current directory and cleans out the files users.txt and usernames.txt incase some duplicate items may be present there.
