// @ts-ignore
/* eslint-disable */
import request from "@/libs/request";

export async function getQuestionBankTestVoByQuestionBankIdUsingGet(
  params: { questionBankId?: string | number },
  options?: { [key: string]: any }
) {
  return request<any>("/api/questionBankTest/get/vo/byQuestionBankId", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

export async function submitQuestionBankTestUsingPost(body: any, options?: { [key: string]: any }) {
  return request<any>("/api/questionBankTest/submit", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

export async function getQuestionBankTestRecordVoUsingGet(
  params: { recordId?: string | number },
  options?: { [key: string]: any }
) {
  return request<any>("/api/questionBankTest/record/get/vo", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

export async function listMyQuestionBankTestRecordVoByPageUsingPost(
  body: any,
  options?: { [key: string]: any }
) {
  return request<any>("/api/questionBankTest/record/my/list/page/vo", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
