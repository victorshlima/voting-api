-- ====================================================================================================
-- Adds indexes to speed up read queries used by counting votes and selecting closed sessions.
-- ====================================================================================================
CREATE INDEX IF NOT EXISTS idx_voting_session_ends_at
    ON voting_session (ends_at);

CREATE INDEX IF NOT EXISTS idx_vote_session_vote
    ON vote (voting_session_id, des_vote);
