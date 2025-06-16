package com.estsoft.finalproject.content.prompting;

public interface PromptBuilder {

    public PromptBuilder addCommand(String command);

    public PromptBuilder addContext(String context);

    public PromptBuilder setOutputFormat(String outputFormat);

    public PromptBuilder addErrorHandler(String errorHandlerPrompt);

    public String buildPrompt();
}
