"use client";

import { BookOutlined, RightOutlined } from "@ant-design/icons";
import { Card, List, Typography } from "antd";
import Link from "next/link";
import "./index.css";

interface Props {
  questionBankList: API.QuestionBankVO[];
}

export default function QuestionBankList(props: Props) {
  const { questionBankList = [] } = props;

  return (
    <div className="question-bank-list">
      <List
        grid={{
          gutter: 18,
          column: 4,
          xs: 1,
          sm: 2,
          md: 2,
          lg: 3,
          xl: 4,
        }}
        dataSource={questionBankList}
        renderItem={(questionBank) => (
          <List.Item>
            <Link href={`/bank/${questionBank.id}`} className="question-bank-link">
              <Card
                hoverable
                className="question-bank-card glass-card"
                cover={
                  <div className="question-bank-cover">
                    <img src={questionBank.picture} alt={questionBank.title} />
                    <span className="question-bank-cover-badge">
                      <BookOutlined />
                      Topic
                    </span>
                  </div>
                }
              >
                <div className="question-bank-meta">
                  <Typography.Title level={4} className="question-bank-title">
                    {questionBank.title}
                  </Typography.Title>
                  <Typography.Paragraph className="question-bank-description" ellipsis={{ rows: 2 }}>
                    {questionBank.description || "围绕一组相关题目进行连续训练，适合专项提升与面试准备。"}
                  </Typography.Paragraph>
                  <div className="question-bank-footer">
                    <span className="question-bank-footer-text">进入题库训练</span>
                    <RightOutlined />
                  </div>
                </div>
              </Card>
            </Link>
          </List.Item>
        )}
      />
    </div>
  );
}
