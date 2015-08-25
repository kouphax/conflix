# Users schema

# --- !Ups

CREATE TABLE user (
    id int(20) NOT NULL AUTO_INCREMENT,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    last_update TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (id)
);

INSERT INTO user(email, password) VALUES ('james', '$2a$04$qnEKdOe4uK0nNS3VYowMOOg35CvVEVtysOroTAoxRBppYqt5mPTw.')

# --- !Downs

DROP TABLE user;