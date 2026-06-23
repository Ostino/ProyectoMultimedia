CREATE TABLE media_files (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    original_name VARCHAR(255) NOT NULL,
    storage_path  VARCHAR(500) NOT NULL,
    mime_type     VARCHAR(127) NOT NULL,
    file_type     VARCHAR(20)  NOT NULL CONSTRAINT chk_file_type  CHECK (file_type IN ('IMAGE','VIDEO','AUDIO','DOCUMENT')),
    size_bytes    BIGINT       NOT NULL,
    folder_id     UUID         REFERENCES folders(id) ON DELETE SET NULL,
    owner_id      UUID         NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'PROCESSING' CONSTRAINT chk_status CHECK (status IN ('PROCESSING','READY','FAILED')),
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_media_files_owner_id  ON media_files(owner_id);
CREATE INDEX idx_media_files_folder_id ON media_files(folder_id);
