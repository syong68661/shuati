"use client";

import { GithubFilled, LogoutOutlined, UserOutlined } from "@ant-design/icons";
import { ProLayout } from "@ant-design/pro-components";
import { Dropdown, message } from "antd";
import Image from "next/image";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import getAccessibleMenus from "@/access/menuAccess";
import { userLogoutUsingPost } from "@/api/userController";
import GlobalFooter from "@/components/GlobalFooter";
import ThemeToggle from "@/components/ThemeToggle";
import { DEFAULT_USER } from "@/constants/user";
import SearchInput from "@/layouts/BasicLayout/components/SearchInput";
import { AppDispatch, RootState } from "@/stores";
import { setLoginUser } from "@/stores/loginUser";
import { menus } from "../../../config/menu";
import "./index.css";

interface Props {
  children: React.ReactNode;
}

export default function BasicLayout({ children }: Props) {
  const pathname = usePathname();
  const loginUser = useSelector((state: RootState) => state.loginUser);
  const dispatch = useDispatch<AppDispatch>();
  const router = useRouter();

  const userLogout = async () => {
    try {
      await userLogoutUsingPost();
      message.success("已退出登录");
      dispatch(setLoginUser(DEFAULT_USER));
      router.push("/user/login");
    } catch (e: any) {
      message.error(`操作失败：${e.message}`);
    }
  };

  return (
    <div id="basicLayout" className="basic-layout-shell">
      <ProLayout
        title="在线试题系统"
        layout="top"
        logo={<Image src="/assets/logo.png" height={32} width={32} alt="在线试题系统" />}
        location={{
          pathname,
        }}
        avatarProps={{
          src: loginUser.userAvatar || "/assets/logo.png",
          size: "small",
          title: loginUser.userName || "用户",
          render: (_, dom) => {
            if (!loginUser.id) {
              return <div onClick={() => router.push("/user/login")}>{dom}</div>;
            }
            return (
              <Dropdown
                menu={{
                  items: [
                    {
                      key: "userCenter",
                      icon: <UserOutlined />,
                      label: "个人中心",
                    },
                    {
                      key: "logout",
                      icon: <LogoutOutlined />,
                      label: "退出登录",
                    },
                  ],
                  onClick: async (event: { key: React.Key }) => {
                    const { key } = event;
                    if (key === "logout") {
                      userLogout();
                    } else if (key === "userCenter") {
                      router.push("/user/center");
                    }
                  },
                }}
              >
                {dom}
              </Dropdown>
            );
          },
        }}
        actionsRender={(props) => {
          if (props.isMobile) {
            return [];
          }
          return [
            <SearchInput key="search" />,
            <ThemeToggle key="theme" />,
            <a
              key="github"
              href="https://github.com/LightingForest"
              target="_blank"
              className="basic-layout-action-link"
            >
              <GithubFilled />
            </a>,
          ];
        }}
        headerTitleRender={(logo, title) => <a className="basic-layout-brand">{logo}{title}</a>}
        footerRender={() => <GlobalFooter />}
        menuDataRender={() => getAccessibleMenus(loginUser, menus)}
        menuItemRender={(item, dom) => (
          <Link href={item.path || "/"} target={item.target}>
            {dom}
          </Link>
        )}
      >
        {children}
      </ProLayout>
    </div>
  );
}
