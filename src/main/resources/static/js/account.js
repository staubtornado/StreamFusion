document.getElementById('image-upload').addEventListener('change', function() {
    let reader = new FileReader();
    reader.onload = function(e) {
        document.getElementById('image-preview').src = e.target.result;

    };
    reader.readAsDataURL(this.files[0]);
});