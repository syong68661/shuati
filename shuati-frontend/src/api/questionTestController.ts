// @ts-ignore
/* eslint-disable */
import request from "@/libs/request";

export async function addQuestionTestUsingPost(body: any, options?: { [key: string]: any }) {
  return request<any>("/api/questionTest/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

export async function updateQuestionTestUsingPost(body: any, options?: { [key: string]: any }) {
  return request<any>("/api/questionTest/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

export async function deleteQuestionTestUsingPost(body: any, options?: { [key: string]: any }) {
  return request<any>("/api/questionTest/delete", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

export async function getQuestionTestByQuestionIdUsingGet(
  params: { questionId?: string | number },
  options?: { [key: string]: any }
) {
  return request<any>("/api/questionTest/get/byQuestionId", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

export async function getQuestionTestVoByQuestionIdUsingGet(
  params: { questionId?: string | number },
  options?: { [key: string]: any }
) {
  return request<any>("/api/questionTest/get/vo/byQuestionId", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

export async function submitQuestionTestUsingPost(body: any, options?: { [key: string]: any }) {
  return request<any>("/api/questionTest/submit", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

export async function getQuestionTestRecordVoUsingGet(
  params: { recordId?: string | number },
  options?: { [key: string]: any }
) {
  return request<any>("/api/questionTest/record/get/vo", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

export async function listMyQuestionTestRecordVoByPageUsingPost(
  body: any,
  options?: { [key: string]: any }
) {
  return request<any>("/api/questionTest/record/my/list/page/vo", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
