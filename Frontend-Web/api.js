api.interceptors.response.use((response) => {
    const box = response.data;
    if (box && typeof box.success !== 'undefined') {
        if (box.success) return { data: box.data, message: box.message };
        return Promise.reject(new Error(box.message || 'Error'));
    }
    return response;
}, (error) => Promise.reject(error));
