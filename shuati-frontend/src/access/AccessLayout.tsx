"use client";

import React from "react";
import { usePathname } from "next/navigation";
import { useSelector } from "react-redux";
import ACCESS_ENUM from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";
import Forbidden from "@/app/forbidden";
import { RootState } from "@/stores";
import { findAllMenuItemByPath } from "../../config/menu";

const AccessLayout: React.FC<
  Readonly<{
    children: React.ReactNode;
  }>
> = ({ children }) => {
  const pathname = usePathname();
  const loginUser = useSelector((state: RootState) => state.loginUser);

  const menu = findAllMenuItemByPath(pathname);
  const needAccess = menu?.access ?? ACCESS_ENUM.NOT_LOGIN;
  const canAccess = checkAccess(loginUser, needAccess);

  if (!canAccess) {
    return <Forbidden />;
  }

  return children;
};

export default AccessLayout;
