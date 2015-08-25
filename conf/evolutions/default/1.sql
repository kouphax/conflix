# Videos schema

# --- !Ups

CREATE TABLE video (
    id int(20) NOT NULL AUTO_INCREMENT,
    title varchar(255) NOT NULL,
    tags array NOT NULL,
    last_update TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (id)
);

INSERT INTO video(title, tags) VALUES ('a talk', ('a', 'b', 'c'));
INSERT INTO video(title, tags) VALUES ('b talk', ('b', 'c', 'd'));
INSERT INTO video(title, tags) VALUES ('c talk', ('c', 'e', 'f'));

# --- !Downs

DROP TABLE video;