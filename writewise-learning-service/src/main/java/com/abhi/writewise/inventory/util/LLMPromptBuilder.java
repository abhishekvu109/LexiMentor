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
                        The response must strictly follow the JSON structure enclosed in <response>...</response> tags.
                                        
                        The JSON example below is for reference only; do NOT copy the content. Generate new, relevant topics instead.
                                        
                                        
                        
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
        public static synchronized String getLowLevelEvaluationPrompt(String topic, String subject, String points, String userResponse) {
            return String.format("""
                    You are a professional English writing evaluator specializing in critical, low-level feedback for students preparing for exams like IELTS or GRE.

                    ## Objective:
                    You will receive a JSON input wrapped in <Request></Request> tags. Your task is to evaluate the user's written response and return **detailed, localized feedback** in a **strict JSON format**.

                    ## Evaluation Focus:
                    You must evaluate and provide feedback under the following **five distinct categories**:
                    1. Grammar
                    2. Punctuation
                    3. Vocabulary
                    4. Style & Tone
                    5. Creativity

                    ## Error Detection Criteria:
                    - Identify low-level errors detectable with high confidence.
                    - Make sure the writing covers all the points given in the request.
                    - For **Grammar**, classify errors by subtypes like: Tense, Articles, Prepositions, Subject-Verb Agreement, Sentence Fragments, etc.
                    - For **each category**, return either:
                      - **Actual errors**, OR
                      - A **"no issue found" placeholder** object if that type has no issues

                    ## Response Rules:
                    For each issue, return:
                    - `id`: A unique identifier like "err-1", "err-2", etc.
                    - `type`: One of the five types
                    - `subType`: A precise subtype like Tense, Clarity, Idiom, Word Choice, etc.
                    - `incorrectText`: The original text
                    - `correctedText`: The recommended correction
                    - `explanation`: A short explanation of the issue

                    ## Required Output Format:
                    Respond in **exactly** this JSON structure:

                    {
                      
                      "errorList": [
                        {
                          "id": "err-1",
                          "type": "<Grammar|Punctuation|Vocabulary|Style & Tone|Creativity>",
                          "subType": "<e.g., Tense, Clarity, Word Choice, etc.>",
                          "incorrectText": "<what was written>",
                          "correctedText": "<what should be written>",
                          "explanation": "<short reason why it is wrong>"
                        },
                        ...
                      ]
                    }


                    ## Example for you to understand:
                    **Input:**
                    <Request>
                    {
                      "topic": "The Impact of Cryptocurrency on the World Economy",
                      "subject": "Economy",
                      "points": ["Disruption of Traditional Financial Systems.",
                      "New Opportunities for Investment and Business.",
                      "Risks and Volatility.",
                      "Environmental and Ethical Concerns."],
                      "userResponse": "Cryptocurrency is like the new cool thing that is changing everything in the world, like how we use money and do buisness. It’s basicly digital money that you can’t see or touch but you can use to buy stuff or invest like stocks or somthing. 
                      People say its very secure becuase of the blockchain which is some tech thing that stores all the records in blocks or chains or whatever. 
                      Bitcoin was the first crypto coin and now theres like thousands of them floating around the internets. 
                      Some pepole got rich by buying them early and now everyone wants a peice of that pie. Even celebs and big companys are talking about crypto. 
                      But is it all good? Not really. Theres been many scams and people loosing their life savings by investing in coins that just disapear. 
                      Also, its not stable. One day its up, next day it crashes like crazy. That makes it risky for normal people. Governments dont know what to do with it. 
                      Some countries like El Salvador made it a legal thing while others are banning it completly. 
                      The banks are also kind of scared becuase crypto doesn’t need banks so it might take away their power. 
                      But at the same time, crypto could help poor people who don’t have banks to save money and send money to family in other places.
                      Another thing is the environment. Mining crypto takes so much electricity that it can light up a whole city! 
                      This is not good for our planet because of pollution and global warming and all that. So some people hate crypto for that reason. 
                      In conclution, crypto is a big deal for the world economy but it's not perfect. 
                      It got good sides like making people rich or helping those without banks, but also bad stuff like scams, pollution, and confusing goverment rules. 
                      I think it will keep changing and maybe become more normal in the future but for now its kind of a wild west money situation."
                    }
                    </Request>

                    **Output:**
                    {
                      "errorList": [
                         {
                           "id": "err-1",
                           "type": "Spelling",
                           "subType": "Typo",
                           "incorrectText": "buisness",
                           "correctedText": "business",
                           "explanation": "Misspelled word; 'business' is the correct spelling."
                         },
                         {
                           "id": "err-2",
                           "type": "Spelling",
                           "subType": "Phonetic Error",
                           "incorrectText": "basicly",
                           "correctedText": "basically",
                           "explanation": "The word is spelled phonetically but incorrectly."
                         },
                         {
                           "id": "err-3",
                           "type": "Spelling",
                           "subType": "Typo",
                           "incorrectText": "becuase",
                           "correctedText": "because",
                           "explanation": "Common typo; correct spelling is 'because'."
                         },
                         {
                           "id": "err-4",
                           "type": "Spelling",
                           "subType": "Contraction Error",
                           "incorrectText": "theres",
                           "correctedText": "there's",
                           "explanation": "Missing apostrophe in contraction for 'there is'."
                         },
                         {
                           "id": "err-5",
                           "type": "Spelling",
                           "subType": "Typo",
                           "incorrectText": "pepole",
                           "correctedText": "people",
                           "explanation": "Misspelled version of 'people'."
                         },
                         {
                           "id": "err-6",
                           "type": "Vocabulary",
                           "subType": "Word Choice",
                           "incorrectText": "you can use to buy stuff",
                           "correctedText": "used for purchasing goods",
                           "explanation": "'Stuff' is too vague and informal."
                         },
                         {
                           "id": "err-7",
                           "type": "Vocabulary",
                           "subType": "Word Choice",
                           "incorrectText": "stocks or somthing",
                           "correctedText": "stocks or something similar",
                           "explanation": "'Somthing' is misspelled and too vague."
                         },
                         {
                           "id": "err-8",
                           "type": "Vocabulary",
                           "subType": "Precision",
                           "incorrectText": "poor people",
                           "correctedText": "underbanked individuals",
                           "explanation": "More precise and respectful terminology needed."
                         },
                         {
                           "id": "err-9",
                           "type": "Vocabulary",
                           "subType": "Clarity",
                           "incorrectText": "all that",
                           "correctedText": "climate-related concerns",
                           "explanation": "'All that' lacks specificity in academic writing."
                         },
                         {
                           "id": "err-10",
                           "type": "Vocabulary",
                           "subType": "Register",
                           "incorrectText": "crazy",
                           "correctedText": "highly volatile",
                           "explanation": "Informal slang; use academic terminology instead."
                         },
                         {
                           "id": "err-11",
                           "type": "Grammar",
                           "subType": "Verb Form",
                           "incorrectText": "loosing",
                           "correctedText": "losing",
                           "explanation": "'Loosing' is incorrect; 'losing' is the right form."
                         },
                         {
                           "id": "err-12",
                           "type": "Grammar",
                           "subType": "Subject-Verb Agreement",
                           "incorrectText": "banning it completly",
                           "correctedText": "completely banning it",
                           "explanation": "Incorrect word order and misspelled adverb."
                         },
                         {
                           "id": "err-13",
                           "type": "Grammar",
                           "subType": "Tense",
                           "incorrectText": "is",
                           "correctedText": "has been",
                           "explanation": "Present tense doesn’t match the context of ongoing impact."
                         },
                         {
                           "id": "err-14",
                           "type": "Grammar",
                           "subType": "Countable Noun",
                           "incorrectText": "coin",
                           "correctedText": "cryptocurrency",
                           "explanation": "More appropriate term is needed in this context."
                         },
                         {
                           "id": "err-15",
                           "type": "Grammar",
                           "subType": "Contraction",
                           "incorrectText": "can’t",
                           "correctedText": "cannot",
                           "explanation": "Contractions are inappropriate in formal writing."
                         },
                         {
                           "id": "err-16",
                           "type": "Punctuation",
                           "subType": "Comma Splice",
                           "incorrectText": ",",
                           "correctedText": ".",
                           "explanation": "Comma should be replaced with a period to avoid a run-on sentence."
                         },
                         {
                           "id": "err-17",
                           "type": "Punctuation",
                           "subType": "Missing Apostrophe",
                           "incorrectText": "its",
                           "correctedText": "it's",
                           "explanation": "Missing apostrophe in the contraction for 'it is'."
                         },
                         {
                           "id": "err-18",
                           "type": "Punctuation",
                           "subType": "Quotation Mark",
                           "incorrectText": "take away their power",
                           "correctedText": "\\"take away their power\\"",
                           "explanation": "Direct quotes should be enclosed in quotation marks."
                         },
                         {
                           "id": "err-19",
                           "type": "Punctuation",
                           "subType": "Comma Usage",
                           "incorrectText": "that.",
                           "correctedText": "that,",
                           "explanation": "Improper use of period instead of comma."
                         },
                         {
                           "id": "err-20",
                           "type": "Punctuation",
                           "subType": "Final Punctuation",
                           "incorrectText": "",
                           "correctedText": ".",
                           "explanation": "Missing period at the end of the sentence."
                         },
                         {
                           "id": "err-21",
                           "type": "Style & Tone",
                           "subType": "Informal Language",
                           "incorrectText": "Cryptocurrency is like the new cool thing",
                           "correctedText": "Cryptocurrency is an emerging innovation",
                           "explanation": "Too informal for a formal topic."
                         },
                         {
                           "id": "err-22",
                           "type": "Style & Tone",
                           "subType": "Colloquial Phrase",
                           "incorrectText": "that pie",
                           "correctedText": "those profits",
                           "explanation": "Colloquial expression inappropriate for academic tone."
                         },
                         {
                           "id": "err-23",
                           "type": "Style & Tone",
                           "subType": "Idiomatic Expression",
                           "incorrectText": "peice of that pie",
                           "correctedText": "share in the profits",
                           "explanation": "Idioms should be avoided in formal writing."
                         },
                         {
                           "id": "err-24",
                           "type": "Style & Tone",
                           "subType": "Overgeneralization",
                           "incorrectText": "now everyone wants a peice",
                           "correctedText": "many investors are now interested",
                           "explanation": "'Everyone' is an overgeneralization."
                         },
                         {
                           "id": "err-25",
                           "type": "Style & Tone",
                           "subType": "Slang",
                           "incorrectText": "wild west money situation",
                           "correctedText": "unregulated financial environment",
                           "explanation": "Too informal and vague for academic writing."
                         },
                         {
                           "id": "err-26",
                           "type": "Creativity",
                           "subType": "Cliché",
                           "incorrectText": "take away their power",
                           "correctedText": "disrupt the traditional financial structure",
                           "explanation": "Clichés should be replaced with original, specific language."
                         },
                         {
                           "id": "err-27",
                           "type": "Creativity",
                           "subType": "Overused Phrase",
                           "incorrectText": "global warming",
                           "correctedText": "environmental degradation",
                           "explanation": "'Global warming' is too generic and commonly used."
                         },
                         {
                           "id": "err-28",
                           "type": "Creativity",
                           "subType": "Repetition",
                           "incorrectText": "take away their power",
                           "correctedText": "challenge traditional finance",
                           "explanation": "Avoid repeating basic phrases; use varied language."
                         },
                         {
                           "id": "err-29",
                           "type": "Creativity",
                           "subType": "Imagery",
                           "incorrectText": "takes so much electricity that it can light up a whole city",
                           "correctedText": "consumes large amounts of energy comparable to that of entire cities",
                           "explanation": "Over-simplified and exaggerated imagery."
                         },
                         {
                           "id": "err-30",
                           "type": "Creativity",
                           "subType": "Simplistic Thinking",
                           "incorrectText": "poor people",
                           "correctedText": "economically underserved populations",
                           "explanation": "Oversimplified and lacks nuance."
                         }
                       ]
                 
                    }

                    ## Now process the input below:
                    <Request>
                    {
                      "topic": "%s",
                      "subject": "%s",
                      "points": [%s],
                      "userResponse": "%s"
                    }
                    </Request>
                    """, topic, subject, points, userResponse);
        }

        public static synchronized String getHighLevelEvaluationPrompt(String topic, String subject, String points, String recommendations, String userResponse) {
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
                    Your response must be  a **valid JSON object**,
                    containing all these fields in the JSON [**spelling**, **grammar**, **punctuation**, **vocabulary**, **styleAndTone**, **creativityAndThinking**, **OverallRecommendations**]
                                            
                    You must **NOT generate any explanations, extra text, or summaries outside the JSON.
                                            
                    Ensure the response strictly follows the format below:
                                            
                    {
                      "spelling":{
                        "score": 0-100,
                        "comments": ["...minimum 10 critical comments..."],
                        "alternateSuggestions": ["...minimum 5 suggestions to improve..."]
                      },
                      "grammar":{
                        "score": 0-100,
                        "comments": ["...minimum 10 critical comments..."],
                        "alternateSuggestions": ["...minimum 5 suggestions to improve..."]
                      },
                      "punctuation":{
                        "score": 0-100,
                        "comments": ["...minimum 10 critical comments..."],
                        "alternateSuggestions": ["...minimum 5 suggestions to improve..."]
                      },
                      "vocabulary":{
                        "score": 0-100,
                        "comments": ["...minimum 10 critical comments..."],
                        "alternateSuggestions": ["...minimum 5 suggestions to improve..."]
                      },
                      "styleAndTone":{
                        "score": 0-100,
                        "comments": ["...minimum 10 critical comments..."],
                        "alternateSuggestions": ["...minimum 5 suggestions to improve..."]
                      },
                      "creativityAndThinking":{
                        "score": 0-100,
                        "comments": ["...minimum 10 critical comments..."],
                        "alternateSuggestions": ["...minimum 5 suggestions to improve..."]
                      }
                      "OverallRecommendations":[
                        "Suggest on high-level what to improve like a judge."
                      ]
                    }
                                            
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
