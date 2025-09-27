document.addEventListener('DOMContentLoaded', () => {
    const API_URL = '/api';

    // Seletores de UI
    const views = document.querySelectorAll('.view');
    const navLinks = {
        catalog: document.getElementById('nav-catalog'),
        reservations: document.getElementById('nav-reservations'),
        admin: document.getElementById('nav-admin'),
        login: document.getElementById('nav-login'),
        register: document.getElementById('nav-register'),
        logout: document.getElementById('nav-logout'),
        userGreeting: document.getElementById('nav-user-greeting'),
    };
    const activationSection = document.getElementById('activation-section');
    const activateAdminForm = document.getElementById('activate-admin-form');
    const catalogContainer = document.getElementById('catalog-container');
    const reservationsContainer = document.getElementById('reservations-container');
    const adminBookList = document.getElementById('admin-book-list');
    const categoryFilter = document.getElementById('category-filter');
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const bookModal = document.getElementById('modal-book-form');
    const bookForm = document.getElementById('book-form');
    const modalTitle = document.getElementById('modal-title');
    const btnCancelBook = document.getElementById('btn-cancel-book');
    const btnAddNewBook = document.getElementById('btn-add-new-book');

    const getLoggedInUser = () => JSON.parse(localStorage.getItem('loggedInUser'));

    const showView = (viewId) => {
        views.forEach(view => view.classList.add('hidden'));
        document.getElementById(viewId)?.classList.remove('hidden');
    };

    const updateNavbar = () => {
        const user = getLoggedInUser();
        if (user) {
            navLinks.login.classList.add('hidden');
            navLinks.register.classList.add('hidden');
            navLinks.logout.classList.remove('hidden');
            navLinks.reservations.classList.remove('hidden');
            navLinks.userGreeting.textContent = `Olá, ${user.nome.split(' ')[0]}!`;
            navLinks.userGreeting.classList.remove('hidden');
            navLinks.admin.classList.toggle('hidden', !user.isAdmin);
        } else {
            navLinks.login.classList.remove('hidden');
            navLinks.register.classList.remove('hidden');
            navLinks.logout.classList.add('hidden');
            navLinks.reservations.classList.add('hidden');
            navLinks.admin.classList.add('hidden');
            navLinks.userGreeting.classList.add('hidden');
        }
    };

    const renderCatalog = async (categoryId = 'all') => {
        const user = getLoggedInUser();
        let url = `${API_URL}/livros`;
        if (categoryId !== 'all' && categoryId) {
            url = `${API_URL}/livros/categoria/${categoryId}`;
        }
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error('Falha ao buscar livros.');
            const books = await response.json();
            catalogContainer.innerHTML = '';
            if (!books || books.length === 0) {
                catalogContainer.innerHTML = '<p>Nenhum livro encontrado.</p>';
                return;
            }
            books.forEach(book => {
                const card = document.createElement('div');
                card.className = 'book-card';
                const isAvailable = book.quantidadeDisponivel > 0;
                let reserveButtonHtml = '';
                if (user && isAvailable) {
                    reserveButtonHtml = `<button class="btn-reserve" data-book-id="${book.id}">Reservar</button>`;
                } else if (user && !isAvailable) {
                    reserveButtonHtml = `<button disabled>Indisponível</button>`;
                }
                card.innerHTML = `<img src="${book.urlCapa}" alt="Capa do livro ${book.titulo}" onerror="this.onerror=null;this.src='https.picsum.photos/200/300';"><h3>${book.titulo}</h3><p>${book.autor}</p><p class="stock ${isAvailable ? 'available' : 'unavailable'}">${isAvailable ? `${book.quantidadeDisponivel} disponíveis` : 'Indisponível'}</p>${reserveButtonHtml}`;
                catalogContainer.appendChild(card);
            });
        } catch (error) {
            console.error('Erro ao renderizar catálogo:', error);
            catalogContainer.innerHTML = `<p class="error-message">Não foi possível carregar os livros.</p>`;
        }
    };

    const renderCategoryFilter = async () => {
        try {
            const response = await fetch(`${API_URL}/categorias`);
            if (!response.ok) throw new Error();
            const categories = await response.json();
            categoryFilter.innerHTML = `<option value="all">Todas as Categorias</option>`;
            categories.forEach(cat => {
                const option = document.createElement('option');
                option.value = cat.id;
                option.textContent = cat.nome;
                categoryFilter.appendChild(option);
            });
        } catch (error) {
            categoryFilter.innerHTML = '<option value="all">Não foi possível carregar</option>';
        }
    };

    const renderReservations = async () => {
        const user = getLoggedInUser();
        if (!user) return;
        reservationsContainer.innerHTML = '';
        const response = await fetch(`${API_URL}/emprestimos/usuario/${user.id}`, { credentials: 'include' });
        if (!response.ok) {
            reservationsContainer.innerHTML = '<p>Não foi possível carregar suas reservas.</p>';
            return;
        }
        const reservations = await response.json();
        if (reservations.length === 0) {
            reservationsContainer.innerHTML = '<p>Você não tem nenhuma reserva ativa.</p>';
        } else {
            reservations.forEach(res => {
                const item = document.createElement('div');
                item.className = 'reservation-item';
                item.innerHTML = `<div><strong>${res.livro.titulo}</strong><p>Devolver até: ${new Date(res.dataDevolucaoPrevista).toLocaleDateString()}</p></div><button class="btn-return" data-loan-id="${res.id}">Devolver</button>`;
                reservationsContainer.appendChild(item);
            });
        }
    };

    const renderAdminBookList = async () => {
        try {
            const response = await fetch(`${API_URL}/livros`);
            const books = await response.json();
            adminBookList.innerHTML = ''; // Limpa a lista antes de renderizar
            books.forEach(book => {
                const item = document.createElement('div');
                item.className = 'admin-book-item';
                item.innerHTML = `
                    <span>${book.titulo} (${book.quantidadeDisponivel})</span>
                    <div>
                        <button class="btn-edit-book" data-book-id="${book.id}">Editar</button>
                        <button class="btn-delete-book danger" data-book-id="${book.id}">Excluir</button>
                    </div>`;
                adminBookList.appendChild(item);
            });
        } catch(error) {
            adminBookList.innerHTML = `<p class="error-message">Não foi possível carregar a lista de livros.</p>`;
        }
    };

    const openBookModal = async (book = null) => {
        bookForm.reset();
        const categorySelect = document.getElementById('book-category');
        try {
            const response = await fetch(`${API_URL}/categorias`);
            const categories = await response.json();
            categorySelect.innerHTML = categories.map(cat => `<option value="${cat.id}">${cat.nome}</option>`).join('');

            if (book) {
                modalTitle.textContent = 'Editar Livro';
                document.getElementById('book-id').value = book.id;
                document.getElementById('book-title').value = book.titulo;
                document.getElementById('book-author').value = book.autor;
                document.getElementById('book-year').value = book.anoPublicacao;
                document.getElementById('book-isbn').value = book.isbn;
                document.getElementById('book-cover-url').value = book.urlCapa;
                document.getElementById('book-quantity').value = book.quantidadeDisponivel;
                categorySelect.value = book.categoria.id;
            } else {
                modalTitle.textContent = 'Adicionar Novo Livro';
                document.getElementById('book-id').value = '';
            }
            bookModal.classList.remove('hidden');
        } catch (error) {
            alert('Não foi possível carregar as categorias para o formulário.');
        }
    };

    // --- EVENT LISTENERS ---

    // Navegação Principal
    navLinks.catalog.addEventListener('click', (e) => { e.preventDefault(); showView('view-catalog'); });
    navLinks.login.addEventListener('click', (e) => { e.preventDefault(); showView('view-login'); });
    navLinks.register.addEventListener('click', (e) => { e.preventDefault(); showView('view-register'); });
    navLinks.logout.addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.removeItem('loggedInUser');
        initializeApp();
    });
    navLinks.reservations.addEventListener('click', (e) => {
        e.preventDefault();
        const user = getLoggedInUser();
        showView('view-my-reservations');
        renderReservations();
        activationSection.classList.toggle('hidden', !user || user.isAdmin);
    });
    navLinks.admin.addEventListener('click', (e) => { e.preventDefault(); showView('view-admin-panel'); renderAdminBookList(); });

    // Formulários
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('login-email').value;
        const password = document.getElementById('login-password').value;
        try {
            const response = await fetch(`${API_URL}/usuarios/login`, {
                method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email: email, senha: password }),
            });
            if (!response.ok) throw new Error('Credenciais inválidas');
            const user = await response.json();
            localStorage.setItem('loggedInUser', JSON.stringify(user));
            loginForm.reset();
            initializeApp();
        } catch (error) {
            document.getElementById('login-error').textContent = 'Email ou senha inválidos.';
        }
    });

    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const usuario = {
            nome: document.getElementById('register-name').value, email: document.getElementById('register-email').value,
            telefone: document.getElementById('register-phone').value, senha: document.getElementById('register-password').value,
        };
        const response = await fetch(`${API_URL}/usuarios`, {
            method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(usuario),
        });
        if (response.ok) {
            alert('Registro realizado com sucesso! Faça o login.');
            showView('view-login');
        } else {
            document.getElementById('register-error').textContent = 'Erro ao registrar. Verifique os dados.';
        }
    });

    activateAdminForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const user = getLoggedInUser();
        const key = document.getElementById('activate-admin-key').value;
        if (!user || !key) return;
        try {
            const response = await fetch(`${API_URL}/usuarios/ativar-admin?email=${encodeURIComponent(user.email)}&chave=${encodeURIComponent(key)}`, {
                method: 'POST', credentials: 'include',
            });
            if (!response.ok) throw new Error('Chave inválida');
            alert('Permissão de administrador concedida! Por favor, faça o login novamente para aplicar as alterações.');
            localStorage.removeItem('loggedInUser');
            initializeApp();
        } catch (error) {
            alert('Chave mestra inválida.');
        }
    });

    bookForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const bookId = document.getElementById('book-id').value;
        const method = bookId ? 'PUT' : 'POST';
        const url = bookId ? `${API_URL}/livros/${bookId}` : `${API_URL}/livros`;
        const bookData = {
            titulo: document.getElementById('book-title').value, autor: document.getElementById('book-author').value,
            anoPublicacao: parseInt(document.getElementById('book-year').value), isbn: document.getElementById('book-isbn').value,
            urlCapa: document.getElementById('book-cover-url').value,
            quantidadeDisponivel: parseInt(document.getElementById('book-quantity').value),
            categoria: { id: document.getElementById('book-category').value }
        };
        try {
            const response = await fetch(url, {
                method: method, headers: { 'Content-Type': 'application/json' }, credentials: 'include', body: JSON.stringify(bookData)
            });
            if (!response.ok) throw new Error('Não foi possível salvar o livro. Verifique as permissões.');
            alert(`Livro ${bookId ? 'atualizado' : 'adicionado'} com sucesso!`);
            bookModal.classList.add('hidden');
            renderAdminBookList();
            renderCatalog();
        } catch(error) {
            alert(`Erro: ${error.message}`);
        }
    });

    // Listeners de Ações Dinâmicas (usando delegação de eventos)
    document.addEventListener('click', async (e) => {
        const user = getLoggedInUser();

        // Botão de Reservar no catálogo
        if (e.target.classList.contains('btn-reserve')) {
            if (!user) { alert('Você precisa estar logado para reservar.'); return; }
            const bookId = e.target.dataset.bookId;
            const dataDevolucao = new Date();
            dataDevolucao.setDate(dataDevolucao.getDate() + 14);
            try {
                const response = await fetch(`${API_URL}/emprestimos`, {
                    method: 'POST', headers: { 'Content-Type': 'application/json' }, credentials: 'include',
                    body: JSON.stringify({ livroId: bookId, dataDevolucao: dataDevolucao.toISOString().split('T')[0] })
                });
                if (!response.ok) {
                    const errorMsg = await response.text();
                    throw new Error(errorMsg || 'Não foi possível reservar o livro.');
                }
                alert('Livro reservado com sucesso!');
                renderCatalog(categoryFilter.value);
            } catch (error) {
                alert(`Erro: ${error.message}`);
            }
        }

        // Botão de Devolver em "Minhas Reservas"
        if (e.target.classList.contains('btn-return')) {
            if (!user) return;
            const loanId = e.target.dataset.loanId;
            if (confirm('Tem certeza que deseja devolver este livro?')) {
                const response = await fetch(`${API_URL}/emprestimos/${loanId}/devolver`, { method: 'PUT', credentials: 'include' });
                if(response.ok) {
                    alert('Livro devolvido com sucesso!');
                    renderReservations();
                } else { alert('Erro ao devolver o livro.'); }
            }
        }

        // Botão de Editar no Painel de Admin
        if (e.target.classList.contains('btn-edit-book')) {
            const bookId = e.target.dataset.bookId;
            const response = await fetch(`${API_URL}/livros/${bookId}`);
            const book = await response.json();
            openBookModal(book);
        }

        // Botão de Excluir no Painel de Admin
        if (e.target.classList.contains('btn-delete-book')) {
            const bookId = e.target.dataset.bookId;
            if (confirm('ATENÇÃO: Deseja realmente excluir este livro do acervo?')) {
                const response = await fetch(`${API_URL}/livros/${bookId}`, { method: 'DELETE', credentials: 'include' });
                if (response.ok) {
                    alert('Livro excluído com sucesso.');
                    renderAdminBookList();
                    renderCatalog();
                } else { alert('Falha ao excluir livro. Verifique suas permissões.'); }
            }
        }
    });

    // Outros Listeners
    categoryFilter.addEventListener('change', () => renderCatalog(categoryFilter.value));
    btnAddNewBook.addEventListener('click', () => openBookModal());
    btnCancelBook.addEventListener('click', () => bookModal.classList.add('hidden'));

    const initializeApp = () => {
        updateNavbar();
        showView('view-catalog');
        renderCatalog();
        renderCategoryFilter();
    };

    initializeApp();
});