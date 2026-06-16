"use client";

import {
  BarChartOutlined,
  LockOutlined,
  SafetyCertificateOutlined,
  UserOutlined,
} from "@ant-design/icons";
import { LoginForm, ProFormText } from "@ant-design/pro-components";
import { message } from "antd";
import { ProForm } from "@ant-design/pro-form/lib";
import Image from "next/image";
import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import React from "react";
import { useDispatch } from "react-redux";
import { userLoginUsingPost } from "@/api/userController";
import { AppDispatch } from "@/stores";
import { setLoginUser } from "@/stores/loginUser";
import "../auth.css";
import "./index.css";

/**
 * 用户登录页面
 * @constructor
 */
const UserLoginPage: React.FC = () => {
  const [form] = ProForm.useForm();
  const dispatch = useDispatch<AppDispatch>();
  const router = useRouter();
  const searchParams = useSearchParams();

  /**
   * 提交
   */
  const doSubmit = async (values: API.UserLoginRequest) => {
    try {
      const res = await userLoginUsingPost(values);
      if (res.data) {
        message.success("登录成功");
        dispatch(setLoginUser(res.data));
        const redirect = searchParams.get("redirect");
        router.replace(redirect || "/");
        form.resetFields();
      }
    } catch (e) {
      message.error("登录失败，" + e.message);
    }
  };

  return (
    <div id="userLoginPage" className="auth-page">
      <div className="auth-page__aurora auth-page__aurora--one" />
      <div className="auth-page__aurora auth-page__aurora--two" />
      <div className="auth-page__grid" />
      <div className="auth-shell">
        <section className="auth-showcase">
          <div className="auth-showcase__badge">在线题库训练平台</div>
          <h1 className="auth-showcase__title">进入你的数据库刷题控制台</h1>
          <p className="auth-showcase__subtitle">
            从章节题库、单题测试到题库测试与做题记录，把每一次练习沉淀成可追踪的进步轨迹。
          </p>
          <div className="auth-showcase__panel">
            <div className="auth-showcase__item">
              <span className="auth-showcase__icon">
                <SafetyCertificateOutlined />
              </span>
              <div>
                <div className="auth-showcase__itemTitle">即时判分与解析</div>
                <div className="auth-showcase__itemText">提交后快速查看结果，训练反馈更直接。</div>
              </div>
            </div>
            <div className="auth-showcase__item">
              <span className="auth-showcase__icon">
                <BarChartOutlined />
              </span>
              <div>
                <div className="auth-showcase__itemTitle">记录你的刷题轨迹</div>
                <div className="auth-showcase__itemText">单题测试与题库测试历史统一沉淀到个人中心。</div>
              </div>
            </div>
          </div>
        </section>

        <section className="auth-formShell">
          <div className="auth-formCard">
            <LoginForm
              form={form}
              logo={<Image src="/assets/logo.png" alt="在线试题系统" height={52} width={52} />}
              title="欢迎回来"
              subTitle="登录后继续你的题库训练与在线测试"
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
              <div className="auth-formFooter">
                还没有账号？
                <Link href={"/user/register"} className="auth-inlineLink">
                  立即注册
                </Link>
              </div>
            </LoginForm>
          </div>
        </section>
      </div>
    </div>
  );
};

export default UserLoginPage;
