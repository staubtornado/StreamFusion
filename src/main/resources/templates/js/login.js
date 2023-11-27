function sendFormData() {
    fetch("/api/v1/auth/authenticate", {
        method: "POST",
        body: JSON.stringify({
            email: document.getElementById('email').value,
            password: document.getElementById('password').value
        }),
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        }
    })  .then((response) => response.json())
        .then((json) => console.log(json));
}