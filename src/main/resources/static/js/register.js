document.getElementById('image-upload').addEventListener('change', function() {
    let reader = new FileReader();
    reader.onload = function(e) {
        document.getElementById('image-preview').src = e.target.result;

    };
    reader.readAsDataURL(this.files[0]);
});

const form = document.getElementById('register-form');

form.addEventListener('submit', function(e) {
    e.preventDefault();

    if (document.getElementById('password').value !== document.getElementById('confirmPassword').value) {
        alert('Passwords do not match');
        return;
    }

    fetch('/api/v1/auth/register', {
        method: 'POST',
        body: JSON.stringify({
            username: document.getElementById('username').value,
            password: document.getElementById('password').value,
            firstName: document.getElementById('firstName').value,
            lastName: document.getElementById('lastName').value,
            email: document.getElementById('email').value,
            dateOfBirth: document.getElementById('dateOfBirth').value
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
    }).catch(function(error) {
        alert(error);
    });
});