const form = document.getElementById('login-form');

form.addEventListener('change', () => {
    let element = document.getElementsByClassName('message')[0];
    if (element.classList.contains('red')) {
        element.classList.remove('red');
    }
});

form.addEventListener('submit', function(e) {
    e.preventDefault();

    fetch('/api/v1/auth/authenticate', {
        method: 'POST',
        body: JSON.stringify({
            email: document.getElementById('email').value,
            password: document.getElementById('password').value,
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(function(response) {
        if (response.ok) {
            return response;
        } else {
            return response.text().then(function(text) {
                throw new Error(text);
            });
        }
    }).then(function() {
        window.location.href = '/'
    }).catch(function() {
        let element = document.getElementsByClassName('message')[0];
        if (element.classList.contains('blue')) {
            element.classList.remove('blue');
        }
        if (element.classList.contains('red')) {
            element.classList.remove('red');
        }
        element.classList.add('red');
        element.textContent = 'Invalid email or password.'
    });
});