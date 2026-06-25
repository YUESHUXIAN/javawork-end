/* common.js - 公共 JavaScript 工具库 */

/**
 * 获取应用的上下文路径
 * 自动检测部署路径，支持根路径和子路径部署
 */
function getContextPath() {
    let path = window.location.pathname;
    let segments = path.split('/').filter(function(s) { return s.length > 0; });
    // 已知的子目录列表（不是 context path）
    var knownDirs = ['admin', 'owner', 'gate', 'common', 'images', 'WEB-INF'];
    if (segments.length > 1 && knownDirs.indexOf(segments[0]) === -1) {
        return '/' + segments[0];
    }
    return '';
}

// 全局上下文路径
var CTX = getContextPath();

// ---- Axios 拦截器：自动给所有请求 URL 加上 context path ----
if (typeof axios !== 'undefined') {
    axios.interceptors.request.use(function(config) {
        // 只处理以 / 开头的相对路径，且避免重复添加 context path
        if (config.url && config.url.startsWith('/') && !config.url.startsWith('//')) {
            if (CTX && !config.url.startsWith(CTX + '/')) {
                config.url = CTX + config.url;
            }
        }
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

// 管理员页面登录检查（本地缓存 + 服务端验证）
function checkAdminLogin() {
    // 快速检查本地缓存（避免每次跳转都发 HTTP 请求）
    var loginTime = sessionStorage.getItem('adminLoginTime');
    if (loginTime) {
        var elapsed = Date.now() - parseInt(loginTime);
        // 30 分钟内认为有效，跳过 HTTP 检查
        if (elapsed < 30 * 60 * 1000) return;
    }
    // 缓存过期或不存在，发请求验证
    axios.get('/admin/checkLogin').then(function(res) {
        if (res.data.code === 200) {
            sessionStorage.setItem('adminLoginTime', Date.now().toString());
        } else {
            sessionStorage.removeItem('adminLoginTime');
            alert('请先登录管理员');
            window.location.href = CTX + '/admin/index.html';
        }
    }).catch(function() {});
}

// 管理员退出登录
function adminLogout() {
    sessionStorage.removeItem('adminLoginTime');
    window.location.href = CTX + '/admin/logout';
}

console.log('[common.js] 上下文路径: "' + CTX + '"');

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
