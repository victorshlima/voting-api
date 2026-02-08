-- ====================================================================================================
-- Remove associate table and FK. Votes now store associate_id directly from request.
-- ====================================================================================================
ALTER TABLE vote DROP CONSTRAINT fk_vote_associate;

DROP TABLE associate;

COMMENT ON COLUMN vote.associate_id IS
'Associate identifier (UUID) sent in the request.';
