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
                    You are an expert English writing coach. Generate exactly %d unique writing topics in the subject of "%s" \
                    to help a student improve their English writing skills for "%s".

                    ## Rules:
                    - Return ONLY the JSON enclosed in <response>...</response> tags. No extra text, no explanations.
                    - Do NOT copy the example content below. Generate completely new, relevant topics.
                    - Each topic must have exactly %d words as the target writing length.
                    - The subject of every topic must be directly related to "%s".

                    ## Required JSON structure:
                    {
                      "topics": [
                        {
                          "topicNo": <integer>,
                          "topic": "<title of the topic>",
                          "subject": "<subject category>",
                          "description": "<1-2 sentences explaining how this topic improves English writing>",
                          "points": [
                            "<key point 1>",
                            "<key point 2>",
                            "<key point 3>"
                          ],
                          "learning": "<what the student will learn by writing about this topic>"
                        }
                      ],
                      "recommendations": [
                        "<general writing tip 1>",
                        "<general writing tip 2>",
                        "<general writing tip 3>"
                      ]
                    }

                    ## Example (structure only — do NOT copy this content):
                    <response>
                    {
                      "topics": [
                        {
                          "topicNo": 1,
                          "topic": "The Role of Artificial Intelligence in Modern Society",
                          "subject": "Technology",
                          "description": "Explores how AI impacts industries, encouraging analytical and structured writing.",
                          "points": [
                            "Definition and brief history of AI",
                            "AI's role in healthcare, finance, and automation",
                            "Ethical concerns and risks of AI"
                          ],
                          "learning": "Writing on this topic builds skills in presenting arguments and using technical vocabulary."
                        }
                      ],
                      "recommendations": [
                        "Use structured arguments with clear introductions and conclusions.",
                        "Practice a variety of sentence structures to enhance fluency.",
                        "Include examples and data to support your claims."
                      ]
                    }
                    </response>
                    """, numOfTopics, subject, purpose, wordCount, subject);
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
                        "alternateSuggestion": ["...minimum 5 suggestions to improve..."]
                      },
                      "grammar":{
                        "score": 0-100,
                        "comments": ["...minimum 10 critical comments..."],
                        "alternateSuggestion": ["...minimum 5 suggestions to improve..."]
                      },
                      "punctuation":{
                        "score": 0-100,
                        "comments": ["...minimum 10 critical comments..."],
                        "alternateSuggestion": ["...minimum 5 suggestions to improve..."]
                      },
                      "vocabulary":{
                        "score": 0-100,
                        "comments": ["...minimum 10 critical comments..."],
                        "alternateSuggestion": ["...minimum 5 suggestions to improve..."]
                      },
                      "styleAndTone":{
                        "score": 0-100,
                        "comments": ["...minimum 10 critical comments..."],
                        "alternateSuggestion": ["...minimum 5 suggestions to improve..."]
                      },
                      "creativityAndThinking":{
                        "score": 0-100,
                        "comments": ["...minimum 10 critical comments..."],
                        "alternateSuggestion": ["...minimum 5 suggestions to improve..."]
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

    public static class AnalyticsPrompt {
        public static synchronized String getInsightPrompt(String analyticsSummary) {
            return String.format("""
                    You are a writing coach. Based on this student's writing performance data, provide concise, actionable feedback.

                    ## Student Performance Summary:
                    %s

                    ## Your response must include exactly these 4 sections:

                    ### 1. Root Causes (2-3 sentences)
                    Explain WHY the student makes their most common mistakes.

                    ### 2. Priority Focus Areas
                    List the top 3 specific things to improve, ordered by impact.

                    ### 3. Practice Exercises
                    Give 2-3 concrete, specific exercises to address the weaknesses.

                    ### 4. Encouragement
                    One sentence acknowledging their strengths and overall progress.

                    Be specific, concise, and encouraging. No generic advice.
                    """, analyticsSummary);
        }
    }
}
