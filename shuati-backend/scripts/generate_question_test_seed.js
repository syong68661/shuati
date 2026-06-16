const fs = require("fs");
const path = require("path");

const root = path.resolve(__dirname, "..");
const insertDataPath = path.join(root, "sql", "insert_data.sql");
const outputPath = path.join(root, "sql", "question_test_seed.sql");

const questionPattern =
  /\((\d+),\s*'((?:[^']|(?:''))*)',\s*'((?:[^']|(?:''))*)',\s*'((?:[^']|(?:''))*)',\s*'((?:[^']|(?:''))*)',\s*1,\s*NOW\(\),\s*NOW\(\),\s*NOW\(\),\s*0\)/g;

const itemTitleTemplates = [
  "关于“{title}”，下列说法正确的是？",
  "下列对“{title}”的理解，最准确的是哪一项？",
  "在数据库课程考试语境下，关于“{title}”的标准表述是？",
  "结合题干概念“{title}”，哪项表述符合教材定义？",
  "若考查“{title}”的核心含义，下列答案中正确的是？",
];

const distractorIndexSets = [
  [0, 1, 2, 3],
  [2, 4, 5, 7],
  [1, 3, 6, 8],
  [0, 4, 6, 7],
  [1, 2, 5, 8],
];

function escapeSql(value) {
  return value.replace(/\\/g, "\\\\").replace(/'/g, "''");
}

function parseQuestions(sqlText) {
  const questions = [];
  for (const match of sqlText.matchAll(questionPattern)) {
    const questionId = Number(match[1]);
    if (questionId >= 3001 && questionId <= 3080) {
      questions.push({
        id: questionId,
        title: match[2].replace(/''/g, "'"),
        content: match[3].replace(/''/g, "'"),
        tags: match[4].replace(/''/g, "'"),
        answer: match[5].replace(/''/g, "'"),
      });
    }
  }
  questions.sort((a, b) => a.id - b.id);
  return questions;
}

function buildSeedSql(questions) {
  if (questions.length !== 80) {
    throw new Error(`Expected 80 questions, got ${questions.length}`);
  }

  const lines = [];
  lines.push("-- 数据库课程题目测试配置（每个原题 1 套测试，每套 5 道单选小题）");
  lines.push("SET NAMES utf8mb4;");
  lines.push("USE shuati;");
  lines.push("");
  lines.push("-- 清理 3001 ~ 3080 题目的旧测试小题，避免重复导入");
  lines.push(
    "DELETE qti FROM question_test_item qti " +
      "INNER JOIN question_test qt ON qti.questionTestId = qt.id " +
      "WHERE qt.questionId BETWEEN 3001 AND 3080;"
  );
  lines.push("");
  lines.push("-- 重新写入 3001 ~ 3080 题目的测试主表");
  lines.push(
    "REPLACE INTO question_test " +
      "(id, questionId, title, description, status, userId, createTime, updateTime, isDelete)"
  );
  lines.push("VALUES");

  const testRows = questions.map((question, index) => {
    const testId = 5001 + index;
    const title = escapeSql(`${question.title}·专项测试`);
    const description = escapeSql(
      `基于原题《${question.title}》生成的 5 道单选测试题，解析复用原题标准答案。`
    );
    return `    (${testId}, ${question.id}, '${title}', '${description}', 1, 1, NOW(), NOW(), 0)`;
  });
  lines.push(`${testRows.join(",\n")};`);
  lines.push("");
  lines.push("-- 写入测试小题：每题 5 道单选，A 为正确答案，解析直接复用原题答案");
  lines.push(
    "INSERT INTO question_test_item " +
      "(id, questionTestId, title, optionA, optionB, optionC, optionD, optionE, answer, analysis, sort, createTime, updateTime, isDelete)"
  );
  lines.push("VALUES");

  const itemRows = [];
  for (let chapterStart = 3001; chapterStart <= 3080; chapterStart += 10) {
    const chapterQuestions = questions.filter(
      (question) => question.id >= chapterStart && question.id <= chapterStart + 9
    );
    for (const question of chapterQuestions) {
      const testId = 5001 + (question.id - 3001);
      const siblings = chapterQuestions.filter((item) => item.id !== question.id);
      itemTitleTemplates.forEach((template, sortIndex) => {
        const distractors = distractorIndexSets[sortIndex].map(
          (position) => siblings[position].answer
        );
        const itemId = 600000 + (question.id - 3000) * 10 + (sortIndex + 1);
        const itemTitle = escapeSql(template.replace("{title}", question.title));
        const optionA = escapeSql(question.answer);
        const optionB = escapeSql(distractors[0]);
        const optionC = escapeSql(distractors[1]);
        const optionD = escapeSql(distractors[2]);
        const optionE = escapeSql(distractors[3]);
        itemRows.push(
          `    (${itemId}, ${testId}, '${itemTitle}', '${optionA}', '${optionB}', '${optionC}', '${optionD}', '${optionE}', 'A', '${optionA}', ${
            sortIndex + 1
          }, NOW(), NOW(), 0)`
        );
      });
    }
  }
  lines.push(`${itemRows.join(",\n")};`);
  lines.push("");

  return lines.join("\n");
}

function main() {
  const sqlText = fs.readFileSync(insertDataPath, "utf8");
  const questions = parseQuestions(sqlText);
  const output = buildSeedSql(questions);
  fs.writeFileSync(outputPath, output, "utf8");
  console.log(`Generated: ${outputPath}`);
}

main();
