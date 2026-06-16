"use client";

import {
  batchDeleteQuestionsUsingPost,
  deleteQuestionUsingPost,
  listQuestionByPageUsingPost,
} from "@/api/questionController";
import UpdateBankModal from "@/app/admin/question/components/UpdateBankModal";
import MdEditor from "@/components/MdEditor";
import TagList from "@/components/TagList";
import { PlusOutlined } from "@ant-design/icons";
import type { ActionType, ProColumns } from "@ant-design/pro-components";
import { PageContainer, ProTable } from "@ant-design/pro-components";
import { Button, message, Popconfirm, Space, Table, Typography } from "antd";
import Link from "next/link";
import React, { useRef, useState } from "react";
import BatchAddQuestionsToBankModal from "./components/BatchAddQuestionsToBankModal";
import BatchRemoveQuestionsFromBankModal from "./components/BatchRemoveQuestionsFromBankModal";
import CreateModal from "./components/CreateModal";
import UpdateModal from "./components/UpdateModal";
import "./index.css";

/**
 * 题目管理页面
 */
const QuestionAdminPage: React.FC = () => {
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [updateModalVisible, setUpdateModalVisible] = useState(false);
  const [updateBankModalVisible, setUpdateBankModalVisible] = useState(false);
  const [batchAddQuestionsToBankModalVisible, setBatchAddQuestionsToBankModalVisible] = useState(false);
  const [batchRemoveQuestionsFromBankModalVisible, setBatchRemoveQuestionsFromBankModalVisible] =
    useState(false);
  const [selectedQuestionIdList, setSelectedQuestionIdList] = useState<number[]>([]);
  const [currentRow, setCurrentRow] = useState<API.Question>();
  const actionRef = useRef<ActionType>();

  const handleDelete = async (row: API.Question) => {
    const hide = message.loading("正在删除");
    if (!row) {
      return true;
    }
    try {
      await deleteQuestionUsingPost({
        id: row.id as any,
      });
      hide();
      message.success("删除成功");
      actionRef.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error(`删除失败，${error.message}`);
      return false;
    }
  };

  const handleBatchDelete = async (questionIdList: number[]) => {
    const hide = message.loading("正在操作");
    try {
      await batchDeleteQuestionsUsingPost({
        questionIdList,
      });
      hide();
      message.success("操作成功");
      actionRef.current?.reload();
    } catch (error: any) {
      hide();
      message.error(`操作失败，${error.message}`);
    }
  };

  const columns: ProColumns<API.Question>[] = [
    {
      title: "id",
      dataIndex: "id",
      valueType: "text",
      hideInForm: true,
    },
    {
      title: "所属题库",
      dataIndex: "questionBankId",
      hideInTable: true,
      hideInForm: true,
    },
    {
      title: "标题",
      dataIndex: "title",
      valueType: "text",
    },
    {
      title: "内容",
      dataIndex: "content",
      valueType: "text",
      hideInSearch: true,
      width: 240,
      renderFormItem: (_, { fieldProps }) => {
        return <MdEditor {...fieldProps} />;
      },
    },
    {
      title: "答案",
      dataIndex: "answer",
      valueType: "text",
      hideInSearch: true,
      width: 640,
      renderFormItem: (_, { fieldProps }) => {
        return <MdEditor {...fieldProps} />;
      },
    },
    {
      title: "标签",
      dataIndex: "tags",
      valueType: "select",
      fieldProps: {
        mode: "tags",
      },
      render: (_, record) => {
        const tagList = JSON.parse(record.tags || "[]");
        return <TagList tagList={tagList} />;
      },
    },
    {
      title: "创建用户",
      dataIndex: "userId",
      valueType: "text",
      hideInForm: true,
    },
    {
      title: "创建时间",
      sorter: true,
      dataIndex: "createTime",
      valueType: "dateTime",
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: "编辑时间",
      sorter: true,
      dataIndex: "editTime",
      valueType: "dateTime",
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: "更新时间",
      sorter: true,
      dataIndex: "updateTime",
      valueType: "dateTime",
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: "操作",
      dataIndex: "option",
      valueType: "option",
      render: (_, record) => (
        <Space size="middle">
          <Typography.Link
            onClick={() => {
              setCurrentRow(record);
              setUpdateModalVisible(true);
            }}
          >
            修改
          </Typography.Link>
          <Typography.Link
            onClick={() => {
              setCurrentRow(record);
              setUpdateBankModalVisible(true);
            }}
          >
            修改所属题库
          </Typography.Link>
          <Link href={`/admin/question/test/${record.id}`}>配置测试</Link>
          <Typography.Link type="danger" onClick={() => handleDelete(record)}>
            删除
          </Typography.Link>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer>
      <ProTable<API.Question>
        headerTitle="查询表格"
        actionRef={actionRef}
        scroll={{ x: true }}
        search={{ labelWidth: 120 }}
        rowKey="id"
        rowSelection={{
          selections: [Table.SELECTION_ALL, Table.SELECTION_INVERT],
          defaultSelectedRowKeys: [1],
        }}
        tableAlertRender={({ selectedRowKeys, onCleanSelected }) => {
          return (
            <Space size={24}>
              <span>
                已选 {selectedRowKeys.length} 项
                <a style={{ marginInlineStart: 8 }} onClick={onCleanSelected}>
                  取消选择
                </a>
              </span>
            </Space>
          );
        }}
        tableAlertOptionRender={({ selectedRowKeys }) => {
          return (
            <Space size={16}>
              <Button
                onClick={() => {
                  setSelectedQuestionIdList(selectedRowKeys as number[]);
                  setBatchAddQuestionsToBankModalVisible(true);
                }}
              >
                批量向题库添加题目
              </Button>
              <Button
                onClick={() => {
                  setSelectedQuestionIdList(selectedRowKeys as number[]);
                  setBatchRemoveQuestionsFromBankModalVisible(true);
                }}
              >
                批量从题库移除题目
              </Button>
              <Popconfirm
                title="确认删除"
                description="你确定要删除这些题目吗？"
                onConfirm={() => {
                  handleBatchDelete(selectedRowKeys as number[]);
                }}
                okText="确认"
                cancelText="取消"
              >
                <Button danger>批量删除题目</Button>
              </Popconfirm>
            </Space>
          );
        }}
        toolBarRender={() => [
          <Button
            type="primary"
            key="create"
            onClick={() => {
              setCreateModalVisible(true);
            }}
          >
            <PlusOutlined /> 新建
          </Button>,
        ]}
        request={async (params, sort, filter) => {
          const sortField = Object.keys(sort)?.[0];
          const sortOrder = sort?.[sortField] ?? undefined;

          const { data, code } = await listQuestionByPageUsingPost({
            ...params,
            sortField,
            sortOrder,
            ...filter,
          } as API.QuestionQueryRequest);

          return {
            success: code === 0,
            data: data?.records || [],
            total: Number(data?.total) || 0,
          };
        }}
        columns={columns}
      />
      <CreateModal
        visible={createModalVisible}
        columns={columns}
        onSubmit={() => {
          setCreateModalVisible(false);
          actionRef.current?.reload();
        }}
        onCancel={() => {
          setCreateModalVisible(false);
        }}
      />
      <UpdateModal
        visible={updateModalVisible}
        columns={columns}
        oldData={currentRow}
        onSubmit={() => {
          setUpdateModalVisible(false);
          setCurrentRow(undefined);
          actionRef.current?.reload();
        }}
        onCancel={() => {
          setUpdateModalVisible(false);
        }}
      />
      <UpdateBankModal
        visible={updateBankModalVisible}
        questionId={currentRow?.id}
        onCancel={() => {
          setUpdateBankModalVisible(false);
        }}
      />
      <BatchAddQuestionsToBankModal
        visible={batchAddQuestionsToBankModalVisible}
        questionIdList={selectedQuestionIdList}
        onSubmit={() => {
          setBatchAddQuestionsToBankModalVisible(false);
        }}
        onCancel={() => {
          setBatchAddQuestionsToBankModalVisible(false);
        }}
      />
      <BatchRemoveQuestionsFromBankModal
        visible={batchRemoveQuestionsFromBankModalVisible}
        questionIdList={selectedQuestionIdList}
        onSubmit={() => {
          setBatchRemoveQuestionsFromBankModalVisible(false);
        }}
        onCancel={() => {
          setBatchRemoveQuestionsFromBankModalVisible(false);
        }}
      />
    </PageContainer>
  );
};

export default QuestionAdminPage;
