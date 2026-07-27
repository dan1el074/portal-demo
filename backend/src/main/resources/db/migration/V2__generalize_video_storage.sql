ALTER TABLE tb_stepflow_video RENAME TO tb_video;
ALTER TABLE tb_video RENAME COLUMN bunny_video_id TO provider_video_id;
ALTER TABLE tb_video RENAME COLUMN view_url TO playback_url;
ALTER TABLE tb_video ADD COLUMN provider VARCHAR(32) DEFAULT 'BUNNY' NOT NULL;
