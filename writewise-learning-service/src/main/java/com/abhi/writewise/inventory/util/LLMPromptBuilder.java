package com.abhi.writewise.inventory.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LLMPromptBuilder {
    public static class WordDefinitionPrompt {
        public static synchronized String prompt(String words) {
            return String.format("""
                    I am improving my English vocabulary and need structured metadata for given words.

                    ## **Instructions (Follow Carefully)**
                    1. **Input words** are enclosed in XML tags given below:  
                       ```
                       <inputWords>
                         %s
                       </inputWords>
                       ```
                    2. If **no words** are provided, return: `<response></response>`  
                    3. Always enclose your response in `<response>...</response>`  
                    4. The response **must be valid JSON** enclosed in XML with the following structure:  

                    ## **Required JSON Structure**
                    <response>
                      [
                        {
                          "word": "string",  // The input word
                          "source": "Llama",  // Always "Llama"
                          "pronunciation": "string",  // IPA or phonetic pronunciation
                          "mnemonic": "string",  // A memory aid (null if not available)
                          "localMeaning": "string",  // Hindi meaning in **Devanagari script**
                          "meanings": [
                            { "meaning": "string", "source": "Llama" }
                          ],
                          "synonyms": [
                            { "synonym": "string", "source": "Llama" },
                            { "synonym": "string", "source": "Llama" },
                            { "synonym": "string", "source": "Llama" }
                          ],
                          "antonyms": [
                            { "antonym": "string", "source": "Llama" },
                            { "antonym": "string", "source": "Llama" }
                          ],
                          "examples": [
                            { "example": "string", "source": "Llama" }
                          ],
                          "partsOfSpeeches": [
                            { "pos": "string", "source": "Llama" }
                          ],
                          "category": "string"
                        }
                      ]
                    </response>

                    ---
                    ## **Example Response**
                    If input is:
                    ```
                    <inputWords>
                      <word>genre</word>
                    </inputWords>
                    ```
                    The output must be:

                    <response>
                    [
                      {
                        "word": "genre",
                        "source": "Llama",
                        "pronunciation": "ZHAN-ruh",
                        "mnemonic": "Think of 'genre' as a category, like different books in a library section.",
                        "localMeaning": "शैली: साहित्य, संगीत या फ़िल्म की श्रेणी",
                        "meanings": [
                          { "meaning": "a category of artistic composition", "source": "Llama" }
                        ],
                        "synonyms": [
                          { "synonym": "category", "source": "Llama" },
                          { "synonym": "type", "source": "Llama" },
                          { "synonym": "style", "source": "Llama" }
                        ],
                        "antonyms": [
                          { "antonym": "hybrid", "source": "Llama" },
                          { "antonym": "mixture", "source": "Llama" }
                        ],
                        "examples": [
                          { "example": "The novel falls under the mystery genre.", "source": "Llama" }
                        ],
                        "partsOfSpeeches": [
                          { "pos": "noun", "source": "Llama" }
                        ],
                        "category": "Art"
                      }
                    ]
                    </response>

                    ---
                    ## **BONUS TIPS**
                    - **Do NOT generate extra text or explanations.**  
                    - **Ensure the JSON follows the exact structure given above.**  
                    - **Always use <response>...</response> to enclose the JSON.**  
                    - **If multiple words are given, return a JSON array inside XML <response>...</response>.**  

                    ---
                    Failure to follow these instructions will result in incorrect output.
                    """, words);
        }


    }

    public static class TopicPrompt {
        public static synchronized String prompt(String subject, int numOfTopics, String purpose, Integer wordCount) {
            return String.format("""
                    {
                        "prompt": "Generate %d unique topics in the subject of '%s' to improve English writing skills for '%s'. \n
                        The response must strictly follow the JSON structure enclosed in <response>...</response> tags.\n
                        The JSON example below is for reference only; do NOT copy the content. Generate new, relevant topics instead.\n\n
                        
                        ## **JSON Response Format** (Strictly Follow This)
                        {
                          "topics": [
                            {
                              "topicNo": Integer, // The topic number (e.g., 1, 2, etc.)
                              "topic": "String", // The title of the topic
                              "subject": "String", // The subject category (e.g., Global Economy, Science, History)
                              "description": "String", // Short explanation of how this topic improves English writing for '%s'
                              "points": [ // Key points to cover in writing (adjusted for %d words)
                                "String",
                                "String",
                                "String"
                              ],
                              "learning": "String" // What the user will learn from writing about this topic
                            }
                          ],
                          "recommendations": [ // General tips for improving writing skills
                            "String",
                            "String",
                            "String"
                          ]
                        }
                        
                        ---
                        ## **Example Response (For Format Only, Do Not Copy Content)**
                        <response>
                        {
                          "topics": [
                            {
                              "topicNo": 1,
                              "topic": "The Role of Artificial Intelligence in Modern Society",
                              "subject": "Technology",
                              "description": "This topic explores how artificial intelligence impacts different sectors, encouraging analytical and structured writing.",
                              "points": [
                                "Definition and brief history of AI",
                                "AI's role in industries such as healthcare, finance, and automation",
                                "Ethical concerns and risks of AI",
                                "Future predictions about AI development"
                              ],
                              "learning": "Writing on this topic will improve your ability to present arguments, use technical vocabulary, and structure your essay effectively."
                            },
                            {
                              "topicNo": 2,
                              "topic": "Climate Change: Causes, Effects, and Solutions",
                              "subject": "Environment",
                              "description": "This topic encourages structured argumentation about climate change, helping refine persuasive writing skills.",
                              "points": [
                                "Definition and scientific evidence of climate change",
                                "Major causes of climate change (e.g., carbon emissions, deforestation)",
                                "Effects on biodiversity, weather patterns, and human health",
                                "Possible solutions and global initiatives"
                              ],
                              "learning": "This topic enhances persuasive writing by requiring logical reasoning and factual support."
                            }
                          ],
                          "recommendations": [
                            "Use structured arguments with clear introductions and conclusions.",
                            "Practice writing with a variety of sentence structures to enhance fluency.",
                            "Include examples and data to support claims.",
                            "Use topic-specific vocabulary to demonstrate knowledge.",
                            "Practice writing under timed conditions for exam preparation."
                          ]
                        }
                        </response>
                        
                        ---
                        ## **Guidelines to Ensure a Consistent Response**
                        - **Do NOT generate explanations or extra text; return only the JSON.**
                        - **Ensure the JSON follows the exact format given above.**
                        - **Always enclose the response in `<response>...</response>` tags.**
                        - **Each topic should be unique, relevant, and structured for easy writing practice.**
                        - **Adjust the number of points based on the provided word count (%d words).**
                        - **The subject must be relevant to '%s'.**
                        - **Use correct grammar, spelling, and structured sentences.**
                        
                        Failure to follow these instructions will result in an incorrect response.
                    }
                    """, numOfTopics, subject, purpose, purpose, wordCount, wordCount, subject);
        }


    }

    public static class EvaluationPrompt {
        public static synchronized String prompt(String topic, String subject, String points, String recommendations, String userResponse) {
            return String.format("""
                    You are an expert writing evaluator specializing in English writing skills, particularly for exams like IELTS.
                                            
                    ## Task:
                    You will receive a JSON payload enclosed within `<Request></Request>` tags.
                    The payload contains:
                    - A topic.
                    - A genre.
                    - Key focus points.
                    - Recommendations.
                    - The user's written response.
                                            
                    Your role is to critically evaluate the `userResponse` on the following **six writing quality metrics**:
                    1. Spelling
                    2. Grammar
                    3. Punctuation
                    4. Vocabulary
                    5. StyleAndTone
                    6. CreativityAndThinking
                                            
                    For **each metric**, you must:
                    - Assign a **score out of 100** (integer only).
                    - Provide **at least 10 critical comments** highlighting specific issues in the writing.
                    - Provide **at least 5 alternate suggestions** showing how the user could have written better.
                                            
                    ## Mandatory Output Format:
                    Your response must be **strictly enclosed within `<Response>...</Response>` tags**, and the body must be a **valid JSON object**.
                                            
                    You must **NOT generate any explanations, extra text, or summaries outside the JSON and `<Response>` tags**.
                                            
                    Ensure the response strictly follows the format below:
                                            
                    <Response>
                    {
                      "spelling":{
                        "score": 0-100,
                        "comments": ["...minimum 10 critical comments..."],
                        "alternateSuggestions": ["...minimum 5 suggestions to improve..."]
                      },
                      "grammar":{
                        ...
                      },
                      "punctuation":{
                        ...
                      },
                      "vocabulary":{
                        ...
                      },
                      "styleAndTone":{
                        ...
                      },
                      "creativityAndThinking":{
                        ...
                      }
                      "OverallRecommendations":[
                        "Suggest on high-level what to improve like a judge."
                      ]
                    }
                    </Response>
                                            
                    ## Understanding the parameters
                    - Spelling: If the spelling is wrong then indicate which word is written is wrong and the mention the correct spelling.
                    - Grammar: Check for English grammar and point out the sentence where the grammar is incorrect. Be critical at low-level, do not provide comments on high-level.
                    - Punctuation: Look for commas, full-stop everything.
                    - Vocabulary: explain why the word is incorrect and what should have been used.
                    - Creativity and Thinking: Eventually this is the most important thing for improvement. Explain why it is not very creative and what is creative in the writing.
                    - OverallRecommendations: Suggest multiple recommendations to focus so that the user understands the major problems in writing. Things to learn as much as the user can.
                                            
                    ## Important guidelines:
                    - Be strictly critical and detailed in your comments.
                    - Focus comments on both technical correctness and writing style.
                    - Use bullet-style critical comments, pointing out flaws precisely.
                    - For suggestions, show better phrasing, alternate sentences, or improvements the user can make.
                    - Ensure the suggestions are actionable, practical, and demonstrate correct English usage.
                                            
                    ## Input:
                    <Request>
                    {
                      "topic":"%s",
                      "genre":"%s",
                      "points":[
                        %s
                      ],
                      "recommendations":[
                        %s
                      ],
                      "userResponse":"%s"
                    }
                    </Request>
                                            
                    """, topic, subject, points, recommendations, userResponse);
        }
    }
}
