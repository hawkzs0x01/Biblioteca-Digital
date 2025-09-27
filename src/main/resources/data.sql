-- Script SQL definitivo
-- Todos os livros são cadastrados com estoque disponível.

DELETE FROM emprestimo WHERE id > 0;
DELETE FROM livro WHERE id > 0;
DELETE FROM categoria WHERE id > 0;
DELETE FROM usuario WHERE id > 0;

INSERT INTO categoria (nome) VALUES
('Ficção'), ('Literatura Clássica'), ('Fantasia'), ('Ficção Científica'),
('Romance'), ('Literatura Brasileira'), ('Autoajuda'), ('História');


INSERT INTO livro (titulo, autor, ano_publicacao, isbn, quantidade_disponivel, url_capa, categoria_id) VALUES
('A Revolução dos Bichos', 'George Orwell', 1945, '978-85-359-0931-4', 5, 'https://picsum.photos/seed/1/200/300', 2),
('1984', 'George Orwell', 1949, '978-85-359-0825-6', 3, 'https://picsum.photos/seed/2/200/300', 2),
('O Senhor dos Anéis: A Sociedade do Anel', 'J.R.R. Tolkien', 1954, '978-85-359-0746-4', 4, 'https://picsum.photos/seed/3/200/300', 3),
('Duna', 'Frank Herbert', 1965, '978-85-7827-023-7', 2, 'https://picsum.photos/seed/4/200/300', 4),
('O Hobbit', 'J.R.R. Tolkien', 1937, '978-85-359-1707-4', 6, 'https://picsum.photos/seed/5/200/300', 3),
('Dom Casmurro', 'Machado de Assis', 1899, '978-85-04-01234-5', 3, 'https://picsum.photos/seed/6/200/300', 6),
('O Cortiço', 'Aluísio Azevedo', 1890, '978-85-16-05678-9', 2, 'https://picsum.photos/seed/7/200/300', 6),
('Iracema', 'José de Alencar', 1865, '978-85-08-09876-5', 4, 'https://picsum.photos/seed/8/200/300', 6),
('O Alquimista', 'Paulo Coelho', 1988, '978-85-325-1234-7', 7, 'https://picsum.photos/seed/9/200/300', 7),
('Capitães da Areia', 'Jorge Amado', 1937, '978-85-254-5678-1', 3, 'https://picsum.photos/seed/10/200/300', 6),
('Orgulho e Preconceito', 'Jane Austen', 1813, '978-85-254-1234-8', 4, 'https://picsum.photos/seed/11/200/300', 5),
('O Pequeno Príncipe', 'Antoine de Saint-Exupéry', 1943, '978-85-325-5678-2', 5, 'https://picsum.photos/seed/12/200/300', 1);

