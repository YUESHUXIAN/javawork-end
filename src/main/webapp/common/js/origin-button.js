/**
 * Origin Button - 源按钮动画效果
 * 实现鼠标进入点的圆形填充动画
 */

(function() {
    'use strict';

    // 计算覆盖整个按钮所需的圆形直径
    function getCoverDiameter(width, height, x, y) {
        return Math.ceil(
            2 * Math.max(
                Math.hypot(x, y),
                Math.hypot(width - x, y),
                Math.hypot(x, height - y),
                Math.hypot(width - x, height - y)
            )
        );
    }

    // 初始化单个按钮
    function initOriginButton(button) {
        // 创建填充层
        var fill = document.createElement('span');
        fill.className = 'origin-btn-fill';
        button.insertBefore(fill, button.firstChild);

        // 创建内容层包裹器（如果还没有）
        var content = button.querySelector('.origin-btn-content');
        if (!content) {
            content = document.createElement('span');
            content.className = 'origin-btn-content';
            // 将除 fill 以外的所有子元素移到 content 中
            while (button.childNodes.length > 1) {
                content.appendChild(button.childNodes[1]);
            }
            button.appendChild(content);
        }

        // 鼠标进入时更新填充位置和大小
        button.addEventListener('pointerenter', function(e) {
            if (button.disabled) return;
            var rect = button.getBoundingClientRect();
            var x = e.clientX - rect.left;
            var y = e.clientY - rect.top;
            var diameter = getCoverDiameter(rect.width, rect.height, x, y);

            fill.style.left = x + 'px';
            fill.style.top = y + 'px';
            fill.style.width = diameter + 'px';
            fill.style.height = diameter + 'px';
        });

        // 键盘聚焦时从中心填充
        button.addEventListener('focus', function() {
            if (button.disabled) return;
            if (button.matches(':focus-visible')) {
                var rect = button.getBoundingClientRect();
                var x = rect.width / 2;
                var y = rect.height / 2;
                var diameter = getCoverDiameter(rect.width, rect.height, x, y);

                fill.style.left = x + 'px';
                fill.style.top = y + 'px';
                fill.style.width = diameter + 'px';
                fill.style.height = diameter + 'px';
            }
        });
    }

    // 初始化所有 origin-btn
    function initAllButtons() {
        var buttons = document.querySelectorAll('.origin-btn');
        buttons.forEach(function(btn) {
            if (!btn.dataset.originInit) {
                btn.dataset.originInit = 'true';
                initOriginButton(btn);
            }
        });
    }

    // DOM 加载完成后初始化
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initAllButtons);
    } else {
        initAllButtons();
    }

    // 暴露全局初始化函数
    window.initOriginButtons = initAllButtons;
})();
