const VIDEO_ID = window.location.search.substring(4);
const commentForm = document.getElementById('comment-form');
let likes = parseInt(document.getElementById('like-count').textContent);
let dislikes = parseInt(document.getElementById('dislike-count').textContent);
let isLiked = document.getElementById('is-liked').textContent;
let isDisliked = document.getElementById('is-disliked').textContent;

if (isLiked === "true") {
    document.getElementById("likes").classList.add('selected');
}
else if (isDisliked === "true") {
    document.getElementById("dislikes").classList.add('selected');
}

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
        document.getElementById("likes").classList.add('selected');
    }
}

function addDislikeDisplay(response) {
    if (response.ok) {
        document.getElementById('dislike-count').textContent = (dislikes + 1).toString();
        dislikes = parseInt(document.getElementById('dislike-count').textContent);
        document.getElementById("dislikes").classList.add('selected');
    }
}

function removeLikeDisplay(response) {
    if (response.ok) {
        document.getElementById('like-count').textContent = (likes - 1).toString();
        likes = parseInt(document.getElementById('like-count').textContent);
        document.getElementById("likes").classList.remove('selected');
    }
}

function removeDislikeDisplay(response) {
    if (response.ok) {
        document.getElementById('dislike-count').textContent = (dislikes - 1).toString();
        dislikes = parseInt(document.getElementById('dislike-count').textContent)
        document.getElementById("dislikes").classList.remove('selected');
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

commentForm.addEventListener('submit', (e) => {
    e.preventDefault()

    fetch('/api/v1/comment/post-comment', {
        method: 'POST',
        headers: {
            'Content-Type': "application/json; charset=UTF-8"
        },
        body: JSON.stringify({
            videoID: VIDEO_ID,
            commentContent: document.getElementById('comment-input').value
        })
    }).then((response) => {
        if (!response.ok) {
            console.error((response.text()));
            return;
        }
        createComment();
        document.getElementById('comment-input').value = "";
    })
})

function createComment() {
    const frag = document.createDocumentFragment();
    const temp = document.createElement('div');

    temp.innerHTML =
        '<div class="comment"> ' +
            '<img alt="" src=' + document.getElementById('profile-picture').src +'>' +
            '<div>' +
                '<span>' + document.getElementById('user-auth-test').textContent + '</span>' +
                '<br>' +
                '<span class="username-font-size">' + document.getElementById('username-header').textContent +
                '</span>' +
                '<br>' +
                '<span>' + document.getElementById('comment-input').value + '</span>' +
            '</div>' +
        '</div>'
    ;
    while (temp.firstChild) {
        frag.appendChild(temp.firstChild);
    }
    document.getElementById('comment-body').appendChild(frag);
}

