"use client";

import {
  LockOutlined,
  RocketOutlined,
  ThunderboltOutlined,
  UserOutlined,
} from "@ant-design/icons";
import { LoginForm, ProFormText } from "@ant-design/pro-components";
import { message } from "antd";
import { ProForm } from "@ant-design/pro-form/lib";
import Image from "next/image";
import Link from "next/link";
import { useRouter } from "next/navigation";
import React from "react";
import { userRegisterUsingPost } from "@/api/userController";
import "../auth.css";
import "./index.css";

/**
 * 用户注册页面
 * @constructor
 */
const UserRegisterPage: React.FC = () => {
  const [form] = ProForm.useForm();
  const router = useRouter();

  /**
   * 提交
   */
  const doSubmit = async (values: API.UserRegisterRequest) => {
    try {
      const res = await userRegisterUsingPost(values);
      if (res.data) {
        message.success("注册成功，请登录");
        router.replace("/user/login");
        form.resetFields();
      }
    } catch (e) {
      message.error("注册失败，" + e.message);
    }
  };

  return (
    <div id="userRegisterPage" className="auth-page">
      <div className="auth-page__aurora auth-page__aurora--one" />
      <div className="auth-page__aurora auth-page__aurora--two" />
      <div className="auth-page__grid" />
      <div className="auth-shell">
        <section className="auth-showcase">
          <div className="auth-showcase__badge">开启你的刷题空间</div>
          <h1 className="auth-showcase__title">创建账号，开始更系统的在线训练</h1>
          <p className="auth-showcase__subtitle">
            加入后即可进入题库、参与在线测试、查看做题记录，并逐步形成自己的章节化学习路径。
          </p>
          <div className="auth-showcase__panel">
            <div className="auth-showcase__item">
              <span className="auth-showcase__icon">
                <ThunderboltOutlined />
              </span>
              <div>
                <div className="auth-showcase__itemTitle">章节化训练入口</div>
                <div className="auth-showcase__itemText">从数据库基础到体系结构扩展，按题库逐步推进。</div>
              </div>
            </div>
            <div className="auth-showcase__item">
              <span className="auth-showcase__icon">
                <RocketOutlined />
              </span>
              <div>
                <div className="auth-showcase__itemTitle">更快进入训练状态</div>
                <div className="auth-showcase__itemText">注册后即可开始刷题、测试与结果回看。</div>
              </div>
            </div>
          </div>
        </section>

        <section className="auth-formShell">
          <div className="auth-formCard">
            <LoginForm
              form={form}
              logo={<Image src="/assets/logo.png" alt="在线试题网站" height={52} width={52} />}
              title="创建账号"
              subTitle="注册后即可进入题库训练、在线测试与做题记录"
              submitter={{
                searchConfig: {
                  submitText: "立即注册",
                },
              }}
              onFinish={doSubmit}
            >
              <ProFormText
                name="userAccount"
                fieldProps={{
                  size: "large",
                  prefix: <UserOutlined />,
                  className: "auth-input",
                }}
                placeholder={"请输入用户账号"}
                rules={[
                  {
                    required: true,
                    message: "请输入用户账号",
                  },
                ]}
              />
              <ProFormText.Password
                name="userPassword"
                fieldProps={{
                  size: "large",
                  prefix: <LockOutlined />,
                  className: "auth-input",
                }}
                placeholder={"请输入密码"}
                rules={[
                  {
                    required: true,
                    message: "请输入密码",
                  },
                ]}
              />
              <ProFormText.Password
                name="checkPassword"
                fieldProps={{
                  size: "large",
                  prefix: <LockOutlined />,
                  className: "auth-input",
                }}
                placeholder={"请输入确认密码"}
                rules={[
                  {
                    required: true,
                    message: "请输入确认密码",
                  },
                ]}
              />
              <div className="auth-formFooter">
                已有账号？
                <Link href={"/user/login"} className="auth-inlineLink">
                  立即登录
                </Link>
              </div>
            </LoginForm>
          </div>
        </section>
      </div>
    </div>
  );
};

export default UserRegisterPage;
