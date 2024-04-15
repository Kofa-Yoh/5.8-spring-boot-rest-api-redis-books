INSERT INTO category(id, name) VALUES (1, 'fiction'); select nextval('category_seq');
INSERT INTO category(id, name) VALUES (2, 'fiction'); select nextval('category_seq');
INSERT INTO category(id, name) VALUES (3, 'biography'); select nextval('category_seq');

INSERT INTO book(id, title, author, category_id) VALUES (1, '1984', 'George Orwell', 1); select nextval('book_seq');
INSERT INTO book(id, title, author, category_id) VALUES (2, 'The Great Gatsby', 'F. Scott Fitzgerald', 2); select nextval('book_seq');
INSERT INTO book(id, title, author, category_id) VALUES (3, 'Steve Jobs', 'Walter Isaacson', 3); select nextval('book_seq');