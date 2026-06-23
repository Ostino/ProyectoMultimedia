CREATE TABLE folders (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(255) NOT NULL,
    parent_id  UUID         REFERENCES folders(id) ON DELETE CASCADE,
    owner_id   UUID         NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_folders_owner_id  ON folders(owner_id);
CREATE INDEX idx_folders_parent_id ON folders(parent_id);
