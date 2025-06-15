let isSubmitting = false;

// 댓글 등록 이벤트
async function handleSubmit(e) {
    e.preventDefault();
    if (isSubmitting) return;
    isSubmitting = true;

    const contentInput = document.getElementById("content");
    const postId = Number(document.getElementById("postId").value);
    const userId = Number(document.getElementById("userId").value);
    const content = contentInput.value.trim();

    try {
        const response = await fetch("/api/comments", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ postId, userId, content })
        });

        if (response.ok) {
            contentInput.value = "";
            await loadComments(postId);
        } else {
            const errorText = await response.text();
            if (response.status === 401) {
                alert(errorText || "비회원은 댓글을 달 수 없습니다.");
            } else {
                alert(errorText || "댓글 등록 실패.")
            }
        }
    } catch (err) {
        console.error(err);
        alert("서버 오류가 발생했습니다.");
    }
    isSubmitting = false;
}

// 댓글 목록 불러오기
async function loadComments(postId) {
    const list = document.getElementById("comment-list");
    try {
        const res = await fetch(`/api/comments/post/${postId}`, { cache: "no-store" });
        let data;
        try {
            data = await res.json();
        } catch (err) {
            const text = await res.text();
            console.error("응답이 JSON 형식이 아님:", text);
            throw new Error("댓글 응답 오류");
        }
        list.innerHTML = "";

        data.forEach(comment => {
            const li = document.createElement("li");
            li.classList.add("mb-3");

            const created = new Date(comment.createTime);
            const updated = new Date(comment.updateTime);
            const isEdited = updated.getTime() !== created.getTime();
            const timeLabel = `${updated.toLocaleString()}${isEdited ? ' (수정됨)' : ''}`;

            li.innerHTML = `
                <div class="nickname-box px-3 py-2 rounded-top d-flex justify-content-between align-items-center">
                    <span class="fw-bold">${comment.nickname}</span>
                    <small class="text-muted">${timeLabel}</small>
                </div>
                <div class="px-3 py-2 bg-white">
                    <p class="mb-2" id="content-${comment.id}" style="font-size: 1rem;">${comment.content}</p>
                    <div id="edit-box-${comment.id}"></div>
                    <div class="d-flex align-items-center justify-content-end gap-2 text-muted" style="font-size: 0.85rem;">
                        <button class="btn btn-sm btn-outline-dark" onclick="editComment(${comment.id}, \`${comment.content.replace(/`/g, '\\`')}\`)"><i class="bi bi-pencil"></i></button>
                        <button class="btn btn-sm btn-outline-dark" onclick="deleteComment(${comment.id})"><i class="bi bi-trash"></i></button>
                    </div>
                </div>`;
            list.appendChild(li);
        });
    } catch (err) {
        console.error("댓글 불러오기 실패", err);
        alert("댓글을 불러오지 못했습니다.");
    }
}

// 댓글 삭제
window.deleteComment = async function (commentId) {
    if (!confirm("정말 삭제하시겠습니까?")) return;
    try {
        const res = await fetch(`/api/comments/${commentId}`, { method: "DELETE" });
        if (res.ok) {
            await loadComments(document.getElementById("postId").value);
        } else {
            const errorText = await res.text();
            alert(errorText || "삭제 실패");
        }
    } catch (err) {
        console.error("삭제 중 오류", err);
        alert("삭제 실패.");
    }
};

// 댓글 수정 입력창
window.editComment = function (commentId, currentContent) {
    const editBox = document.getElementById(`edit-box-${commentId}`);
    if (editBox.innerHTML.trim() !== "") return;

    editBox.innerHTML = `
        <textarea id="edit-input-${commentId}" class="form-control mb-2">${currentContent}</textarea>
        <div class="d-flex gap-2 justify-content-end">
            <button class="btn btn-sm" style="background-color: #DCFDC7;" onclick="submitEdit(${commentId})">등록</button>
            <button class="btn btn-sm btn-secondary" onclick="cancelEdit(${commentId})">취소</button>
        </div>`;

    const textarea = document.getElementById(`edit-input-${commentId}`);
    textarea.addEventListener("keydown", function (e) {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            submitEdit(commentId);
        } else if (e.key === "Escape") {
            cancelEdit(commentId);
        }
    });
};

// 댓글 수정 등록
window.submitEdit = async function (commentId) {
    const newContent = document.getElementById(`edit-input-${commentId}`).value.trim();
    try {
        const res = await fetch(`/api/comments/${commentId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ content: newContent })
        });

        if (res.ok) {
            await loadComments(document.getElementById("postId").value);
        } else {
            const errorText = await res.text();
            alert(errorText || "수정 실패");
        }
    } catch (err) {
        console.error("수정 중 오류", err);
        alert("서버 오류로 댓글 수정에 실패했습니다.");
    }
};

// 댓글 수정 취소
window.cancelEdit = function (commentId) {
    const editBox = document.getElementById(`edit-box-${commentId}`);
    editBox.innerHTML = "";
};

// 초기 이벤트 등록
document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("comment-form");
    const contentInput = document.getElementById("content");
    const postId = document.getElementById("postId").value;

    form.addEventListener("submit", handleSubmit);

    contentInput.addEventListener("keydown", function (e) {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            form.requestSubmit();
        }
    });

    loadComments(postId);
});
