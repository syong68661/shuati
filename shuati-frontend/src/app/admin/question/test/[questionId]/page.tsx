"use client";

import {
  addQuestionTestUsingPost,
  deleteQuestionTestUsingPost,
  getQuestionTestByQuestionIdUsingGet,
  updateQuestionTestUsingPost,
} from "@/api/questionTestController";
import { MinusCircleOutlined, PlusOutlined } from "@ant-design/icons";
import {
  Button,
  Card,
  Col,
  Form,
  Input,
  InputNumber,
  Popconfirm,
  Radio,
  Row,
  Space,
  Typography,
  message,
} from "antd";
import Link from "next/link";
import { useEffect, useState } from "react";

export default function QuestionTestAdminConfigPage({ params }: { params: { questionId: string } }) {
  const { questionId } = params;
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [currentTestId, setCurrentTestId] = useState<string>();

  useEffect(() => {
    const loadData = async () => {
      try {
        const res = await getQuestionTestByQuestionIdUsingGet({
          questionId,
        });
        const data = res.data;
        if (data) {
          setCurrentTestId(data.id);
          form.setFieldsValue({
            id: data.id,
            questionId: data.questionId,
            title: data.title,
            description: data.description,
            status: data.status ?? 1,
            itemList:
              data.itemList?.map((item: any, index: number) => ({
                ...item,
                sort: item.sort ?? index + 1,
              })) ?? [],
          });
        } else {
          form.setFieldsValue({
            questionId,
            status: 1,
            itemList: new Array(4).fill(null).map((_, index) => ({
              sort: index + 1,
            })),
          });
        }
      } catch (e: any) {
        message.error(`加载测试配置失败：${e.message}`);
      }
    };
    loadData();
  }, [form, questionId]);

  const onFinish = async (values: any) => {
    if (!values.itemList || values.itemList.length < 4 || values.itemList.length > 5) {
      message.error("测试题数量必须为 4 到 5 题");
      return;
    }
    setLoading(true);
    try {
      if (currentTestId) {
        await updateQuestionTestUsingPost({
          ...values,
          id: currentTestId,
          questionId,
        });
      } else {
        const res = await addQuestionTestUsingPost({
          ...values,
          questionId,
        });
        setCurrentTestId(res.data);
      }
      message.success("保存成功");
    } catch (e: any) {
      message.error(`保存失败：${e.message}`);
    } finally {
      setLoading(false);
    }
  };

  const onDelete = async () => {
    if (!currentTestId) {
      return;
    }
    setLoading(true);
    try {
      await deleteQuestionTestUsingPost({ id: currentTestId });
      setCurrentTestId(undefined);
      form.resetFields();
      form.setFieldsValue({
        questionId,
        status: 1,
        itemList: new Array(4).fill(null).map((_, index) => ({
          sort: index + 1,
        })),
      });
      message.success("删除成功");
    } catch (e: any) {
      message.error(`删除失败：${e.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        maxWidth: 1280,
        margin: "0 auto",
        padding: "32px 24px 48px",
      }}
    >
      <Card
        style={{
          borderRadius: 24,
          marginBottom: 20,
        }}
        bodyStyle={{ padding: "28px 32px" }}
      >
        <Space direction="vertical" size={8} style={{ width: "100%" }}>
          <Typography.Title level={2} style={{ margin: 0 }}>
            配置题目测试
          </Typography.Title>
          <Typography.Text type="secondary">
            为当前题目维护一套 4 到 5 题的选择题测试，用户提交后可立即看到判题结果和解析。
          </Typography.Text>
          <Link href="/admin/question">返回题目管理</Link>
        </Space>
      </Card>

      <Form form={form} layout="vertical" onFinish={onFinish}>
        <Card
          title="基础信息"
          style={{ borderRadius: 24, marginBottom: 20 }}
          bodyStyle={{ padding: "28px 32px" }}
        >
          <Row gutter={24}>
            <Col xs={24} lg={12}>
              <Form.Item
                name="title"
                label="测试标题"
                rules={[{ required: true, message: "请输入测试标题" }]}
              >
                <Input size="large" placeholder="例如：数据库概述基础测试" />
              </Form.Item>
            </Col>
            <Col xs={24} lg={12}>
              <Form.Item name="status" label="状态" initialValue={1}>
                <Radio.Group
                  options={[
                    { label: "启用", value: 1 },
                    { label: "禁用", value: 0 },
                  ]}
                />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item name="description" label="测试说明">
                <Input.TextArea rows={4} placeholder="可填写测试范围、适用对象或答题说明" />
              </Form.Item>
            </Col>
          </Row>
        </Card>

        <Form.List name="itemList">
          {(fields, { add, remove }) => (
            <Card
              title="测试题"
              style={{ borderRadius: 24 }}
              bodyStyle={{ padding: "28px 32px" }}
              extra={
                <Button
                  icon={<PlusOutlined />}
                  disabled={fields.length >= 5}
                  onClick={() => add({ sort: fields.length + 1 })}
                >
                  添加题目
                </Button>
              }
            >
              <Space direction="vertical" style={{ width: "100%" }} size={20}>
                {fields.map((field, index) => (
                  <Card
                    key={field.key}
                    type="inner"
                    style={{ borderRadius: 20 }}
                    bodyStyle={{ padding: "24px 24px 8px" }}
                    title={`第 ${index + 1} 题`}
                    extra={
                      fields.length > 4 ? (
                        <MinusCircleOutlined
                          onClick={() => remove(field.name)}
                          style={{ color: "#ef4444", fontSize: 18 }}
                        />
                      ) : null
                    }
                  >
                    <Row gutter={20}>
                      <Col span={24}>
                        <Form.Item
                          {...field}
                          name={[field.name, "title"]}
                          label="题干"
                          rules={[{ required: true, message: "请输入题干" }]}
                        >
                          <Input.TextArea rows={3} placeholder="请输入测试题题干" />
                        </Form.Item>
                      </Col>

                      <Col xs={24} md={8}>
                        <Form.Item {...field} name={[field.name, "sort"]} label="排序">
                          <InputNumber min={1} style={{ width: "100%" }} />
                        </Form.Item>
                      </Col>
                      <Col xs={24} md={16}>
                        <Form.Item
                          {...field}
                          name={[field.name, "answer"]}
                          label="正确答案"
                          rules={[{ required: true, message: "请选择正确答案" }]}
                        >
                          <Radio.Group
                            options={[
                              { label: "A", value: "A" },
                              { label: "B", value: "B" },
                              { label: "C", value: "C" },
                              { label: "D", value: "D" },
                              { label: "E", value: "E" },
                            ]}
                          />
                        </Form.Item>
                      </Col>

                      <Col xs={24} md={12}>
                        <Form.Item
                          {...field}
                          name={[field.name, "optionA"]}
                          label="选项 A"
                          rules={[{ required: true, message: "请输入选项 A" }]}
                        >
                          <Input placeholder="请输入选项 A" />
                        </Form.Item>
                      </Col>
                      <Col xs={24} md={12}>
                        <Form.Item
                          {...field}
                          name={[field.name, "optionB"]}
                          label="选项 B"
                          rules={[{ required: true, message: "请输入选项 B" }]}
                        >
                          <Input placeholder="请输入选项 B" />
                        </Form.Item>
                      </Col>
                      <Col xs={24} md={12}>
                        <Form.Item
                          {...field}
                          name={[field.name, "optionC"]}
                          label="选项 C"
                          rules={[{ required: true, message: "请输入选项 C" }]}
                        >
                          <Input placeholder="请输入选项 C" />
                        </Form.Item>
                      </Col>
                      <Col xs={24} md={12}>
                        <Form.Item
                          {...field}
                          name={[field.name, "optionD"]}
                          label="选项 D"
                          rules={[{ required: true, message: "请输入选项 D" }]}
                        >
                          <Input placeholder="请输入选项 D" />
                        </Form.Item>
                      </Col>
                      <Col xs={24} md={12}>
                        <Form.Item {...field} name={[field.name, "optionE"]} label="选项 E">
                          <Input placeholder="如无可留空" />
                        </Form.Item>
                      </Col>
                      <Col span={24}>
                        <Form.Item {...field} name={[field.name, "analysis"]} label="解析">
                          <Input.TextArea rows={4} placeholder="请输入答案解析，帮助用户复盘" />
                        </Form.Item>
                      </Col>
                    </Row>
                  </Card>
                ))}
              </Space>
            </Card>
          )}
        </Form.List>

        <div style={{ marginTop: 24 }}>
          <Space size={12}>
            <Button type="primary" htmlType="submit" loading={loading} size="large">
              保存测试配置
            </Button>
            {currentTestId ? (
              <Popconfirm title="确认删除该测试？" onConfirm={onDelete}>
                <Button danger loading={loading} size="large">
                  删除测试
                </Button>
              </Popconfirm>
            ) : null}
          </Space>
        </div>
      </Form>
    </div>
  );
}
