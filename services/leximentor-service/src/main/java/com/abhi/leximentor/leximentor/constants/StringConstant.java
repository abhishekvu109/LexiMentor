package com.abhi.leximentor.leximentor.constants;

public class StringConstant {
    public final static String MODEL_RESPONSE_FORMAT_MEANING_EVALUATION = """
                {
                  "type": "object",
                  "properties": {
                    "confidence": { "type": "integer", "minimum": 0, "maximum": 100 },
                    "explanation": { "type": "string" },
                    "correct": { "type": "string", "enum": ["true", "false"] }
                  },
                  "required": ["confidence", "explanation", "correct"]
                }
            """;
    public final static String MODEL_RESPONSE_FORMAT_EXAMPLE_USAGE_EVALUATION = """
                {
                  "type": "object",
                  "properties": {
                    "confidence": { "type": "integer", "minimum": 0, "maximum": 100 },
                    "explanation": { "type": "string" },
                    "correct": { "type": "string", "enum": ["true", "false"] }
                  },
                  "required": ["confidence", "explanation", "correct"]
                }
            """;
}
