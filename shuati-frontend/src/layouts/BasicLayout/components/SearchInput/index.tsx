"use client";

import { Input } from "antd";
import { useRouter } from "next/navigation";
import "./index.css";

export default function SearchInput() {
  const router = useRouter();

  return (
    <div className="search-input" aria-hidden>
      <Input.Search
        placeholder="搜索题目、标签、关键字"
        onSearch={(value) => {
          router.push(`/questions?q=${value}`);
        }}
      />
    </div>
  );
}
