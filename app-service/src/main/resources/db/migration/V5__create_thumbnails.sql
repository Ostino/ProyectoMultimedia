CREATE TABLE thumbnails (
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    file_id      UUID         NOT NULL REFERENCES media_files(id) ON DELETE CASCADE,
    label        VARCHAR(50)  NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    CONSTRAINT uq_thumbnails_file_label UNIQUE (file_id, label)
);

CREATE INDEX idx_thumbnails_file_id ON thumbnails(file_id);
