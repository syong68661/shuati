"use client";

import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import { App, ConfigProvider, theme as antdTheme } from "antd";

type ThemeMode = "light" | "dark";

type ThemeContextValue = {
  mode: ThemeMode;
  setMode: (mode: ThemeMode) => void;
  toggleMode: () => void;
};

const STORAGE_KEY = "shuati-theme-mode";

const ThemeContext = createContext<ThemeContextValue | undefined>(undefined);

const getPreferredTheme = (): ThemeMode => {
  if (typeof window === "undefined") {
    return "light";
  }
  const savedMode = window.localStorage.getItem(STORAGE_KEY);
  if (savedMode === "light" || savedMode === "dark") {
    return savedMode;
  }
  return window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
};

export function useAppTheme() {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error("useAppTheme must be used within AppThemeProvider");
  }
  return context;
}

export default function AppThemeProvider({ children }: { children: React.ReactNode }) {
  const [mode, setMode] = useState<ThemeMode>("light");

  useEffect(() => {
    setMode(getPreferredTheme());
  }, []);

  useEffect(() => {
    document.documentElement.dataset.theme = mode;
    document.body.dataset.theme = mode;
    window.localStorage.setItem(STORAGE_KEY, mode);
  }, [mode]);

  const value = useMemo<ThemeContextValue>(
    () => ({
      mode,
      setMode,
      toggleMode: () => setMode((current) => (current === "light" ? "dark" : "light")),
    }),
    [mode]
  );

  const themeConfig = useMemo(
    () => ({
      algorithm: mode === "dark" ? antdTheme.darkAlgorithm : antdTheme.defaultAlgorithm,
      token: {
        colorPrimary: "#ffa116",
        colorInfo: "#ffa116",
        colorSuccess: "#2db55d",
        colorWarning: "#f59e0b",
        colorError: "#ef4444",
        borderRadius: 16,
        borderRadiusLG: 22,
        boxShadowSecondary:
          mode === "dark"
            ? "0 22px 80px rgba(0, 0, 0, 0.38)"
            : "0 18px 60px rgba(15, 23, 42, 0.12)",
        fontFamily:
          '"IBM Plex Sans","Segoe UI","PingFang SC","Hiragino Sans GB","Microsoft YaHei",sans-serif',
      },
      components: {
        Button: {
          controlHeight: 42,
          fontWeight: 600,
        },
        Card: {
          borderRadiusLG: 22,
        },
        Input: {
          controlHeight: 42,
        },
        Layout: {
          headerBg: "transparent",
        },
        Table: {
          borderColor:
            mode === "dark" ? "rgba(148, 163, 184, 0.16)" : "rgba(148, 163, 184, 0.18)",
        },
      },
    }),
    [mode]
  );

  return (
    <ThemeContext.Provider value={value}>
      <ConfigProvider theme={themeConfig}>
        <App>{children}</App>
      </ConfigProvider>
    </ThemeContext.Provider>
  );
}
