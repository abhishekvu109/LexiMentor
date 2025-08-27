package com.abhi.writewise.inventory.constants;

public class StringConstants {
    public static final String LLM_EVALUATION_LOW_LEVEL = """
                {
                  "type": "object",
                  "properties": {
                    "errorList": {
                      "type": "array",
                      "items": {
                        "type": "object",
                        "properties": {
                          "id": {
                            "type": "string"
                          },
                          "type": {
                            "type": "string",
                            "enum": ["Grammar", "Punctuation", "Vocabulary", "Style & Tone", "Creativity"]
                          },
                          "subType": {
                            "type": "string"
                          },
                          "incorrectText": {
                            "type": "string"
                          },
                          "correctedText": {
                            "type": "string"
                          },
                          "explanation": {
                            "type": "string"
                          }
                        },
                        "required": [
                          "id",
                          "type",
                          "subType",
                          "incorrectText",
                          "correctedText",
                          "explanation"
                        ]
                      }
                    }
                  },
                  "required": ["errorList"]
                }
                        
            """;
    public static final String LLM_EVALUATION_HIGH_LEVEL = """
            {
              "type": "object",
              "properties": {
                "spelling": {
                  "type": "object",
                  "properties": {
                    "score": { "type": "integer", "minimum": 0, "maximum": 100 },
                    "comments": {
                      "type": "array",
                      "items": { "type": "string" }
                    },
                    "alternateSuggestion": {
                      "type": "array",
                      "items": { "type": "string" }
                    }
                  },
                  "required": ["score", "comments", "alternateSuggestion"]
                },
                "grammar": {
                  "type": "object",
                  "properties": {
                    "score": { "type": "integer", "minimum": 0, "maximum": 100 },
                    "comments": {
                      "type": "array",
                      "items": { "type": "string" }
                    },
                    "alternateSuggestion": {
                      "type": "array",
                      "items": { "type": "string" }
                    }
                  },
                  "required": ["score", "comments", "alternateSuggestion"]
                },
                "punctuation": {
                  "type": "object",
                  "properties": {
                    "score": { "type": "integer", "minimum": 0, "maximum": 100 },
                    "comments": {
                      "type": "array",
                      "items": { "type": "string" }
                    },
                    "alternateSuggestion": {
                      "type": "array",
                      "items": { "type": "string" }
                    }
                  },
                  "required": ["score", "comments", "alternateSuggestion"]
                },
                "vocabulary": {
                  "type": "object",
                  "properties": {
                    "score": { "type": "integer", "minimum": 0, "maximum": 100 },
                    "comments": {
                      "type": "array",
                      "items": { "type": "string" }
                    },
                    "alternateSuggestion": {
                      "type": "array",
                      "items": { "type": "string" }
                    }
                  },
                  "required": ["score", "comments", "alternateSuggestion"]
                },
                "styleAndTone": {
                  "type": "object",
                  "properties": {
                    "score": { "type": "integer", "minimum": 0, "maximum": 100 },
                    "comments": {
                      "type": "array",
                      "items": { "type": "string" }
                    },
                    "alternateSuggestion": {
                      "type": "array",
                      "items": { "type": "string" }
                    }
                  },
                  "required": ["score", "comments", "alternateSuggestion"]
                },
                "creativityAndThinking": {
                  "type": "object",
                  "properties": {
                    "score": { "type": "integer", "minimum": 0, "maximum": 100 },
                    "comments": {
                      "type": "array",
                      "items": { "type": "string" }
                    },
                    "alternateSuggestion": {
                      "type": "array",
                      "items": { "type": "string" }
                    }
                  },
                  "required": ["score", "comments", "alternateSuggestion"]
                }
              },
              "required": [
                "spelling",
                "grammar",
                "punctuation",
                "vocabulary",
                "styleAndTone",
                "creativityAndThinking"
              ]
            }
                        
            """;
}
