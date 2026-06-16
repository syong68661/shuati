"use client";

import {
  getQuestionTestVoByQuestionIdUsingGet,
  submitQuestionTestUsingPost,
} from "@/api/questionTestController";
import { ArrowLeftOutlined, CheckCircleFilled, FileTextOutlined } from "@ant-design/icons";
import {
  Button,
  Card,
  Empty,
  Form,
  Radio,
  Space,
  Spin,
  Typography,
  message,
} from "antd";
import { useRouter } from "next/navigation";
import { useEffect, useMemo, useState } from "react";

type QuestionTestItem = {
  id: string;
  title: string;
  sort: number;
  options: Record<string, string>;
};

type QuestionTestVO = {
  id: string;
  questionId: string;
  title: string;
  description?: string;
  itemList: QuestionTestItem[];
};

export default function QuestionTestPage({ params }: { params: { questionId: string } }) {
  const { questionId } = params;
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [questionTest, setQuestionTest] = useState<QuestionTestVO>();
  const [form] = Form.useForm();
  const router = useRouter();

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      try {
        const res = await getQuestionTestVoByQuestionIdUsingGet({
          questionId,
        });
        setQuestionTest(res.data);
      } catch (e: any) {
        message.error(`获取测试失败：${e.message}`);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [questionId]);

  const answers = Form.useWatch([], form) ?? {};

  const answeredCount = useMemo(() => {
    if (!questionTest?.itemList?.length) {
      return 0;
    }
    return questionTest.itemList.filter((item) => !!answers[`item_${item.id}`]).length;
  }, [answers, questionTest]);

  const scrollToQuestion = (itemId: string) => {
    document.getElementById(`question-${itemId}`)?.scrollIntoView({
      behavior: "smooth",
      block: "start",
    });
  };

  const onFinishFailed = (errorInfo: { errorFields?: { name?: (string | number)[] }[] }) => {
    const firstFieldName = String(errorInfo?.errorFields?.[0]?.name?.[0] ?? "");
    const itemId = firstFieldName.replace("item_", "");
    message.warning("还有未作答题目，请先完成后再提交");
    if (itemId) {
      scrollToQuestion(itemId);
    }
  };

  const onSubmit = async (values: Record<string, string>) => {
    if (!questionTest) {
      return;
    }
    const answerList = questionTest.itemList.map((item) => ({
      questionTestItemId: item.id,
      userAnswer: values[`item_${item.id}`],
    }));
    setSubmitting(true);
    try {
      const res = await submitQuestionTestUsingPost({
        questionTestId: questionTest.id,
        answerList,
      });
      message.success("提交成功");
      router.push(`/question/test-result/${res.data.recordId}`);
    } catch (e: any) {
      message.error(`提交失败：${e.message}`);
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <Spin style={{ width: "100%", marginTop: 120 }} size="large" />;
  }

  if (!questionTest) {
    return (
      <div style={{ padding: "80px 24px" }}>
        <Empty description="当前题目暂未配置测试" />
      </div>
    );
  }

  return (
    <div
      style={{
        minHeight: "100vh",
        background:
          "radial-gradient(circle at top, rgba(9, 105, 218, 0.08), transparent 32%), #f5f7fb",
      }}
    >
      <div
        style={{
          position: "sticky",
          top: 0,
          zIndex: 20,
          background: "rgba(255, 255, 255, 0.92)",
          backdropFilter: "blur(12px)",
          borderBottom: "1px solid #edf1f6",
        }}
      >
        <div
          style={{
            maxWidth: 1440,
            margin: "0 auto",
            padding: "14px 24px",
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            gap: 16,
          }}
        >
          <Button
            type="text"
            icon={<ArrowLeftOutlined />}
            onClick={() => router.push(`/question/${questionId}`)}
            style={{ fontSize: 18, height: 44 }}
          >
            退出答题
          </Button>
          <div style={{ textAlign: "center", flex: 1 }}>
            <div style={{ fontSize: 30, fontWeight: 700, color: "#1f2937" }}>{questionTest.title}</div>
            {questionTest.description ? (
              <div style={{ marginTop: 6, color: "#6b7280", fontSize: 15 }}>{questionTest.description}</div>
            ) : null}
          </div>
          <Button
            type="primary"
            size="large"
            htmlType="submit"
            form="question-test-form"
            loading={submitting}
            style={{ minWidth: 132, height: 44 }}
          >
            交卷
          </Button>
        </div>
      </div>

      <div style={{ maxWidth: 1440, margin: "0 auto", padding: "32px 24px 180px" }}>
        <Form
          id="question-test-form"
          form={form}
          layout="vertical"
          onFinish={onSubmit}
          onFinishFailed={onFinishFailed}
        >
          <Space direction="vertical" size={24} style={{ width: "100%" }}>
            {questionTest.itemList.map((item, index) => {
              const selectedValue = answers[`item_${item.id}`];
              return (
                <Card
                  key={item.id}
                  id={`question-${item.id}`}
                  style={{
                    borderRadius: 24,
                    border: "1px solid #edf1f6",
                    boxShadow: "0 18px 50px rgba(15, 23, 42, 0.06)",
                  }}
                  bodyStyle={{ padding: "32px 40px" }}
                >
                  <div
                    style={{
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "space-between",
                      gap: 16,
                      marginBottom: 24,
                    }}
                  >
                    <Typography.Title level={2} style={{ margin: 0, fontSize: 22 }}>
                      {index + 1}. {item.title}
                    </Typography.Title>
                    {selectedValue ? <CheckCircleFilled style={{ color: "#1677ff", fontSize: 22 }} /> : null}
                  </div>
                  <Form.Item
                    name={`item_${item.id}`}
                    style={{ marginBottom: 0 }}
                    rules={[{ required: true, message: "请选择答案后再提交" }]}
                  >
                    <Radio.Group style={{ width: "100%" }}>
                      <Space direction="vertical" size={18} style={{ width: "100%" }}>
                        {Object.entries(item.options || {}).map(([key, value]) => {
                          const active = selectedValue === key;
                          return (
                            <label
                              key={key}
                              style={{
                                display: "flex",
                                alignItems: "center",
                                gap: 16,
                                width: "100%",
                                padding: "18px 20px",
                                borderRadius: 18,
                                border: active ? "1px solid #1677ff" : "1px solid #e5e7eb",
                                background: active ? "rgba(22, 119, 255, 0.08)" : "#fff",
                                cursor: "pointer",
                                transition: "all 0.2s ease",
                              }}
                            >
                              <Radio value={key} style={{ marginTop: 2 }} />
                              <span
                                style={{
                                  fontSize: 24,
                                  fontWeight: 700,
                                  color: active ? "#1677ff" : "#374151",
                                  minWidth: 28,
                                }}
                              >
                                {key}
                              </span>
                              <span style={{ fontSize: 18, color: "#111827", lineHeight: 1.8 }}>{value}</span>
                            </label>
                          );
                        })}
                      </Space>
                    </Radio.Group>
                  </Form.Item>
                </Card>
              );
            })}
          </Space>
        </Form>
      </div>

      <div
        style={{
          position: "fixed",
          left: 24,
          right: 24,
          bottom: 24,
          zIndex: 30,
        }}
      >
        <Card
          style={{
            maxWidth: 1440,
            margin: "0 auto",
            borderRadius: 24,
            border: "1px solid #e8eef6",
            boxShadow: "0 24px 48px rgba(15, 23, 42, 0.12)",
          }}
          bodyStyle={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            gap: 24,
            padding: "18px 24px",
            flexWrap: "wrap",
          }}
        >
          <Space size={18} wrap>
            <Space size={8}>
              <FileTextOutlined style={{ color: "#64748b" }} />
              <span style={{ fontSize: 18, color: "#334155" }}>答题卡</span>
            </Space>
            <span style={{ fontSize: 16, color: "#1677ff" }}>已答 {answeredCount}</span>
            <span style={{ fontSize: 16, color: "#94a3b8" }}>
              未答 {questionTest.itemList.length - answeredCount}
            </span>
          </Space>

          <Space size={12} wrap>
            {questionTest.itemList.map((item, index) => {
              const active = !!answers[`item_${item.id}`];
              return (
                <Button
                  key={item.id}
                  shape="circle"
                  size="large"
                  onClick={() => scrollToQuestion(item.id)}
                  style={{
                    width: 46,
                    height: 46,
                    borderColor: active ? "#1677ff" : "#dbe3ef",
                    background: active ? "#1677ff" : "#fff",
                    color: active ? "#fff" : "#334155",
                    fontWeight: 600,
                  }}
                >
                  {index + 1}
                </Button>
              );
            })}
          </Space>

          <Button
            type="primary"
            size="large"
            htmlType="submit"
            form="question-test-form"
            loading={submitting}
            style={{ minWidth: 140, height: 46 }}
          >
            提交答案
          </Button>
        </Card>
      </div>
    </div>
  );
}
