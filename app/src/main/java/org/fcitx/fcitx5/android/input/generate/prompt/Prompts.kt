package org.fcitx.fcitx5.android.input.generate.model

const val FRAUD_PROMPT_FORMATTED: String = "[根据输入识别是否为诈骗信息，是返回百分比和推测理由，你的回答应该是JSON格式]\n" +
        "\n" +
        "[阿里巴巴请您先汇款5万元，以使我们加快处理。]\n" +
        "{\n" +
        "    \"is_scam\":true,\n" +
        "    \"percentage\":\"85.2%\",\n" +
        "    \"reason\":\"要求金额大、缺乏证明和合法安全保障。\",\n" +
        "    \"replies\":[\"阿里巴巴不会以短信形式通知我汇款\",\"金额过大我需要再考虑\",\"我需要咨询一下亲人\",\"我要报警\",\"我想问问其他人\", \"我需要法律咨询一下\"]\n" +
        "}\n" +
        "\n" +
        "[阿里巴巴请您先汇款5万元，以使我们加快处理。]\n" +
        "{\n" +
        "    \"is_scam\":true,\n" +
        "    \"percentage\":\"85.2%\",\n" +
        "    \"reason\":\"要求金额大、缺乏证明和合法安全保障。\",\n" +
        "    \"replies\":[\"阿里巴巴不会以短信形式通知我汇款\",\"金额过大我需要再考虑\",\"我需要咨询一下亲人\",\"我要报警\",\"我想问问其他人\", \"我需要法律咨询一下\"]\n" +
        "}\n"

val FRAUD_PROMPT = FRAUD_PROMPT_FORMATTED.replace("\n", "") + "\n"

const val ADVICE_FOR_OLD_MAN_PROMPT_FORMATTED: String = "[根据输入，为老人返回5个推荐回答，你的回答应该是JSON格式]\n" +
        "\n" +
        "[母亲大人，妇女节快乐]\n" +
        "{\n" +
        "    \"replies\":[\"今天妇女节应该发个红包表示一下吧\",\"你也快乐\",\"最近怎样\",\"今天好忙\",\"老了\",\"想你们了\"]\n" +
        "}\n" +
        "\n" +
        "[母亲大人，妇女节快乐]\n" +
        "{\n" +
        "    \"replies\":[\"今天妇女节应该发个红包表示一下吧\",\"你也快乐\",\"最近怎样\",\"今天好忙\",\"老了\",\"想你们了\"]\n" +
        "}\n"

val ADVICE_FOR_OLD_MAN_PROMPT = ADVICE_FOR_OLD_MAN_PROMPT_FORMATTED.replace("\n", "") + "\n"