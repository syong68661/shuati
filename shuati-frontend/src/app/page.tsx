import {
  FireOutlined,
  LineChartOutlined,
  RightOutlined,
  TagsOutlined,
  ThunderboltOutlined,
  TrophyOutlined,
} from "@ant-design/icons";
import { getHomeStatisticsUsingGet } from "@/api/homeController";
import { listQuestionBankVoByPageUsingPost } from "@/api/questionBankController";
import { listQuestionVoByPageUsingPost } from "@/api/questionController";
import QuestionBankList from "@/components/QuestionBankList";
import QuestionList from "@/components/QuestionList";
import Link from "next/link";
import Avatar from "antd/es/avatar";
import Card from "antd/es/card";
import Col from "antd/es/grid/col";
import Row from "antd/es/grid/row";
import Tag from "antd/es/tag";
import Paragraph from "antd/es/typography/Paragraph";
import Title from "antd/es/typography/Title";
import "./index.css";

export const dynamic = "force-dynamic";

const heroMetrics = [
  {
    key: "banks",
    icon: <ThunderboltOutlined />,
    label: "精选题库",
    description: "按课程章节和专题组织内容，更适合连续训练与专项提升",
  },
  {
    key: "tests",
    icon: <TrophyOutlined />,
    label: "在线测试",
    description: "支持单题测试和题库测试，提交后即时判分并查看解析",
  },
  {
    key: "growth",
    icon: <LineChartOutlined />,
    label: "做题反馈",
    description: "个人中心沉淀做题记录，结合热榜和标签持续查漏补缺",
  },
];

export default async function HomePage() {
  let questionBankList = [];
  let questionList = [];
  let statistics = {
    hotQuestionList: [],
    topUserList: [],
    hotTagList: [],
  };

  try {
    const res = await listQuestionBankVoByPageUsingPost({
      pageSize: 8,
      sortField: "createTime",
      sortOrder: "descend",
    });
    questionBankList = (res.data as any)?.records ?? [];
  } catch (e: any) {
    console.error(`获取题库列表失败: ${e.message}`);
  }

  try {
    const res = await listQuestionVoByPageUsingPost({
      pageSize: 10,
      sortField: "createTime",
      sortOrder: "descend",
    });
    questionList = (res.data as any)?.records ?? [];
  } catch (e: any) {
    console.error(`获取题目列表失败: ${e.message}`);
  }

  try {
    const res = await getHomeStatisticsUsingGet();
    statistics = (res.data as any) ?? statistics;
  } catch (e: any) {
    console.error(`获取首页统计失败: ${e.message}`);
  }

  return (
    <div id="homePage" className="max-width-content page-shell">
      <section className="page-hero home-hero">
        <div className="home-hero-content">
          <div>
            <Tag className="home-hero-tag" bordered={false}>
              一站式题库训练与在线测试平台
            </Tag>
            <Title level={1} className="page-hero-title home-hero-title">
              从题库训练到在线测试，搭建清晰可见的刷题闭环
            </Title>
            <Paragraph className="page-hero-subtitle">
              覆盖专题题库、题目检索、单题测试、题库测试与做题记录。你可以按章节进入训练，
              提交后即时查看得分与解析，并结合热门题目、标签和历史记录持续定位薄弱点。
            </Paragraph>
            <div className="home-hero-actions">
              <Link className="home-primary-link" href="/questions">
                开始刷题
                <RightOutlined />
              </Link>
              <Link className="home-secondary-link" href="/banks">
                浏览题库
              </Link>
            </div>
          </div>

          <div className="home-hero-panel">
            <div className="home-hero-panel-title">你可以这样开始</div>
            <div className="home-hero-panel-list">
              {heroMetrics.map((item) => (
                <div className="home-hero-panel-item" key={item.key}>
                  <span className="home-hero-panel-icon">{item.icon}</span>
                  <div>
                    <div className="home-hero-panel-label">{item.label}</div>
                    <div className="home-hero-panel-text">{item.description}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      <Row gutter={[16, 16]}>
        {heroMetrics.map((item) => (
          <Col xs={24} md={8} key={item.key}>
            <Card className="glass-card home-metric-card" bordered={false}>
              <div className="home-metric-icon">{item.icon}</div>
              <div className="home-metric-label">{item.label}</div>
              <div className="home-metric-text">{item.description}</div>
            </Card>
          </Col>
        ))}
      </Row>

      <div className="home-layout">
        <div className="home-main">
          <section className="home-section">
            <div className="section-header">
              <Title level={3} className="section-title">最新题库</Title>
              <Link href="/banks" className="section-link">
                查看更多
              </Link>
            </div>
            <QuestionBankList questionBankList={questionBankList} />
          </section>

          <section className="home-section">
            <div className="section-header">
              <Title level={3} className="section-title">最新题目</Title>
              <Link href="/questions" className="section-link">
                查看更多
              </Link>
            </div>
            <QuestionList questionList={questionList} />
          </section>
        </div>

        <aside className="home-side">
          <Card className="home-side-card glass-card" title="热门题目">
            <div className="home-side-list">
              {(statistics.hotQuestionList ?? []).map((item: any, index) => (
                <div className="home-side-item" key={item.questionId ?? index}>
                  <div className="home-side-row">
                    <Link href={`/question/${item.questionId}`} className="home-side-link">
                      <span className="home-rank-badge">{index + 1}</span>
                      <span className="home-side-link-text">{item.title}</span>
                    </Link>
                    <span className="home-side-count">
                      <FireOutlined /> {item.hotScore}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </Card>

          <Card className="home-side-card glass-card" title="用户排行榜">
            <div className="home-side-list">
              {(statistics.topUserList ?? []).map((item: any, index) => (
                <div className="home-side-item" key={item.userId ?? index}>
                  <div className="home-side-row">
                    <div className="home-user">
                      <span className="home-rank-badge">{index + 1}</span>
                      <Avatar src={item.userAvatar} size={36} />
                      <span className="home-side-link-text">{item.userName}</span>
                    </div>
                    <span className="home-side-count">{item.solveCount}</span>
                  </div>
                </div>
              ))}
            </div>
          </Card>

          <Card className="home-side-card glass-card" title="热门标签">
            <div className="home-side-tags">
              {(statistics.hotTagList ?? []).map((item: any, index) => (
                <Link href={`/questions?tags=${encodeURIComponent(item.tagName)}`} key={item.tagName ?? index}>
                  <span className="home-tag-pill">
                    <TagsOutlined />
                    {item.tagName}
                    <em>{item.useCount}</em>
                  </span>
                </Link>
              ))}
            </div>
          </Card>
        </aside>
      </div>
    </div>
  );
}
