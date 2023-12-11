const onHoverElement = document.getElementById('account-section-header');

try {
    // On width change, change width of new element
    onHoverElement.addEventListener('mouseover', function() {
        document.getElementById('account-popup').style.width = onHoverElement.offsetWidth - 2 + 'px';
    });
} catch (e) {
    const button = document.createElement('button');
    button.textContent = 'Login';
    button.id = 'login-button';
    button.addEventListener('click', () => {
        window.location.href = '/login';
    });
    document.getElementById('header-div').appendChild(button);
}

const form = document.getElementById('search-form');

form.addEventListener('submit', (e) => {
    e.preventDefault()

    fetch('/search?query=' + document.getElementById('search-input').value, {
        method: 'GET',
        headers: {
            'Content-Type': 'String'
        }
    }).then((response) => {
        if (!response.ok) {
            console.error(response.text());
            return;
        }
        window.location.href = response.url;
    })
})