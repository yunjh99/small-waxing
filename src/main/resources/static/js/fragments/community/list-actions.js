document.addEventListener('DOMContentLoaded', () => {
    const actionBox = document.querySelector('.list-actions');

    if (!actionBox) return;

    const btnSelectAll = document.getElementById('btnSelectAll');
    const btnDeleteSelected = document.getElementById('btnDeleteSelected');
    const btnRestoreSelected = document.getElementById('btnRestoreSelected');

    const prefix = actionBox.dataset.prefix;             // /community/events
    const viewType = actionBox.dataset.viewType;         // active / ended / deleted
    const entityName = actionBox.dataset.entityName;     // events / notices / faqs
    const enableRestore = actionBox.dataset.enableRestore === 'true';

    const getChecks = () => Array.from(document.querySelectorAll('.js-list-check'));

    const getSelectedIds = () =>
        getChecks()
            .filter(checkbox => checkbox.checked)
            .map(checkbox => Number(checkbox.value));

    const updateSelectAllButtonText = () => {
        if (!btnSelectAll) return;

        const checks = getChecks();
        const checkedCount = checks.filter(checkbox => checkbox.checked).length;
        const allChecked = checks.length > 0 && checkedCount === checks.length;

        btnSelectAll.textContent = allChecked ? '전체해제' : '전체선택';
    };

    if (btnSelectAll) {
        btnSelectAll.addEventListener('click', () => {
            const checks = getChecks();

            if (checks.length === 0) {
                alert('선택할 항목이 없습니다.');
                return;
            }

            const checkedCount = checks.filter(checkbox => checkbox.checked).length;
            const shouldCheckAll = checkedCount !== checks.length;

            checks.forEach(checkbox => {
                checkbox.checked = shouldCheckAll;
            });

            updateSelectAllButtonText();
        });
    }

    document.addEventListener('change', (event) => {
        if (!event.target.classList.contains('js-list-check')) return;
        updateSelectAllButtonText();
    });

    if (btnDeleteSelected) {
        btnDeleteSelected.addEventListener('click', async () => {
            const ids = getSelectedIds();

            if (ids.length === 0) {
                alert('삭제할 항목을 선택해주세요.');
                return;
            }

            if (!confirm('선택한 항목을 삭제하시겠습니까?')) return;

            try {
                const response = await fetch(`/api${prefix}/delete`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ ids })
                });

                if (!response.ok) {
                    throw new Error('삭제 실패');
                }

                alert('삭제되었습니다.');
                location.reload();
            } catch (error) {
                console.error(error);
                alert('삭제 중 오류가 발생했습니다.');
            }
        });
    }

    if (btnRestoreSelected && enableRestore && viewType === 'deleted') {
        btnRestoreSelected.addEventListener('click', async () => {
            const ids = getSelectedIds();

            if (ids.length === 0) {
                alert('복구할 항목을 선택해주세요.');
                return;
            }

            if (!confirm('선택한 항목을 복구하시겠습니까?')) return;

            try {
                const response = await fetch(`/api${prefix}/restore`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ ids })
                });

                if (!response.ok) {
                    throw new Error('복구 실패');
                }

                alert('복구되었습니다.');
                location.reload();
            } catch (error) {
                console.error(error);
                alert('복구 중 오류가 발생했습니다.');
            }
        });
    }

    updateSelectAllButtonText();
});