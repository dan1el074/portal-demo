ALTER TABLE tb_signature
    ADD COLUMN signed_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE tb_memorando
    ADD COLUMN search_vector TSVECTOR;

UPDATE tb_memorando
SET search_vector = to_tsvector(
    'portuguese'::regconfig,
    COALESCE(client, '') || ' ' || COALESCE(title, '') || ' ' ||
    COALESCE(description, '') || ' ' || COALESCE(reason, '') || ' ' ||
    COALESCE(array_to_string(items, ' '), '')
);

CREATE FUNCTION update_memorando_search_vector()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $function$
BEGIN
    NEW.search_vector := to_tsvector(
        'portuguese'::regconfig,
        COALESCE(NEW.client, '') || ' ' || COALESCE(NEW.title, '') || ' ' ||
        COALESCE(NEW.description, '') || ' ' || COALESCE(NEW.reason, '') || ' ' ||
        COALESCE(array_to_string(NEW.items, ' '), '')
    );
    RETURN NEW;
END;
$function$;

CREATE TRIGGER trg_memorando_search_vector
BEFORE INSERT OR UPDATE OF client, title, description, reason, items
ON tb_memorando
FOR EACH ROW
EXECUTE FUNCTION update_memorando_search_vector();

ALTER TABLE tb_memorando
    ALTER COLUMN search_vector SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_memorando_full_text
ON tb_memorando USING GIN (search_vector);
