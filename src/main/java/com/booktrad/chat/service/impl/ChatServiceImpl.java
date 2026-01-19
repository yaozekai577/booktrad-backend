package com.booktrad.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.booktrad.book.entity.Book;
import com.booktrad.book.mapper.BookMapper;
import com.booktrad.chat.dto.ChatMessageSendDTO;
import com.booktrad.chat.entity.ChatMessage;
import com.booktrad.chat.entity.ChatSession;
import com.booktrad.chat.mapper.ChatMessageMapper;
import com.booktrad.chat.mapper.ChatSessionMapper;
import com.booktrad.chat.service.ChatService;
import com.booktrad.chat.vo.ChatMessageVO;
import com.booktrad.chat.vo.ChatSessionVO;
import com.booktrad.common.context.UserContext;
import com.booktrad.user.entity.User;
import com.booktrad.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 聊天业务实现类
 * @Date 2026/01/19 14:40
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Service
public class ChatServiceImpl implements ChatService {


}
