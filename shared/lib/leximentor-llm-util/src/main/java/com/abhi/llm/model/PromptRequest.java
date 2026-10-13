package com.abhi.llm.model;


public class PromptRequest {
    private String format;
    private String model;
    private String prompt;
    private OllamaOptionsDTO options;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public OllamaOptionsDTO getOptions() {
        return options;
    }

    public void setOptions(OllamaOptionsDTO options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "PromptRequest{" +
                "format='" + format + '\'' +
                ", model='" + model + '\'' +
                ", prompt='" + prompt + '\'' +
                ", options=" + options +
                '}';
    }
}
