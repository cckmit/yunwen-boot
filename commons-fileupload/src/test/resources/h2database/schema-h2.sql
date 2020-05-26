DROP TABLE IF EXISTS attachment;
CREATE TABLE attachment
(
    id           bigint(20)   NOT NULL,
    content      longblob,
    content_type varchar(100) NOT NULL,
    file_name    varchar(255) NOT NULL,
    size         bigint(20)   NOT NULL,
    upload_time  datetime DEFAULT NULL,
    PRIMARY KEY (id)
)
