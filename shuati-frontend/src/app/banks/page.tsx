"use server";

import { AppstoreOutlined } from "@ant-design/icons";
import { listQuestionBankVoByPageUsingPost } from "@/api/questionBankController";
import QuestionBankList from "@/components/QuestionBankList";
import Tag from "antd/es/tag";
import Paragraph from "antd/es/typography/Paragraph";
import Title from "antd/es/typography/Title";
import "./index.css";

export default async function BanksPage() {
  let questionBankList = [];
  const pageSize = 200;

  try {
    const res = await listQuestionBankVoByPageUsingPost({
      pageSize,
      sortField: "createTime",
      sortOrder: "descend",
    });
    questionBankList = (res.data as any)?.records ?? [];
  } catch (e: any) {
    console.error(`获取题库列表失败: ${e.message}`);
  }

  return (
    <div id="banksPage" className="max-width-content page-shell">
      <section className="page-hero">
        <Tag className="banks-page-tag" bordered={false}>
          <AppstoreOutlined /> Question Banks
        </Tag>
        <Title level={2} className="page-hero-title">从专题题库进入连续训练</Title>
        <Paragraph className="page-hero-subtitle">
          按照知识点、岗位方向或训练目标组织题目，适合做阶段性刷题计划，也更贴近灵活式的持续练习体验。
        </Paragraph>
      </section>

      <QuestionBankList questionBankList={questionBankList} />
    </div>
  );
}
