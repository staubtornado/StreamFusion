function onChangeEvent(previewID, inputID) {
    const reader = new FileReader();
    reader.onload = (e) => {
        document.getElementById(previewID).src = e.target.result;
    }
    reader.readAsDataURL(document.getElementById(inputID).files[0]);
}

document.getElementById('image-upload').addEventListener('change', () => {
    onChangeEvent('image-preview', 'image-upload');
});

document.getElementById('banner-upload').addEventListener('change', () => {
    onChangeEvent('banner-preview', 'banner-upload');
});

document.getElementById('banner-button').onclick = () => {
    document.getElementById('banner-upload').click();
}

const userID = document.getElementById('userID').value;

document.getElementById('content').addEventListener('submit', async (event) => {
    event.preventDefault();

    let picture = document.getElementById('image-preview').src.split(',')[1];
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

    picture = await fetchProfilePicture();
    fetch('/api/v1/auth/edit-details', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            newFirstName: document.getElementById('firstName').value,
            newLastName: document.getElementById('lastName').value,
            newEmail: document.getElementById('email').value,
            newUsername: document.getElementById('username').value,
            newDateOfBirth: document.getElementById('dateOfBirth').value,
            newProfilePicture: picture
        })
    }).then((response) => {
        if (response.ok) {
            window.location.href = '/user?id=' + userID;
        } else {
            return response.text().then(function (text) {
                throw new Error(text);
            });
        }
    }).catch((error) => {
        alert(error.message)
    });
});
