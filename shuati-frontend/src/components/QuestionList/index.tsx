"use client";

import { RightOutlined } from "@ant-design/icons";
import TagList from "@/components/TagList";
import { Card, List, Typography } from "antd";
import Link from "next/link";
import "./index.css";

interface Props {
  questionBankId?: number | string;
  questionList: API.QuestionVO[];
  cardTitle?: string;
}

export default function QuestionList(props: Props) {
  const { questionList = [], cardTitle, questionBankId } = props;

  return (
    <Card className="question-list glass-card" title={cardTitle}>
      <List
        dataSource={questionList}
        renderItem={(item, index) => (
          <List.Item className="question-list-item">
            <div className="question-list-item-inner">
              <div className="question-list-index">{index + 1}</div>
              <div className="question-list-main">
                <Link
                  className="question-list-link"
                  href={questionBankId ? `/bank/${questionBankId}/question/${item.id}` : `/question/${item.id}`}
                >
                  {item.title}
                </Link>
                <TagList tagList={item.tagList} />
              </div>
              <Typography.Text className="question-list-arrow">
                <RightOutlined />
              </Typography.Text>
            </div>
          </List.Item>
        )}
      />
    </Card>
  );
}
