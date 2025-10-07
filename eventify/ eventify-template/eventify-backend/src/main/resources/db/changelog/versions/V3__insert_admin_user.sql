
INSERT INTO users (email, password_hash, role)
SELECT 'admin@eventify.com', '$2a$10$YourRealHashHere', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@eventify.com');


INSERT INTO users (email, password_hash, role)
SELECT 'user@example.com', '$2a$10$YourRealHashHere', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'user@example.com');