"use server";

import { BookOutlined, FileTextOutlined, PlayCircleOutlined } from "@ant-design/icons";
import { getQuestionBankVoByIdUsingGet } from "@/api/questionBankController";
import QuestionList from "@/components/QuestionList";
import Button from "antd/es/button";
import Card from "antd/es/card";
import Tag from "antd/es/tag";
import Paragraph from "antd/es/typography/Paragraph";
import Title from "antd/es/typography/Title";
import "./index.css";

export default async function BankPage({ params }: { params: { questionBankId: string } }) {
  const { questionBankId } = params;
  let bank = undefined;

  try {
    const res = await getQuestionBankVoByIdUsingGet({
      id: Number(questionBankId),
      needQueryQuestionList: true,
      pageSize: 200,
    });
    bank = res.data as any;
  } catch (e: any) {
    console.error(`获取题库详情失败: ${e.message}`);
  }

  if (!bank) {
    return <div className="max-width-content">获取题库详情失败，请刷新重试。</div>;
  }

  const firstQuestionId =
    bank.questionPage?.records && bank.questionPage.records.length > 0
      ? bank.questionPage.records[0].id
      : undefined;

  return (
    <div id="bankPage" className="max-width-content page-shell">
      <Card className="bank-hero-card glass-card" bordered={false}>
        <div className="bank-hero-grid">
          <div className="bank-hero-cover">
            <img src={bank.picture} alt={bank.title} />
          </div>

          <div className="bank-hero-content">
            <Tag className="bank-hero-tag" bordered={false}>
              <BookOutlined /> Learning Path
            </Tag>
            <Title level={2} className="bank-hero-title">{bank.title}</Title>
            <Paragraph className="bank-hero-description">
              {bank.description || "围绕同一训练目标整理的题目集合，适合连续刷题与阶段性冲刺。"}
            </Paragraph>

            <div className="bank-hero-actions">
              <Button
                type="primary"
                size="large"
                shape="round"
                href={`/bank/${questionBankId}/question/${firstQuestionId}`}
                disabled={!firstQuestionId}
                icon={<PlayCircleOutlined />}
              >
                开始刷题
              </Button>
              <Button size="large" shape="round" href={`/bank/${questionBankId}/test`} icon={<FileTextOutlined />}>
                在线测试
              </Button>
            </div>
          </div>
        </div>
      </Card>

      <QuestionList
        questionBankId={questionBankId}
        questionList={bank.questionPage?.records ?? []}
        cardTitle={`题目列表 · ${bank.questionPage?.total || 0} 道`}
      />
    </div>
  );
}
