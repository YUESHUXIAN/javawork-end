/* common.js - 公共 JavaScript 工具库 */

// 1. 统一的提示消息函数 (现在只是简单的 alert，以后可以扩展成漂亮的 Toast 弹窗)
function showMessage(msg, isSuccess) {
    if (isSuccess) {
        alert("✅ 成功: " + msg);
    } else {
        alert("❌ 失败: " + msg);
    }
}

// 2. 通用的日期格式化工具 (如果你的后台传的是时间戳，可以用这个)
function formatDate(timestamp) {
    if (!timestamp) return "-";
    let date = new Date(timestamp);
    let y = date.getFullYear();
    let m = ("0" + (date.getMonth() + 1)).slice(-2);
    let d = ("0" + date.getDate()).slice(-2);
    let h = ("0" + date.getHours()).slice(-2);
    let mi = ("0" + date.getMinutes()).slice(-2);
    let s = ("0" + date.getSeconds()).slice(-2);
    return y + "-" + m + "-" + d + " " + h + ":" + mi + ":" + s;
}

// 3. 页面加载完成后执行的基础初始化
$(document).ready(function() {
    console.log("✅ 公共 JS 加载完成，页面准备就绪。");

    // 如果页面有 "返回" 按钮，绑定点击事件
    $('.btn-back').click(function(){
        window.history.back();
    });
});