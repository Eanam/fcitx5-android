package org.fcitx.fcitx5.android.input.wave.prompt

const val FRAUD_PROMPT_FORMATTED: String = "[场景是老年人即将给别人发送信息，根据老年人输入的信息, 识别老年人是否正在经历诈骗, 是返回百分比, 推测理由, 正确的回复内容3条, 你的回答应该是JSON格式]\n" +
        "\n" +
        "[我将请您先汇款5万元, 麻烦您帮我们加快处理。] \n" +
        "{ \n" +
        "    \"is_scam\":true, \n" +
        "    \"percentage\":\"85.2%\", \n" +
        "    \"reason\":\"要求金额大、缺乏证明和合法安全保障。\", \n" +
        "    \"replies\":[\n" +
        "        \"金额过大我需要再考虑\",\"我需要咨询一下亲人\",\n" +
        "        \"我要报警\",\n" +
        "        \"我想问问其他人\", \n" +
        "        \"我需要法律咨询一下\"\n" +
        "    ] \n" +
        "}\n" +
        "\n" +
        "[用户输入内容]"

val FRAUD_PROMPT = FRAUD_PROMPT_FORMATTED.replace("\n", "") + "\n"

const val ADVICE_FOR_OLD_MAN_PROMPT_FORMATTED: String = "[场景是老年人即将给别人发送信息，根据老年人输入的信息, 帮老年人润色下即将要发送到的信息；你的回答应该是JSON格式]\n" +
        "\n" +
        "[最近在干嘛呢？]\n" +
        "{\n" +
        "    \"advices\": [\"近来可好？\",\"最近再干嘛呢？想你了\",\"生活还如意吗\"]\n" +
        "}\n" +
        "\n" +
        "[用户输入内容]"

val ADVICE_FOR_OLD_MAN_PROMPT = ADVICE_FOR_OLD_MAN_PROMPT_FORMATTED.replace("\n", "") + "\n"