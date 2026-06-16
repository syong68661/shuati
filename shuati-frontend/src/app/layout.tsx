"use client";

import { AntdRegistry } from "@ant-design/nextjs-registry";
import React, { useCallback, useEffect } from "react";
import { Provider, useDispatch } from "react-redux";
import { getLoginUserUsingGet } from "@/api/userController";
import AccessLayout from "@/access/AccessLayout";
import AppThemeProvider from "@/components/AppThemeProvider";
import BasicLayout from "@/layouts/BasicLayout";
import store, { AppDispatch } from "@/stores";
import { setLoginUser } from "@/stores/loginUser";
import "./globals.css";

const InitLayout: React.FC<
  Readonly<{
    children: React.ReactNode;
  }>
> = ({ children }) => {
  const dispatch = useDispatch<AppDispatch>();

  const doInitLoginUser = useCallback(async () => {
    try {
      const res = await getLoginUserUsingGet();
      if (res.data) {
        dispatch(setLoginUser(res.data as any));
      }
    } catch (e) {
      // Ignore unauthenticated state on initial load.
    }
  }, [dispatch]);

  useEffect(() => {
    doInitLoginUser();
  }, [doInitLoginUser]);

  return children;
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="zh" suppressHydrationWarning>
      <body>
        <AntdRegistry>
          <Provider store={store}>
            <AppThemeProvider>
              <InitLayout>
                <BasicLayout>
                  <AccessLayout>{children}</AccessLayout>
                </BasicLayout>
              </InitLayout>
            </AppThemeProvider>
          </Provider>
        </AntdRegistry>
      </body>
    </html>
  );
}
