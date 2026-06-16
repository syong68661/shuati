"use client";

import { listMyQuestionBankTestRecordVoByPageUsingPost } from "@/api/questionBankTestController";
import { EyeOutlined } from "@ant-design/icons";
import { Button, Empty, List, Spin, Tag, Typography, message } from "antd";
import dayjs from "dayjs";
import Link from "next/link";
import { useEffect, useState } from "react";

type QuestionBankTestRecordVO = {
  recordId: string;
  questionBankId: string;
  questionBankTitle: string;
  score: number;
  rightCount: number;
  questionCount: number;
  createTime: string;
};

const UserQuestionBankTestRecordList = () => {
  const [loading, setLoading] = useState(true);
  const [recordList, setRecordList] = useState<QuestionBankTestRecordVO[]>([]);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);

  const loadData = async (page = current) => {
    setLoading(true);
    try {
      const res = await listMyQuestionBankTestRecordVoByPageUsingPost({
        current: page,
        pageSize: 10,
      });
      setRecordList(res.data?.records ?? []);
      setTotal(res.data?.total ?? 0);
      setCurrent(page);
    } catch (e: any) {
      message.error(`获取题库测试记录失败：${e.message}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData(1);
  }, []);

  if (loading) {
    return <Spin style={{ width: "100%", marginTop: 48 }} />;
  }

  if (!recordList.length) {
    return <Empty description="暂无题库测试记录" />;
  }

  return (
    <List
      itemLayout="vertical"
      dataSource={recordList}
      pagination={{
        current,
        pageSize: 10,
        total,
        onChange: loadData,
      }}
      renderItem={(item) => (
        <List.Item
          actions={[
            <Typography.Text key="time" type="secondary">
              {dayjs(item.createTime).format("YYYY-MM-DD HH:mm:ss")}
            </Typography.Text>,
            <Typography.Text key="count" type="secondary">
              答对 {item.rightCount} / {item.questionCount}
            </Typography.Text>,
            <Tag key="score" color="processing">
              {item.score} 分
            </Tag>,
            <Link href={`/bank/test-result/${item.recordId}`} key="link">
              <Button type="link" icon={<EyeOutlined />}>
                查看结果
              </Button>
            </Link>,
          ]}
        >
          <List.Item.Meta
            title={<Link href={`/bank/${item.questionBankId}`}>{item.questionBankTitle}</Link>}
            description="题库测试"
          />
        </List.Item>
      )}
    />
  );
};

export default UserQuestionBankTestRecordList;
