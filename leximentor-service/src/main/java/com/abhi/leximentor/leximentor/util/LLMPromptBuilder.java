package com.abhi.leximentor.leximentor.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class LLMPromptBuilder {
    public static class EvaluationModule {
        private static final String DEFAULT_PROMPT = "Consider you are evaluating a student's understanding of vocabulary. Your task is to assess if the student has correctly defined a given word. You will receive the word itself, its part of speech, the official definition, and the student's interpretation. Please provide your evaluation in a JSON format as follows:";
        private static final String NEW_LINE = "\n";
        private static final String FINAL_PROMPT = "Evaluate the student's response based on the provided information. Assign true to isCorrect if the student's definition accurately reflects the meaning of the word. Assign false otherwise. Use the confidence score to indicate how certain you are about your judgment (0 for least confident, 100 for most confident). Provide an explanation to justify your decision.";

        private static class OutputLLM {
            boolean isCorrect = true;
            int confidence = 0;
            String explanation = "Explanation of your decision";

            public boolean isCorrect() {
                return isCorrect;
            }

            public void setCorrect(boolean correct) {
                isCorrect = correct;
            }

            public int getConfidence() {
                return confidence;
            }

            public void setConfidence(int confidence) {
                this.confidence = confidence;
            }

            public String getExplanation() {
                return explanation;
            }

            public void setExplanation(String explanation) {
                this.explanation = explanation;
            }
        }

        private static String json() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            String output = "";
            try {
                OutputLLM outputLLM = new OutputLLM();
                output = objectMapper.writeValueAsString(outputLLM);
            } catch (Exception ex) {
                log.error("Unable to deserialize the JSON");
            }
            return output;
        }

        public static synchronized String getPrompt(String word, String officialMeaning, String candidateResponse) {
            return DEFAULT_PROMPT + json() + NEW_LINE + "Here's the scenario:" + NEW_LINE + "word: " + word + NEW_LINE + "Official Definition:" + officialMeaning + NEW_LINE + "Student's response:" + candidateResponse + NEW_LINE + NEW_LINE + FINAL_PROMPT;
        }

        public static synchronized String getPrompt2(String word, String officialMeaning, String candidateResponse) {
            String prompt = """
                    You are an evaluator assessing whether a student has correctly understood a vocabulary word. \s
                    You will be given: \s
                    - The word \s
                    - The official definition \s
                    - The student's response \s
                                        
                    inside a markup tag below like <input></input> below
                                        
                    Your task is to determine if the student's response captures the same **sense or meaning** as the official definition. \s
                    Do NOT be overly strict with wording or phrasing — if the student's response conveys the same idea, mark it as correct. \s
                    Only mark incorrect if the response significantly changes or distorts the meaning. \s
                                        
                    Return your evaluation in strict JSON format: \s
                    {
                      "confidence": 0-100,\s
                      "explanation": "Clear reasoning for why it is correct or incorrect",
                      "correct": true/false
                    }
                                        
                    ### Notes:
                    - Confidence reflects how sure you are about your judgment. \s
                    - High confidence (80–100) when the meaning clearly matches or clearly does not match. \s
                    - Medium confidence (40–70) when the answer is somewhat ambiguous. \s
                    - Low confidence (0–30) when it's very difficult to decide. \s
                    - Be flexible with synonyms, rephrasings, and variations in style. \s
                    - Focus on the **core sense of meaning**. \s
                                        
                    ### Example Scenario:
                    word: Centrifugal \s
                    Official Definition: Tending to move away from a center \s
                    Student's response: Tendency to move away from the center \s
                                        
                    Expected JSON output: \s
                    {
                      "confidence": 95,
                      "explanation": "The student's response conveys the same meaning as the official definition, only with slightly different wording.",
                      "correct": true
                    }
                    ...........................................................................................................
                    <input>
                    word: %s
                    officialMeaning: %s
                    student's response: %s
                    </input>
                    """;
            return String.format(prompt, word, officialMeaning, candidateResponse);
        }

        public static synchronized String getPromptForSentenceUsage(
                String word,
                String officialMeaning,
                String candidateSentence,
                String exampleUsages) {

            String prompt = """
                    You are a strict but fair vocabulary usage evaluator for language learners.
                    Your task is to judge whether the student's sentence correctly and naturally uses the word
                    according to its official meaning.

                    You will be given:
                    - The target word
                    - The official definition
                    - Some good real-world example usages (important reference!)
                    - The student's usage sentence

                    Evaluation criteria (be reasonably strict):
                    1. Does the sentence use the word with the **correct meaning** according to the official definition?
                    2. Is the word used in a **grammatically correct** way?
                    3. Is the usage **natural** and idiomatic for native speakers?
                    4. Are there any **collocation** errors or clearly unnatural combinations?

                    Return your evaluation in **strict JSON format only**:

                    {
                      "correct": true/false,
                      "confidence": 0-100,
                      "explanation": "clear, specific reasoning (2-4 sentences)",
                    }

                    ### Important guidelines:
                    - Be **lenient** with very minor grammar/spelling mistakes if meaning & usage are clearly correct
                    - Be **strict** when:
                      • Wrong meaning / semantic error
                      • Very unnatural or impossible collocation
                      • Word used with opposite meaning
                    - Use the provided example usages as the gold standard for naturalness and typical contexts
                    - Confidence:
                      • 85–100 = very clear case
                      • 60–84 = mostly correct with small issues
                      • 30–59 = borderline / debatable
                      • 0–29  = clearly wrong

                    ### Examples of evaluation:
                    Good example:
                    Word: reluctant
                    Official meaning: unwilling and hesitant
                    Sentence: She was reluctant to share her real opinion during the meeting.
                    → correct: true, confidence: 94

                    Bad example:
                    Word: reluctant
                    Sentence: He was very reluctant to win the race.
                    → correct: false, confidence: 98 (wrong meaning)

                    <input>
                    word: %s
                    officialMeaning: %s
                    studentSentence: %s
                    exampleSentences: [%s]
                    </input>

                    Respond **only** with valid JSON — no other text.
                    """;

            return String.format(prompt, word, officialMeaning, candidateSentence, exampleUsages);
        }
    }

    public static class WritingModule {
        private static class TopicResponse {
            int topicNo;
            String topic;
            String subject;
            String description;
            List<String> points;
            String learning;
            List<String> recommendations;
        }

        public static synchronized String getTopicsPrompt(String subject, int numOfTopics, String exam) {

            return String.format("""
                                {
                        "prompt": "Suggest %d topics in the subject of %s that can help improve English language writing skills for the %s exam. 
                        The response should be provided in the following JSON format wrapped inside <response></response> tags. 
                        The JSON example provided below is just a sample for your reference. Please generate new, unique topics and do not copy the content from the example.\\n\\n
                        Please follow these guidelines for each field in the JSON format:\\n\\n
                        - \\"topicNo\\": This is the number of the topic (e.g., 1, 2, etc.). Datatype is Integer\\n
                        - \\"topic\\": The title of the economics topic. Datatype is String\\n
                        - \\"subject\\": The field of the topic (e.g., global economy, monetary policy). Datatype is String\\n
                        - \\"description\\": A short description explaining how the topic relates to improving English language writing skills for the IELTS exam. Datatype is String\\n
                        - \\"points\\": A list of important points or arguments related to the topic that the user should focus on in their writing. Datatype is array of string. Datatype is array of String\\n
                        - \\"learning\\": A sentence explaining what kind of knowledge or skills the user will develop by writing about this topic. Datatype is string\\n
                        - \\"recommendations\\": A list of additional recommendations for improving English writing skills while preparing for the IELTS exam. Datatype is array of string.\\n\\n
                        The response should look like this (Note: this is just a sample format for reference, do not copy the same content):\\n\\n
                        <response>\\n
                        {\\n
                          \\"topics\\": [\\n
                            {\\n
                              \\"topicNo\\": 1,\\n
                              \\"topic\\": \\"The Impact of Globalization on Developing Economies\\",\\n
                              \\"subject\\": \\"\\",\\n
                              \\"description\\": \\n
                                \\"In this topic, you can discuss the effects of globalization on developing economies. You could argue whether globalization has brought more benefits or drawbacks to these countries. Some possible points to consider include\\"
                              ,\\n
                              \\"points\\": [\\n
                                \\"Increased trade and investment opportunities\\",\\n
                                \\"Job creation and economic growth\\",\\n
                                \\"Cultural exchange and transfer of knowledge\\",\\n
                                \\"Dependence on foreign aid and debt\\",\\n
                                \\"Uneven distribution of wealth and income\\"
                              ],\\n
                              \\"learning\\": \\"This topic allows you to demonstrate your understanding of global economics, international trade, and the challenges faced by developing economies.\\"\\n
                            },\\n
                            {\\n
                              \\"topicNo\\": 2,\\n
                              \\"topic\\": \\"The Effectiveness of Monetary Policy in Managing Inflation\\",\\n
                              \\"subject\\": \\"economy\\",\\n
                              \\"description\\": \\"In this topic, you can analyze the role of monetary policy (the actions taken by central banks) in controlling inflation. You could discuss whether monetary policy is an effective tool for managing inflation, considering factors such as\\",\\n
                              \\"points\\": [\\n
                                \\"Interest rates and their impact on borrowing and spending.\\",\\n
                                \\"Quantitative easing and its effects on the economy.\\",\\n
                                \\"Inflation targeting and the trade-offs involved.\\",\\n
                                \\"Alternative approaches to managing inflation, such as fiscal policy.\\"
                              ],\\n
                              \\"learning\\": \\"This topic enables you to showcase your knowledge of macroeconomics, monetary policy, and the challenges of managing a country's economy.\\"\\n
                            }\\n
                          ],\\n
                          \\"recommendations\\": [\\n
                            \\"Read widely on the subject to gather information and ideas.\\",\\n
                            \\"Structure your essay with a clear introduction, body paragraphs, and conclusion.\\",\\n
                            \\"Use economic terminology accurately and consistently.\\",\\n
                            \\"Provide specific examples or case studies to support your arguments.\\",\\n
                            \\"Practice writing under timed conditions to simulate the IELTS exam experience.\\"
                          ]\\n
                        }\\n</response>"
                    }
                                """, numOfTopics, subject, exam);
        }

    }

}
