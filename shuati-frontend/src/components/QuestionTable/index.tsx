"use client";

import {
  listQuestionTagListUsingGet,
  searchQuestionVoByPageUsingPost,
} from "@/api/questionController";
import TagList from "@/components/TagList";
import type { ActionType, ProColumns } from "@ant-design/pro-components";
import { ProTable } from "@ant-design/pro-components";
import { TablePaginationConfig } from "antd";
import Link from "next/link";
import React, { useEffect, useRef, useState } from "react";
import "./index.css";

interface Props {
  defaultQuestionList?: API.QuestionVO[];
  defaultTotal?: number;
  defaultSearchParams?: API.QuestionQueryRequest;
}

const QuestionTable: React.FC<Props> = (props: Props) => {
  const { defaultQuestionList, defaultTotal, defaultSearchParams = {} } = props;
  const actionRef = useRef<ActionType>();
  const [questionList, setQuestionList] = useState<API.QuestionVO[]>(defaultQuestionList || []);
  const [total, setTotal] = useState<number>(defaultTotal || 0);
  const [init, setInit] = useState<boolean>(true);
  const [tagOptions, setTagOptions] = useState<{ label: string; value: string }[]>([]);

  useEffect(() => {
    const loadTagOptions = async () => {
      try {
        const res = await listQuestionTagListUsingGet();
        const options = (res.data || []).map((tag: string) => ({
          label: tag,
          value: tag,
        }));
        setTagOptions(options);
      } catch (e) {
        console.error("获取标签列表失败", e);
      }
    };
    loadTagOptions();
  }, []);

  const columns: ProColumns<API.QuestionVO>[] = [
    {
      title: "搜索",
      dataIndex: "searchText",
      valueType: "text",
      hideInTable: true,
    },
    {
      title: "标题",
      dataIndex: "title",
      valueType: "text",
      hideInSearch: true,
      render: (_, record) => <Link href={`/question/${record.id}`}>{record.title}</Link>,
    },
    {
      title: "标签",
      dataIndex: "tags",
      valueType: "select",
      fieldProps: {
        mode: "multiple",
        options: tagOptions,
        showSearch: true,
        placeholder: "请选择标签",
      },
      render: (_, record) => <TagList tagList={record.tagList} linkToQuestions />,
    },
  ];

  return (
    <div className="question-table">
      <ProTable<API.QuestionVO>
        actionRef={actionRef}
        size="large"
        rowKey="id"
        search={{
          labelWidth: "auto",
        }}
        form={{
          initialValues: defaultSearchParams,
        }}
        dataSource={questionList}
        pagination={
          {
            pageSize: 12,
            showTotal: (allTotal) => `共 ${allTotal} 条`,
            showSizeChanger: false,
            total,
          } as TablePaginationConfig
        }
        request={async (params, sort, filter) => {
          if (init) {
            setInit(false);
            if (defaultQuestionList && defaultTotal) {
              return {
                success: true,
                data: defaultQuestionList,
                total: defaultTotal,
              };
            }
          }

          const sortField = Object.keys(sort)?.[0] || "createTime";
          const sortOrder = sort?.[sortField] || "descend";
          const requestParams = {
            ...params,
            tags: (params as any).tags,
            sortField,
            sortOrder,
            ...filter,
          } as API.QuestionQueryRequest;

          const response = await searchQuestionVoByPageUsingPost(requestParams);
          const responseData = response.data as any;
          const newData = responseData?.records || [];
          const newTotal = responseData?.total || 0;
          setQuestionList(newData);
          setTotal(newTotal);

          return {
            success: true,
            data: newData,
            total: newTotal,
          };
        }}
        columns={columns}
      />
    </div>
  );
};

export default QuestionTable;
