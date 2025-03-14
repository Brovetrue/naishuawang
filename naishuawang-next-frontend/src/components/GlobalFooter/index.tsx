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
      <div>© {currentYear} 刷题平台</div>
      <div>
        <a href="https://gitee.com/yotta_chiu/naishuawang_backend" target="_blank">
          作者：Chiu
        </a>
      </div>
    </div>
  );
}
