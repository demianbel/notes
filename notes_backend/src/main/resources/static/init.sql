CREATE USER 'notes'@'localhost' IDENTIFIED BY 'notes';
CREATE DATABASE notes DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_bin;
GRANT ALL PRIVILEGES ON notes.* TO 'notes'@'localhost' IDENTIFIED BY 'notes';
