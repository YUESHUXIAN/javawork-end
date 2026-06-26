/* common.js - 公共 JavaScript 工具库 */

/**
 * 获取应用的上下文路径
 * 自动检测部署路径，支持根路径和子路径部署
 */
function getContextPath() {
    var path = window.location.pathname;
    // 去掉开头的 /，然后取第一段
    var segments = path.replace(/^\//, '').split('/');
    // 已知的子目录列表（不是 context path）
    var knownDirs = ['admin', 'owner', 'gate', 'common', 'images', 'WEB-INF', 'index.html', ''];
    if (segments.length > 1 && knownDirs.indexOf(segments[0]) === -1) {
        return '/' + segments[0];
    }
    return '';
}

// 全局上下文路径
var CTX = getContextPath();
console.log('[common.js] 上下文路径: "' + CTX + '"');
console.log('[common.js] 当前路径: "' + window.location.pathname + '"');

// ---- Axios 拦截器：自动给所有请求 URL 加上 context path ----
if (typeof axios !== 'undefined') {
    axios.interceptors.request.use(function(config) {
        if (config.url && config.url.startsWith('/') && !config.url.startsWith('//')) {
            // 如果有 context path 且 URL 还没加上，就加上
            if (CTX && config.url.indexOf(CTX + '/') !== 0) {
                config.url = CTX + config.url;
            }
        }
        console.log('[axios] 请求: ' + (config.method || '').toUpperCase() + ' ' + config.url);
        return config;
    }, function(error) {
        return Promise.reject(error);
    });
}

// 便捷的 API 请求函数（自动加 context path + 错误处理）
function apiGet(url, params) {
    return axios.get(url, { params: params });
}
function apiPost(url, params) {
    return axios.post(url, params, {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    });
}

// 管理员页面登录检查（服务端验证）
function checkAdminLogin() {
    axios.get(CTX + '/admin/checkLogin').then(function(res) {
        if (res.data.code !== 200) {
            sessionStorage.removeItem('adminLoginTime');
            alert('请先登录管理员');
            window.location.href = CTX + '/index.html';
        } else {
            sessionStorage.setItem('adminLoginTime', Date.now().toString());
        }
    }).catch(function() {
        sessionStorage.removeItem('adminLoginTime');
        window.location.href = CTX + '/index.html';
    });
}

// 管理员退出登录（调用服务端注销 + 清除本地缓存）
function adminLogout() {
    sessionStorage.removeItem('adminLoginTime');
    axios.get(CTX + '/admin/logout').then(function() {
        window.location.href = CTX + '/index.html';
    }).catch(function() {
        window.location.href = CTX + '/index.html';
    });
}

// 统一的提示消息函数
function showMessage(msg, isSuccess) {
    alert(isSuccess ? "✅ 成功: " + msg : "❌ 失败: " + msg);
}

// 通用的日期格式化工具
function formatDate(timestamp) {
    if (!timestamp) return "-";
    var d = new Date(timestamp);
    var pad = function(n) { return n < 10 ? '0' + n : n; };
    return d.getFullYear() + '-' + pad(d.getMonth()+1) + '-' + pad(d.getDate()) + ' ' +
           pad(d.getHours()) + ':' + pad(d.getMinutes()) + ':' + pad(d.getSeconds());
}

// 页面加载完成后执行
$(document).ready(function() {
    console.log('[common.js] 页面准备就绪');
    $('.btn-back').click(function(){ window.history.back(); });
});
