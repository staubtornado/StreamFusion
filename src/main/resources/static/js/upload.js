document.getElementById('thumbnail-upload').addEventListener('change', function() {
    let reader = new FileReader();
    reader.onload = function(e) {
        document.getElementById('preview').src = e.target.result;
        changedProfilePicture = true;
    };
    reader.readAsDataURL(this.files[0]);
});

document.getElementsByTagName('form')[1].addEventListener('submit', (e) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append('file', document.getElementById('video-input').files[0]);
    formData.append('title', document.getElementById('title-input').value);
    formData.append('description', document.getElementById('description-input').value);

    const thumbnail = document.getElementById('thumbnail-upload').files[0];
    const reader = new FileReader();
    reader.readAsDataURL(thumbnail);
    reader.onload = () => {
        const dataURL = reader.result;
        const base64String = dataURL.split(',')[1];
        formData.append('thumbnail', base64String);

        const fileNameSplits = thumbnail.name.split('.');
        formData.append('imgType', fileNameSplits[fileNameSplits.length - 1]);

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
    }
});