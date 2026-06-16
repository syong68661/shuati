"use client";

import { getQuestionBankTestRecordVoUsingGet } from "@/api/questionBankTestController";
import { ArrowLeftOutlined, CheckCircleFilled, CloseCircleFilled, TrophyOutlined } from "@ant-design/icons";
import { Button, Card, Empty, Progress, Space, Spin, Tag, Typography, message } from "antd";
import { useRouter } from "next/navigation";
import { useEffect, useMemo, useState } from "react";

type QuestionBankTestResult = {
  recordId: string;
  questionBankId: string;
  questionBankTitle: string;
  score: number;
  rightCount: number;
  questionCount: number;
  itemResultList: {
    questionTestItemId: string;
    title: string;
    options: Record<string, string>;
    userAnswer: string;
    rightAnswer: string;
    isCorrect: boolean;
    analysis?: string;
  }[];
};

export default function QuestionBankTestResultPage({ params }: { params: { recordId: string } }) {
  const { recordId } = params;
  const [loading, setLoading] = useState(true);
  const [result, setResult] = useState<QuestionBankTestResult>();
  const router = useRouter();

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      try {
        const res = await getQuestionBankTestRecordVoUsingGet({
          recordId,
        });
        setResult(res.data);
      } catch (e: any) {
        message.error(`获取结果失败：${e.message}`);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [recordId]);

  const accuracy = useMemo(() => {
    if (!result?.questionCount) {
      return 0;
    }
    return Math.round((result.rightCount / result.questionCount) * 100);
  }, [result]);

  if (loading) {
    return <Spin style={{ width: "100%", marginTop: 120 }} size="large" />;
  }

  if (!result) {
    return (
      <div style={{ padding: "80px 24px" }}>
        <Empty description="未找到测试结果" />
      </div>
    );
  }

  return (
    <div style={{ minHeight: "100vh", background: "radial-gradient(circle at top, rgba(22, 119, 255, 0.08), transparent 28%), #f5f7fb", padding: "32px 24px 64px" }}>
      <div style={{ maxWidth: 1440, margin: "0 auto" }}>
        <Card style={{ borderRadius: 28, border: "1px solid #e8eef6", boxShadow: "0 24px 60px rgba(15, 23, 42, 0.08)", marginBottom: 24 }} bodyStyle={{ padding: "28px 32px" }}>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 24, flexWrap: "wrap" }}>
            <div>
              <Space size={12} align="center">
                <TrophyOutlined style={{ fontSize: 28, color: "#1677ff" }} />
                <Typography.Title level={1} style={{ margin: 0, fontSize: 40 }}>
                  {result.questionBankTitle} · 测试结果
                </Typography.Title>
              </Space>
              <Typography.Paragraph style={{ margin: "12px 0 0", color: "#64748b", fontSize: 16 }}>
                本次题库测试已完成判分，你可以逐题查看正确答案与解析。
              </Typography.Paragraph>
            </div>
            <Space size={16} wrap>
              <Button icon={<ArrowLeftOutlined />} size="large" onClick={() => router.back()}>
                返回
              </Button>
              <Button type="primary" size="large" href={`/bank/${result.questionBankId}`}>
                返回题库
              </Button>
            </Space>
          </div>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))", gap: 16, marginTop: 28 }}>
            <Card bordered={false} style={{ background: "#f8fbff", borderRadius: 20 }}>
              <Typography.Text style={{ color: "#64748b", fontSize: 15 }}>得分</Typography.Text>
              <div style={{ fontSize: 44, fontWeight: 700, color: "#111827", lineHeight: 1.2 }}>{result.score}</div>
            </Card>
            <Card bordered={false} style={{ background: "#f8fbff", borderRadius: 20 }}>
              <Typography.Text style={{ color: "#64748b", fontSize: 15 }}>答对题数</Typography.Text>
              <div style={{ fontSize: 44, fontWeight: 700, color: "#111827", lineHeight: 1.2 }}>
                {result.rightCount} / {result.questionCount}
              </div>
            </Card>
            <Card bordered={false} style={{ background: "#f8fbff", borderRadius: 20 }}>
              <Typography.Text style={{ color: "#64748b", fontSize: 15 }}>正确率</Typography.Text>
              <div style={{ marginTop: 12 }}>
                <Progress percent={accuracy} strokeColor="#1677ff" showInfo />
              </div>
            </Card>
          </div>
        </Card>

        <Space direction="vertical" size={20} style={{ width: "100%" }}>
          {result.itemResultList.map((item, index) => (
            <Card key={item.questionTestItemId} style={{ borderRadius: 24, border: "1px solid #e8eef6", boxShadow: "0 18px 50px rgba(15, 23, 42, 0.05)" }} bodyStyle={{ padding: "28px 32px" }}>
              <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 16, marginBottom: 20, flexWrap: "wrap" }}>
                <Typography.Title level={3} style={{ margin: 0, fontSize: 26 }}>
                  第 {index + 1} 题
                </Typography.Title>
                {item.isCorrect ? (
                  <Tag color="success" style={{ fontSize: 15, padding: "4px 12px" }}>
                    <CheckCircleFilled /> 正确
                  </Tag>
                ) : (
                  <Tag color="error" style={{ fontSize: 15, padding: "4px 12px" }}>
                    <CloseCircleFilled /> 错误
                  </Tag>
                )}
              </div>
              <Typography.Paragraph style={{ fontSize: 24, fontWeight: 600, color: "#111827", lineHeight: 1.7 }}>
                {item.title}
              </Typography.Paragraph>
              <Space direction="vertical" size={14} style={{ width: "100%", marginTop: 20 }}>
                {Object.entries(item.options || {}).map(([key, value]) => {
                  const isRight = item.rightAnswer === key;
                  const isSelected = item.userAnswer === key;
                  return (
                    <div
                      key={key}
                      style={{
                        padding: "16px 18px",
                        borderRadius: 18,
                        border: isRight ? "1px solid #22c55e" : isSelected ? "1px solid #f59e0b" : "1px solid #e5e7eb",
                        background: isRight ? "rgba(34, 197, 94, 0.08)" : isSelected ? "rgba(245, 158, 11, 0.08)" : "#fff",
                        fontSize: 18,
                        color: "#111827",
                      }}
                    >
                      <span style={{ fontWeight: 700, marginRight: 10 }}>{key}.</span>
                      {value}
                    </div>
                  );
                })}
              </Space>
              <div style={{ display: "flex", gap: 12, flexWrap: "wrap", marginTop: 22 }}>
                <Tag color={item.isCorrect ? "success" : "error"} style={{ fontSize: 15, padding: "6px 12px" }}>
                  你的答案：{item.userAnswer || "未作答"}
                </Tag>
                <Tag color="processing" style={{ fontSize: 15, padding: "6px 12px" }}>
                  正确答案：{item.rightAnswer}
                </Tag>
              </div>
              {item.analysis ? (
                <Card bordered={false} style={{ marginTop: 20, borderRadius: 18, background: "#f8fafc" }}>
                  <Typography.Text style={{ display: "block", fontSize: 16, color: "#64748b", marginBottom: 8 }}>解析</Typography.Text>
                  <Typography.Paragraph style={{ margin: 0, fontSize: 17, color: "#1f2937", lineHeight: 1.85 }}>
                    {item.analysis}
                  </Typography.Paragraph>
                </Card>
              ) : null}
            </Card>
          ))}
        </Space>
      </div>
    </div>
  );
}
