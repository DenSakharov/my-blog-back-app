ALTER TABLE comments
DROP CONSTRAINT IF EXISTS comments_post_id_fkey;

ALTER TABLE comments
    ADD CONSTRAINT comments_post_id_fkey
        FOREIGN KEY (post_id)
            REFERENCES posts(id)
            ON DELETE CASCADE;