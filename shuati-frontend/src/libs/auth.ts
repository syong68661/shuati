import { cookies } from "next/headers";
import { redirect } from "next/navigation";

const SA_TOKEN_COOKIE_NAMES = ["mianshiya", "shuati"];

export function requireLogin(redirectPath = "/user/login") {
  const cookieStore = cookies();
  const token = SA_TOKEN_COOKIE_NAMES.map((cookieName) => cookieStore.get(cookieName)?.value).find(Boolean);
  if (!token) {
    redirect(redirectPath);
  }
}
