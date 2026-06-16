import React from "react";
import "./index.css";

/**
 * 全局底部栏组件
 * @constructor
 */
export default function GlobalFooter() {
  const currentYear = new Date().getFullYear();

  return (
    <div className="global-footer">
      <div>© {currentYear} 在线试题系统开发</div>
      <div>
        <a href="https://github.com/LightingForest" target="_blank">
          作者：计算机2202 许宗杰
        </a>
      </div>
    </div>
  );
}
