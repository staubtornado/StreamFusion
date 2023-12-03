const VIDEO_ID = window.location.search.substring(4);
let likes = parseInt(document.getElementById('likes').textContent);
let dislikes = parseInt(document.getElementById('dislikes').textContent);

async function addLike() {
    const data = await fetch("/api/v1/video/add-like", {
        method: "PUT",
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        },
        body: VIDEO_ID
    })
    return await data.text();
}

async function removeLike() {
    const data = await fetch("/api/v1/video/remove-like", {
        method: "PUT",
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        },
        body: VIDEO_ID
    })
    return await data.text();
}

async function addDislike() {
    const data = await fetch("/api/v1/video/add-dislike", {
        method: "PUT",
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        },
        body: VIDEO_ID
    })
    return await data.text();
}

async function removeDislike() {
    const data = await fetch("/api/v1/video/remove-dislike", {
        method: "PUT",
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        },
        body: VIDEO_ID
    })
    return await data.text();
}

function like() {
    addLike().then((text) => {
        if (text === "Liked video successfully") {
            document.getElementById('likes').textContent = (likes + 1).toString();
            likes = parseInt(document.getElementById('likes').textContent);
        }
        if (text === "Video already disliked") {
            removeDislike().then((text) => {
                if (text === "Removed dislike from video successfully"){
                    document.getElementById('dislikes').textContent = (dislikes - 1).toString();
                    dislikes = parseInt(document.getElementById('dislikes').textContent);

                    addLike().then((text) => {
                        if (text === "Liked video successfully") {
                            document.getElementById('likes').textContent = (likes + 1).toString();
                            likes = parseInt(document.getElementById('likes').textContent);
                        }
                    })
                }
            });
        }
        if (text === "Video already liked") {
            removeLike().then((text) => {
                if (text === "Removed like from video successfully"){
                    document.getElementById('likes').textContent = (likes - 1).toString();
                    likes = parseInt(document.getElementById('likes').textContent);
                }
            });
        }
    })
}

function dislike() {
    addDislike().then((text) => {
        if (text === "Disliked video successfully") {
            document.getElementById('dislikes').textContent = (dislikes + 1).toString();
            dislikes = parseInt(document.getElementById('dislikes').textContent);
        }
        if (text === "Video already liked") {
            removeLike().then((text) => {
                if (text === "Removed like from video successfully"){
                    document.getElementById('likes').textContent = (likes - 1).toString();
                    likes = parseInt(document.getElementById('likes').textContent);

                    addDislike().then((text) => {
                        if (text === "Disliked video successfully") {
                            document.getElementById('dislikes').textContent = (dislikes + 1).toString();
                            dislikes = parseInt(document.getElementById('dislikes').textContent);
                        }
                    })
                }
            });
        }
        if (text === "Video already disliked") {
            removeDislike().then((text) => {
                if (text === "Removed dislike from video successfully"){
                    document.getElementById('dislikes').textContent = (dislikes - 1).toString();
                    dislikes = parseInt(document.getElementById('dislikes').textContent);
                }
            });
        }
    })
}
