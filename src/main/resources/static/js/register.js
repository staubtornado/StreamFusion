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


function validatePassword() {
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (password === confirmPassword && confirmPassword.length > 0) {
        document.getElementById('confirmPassword-div').style.borderBottomColor = 'hsl(120, 100%, 50%)';
        return;
    }
    document.getElementById('confirmPassword-div').style.borderBottomColor = 'white';
}


document.getElementById('password').addEventListener('input', (e) => {
    const password = e.target.value;
    validatePassword();

    if (password.length === 0) {
        document.getElementById('password-div').style.borderBottomColor = 'white';
        return;
    }
    let percentage = 0;

    percentage += Number(new RegExp(/[A-Z]/).test(password)) * 25;
    percentage += Number(new RegExp(/[a-z]/).test(password)) * 25;
    percentage += Number(new RegExp(/[0-9]/).test(password)) * 25;
    percentage += Number(new RegExp(/[^A-Za-z0-9]/).test(password)) * 25;
    percentage *= Math.min(password.length / 8, 1);
    document.getElementById('password-div').style.borderBottomColor = `hsl(${percentage * 1.2}, 100%, 50%)`;
});

document.getElementById('confirmPassword').addEventListener('input', (e) => {
    validatePassword();
});