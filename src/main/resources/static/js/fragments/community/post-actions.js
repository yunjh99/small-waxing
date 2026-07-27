async function handleRestore(button) {
    const url = button.dataset.url;
    const redirectUrl = button.dataset.redirect;

    if (!confirm('정말 복구하시겠습니까?')) {
        return;
    }

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: getCsrfHeaders({
                'X-Requested-With': 'XMLHttpRequest'
            })
        });

        const contentType = response.headers.get("content-type");

        let result = {};
        if (contentType && contentType.includes("application/json")) {
            result = await response.json();
        }

        if (!response.ok) {
            showToast(result.message || '복구 실패', 'error');
            return;
        }

        // ✅ 성공 시 바로 이동
        location.href = redirectUrl;

    } catch (error) {
        showToast('복구 처리 중 오류가 발생했습니다.', 'error');
    }
}

async function handleDelete(button) {
    const url = button.dataset.url;
    const redirectUrl = button.dataset.redirect;

    if (!confirm('정말 삭제하시겠습니까?')) {
        return;
    }

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: getCsrfHeaders({
                'X-Requested-With': 'XMLHttpRequest'
            })
        });

        const contentType = response.headers.get("content-type");

        let result = {};
        if (contentType && contentType.includes("application/json")) {
            result = await response.json();
        }

        if (!response.ok) {
            showToast(result.message || '삭제 실패', 'error');
            return;
        }

        // ✅ 성공 시 바로 이동
        location.href = redirectUrl;

    } catch (error) {
        showToast('삭제 처리 중 오류가 발생했습니다.', 'error');
    }
}
