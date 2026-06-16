// @ts-ignore
/* eslint-disable */
import request from "@/libs/request";

export async function getHomeStatisticsUsingGet(options?: { [key: string]: any }) {
  return request<any>("/api/home/statistics", {
    method: "GET",
    ...(options || {}),
  });
}
