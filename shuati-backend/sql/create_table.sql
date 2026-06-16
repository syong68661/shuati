# @author <a href="https://github.com/LightingForest">SYong</a>
-- create database
create database if not exists shuati;

-- use database
use shuati;

-- user
create table user
(
    id                 bigint auto_increment comment 'id'
        primary key,
    userAccount        varchar(256)                           not null comment '账号',
    userPassword       varchar(512)                           not null comment '密码',
    unionId            varchar(256)                           null comment '微信开放平台id',
    mpOpenId           varchar(256)                           null comment '公众号openId',
    userName           varchar(256)                           null comment '用户昵称',
    userAvatar         varchar(1024)                          null comment '用户头像',
    userProfile        varchar(512)                           null comment '用户简介',
    userRole           varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    editTime           datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime         datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime         datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete           tinyint      default 0                 not null comment '是否删除',
    phoneNumber        varchar(20)                            null comment '手机号',
    email              varchar(256)                           null comment '邮箱',
    grade              varchar(50)                            null comment '年级',
    workExperience     varchar(512)                           null comment '工作经验',
    expertiseDirection varchar(512)                           null comment '擅长方向'
)
    comment '用户' collate = utf8mb4_unicode_ci;

create index idx_unionId
    on user (unionId);

-- question bank
create table if not exists question_bank
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(256)                       null comment 'title',
    description text                               null comment 'description',
    picture     varchar(2048)                      null comment 'picture',
    userId      bigint                             not null comment 'creator user id',
    editTime    datetime default CURRENT_TIMESTAMP not null comment 'edit time',
    createTime  datetime default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    isDelete    tinyint  default 0                 not null comment 'is deleted',
    index idx_title (title)
) comment 'question bank' collate = utf8mb4_unicode_ci;

-- question
create table if not exists question
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(256)                       null comment 'title',
    content    text                               null comment 'content',
    tags       varchar(1024)                      null comment 'tags json array',
    answer     text                               null comment 'recommended answer',
    userId     bigint                             not null comment 'creator user id',
    editTime   datetime default CURRENT_TIMESTAMP not null comment 'edit time',
    createTime datetime default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    isDelete   tinyint  default 0                 not null comment 'is deleted',
    index idx_title (title),
    index idx_userId (userId)
) comment 'question' collate = utf8mb4_unicode_ci;

-- question bank question relation
create table if not exists question_bank_question
(
    id             bigint auto_increment comment 'id' primary key,
    questionBankId bigint                             not null comment 'question bank id',
    questionId     bigint                             not null comment 'question id',
    userId         bigint                             not null comment 'creator user id',
    createTime     datetime default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    UNIQUE (questionBankId, questionId)
) comment 'question bank question' collate = utf8mb4_unicode_ci;

-- question test
create table if not exists question_test
(
    id          bigint auto_increment comment 'id' primary key,
    questionId  bigint                             not null comment 'question id',
    title       varchar(256)                       not null comment 'test title',
    description text                               null comment 'test description',
    status      int      default 1                 not null comment 'status: 0-disabled 1-enabled',
    userId      bigint                             not null comment 'creator user id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    isDelete    tinyint  default 0                 not null comment 'is deleted',
    unique key uk_questionId (questionId),
    index idx_userId (userId)
) comment 'question test' collate = utf8mb4_unicode_ci;

-- question test item
create table if not exists question_test_item
(
    id             bigint auto_increment comment 'id' primary key,
    questionTestId bigint                             not null comment 'question test id',
    title          varchar(1024)                      not null comment 'item title',
    optionA        varchar(1024)                      not null comment 'option A',
    optionB        varchar(1024)                      not null comment 'option B',
    optionC        varchar(1024)                      not null comment 'option C',
    optionD        varchar(1024)                      not null comment 'option D',
    optionE        varchar(1024)                      null comment 'option E',
    answer         varchar(16)                        not null comment 'right answer',
    analysis       text                               null comment 'answer analysis',
    sort           int      default 0                 not null comment 'sort order',
    createTime     datetime default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    isDelete       tinyint  default 0                 not null comment 'is deleted',
    index idx_questionTestId (questionTestId)
) comment 'question test item' collate = utf8mb4_unicode_ci;

-- question test record
create table if not exists question_test_record
(
    id             bigint auto_increment comment 'id' primary key,
    questionId     bigint                             not null comment 'question id',
    questionTestId bigint                             not null comment 'question test id',
    userId         bigint                             not null comment 'answer user id',
    score          int      default 0                 not null comment 'score',
    rightCount     int      default 0                 not null comment 'right count',
    questionCount  int      default 0                 not null comment 'question count',
    createTime     datetime default CURRENT_TIMESTAMP not null comment 'submit time',
    index idx_questionTestId (questionTestId),
    index idx_questionId (questionId),
    index idx_userId (userId)
) comment 'question test record' collate = utf8mb4_unicode_ci;

-- question test record detail
create table if not exists question_test_record_detail
(
    id                 bigint auto_increment comment 'id' primary key,
    recordId           bigint                             not null comment 'record id',
    questionTestItemId bigint                             not null comment 'question test item id',
    titleSnapshot      varchar(1024)                      not null comment 'title snapshot',
    optionsSnapshot    text                               not null comment 'options snapshot json',
    userAnswer         varchar(16)                        null comment 'user answer',
    rightAnswer        varchar(16)                        not null comment 'right answer',
    isCorrect          tinyint  default 0                 not null comment 'is correct',
    analysisSnapshot   text                               null comment 'analysis snapshot',
    createTime         datetime default CURRENT_TIMESTAMP not null comment 'create time',
    index idx_recordId (recordId),
    index idx_questionTestItemId (questionTestItemId)
) comment 'question test record detail' collate = utf8mb4_unicode_ci;

-- question bank test record
create table if not exists question_bank_test_record
(
    id            bigint auto_increment comment 'id' primary key,
    questionBankId bigint                             not null comment 'question bank id',
    userId        bigint                             not null comment 'answer user id',
    score         int      default 0                 not null comment 'score',
    rightCount    int      default 0                 not null comment 'right count',
    questionCount int      default 0                 not null comment 'question count',
    createTime    datetime default CURRENT_TIMESTAMP not null comment 'submit time',
    index idx_questionBankId (questionBankId),
    index idx_userId (userId)
) comment 'question bank test record' collate = utf8mb4_unicode_ci;

-- question bank test record detail
create table if not exists question_bank_test_record_detail
(
    id                 bigint auto_increment comment 'id' primary key,
    recordId           bigint                             not null comment 'record id',
    questionId         bigint                             not null comment 'question id',
    questionTestId     bigint                             not null comment 'question test id',
    questionTestItemId bigint                             not null comment 'question test item id',
    titleSnapshot      varchar(1024)                      not null comment 'title snapshot',
    optionsSnapshot    text                               not null comment 'options snapshot json',
    userAnswer         varchar(16)                        null comment 'user answer',
    rightAnswer        varchar(16)                        not null comment 'right answer',
    isCorrect          tinyint  default 0                 not null comment 'is correct',
    analysisSnapshot   text                               null comment 'analysis snapshot',
    createTime         datetime default CURRENT_TIMESTAMP not null comment 'create time',
    index idx_recordId (recordId),
    index idx_questionId (questionId),
    index idx_questionTestId (questionTestId),
    index idx_questionTestItemId (questionTestItemId)
) comment 'question bank test record detail' collate = utf8mb4_unicode_ci;
