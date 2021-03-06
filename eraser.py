import os

#replace dir_name with the current directory where the app.py is saving the images
dir_name = "D:/Projects/Flask"
test = os.listdir(dir_name)

for item in test:
    if item.endswith(".jpg"):
        os.remove(os.path.join(dir_name, item))


lines_seen = set() # holds lines already seen
outfile = open("users1.txt", "w")
for line in open("users.txt", "r"):
    if line not in lines_seen: # not a duplicate
        outfile.write(line)
        lines_seen.add(line)
outfile.close()

lines_seen = set() # holds lines already seen
outfile = open("usernames1.txt", "w")
for line in open("usernames.txt", "r"):
    if line not in lines_seen: # not a duplicate
        outfile.write(line)
        lines_seen.add(line)
outfile.close()

#these two blocks removes the duplicates in users.txt and usernames1.txt which may come
#due to multiple requests from the user
os.rename("users.txt", 'temp')
os.rename("users1.txt", "users.txt")
os.rename('temp', "users1.txt")

os.rename("usernames1.txt", 'temp')
os.rename("usernames.txt", "usernames1.txt")
os.rename('temp', "usernames.txt")