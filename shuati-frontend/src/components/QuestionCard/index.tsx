"use client";

import { PlayCircleOutlined, ReadOutlined, SafetyCertificateOutlined } from "@ant-design/icons";
import MdViewer from "@/components/MdViewer";
import TagList from "@/components/TagList";
import useAddUserSignInRecord from "@/hooks/useAddUserSignInRecord";
import { Button, Card, Space, Typography } from "antd";
import Link from "next/link";
import "./index.css";

interface Props {
  question: API.QuestionVO;
}

export default function QuestionCard(props: Props) {
  const { question } = props;

  useAddUserSignInRecord();

  return (
    <div className="question-card">
      <Card className="question-card-main glass-card" bordered={false}>
        <div className="question-card-hero">
          <div className="question-card-badge">
            <ReadOutlined />
            Problem Detail
          </div>
          <Typography.Title level={1} className="question-card-title">
            {question.title}
          </Typography.Title>
          <div className="question-card-tag-wrap">
            <TagList tagList={question.tagList} />
          </div>
          <Space wrap size={12} className="question-card-actions">
            <Link href={`/question/${question.id}/test`}>
              <Button type="primary" size="large" icon={<PlayCircleOutlined />}>
                开始测试
              </Button>
            </Link>
            <Button size="large" icon={<SafetyCertificateOutlined />} href="#question-answer">
              题解与答案
            </Button>
          </Space>
        </div>

        <div className="question-card-content">
          <div className="question-card-section-title">题目描述</div>
          <MdViewer value={question.content} />
        </div>
      </Card>

      <Card
        id="question-answer"
        className="question-card-answer glass-card"
        title="参考答案"
        bordered={false}
      >
        <MdViewer value={question.answer} />
      </Card>
    </div>
  );
}
