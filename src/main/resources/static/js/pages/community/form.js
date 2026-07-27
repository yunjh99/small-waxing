document.addEventListener("DOMContentLoaded", function () {
    // ===== DOM =====
    const form = document.getElementById("writeForm");

    const thumbnailInputText = document.getElementById("thumbnailInput");
    const bodyImageInputText = document.getElementById("bodyImageInput");

    const thumbnailInputFile = document.getElementById("thumbnail");
    const bodyImageInputFile = document.getElementById("bodyImage");

    const existingThumbnailList = document.getElementById("existingThumbnailList");
    const existingBodyImageList = document.getElementById("existingBodyImageList");

    const thumbWarning = document.getElementById("thumbnail-warning");
    const bodyWarning = document.getElementById("bodyImage-warning");

    const deleteThumbnailInput = document.getElementById("deleteThumbnail");
    const deleteBodyImageInput = document.getElementById("deleteBodyImage");

    const btnDeleteThumbnail = document.getElementById("btnDeleteThumbnail");
    const btnDeleteBodyImage = document.getElementById("btnDeleteBodyImage");

    // ===== Util =====
    function showWarning(el, msg) {
        if (!el) return;
        el.textContent = msg;
        el.style.display = msg ? "block" : "none";
    }

    function clearList(ul) {
        if (!ul) return;
        ul.innerHTML = "";
    }

    function isImage(file) {
        return file && file.type && file.type.startsWith("image/");
    }

    // ✅ li로 만들어서 ul 구조 유지
    function createFileLi({ label, onDelete }) {
        const li = document.createElement("li");

        const wrap = document.createElement("div");
        wrap.classList.add("file-item");

        const nameSpan = document.createElement("span");
        nameSpan.textContent = label;
        wrap.appendChild(nameSpan);

        const delBtn = document.createElement("button");
        delBtn.type = "button";
        delBtn.textContent = "삭제";
        delBtn.classList.add("delete-btn");
        delBtn.addEventListener("click", onDelete);
        wrap.appendChild(delBtn);

        li.appendChild(wrap);
        return li;
    }

    // ===== 기존 이미지 삭제 버튼 처리 =====
    btnDeleteThumbnail?.addEventListener("click", function () {
        deleteThumbnailInput.value = "true";
        clearList(existingThumbnailList);
        thumbnailInputText.value = "";
        thumbnailInputFile.value = "";
        showWarning(thumbWarning, "");
    });

    btnDeleteBodyImage?.addEventListener("click", function () {
        deleteBodyImageInput.value = "true";
        clearList(existingBodyImageList);
        bodyImageInputText.value = "";
        bodyImageInputFile.value = "";
        showWarning(bodyWarning, "");
    });

    // ===== 파일 선택 핸들러 =====
    thumbnailInputFile?.addEventListener("change", function (e) {
        const file = e.target.files && e.target.files[0];
        showWarning(thumbWarning, "");
        if (!file) return;

        if (!isImage(file)) {
            showWarning(thumbWarning, "이미지 파일만 업로드 가능합니다.");
            thumbnailInputFile.value = "";
            return;
        }

        // 새 파일 선택 → 기존 삭제 의도 해제
        deleteThumbnailInput.value = "false";

        // 하단 리스트에 “첨부한 파일” 표시
        clearList(existingThumbnailList);
        existingThumbnailList.appendChild(
            createFileLi({
                label: file.name,
                onDelete: function () {
                    thumbnailInputFile.value = "";
                    thumbnailInputText.value = "";
                    clearList(existingThumbnailList);
                }
            })
        );

        // 상단 텍스트 input 표시
        thumbnailInputText.value = file.name;
    });

    bodyImageInputFile?.addEventListener("change", function (e) {
        const file = e.target.files && e.target.files[0];
        showWarning(bodyWarning, "");
        if (!file) return;

        if (!isImage(file)) {
            showWarning(bodyWarning, "이미지 파일만 업로드 가능합니다.");
            bodyImageInputFile.value = "";
            return;
        }

        deleteBodyImageInput.value = "false";

        clearList(existingBodyImageList);
        existingBodyImageList.appendChild(
            createFileLi({
                label: file.name,
                onDelete: function () {
                    bodyImageInputFile.value = "";
                    bodyImageInputText.value = "";
                    clearList(existingBodyImageList);
                }
            })
        );

        bodyImageInputText.value = file.name;
    });

    form?.addEventListener("submit", function () {
        // 기본 submit
    });
});