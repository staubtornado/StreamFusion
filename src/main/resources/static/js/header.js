const form = document.getElementById('search-form');

form.addEventListener('submit', (e) => {
    e.preventDefault();
    const search = document.getElementById('search-input').value;
    window.location.href = '/search?q=' + search;
})