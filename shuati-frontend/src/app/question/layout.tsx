import React from "react";
import { requireLogin } from "@/libs/auth";

export default function QuestionLayout({ children }: { children: React.ReactNode }) {
  requireLogin("/user/login");
  return children;
}
