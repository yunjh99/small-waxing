window.getCsrfHeaders = function (additionalHeaders = {}) {
    const token = document.querySelector('meta[name="_csrf"]')?.content;
    const headerName = document.querySelector('meta[name="_csrf_header"]')?.content;

    if (!token || !headerName) {
        throw new Error('CSRF token is not available.');
    }

    return {
        ...additionalHeaders,
        [headerName]: token
    };
};
