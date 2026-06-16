"use server";

import { getQuestionVoByIdUsingGet } from "@/api/questionController";
import QuestionCard from "@/components/QuestionCard";
import "./index.css";

export default async function QuestionPage({ params }: { params: { questionId: string } }) {
  const { questionId } = params;

  let question = undefined;
  try {
    const res = await getQuestionVoByIdUsingGet({
      id: Number(questionId),
    });
    question = res.data as any;
  } catch (e: any) {
    console.error(`获取题目详情失败: ${e.message}`);
  }

  if (!question) {
    return <div className="max-width-content">获取题目详情失败，请刷新重试。</div>;
  }

  return (
    <div id="questionPage" className="max-width-content">
      <QuestionCard question={question} />
    </div>
  );
}
