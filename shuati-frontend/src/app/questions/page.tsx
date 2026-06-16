"use server";

import { SearchOutlined } from "@ant-design/icons";
import { searchQuestionVoByPageUsingPost } from "@/api/questionController";
import QuestionTable from "@/components/QuestionTable";
import Tag from "antd/es/tag";
import Paragraph from "antd/es/typography/Paragraph";
import Title from "antd/es/typography/Title";
import "./index.css";

export default async function QuestionsPage({ searchParams }: { searchParams: any }) {
  const { q: searchText } = searchParams;
  const rawTags = searchParams?.tags;
  const tags = Array.isArray(rawTags)
    ? rawTags.flatMap((item: string) => String(item).split(",")).filter(Boolean)
    : rawTags
      ? String(rawTags).split(",").filter(Boolean)
      : undefined;

  let questionList = [];
  let total = 0;

  try {
    const res = await searchQuestionVoByPageUsingPost({
      searchText,
      tags,
      pageSize: 12,
      sortField: "createTime",
      sortOrder: "descend",
    });
    questionList = (res.data as any)?.records ?? [];
    total = (res.data as any)?.total ?? 0;
  } catch (e: any) {
    console.error(`获取题目列表失败: ${e.message}`);
  }

  return (
    <div id="questionsPage" className="max-width-content page-shell">
      <section className="page-hero">
        <Tag className="questions-page-tag" bordered={false}>
          <SearchOutlined /> Problem Explorer
        </Tag>
        <Title level={2} className="page-hero-title">可以筛选、定位、进入题目</Title>
        <Paragraph className="page-hero-subtitle">
          支持关键词搜索、标签筛选和题目跳转。你可以把这里当成日常刷题入口，也可以直接根据标签快速切到专项训练。
        </Paragraph>
      </section>

      <QuestionTable
        defaultQuestionList={questionList}
        defaultTotal={total}
        defaultSearchParams={{
          searchText,
          tags,
        }}
      />
    </div>
  );
}
