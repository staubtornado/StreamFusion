let changedProfilePicture = false;

document.getElementById('image-upload').addEventListener('change', function() {
    let reader = new FileReader();
    reader.onload = function(e) {
        document.getElementById('image-preview').src = e.target.result;
        changedProfilePicture = true;
    };
    reader.readAsDataURL(this.files[0]);
});

function setErrorMessage(message) {
    let element = document.getElementsByClassName('message')[0];
    if (element.classList.contains('red')) {
        element.classList.remove('red');
    }
    element.classList.add('red');
    element.textContent = message;

    const horizontalShakeAnimation = [
        { transform: "translateX(50px)" },
        { transform: "translateX(-50px)" },
        { transform: "translateX(25px)" },
        { transform: "translateX(-25px)" },
        { transform: "translateX(12px)" },
        { transform: "translateX(-12px)" },
        { transform: "translateX(6px)" },
        { transform: "translateX(-6px)" },
        { transform: "translateX(3px)" },
        { transform: "translateX(-3px)" },
        { transform: "translateX(1px)" },
        { transform: "translateX(-1px)" },
        { transform: "translateX(0px)" }
    ];
    element.animate(horizontalShakeAnimation, 500);
}

const form = document.getElementById('register-form');

form.addEventListener('submit', function(e) {
    e.preventDefault();

    if (document.getElementById('password').value !== document.getElementById('confirmPassword').value) {
        return setErrorMessage('Passwords do not match.');
    }

    let img = document.getElementById('image-preview');
    if (!changedProfilePicture) {
        const firstName = document.getElementById('firstName').value;
        const lastName = document.getElementById('lastName').value;

        img.src = '/cdn/profile-picture/generate?first-name=' + firstName + '&last-name=' + lastName;
    }

    let picture = img.src.split(',')[1];

    const fetchProfilePicture = () => {
        return new Promise((resolve, reject) => {
            if (typeof picture !== 'undefined') {
                resolve(picture);
            } else {
                const url = document.getElementById('image-preview').src;

                fetch(url)
                    .then(response => response.blob())
                    .then(blob => {
                        let reader = new FileReader();
                        reader.onload = function (e) {
                            picture = e.target.result.split(',')[1];
                            resolve(picture);
                        };
                        reader.readAsDataURL(blob);
                    })
                    .catch(error => {
                        console.error(error);
                        reject(error);
                    });
            }
        });
    };

    fetchProfilePicture().then(picture => {
        fetch('/api/v1/auth/register', {
            method: 'POST',
            body: JSON.stringify({
                username: document.getElementById('username').value,
                password: document.getElementById('password').value,
                firstName: document.getElementById('firstName').value,
                lastName: document.getElementById('lastName').value,
                email: document.getElementById('email').value,
                dateOfBirth: document.getElementById('dateOfBirth').value,
                // Get bytes from image
                profilePicture: picture
            }),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(function (response) {
            if (response.ok) {
                return response;
            } else {
                return response.text().then(function (text) {
                    throw new Error(text);
                });
            }
        }).then(function () {
            window.location.href = '/'
        }).catch(function (error) {
            setErrorMessage(error.message);
        });
    }).catch(error => {
        console.error('Failed to fetch profile picture:', error);
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

document.getElementById('confirmPassword').addEventListener('input', () => {
    validatePassword();
});