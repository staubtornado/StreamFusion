const VIDEO_ID = window.location.search.substring(4);
let likes = parseInt(document.getElementById('like-count').textContent);
let dislikes = parseInt(document.getElementById('dislike-count').textContent);
let isLiked = document.getElementById('is-liked').textContent;
let isDisliked = document.getElementById('is-disliked').textContent;

async function addLike() {
    const data = await fetch("/api/v1/video/add-like", {
        method: "PUT",
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        },
        body: VIDEO_ID
    });
    if (data.status === 405) {
        redirectToLogin()
    }
    return data;
}

async function removeLike() {
    return await fetch("/api/v1/video/remove-like", {
        method: "PUT",
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        },
        body: VIDEO_ID
    });
}

async function addDislike() {
    const data =  await fetch("/api/v1/video/add-dislike", {
        method: "PUT",
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        },
        body: VIDEO_ID
    });
    if (data.status === 405) {
        redirectToLogin()
    }
    return data;
}

async function removeDislike() {
    return await fetch("/api/v1/video/remove-dislike", {
        method: "PUT",
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        },
        body: VIDEO_ID
    });
}

function addLikeDisplay(response) {
    if (response.ok) {
        document.getElementById('like-count').textContent = (likes + 1).toString();
        likes = parseInt(document.getElementById('like-count').textContent);
    }
}

function addDislikeDisplay(response) {
    if (response.ok) {
        document.getElementById('dislike-count').textContent = (dislikes + 1).toString();
        dislikes = parseInt(document.getElementById('dislike-count').textContent);
    }
}

function removeLikeDisplay(response) {
    if (response.ok) {
        document.getElementById('like-count').textContent = (likes - 1).toString();
        likes = parseInt(document.getElementById('like-count').textContent);
    }
}

function removeDislikeDisplay(response) {
    if (response.ok) {
        document.getElementById('dislike-count').textContent = (dislikes - 1).toString();
        dislikes = parseInt(document.getElementById('dislike-count').textContent)
    }
}

function redirectToLogin() {
    window.location.href = '/login';
}

function like() {
    if (isLiked === "true") {
        removeLike().then((response) => {
            removeLikeDisplay(response);
            document.getElementById('is-liked').textContent = "false";
            isLiked = "false";
        });

    }
    else if (isDisliked === "true") {
        removeDislike().then((response) => {
            removeDislikeDisplay(response);
            document.getElementById('is-disliked').textContent = "false";
            isDisliked = "false";

            addLike().then((response) => {
                addLikeDisplay(response);
                document.getElementById('is-liked').textContent = "true";
                isLiked = "true";
            });
        });
    }
    else {
        addLike().then((response) => {
            addLikeDisplay(response);
            document.getElementById('is-liked').textContent = "true";
            isLiked = "true";
        });
    }
}

function dislike() {
    if (isDisliked === "true") {
        removeDislike().then((response) => {
            removeDislikeDisplay(response);
            document.getElementById('is-disliked').textContent = "false";
            isDisliked = "false";
        });
    }
    else if (isLiked === "true") {
        removeLike().then((response) => {
            removeLikeDisplay(response);
            document.getElementById('is-liked').textContent = "false";
            isLiked = "false";

            addDislike().then((response) => {
                addDislikeDisplay(response);
                document.getElementById('is-disliked').textContent = "true";
                isDisliked = "true";
            });
        });
    }
    else {
        addDislike().then((response) => {
            addDislikeDisplay(response);
            document.getElementById('is-disliked').textContent = "true";
            isDisliked = "true";
        });
    }
}
