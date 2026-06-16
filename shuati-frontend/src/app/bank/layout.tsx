import React from "react";
import { requireLogin } from "@/libs/auth";

export default function BankLayout({ children }: { children: React.ReactNode }) {
  requireLogin("/user/login");
  return children;
}
