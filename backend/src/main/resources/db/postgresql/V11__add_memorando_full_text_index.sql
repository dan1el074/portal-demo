ALTER TABLE tb_signature
    ADD COLUMN signed_at TIMESTAMP WITHOUT TIME ZONE;

CREATE INDEX IF NOT EXISTS idx_memorando_full_text
ON tb_memorando USING GIN (
    to_tsvector(
        'portuguese',
        COALESCE(client, '') || ' ' || COALESCE(title, '') || ' ' ||
        COALESCE(description, '') || ' ' || COALESCE(reason, '') || ' ' ||
        COALESCE(array_to_string(items, ' '), '')
    )
);
