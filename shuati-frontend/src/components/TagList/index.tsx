import { Tag } from "antd";
import Link from "next/link";
import "./index.css";

interface Props {
  tagList?: string[];
  linkToQuestions?: boolean;
}

export default function TagList(props: Props) {
  const { tagList = [], linkToQuestions = false } = props;

  return (
    <div className="tag-list">
      {tagList.map((tag) => {
        const tagNode = (
          <Tag className="tag-list-item" key={tag} bordered={false}>
            {tag}
          </Tag>
        );

        if (linkToQuestions) {
          return (
            <Link className="tag-list-link" key={tag} href={`/questions?tags=${encodeURIComponent(tag)}`}>
              {tagNode}
            </Link>
          );
        }
        return tagNode;
      })}
    </div>
  );
}
