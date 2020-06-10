# --- !Ups

ALTER TABLE article_comment ADD COLUMN name varchar(255);
ALTER TABLE article_comment ADD COLUMN email varchar(255);
ALTER TABLE article_comment ADD COLUMN website varchar(255);

# --- !Downs

ALTER TABLE article_comment DROP COLUMN name;
ALTER TABLE article_comment DROP COLUMN email;
ALTER TABLE article_comment DROP COLUMN website;

