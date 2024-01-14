const VIDEO_ID = window.location.search.substring(4);
const USER_ID  = document.getElementById('profile-picture').src.split('=')[1];
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
            '<a class="comment-user-picture" href=' + "/user?id=" + USER_ID + '>' +
                '<img alt="" src=' + document.getElementById('profile-picture').src +'>' +
            '</a>' +
            '<a class="comment-usernames" href=' + "/user?id=" + USER_ID + '>' +
                '<span>' + document.getElementById('nms').textContent + '</span>' +
                '<br>' +
                '<span>' + document.getElementById('username-header').textContent + '</span>' +
            '</a>'+
            '<svg class="dots" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMinYMin meet" viewBox="0 0 2 8" fill="none">' +
                '<path d="M2 7C2 7.55228 1.55228 8 1 8C0.447715 8 0 7.55228 0 7C0 6.44772 0.447715 6 1 6C1.55228 6 2 6.44772 2 7Z" fill="white"/>' +
                '<path d="M2 4C2 4.55228 1.55228 5 1 5C0.447715 5 0 4.55228 0 4C0 3.44772 0.447715 3 1 3C1.55228 3 2 3.44772 2 4Z" fill="white"/>' +
                '<path d="M2 1C2 1.55228 1.55228 2 1 2C0.447715 2 0 1.55228 0 1C0 0.447715 0.447715 0 1 0C1.55228 0 2 0.447715 2 1Z" fill="white"/>' +
            '</svg>' +
            '<span>' + document.getElementById('comment-input').value + '</span>' +
        '</div>'
    ;
    while (temp.firstChild) {
        frag.appendChild(temp.firstChild);
    }
    document.getElementById('comments').insertBefore(frag, document.getElementById('comments').firstChild);
}

document.getElementById('comments-control').addEventListener('click', (event) => {
    event.stopPropagation();
    document.getElementById('comments-control').classList.add('selected');
});
window.onclick = function() {
    document.getElementById('comments-control').classList.remove('selected');
}

const dots = document.querySelectorAll('.dots');
document.addEventListener('click', (event) => {
    dots.forEach((dot) => {
        const parent = dot.parentElement;
        const div = parent.querySelector('div');
        if (!parent.contains(event.target) && div.classList.contains('show')) {
            div.classList.remove('show');
        }
    });
});

dots.forEach((dot) => {
    dot.addEventListener('click', () => {
        const parent = dot.parentElement;
        const comment_ID = parent.querySelector('p').textContent;
        parent.querySelector('div').classList.add('show');
        const ul = parent.querySelector('ul').children;

        ul[0].addEventListener('click', (e => {
            fetch('/api/v1/comment/like-comment', {
                method: 'PUT',
                headers: {
                    'Content-Type': "application/json; charset=UTF-8"
                },
                body: comment_ID
            }).then((response) => {
                if (response.ok) {
                    console.log(response);
                }
            })
        }))

        ul[1].addEventListener('click', (e => {
            fetch('/api/v1/comment/dislike-comment', {
                method: 'PUT',
                headers: {
                    'Content-Type': "application/json; charset=UTF-8"
                },
                body: comment_ID
            }).then((response) => {
                if (response.ok) {
                    console.log(response);
                }
            })
        }))

        // ul[2].addEventListener('click', (e => {
        //     document.getElementById('comment-input').focus();
        //     //await user input & send!
        //
        //     fetch('/api/v1/comment/edit-comment', {
        //         method: 'PUT',
        //         headers: {
        //             'Content-Type': "application/json; charset=UTF-8"
        //         },
        //         body: JSON.stringify({
        //             commentID: comment_ID,
        //             commentContent: document.getElementById('comment-input').value
        //         })
        //     }).then((response) => {
        //         if (response.ok) {
        //             console.log(response);
        //         }
        //     })
        // }))

        ul[2].addEventListener('click', (e => {
            fetch('/api/v1/comment/remove-comment', {
                method: 'DELETE',
                headers: {
                    'Content-Type': "application/json; charset=UTF-8"
                },
                body: comment_ID
            }).then((response) => {
                if (response.ok) {
                    console.log(response);
                }
            })
        }))
    });
});