package com.abhi.writewise.inventory.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LLMPromptBuilder {
    public static class WordDefinitionPrompt {
        public static synchronized String prompt(String words) {
            return String.format("""
                    I am improving my English vocabulary and need structured metadata for given words.

                    ## **Instructions (Follow Carefully)**
                    1. **Input words** are enclosed in `<inputWords>...</inputWords>`:  
                       ```
                       <inputWords>
                         %s
                       </inputWords>
                       ```
                    2. If **no words** are provided, return: `<response></response>`  
                    3. Always enclose your response in `<response>...</response>`  
                    4. The response **must be valid JSON** with the following structure:  

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
                    - **Always use `<response>...</response>` to enclose the JSON.**  
                    - **If multiple words are given, return a JSON array inside `<response>...</response>`.**  

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

}
