package com.syong.shuati.manager;

import com.volcengine.ark.runtime.model.responses.constant.ResponsesConstants;
import com.volcengine.ark.runtime.model.responses.content.InputContentItemText;
import com.volcengine.ark.runtime.model.responses.item.ItemEasyMessage;
import com.volcengine.ark.runtime.model.responses.item.MessageContent;
import com.volcengine.ark.runtime.model.responses.request.CreateResponsesRequest;
import com.volcengine.ark.runtime.model.responses.request.ResponsesInput;
import com.volcengine.ark.runtime.model.responses.response.ResponseObject;
import com.volcengine.ark.runtime.service.ArkService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AiManager {

//    @Resource
//    private ArkService arkService;
//
//    public String doChat(String prompt, String model) {
//
//        CreateResponsesRequest request = CreateResponsesRequest.builder()
//                .model(model)
//                .input(
//                        ResponsesInput.builder()
//                                .addListItem(
//                                        ItemEasyMessage.builder()
//                                                .role(ResponsesConstants.MESSAGE_ROLE_USER)
//                                                .content(
//                                                        MessageContent.builder()
//                                                                .addListItem(
//                                                                        InputContentItemText.builder()
//                                                                                .text(prompt)
//                                                                                .build()
//                                                                )
//                                                                .build()
//                                                )
//                                                .build()
//                                )
//                                .build()
//                )
//                .build();
//
//        try {
//            ResponseObject response = arkService.createResponse(request);
//            DateTime AiUtils;
//            return AiUtils.parse(response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return "AI调用失败";
//    }
}