import axios from "axios";

const isServer = typeof window === "undefined";

const browserBaseURL = process.env.NEXT_PUBLIC_API_BASE_URL || "";
const serverBaseURL =
  process.env.INTERNAL_API_BASE_URL ||
  process.env.NEXT_PUBLIC_API_BASE_URL ||
  "http://127.0.0.1:8101";

const myAxios = axios.create({
  baseURL: isServer ? serverBaseURL : browserBaseURL,
  timeout: 60000,
  withCredentials: true,
});

myAxios.interceptors.request.use(
  async function (config) {
    if (typeof window === "undefined") {
      try {
        const { cookies } = await import("next/headers");
        const cookieHeader = cookies()
          .getAll()
          .map(({ name, value }) => `${name}=${value}`)
          .join("; ");
        if (cookieHeader) {
          config.headers = {
            ...(config.headers ?? {}),
            Cookie: cookieHeader,
          };
        }
      } catch (e) {
        // Ignore cookie forwarding failures in non-Next runtimes.
      }
    }
    return config;
  },
  function (error) {
    return Promise.reject(error);
  },
);

myAxios.interceptors.response.use(
  function (response) {
    const { data } = response;
    const isBrowser = typeof window !== "undefined";
    const responseUrl = response?.request?.responseURL ?? "";

    if (data.code === 40100) {
      if (
        isBrowser &&
        !responseUrl.includes("user/get/login") &&
        !window.location.pathname.includes("/user/login")
      ) {
        window.location.href = `/user/login?redirect=${window.location.href}`;
      }
    } else if (data.code !== 0) {
      throw new Error(data.message ?? "服务器错误");
    }

    return data;
  },
  function (error) {
    return Promise.reject(error);
  },
);

export default myAxios;
