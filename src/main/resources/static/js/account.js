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

function fetchPicture(elementID) {
    return new Promise((resolve, reject) => {
        let elementContent = document.getElementById(elementID).src.split(',')[1];

        if (typeof elementContent !== 'undefined') {
            resolve(elementContent);
        } else {
            const url = document.getElementById(elementID).src;

            fetch(url)
                .then(response => response.blob())
                .then(blob => {
                    let reader = new FileReader();
                    reader.onload = function (e) {
                        elementContent = e.target.result.split(',')[1];
                        resolve(elementContent);
                    };
                    reader.readAsDataURL(blob);
                })
                .catch(error => {
                    console.error(error);
                    reject(error);
                });
        }
    });
}

document.getElementById('content').addEventListener('submit', async (event) => {
    event.preventDefault();

    let picture = await fetchPicture('image-preview');
    let banner = await fetchPicture('banner-preview');
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
            newProfilePicture: picture,
            newBannerPicture: banner,
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
