CREATE TABLE posts (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       text TEXT NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE post_tags (
                           post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
                           tag VARCHAR(100) NOT NULL,
                           PRIMARY KEY (post_id, tag)
);