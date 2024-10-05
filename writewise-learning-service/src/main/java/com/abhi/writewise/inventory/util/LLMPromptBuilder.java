package com.abhi.writewise.inventory.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LLMPromptBuilder {
    public static class TopicPrompt {
        public static synchronized String prompt(String subject, int numOfTopics, String exam) {

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
