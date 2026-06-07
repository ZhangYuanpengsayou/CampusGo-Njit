(function () {
    async function request(url, options = {}) {
        const config = {
            credentials: "include",
            headers: {},
            ...options
        };

        if (config.body && !(config.body instanceof FormData)) {
            config.headers["Content-Type"] = "application/json";
            config.body = JSON.stringify(config.body);
        }

        const response = await fetch(url, config);
        const result = await response.json().catch(() => ({
            code: response.status,
            message: "请求失败",
            data: null
        }));

        if (!response.ok || result.code !== 200) {
            const error = new Error(result.message || "请求失败");
            error.code = result.code || response.status;
            throw error;
        }

        return result.data;
    }

    window.CampusGoApi = {
        get(url) {
            return request(url);
        },
        post(url, body) {
            return request(url, { method: "POST", body });
        },
        put(url, body) {
            return request(url, { method: "PUT", body });
        },
        delete(url) {
            return request(url, { method: "DELETE" });
        },
        upload(url, formData) {
            return request(url, { method: "POST", body: formData });
        }
    };
})();
