package com.abhi.writewise.inventory.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LLMPromptBuilder {
    public static class WordDefinitionPrompt {
        public static synchronized String prompt(String words) {
            return String.format("""
                    I am working on improving my english vocabulary. For that purpose, I would need the some information about the words given in the array(comma separated and enclosed in square brackets). \n
                    word_array=[%s] \n \n
                    The information of the each word should be a JSON array in the below format. I will explain the meaning of each attribute used in the JSON. \n
                    [
                      {
                        "word": "string",
                        "source": "Llama",
                        "pronunciation": "string",
                        "mnemonic": "string",
                        "localMeaning": "string",
                        "meanings": [
                          {
                            "meaning": "string",
                            "source": "string"
                          }
                        ],
                        "synonyms": [
                          {
                            "synonym": "string",
                            "source": "string"
                          }
                        ],
                        "antonyms": [
                          {
                            "antonym": "string",
                            "source": "string"
                          }
                        ],
                        "examples": [
                          {
                            "example": "string",
                            "source": "string"
                          }
                        ],
                        "partsOfSpeeches": [
                          {
                            "pos": "string",
                            "source": "string"
                          }
                        ],
                        "category": "string"
                      }
                    ]
                                        
                    \n \n The following is the detail of each attribute in the JSON. \n
                    \n Word: it is the input word
                    \n Source: It should always be "Llama" because you are generating it.
                    \n Pronunciation: Add the pronunciation of the word.
                    \n Mnemonic: Add some mnemonic statement that would help to remember the word for ever. The mnemonic should be in the context of it's original definition.If you don't find one, then keep it null.
                    \n localMeaning: Add the hindi meaning of the word, and write in hindi language.
                    \n Meanings: The definition of the word.
                    \n Synonyms: Add at least 5 words that are synonyms.
                    \n Antonyms: Add at least 3 words that are antonyms.
                    \n Examples: This is basically usages, add at least 3 examples usages of the word.
                    \n partsOfSpeeches: Add the Part of speech the word belongs too.
                    \n Category: The category to which the word belongs. For example, whether is a cloth,some sport anything. add some meaningful category to which it belongs.
                                        
                    \n Also, make sure that you generate the response to the prompt enclose in response tag <response>yor JSON response.</response>
                    \n enclosing in response tag is important because that will help me to identify your response.
                                        
                                        
                    \n For example, if the input array is [laity,mincing]
                    \n then the response to this would be like below.
                                      
                    \n <response>                    
                    [
                        {
                            "word": "laity",
                            "source": "Llama",
                            "pronunciation": "LEY-i-tee",
                            "mnemonic": "Remember that 'laity' sounds like 'lay-ity', and lay people are those who are not part of the clergy or professional religious leadership.",
                            "localMeaning": "लेयता: धर्मसंस्थान के पादरी या पेशेवर धार्मिक नेतृत्व का हिस्सा नहीं होने वाले लोग।",
                            "meanings": [
                                {
                                    "meaning": "the people of a religious faith as distinguished from its clergy",
                                    "source": "Llama"
                                }
                            ],
                            "synonyms": [
                                {
                                    "synonym": "congregation",
                                    "source": "Llama"
                                },
                                {
                                    "synonym": "laypeople",
                                    "source": "Llama"
                                },
                                {
                                    "synonym": "parishioners",
                                    "source": "Llama"
                                },
                                {
                                    "synonym": "believers",
                                    "source": "Llama"
                                },
                                {
                                    "synonym": "faithful",
                                    "source": "Llama"
                                }
                            ],
                            "antonyms": [
                                {
                                    "antonym": "clergy",
                                    "source": "Llama"
                                },
                                {
                                    "antonym": "ecclesiastics",
                                    "source": "Llama"
                                },
                                {
                                    "antonym": "ministers",
                                    "source": "Llama"
                                }
                            ],
                            "examples": [
                                {
                                    "example": "The laity actively participates in the church community.",
                                    "source": "Llama"
                                },
                                {
                                    "example": "The sermon was directed towards the laity rather than the clergy.",
                                    "source": "Llama"
                                },
                                {
                                    "example": "The conference addressed issues relevant to both clergy and laity.",
                                    "source": "Llama"
                                }
                            ],
                            "partsOfSpeeches": [
                                {
                                    "pos": "noun",
                                    "source": "Llama"
                                }
                            ],
                            "category": "Religion"
                        },
                        {
                            "word": "mincing",
                            "source": "Llama",
                            "pronunciation": "MIN-sing",
                            "mnemonic": "Imagine someone delicately chopping onions into tiny pieces, as if they're being very careful not to hurt the onions' feelings - that's 'mincing'.",
                            "localMeaning": "कुशलतापूर्वक छोटा करना।",
                            "meanings": [
                                {
                                    "meaning": "affectedly dainty or refined",
                                    "source": "Llama"
                                }
                            ],
                            "synonyms": [
                                {
                                    "synonym": "dainty",
                                    "source": "Llama"
                                },
                                {
                                    "synonym": "delicate",
                                    "source": "Llama"
                                },
                                {
                                    "synonym": "precise",
                                    "source": "Llama"
                                },
                                {
                                    "synonym": "elegant",
                                    "source": "Llama"
                                },
                                {
                                    "synonym": "fussy",
                                    "source": "Llama"
                                }
                            ],
                            "antonyms": [
                                {
                                    "antonym": "rough",
                                    "source": "Llama"
                                },
                                {
                                    "antonym": "crude",
                                    "source": "Llama"
                                },
                                {
                                    "antonym": "bold",
                                    "source": "Llama"
                                }
                            ],
                            "examples": [
                                {
                                    "example": "She spoke in a mincing, affected voice that annoyed everyone.",
                                    "source": "Llama"
                                },
                                {
                                    "example": "He walked with mincing steps, careful not to disturb anything in the room.",
                                    "source": "Llama"
                                },
                                {
                                    "example": "Her mincing gestures made her seem overly prim and proper.",
                                    "source": "Llama"
                                }
                            ],
                            "partsOfSpeeches": [
                                {
                                    "pos": "adjective",
                                    "source": "Llama"
                                }
                            ],
                            "category": "Behavior"
                        }
                    ]
                    </response>                
                    """, words);
        }
    }

    public static class TopicPrompt {
        public static synchronized String prompt(String subject, int numOfTopics, String purpose, Integer wordCount) {

            return String.format("""
                                {
                        "prompt": "Suggest %d topics in the subject of %s that can help improve English language writing skills for the %s.\s
                        The response should be provided in the following JSON format wrapped inside <response></response> tags.\s
                        The JSON example provided below is just a sample for your reference. Please generate new, unique topics and do not copy the content from the example. \n \n
                        Please follow these guidelines for each field in the JSON format: \n \n
                        - "topicNo": This is the number of the topic (e.g., 1, 2, etc.). Datatype is Integer \n
                        - "topic": The title of the economics topic. Datatype is String \n
                        - "subject": The field of the topic (e.g., global economy, monetary policy). Datatype is String \n
                        - "description": A short description explaining how the topic relates to improving English language writing skills for the IELTS exam. Datatype is String\n
                        - "points": A list of important points or arguments related to the topic that the user should focus on in their writing. The writer will write in %d words, so suggest points accordingly.Datatype is array of string. Datatype is array of String\n
                        - "learning": A sentence explaining what kind of knowledge or skills the user will develop by writing about this topic. Datatype is string\n
                        - "recommendations": A list of additional recommendations for improving English writing skills while preparing for the IELTS exam. Datatype is array of string.\n\n
                        The response should look like this (Note: this is just a sample format for reference, do not copy the same content): \n \n
                        <response> \n
                        { \n
                          "topics": [ \n
                            { \n
                              "topicNo": 1, \n
                              "topic": "The Impact of Globalization on Developing Economies", \n
                              "subject": "", \n
                              "description": \n
                                "In this topic, you can discuss the effects of globalization on developing economies. You could argue whether globalization has brought more benefits or drawbacks to these countries. Some possible points to consider include"
                              , \n
                              "points": [ \n
                                "Increased trade and investment opportunities", \n
                                "Job creation and economic growth", \n
                                "Cultural exchange and transfer of knowledge", \n
                                "Dependence on foreign aid and debt", \n
                                "Uneven distribution of wealth and income"
                              ],\n
                              "learning": "This topic allows you to demonstrate your understanding of global economics, international trade, and the challenges faced by developing economies." \n
                            }, \n
                            { \n
                              "topicNo": 2, \n
                              "topic": "The Effectiveness of Monetary Policy in Managing Inflation", \n
                              "subject": "economy", \n
                              "description": "In this topic, you can analyze the role of monetary policy (the actions taken by central banks) in controlling inflation. You could discuss whether monetary policy is an effective tool for managing inflation, considering factors such as", \n
                              "points": [ \n
                                "Interest rates and their impact on borrowing and spending.", \n
                                "Quantitative easing and its effects on the economy.", \n
                                "Inflation targeting and the trade-offs involved.", \n
                                "Alternative approaches to managing inflation, such as fiscal policy."
                              ], \n
                              "learning": "This topic enables you to showcase your knowledge of macroeconomics, monetary policy, and the challenges of managing a country's economy." \n
                            } \n
                          ], \n
                          "recommendations": [ \n
                            "Read widely on the subject to gather information and ideas.", \n
                            "Structure your essay with a clear introduction, body paragraphs, and conclusion.", \n
                            "Use economic terminology accurately and consistently.", \n
                            "Provide specific examples or case studies to support your arguments.", \n
                            "Practice writing under timed conditions to simulate the IELTS exam experience."
                          ] \n
                        } \n</response>"
                    }
                                """, numOfTopics, subject, purpose, wordCount);
        }

    }

}
