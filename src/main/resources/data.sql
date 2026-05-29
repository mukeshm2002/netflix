INSERT INTO categories (name) VALUES ('Trending Now') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Action Movies') ON CONFLICT (name) DO NOTHING;