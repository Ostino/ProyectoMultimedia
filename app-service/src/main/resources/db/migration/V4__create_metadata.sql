CREATE TABLE metadata (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    file_id          UUID         NOT NULL UNIQUE REFERENCES media_files(id) ON DELETE CASCADE,
    width            INTEGER,
    height           INTEGER,
    duration_seconds NUMERIC(12,3),
    camera_make      VARCHAR(100),
    camera_model     VARCHAR(100),
    taken_at         TIMESTAMP,
    video_codec      VARCHAR(50),
    audio_codec      VARCHAR(50),
    artist           VARCHAR(255),
    album            VARCHAR(255),
    page_count       INTEGER,
    raw              JSONB
);

CREATE INDEX idx_metadata_file_id ON metadata(file_id);
