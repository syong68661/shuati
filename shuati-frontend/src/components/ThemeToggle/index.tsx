"use client";

import { BulbOutlined, MoonOutlined } from "@ant-design/icons";
import { Button, Tooltip } from "antd";
import { useAppTheme } from "@/components/AppThemeProvider";

export default function ThemeToggle() {
  const { mode, toggleMode } = useAppTheme();

  return (
    <Tooltip title={mode === "light" ? "切换到夜间模式" : "切换到日间模式"}>
      <Button
        className="theme-toggle-button"
        shape="circle"
        size="large"
        type="text"
        aria-label="toggle theme"
        icon={mode === "light" ? <MoonOutlined /> : <BulbOutlined />}
        onClick={toggleMode}
      />
    </Tooltip>
  );
}
