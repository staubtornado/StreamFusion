document.getElementById('upload-form').addEventListener('submit',  (e) => {
    e.preventDefault();

    console.log(typeof document.getElementById('file').files[0]);

    const formData = new FormData();
    formData.append('file', document.getElementById('file').files[0]);
    formData.append('title', document.getElementById('title').value);
    formData.append('description', document.getElementById('description').value);
    formData.append('thumbnail', document.getElementById('thumbnail-upload').value);

    fetch('/api/v1/video/upload', {
        method: 'POST',
        body: formData
    }).then((response) => {
        if (!response.ok) {
            throw Error(response.statusText);
        }
        return response.text();
    }).then((id) => {
        window.location.href = '/video?id=' + id;
    }).catch((error) => {
        console.log(error);
    })
});