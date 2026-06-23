CREATE TABLE tags (
    id       UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name     VARCHAR(100) NOT NULL,
    owner_id UUID         NOT NULL,
    CONSTRAINT uq_tags_name_owner UNIQUE (name, owner_id)
);
CREATE INDEX idx_tags_owner_id ON tags(owner_id);

CREATE TABLE file_tags (
    file_id UUID NOT NULL REFERENCES media_files(id) ON DELETE CASCADE,
    tag_id  UUID NOT NULL REFERENCES tags(id)        ON DELETE CASCADE,
    PRIMARY KEY (file_id, tag_id)
);
