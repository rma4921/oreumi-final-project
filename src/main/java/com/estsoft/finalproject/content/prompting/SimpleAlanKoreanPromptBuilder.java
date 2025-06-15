package com.estsoft.finalproject.content.prompting;

import java.util.ArrayList;
import java.util.List;

public class SimpleAlanKoreanPromptBuilder implements PromptBuilder {
    private static final String COMMAND_PREFIX = "실행할 요청: ";
    private static final String CONTEXT_PREFIX = "조건, 맥락 혹은 전제상황: ";
    private static final String OUTPUT_FORMAT_PREFIX = "출력 형식: ";
    private static final String ERROR_HANDLER_PREFIX = "만약 오류가 발생하거나 요청을 실행할 수 없으면 다음을 실행: ";

    private final List<String> commandList = new ArrayList<>();
    private final List<String> contextList = new ArrayList<>();
    private String outputFormat = "텍스트 문장 형식";
    private final List<String> errorHandlers = new ArrayList<>();

    public static SimpleAlanKoreanPromptBuilder start() {
        return new SimpleAlanKoreanPromptBuilder();
    }

    @Override
    public SimpleAlanKoreanPromptBuilder addCommand(String command) {
        commandList.add(command.replace("\n", "").trim() + " ");
        return this;
    }

    @Override
    public SimpleAlanKoreanPromptBuilder addContext(String context) {
        contextList.add(context.replace("\n", "").trim() + " ");
        return this;
    }

    @Override
    public SimpleAlanKoreanPromptBuilder setOutputFormat(String outputFormat) {
        this. outputFormat = outputFormat.replace("\n", "").trim();
        return this;
    }

    @Override
    public SimpleAlanKoreanPromptBuilder addErrorHandler(String errorHandlerPrompt) {
        errorHandlers.add(errorHandlerPrompt.replace("\n", "").trim() + " ");
        return this;
    }

    @Override
    public String buildPrompt() {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(COMMAND_PREFIX);
        commandList.forEach(promptBuilder::append);
        if (!contextList.isEmpty()) {
            promptBuilder.append(CONTEXT_PREFIX);
            contextList.forEach(promptBuilder::append);
        }
        promptBuilder.append(OUTPUT_FORMAT_PREFIX);
        promptBuilder.append(outputFormat);
        if (!errorHandlers.isEmpty()) {
            promptBuilder.append(ERROR_HANDLER_PREFIX);
            errorHandlers.forEach(promptBuilder::append);
        }
        return promptBuilder.toString();
    }

    @Override
    public String toString() {
        return buildPrompt();
    }
    
}
