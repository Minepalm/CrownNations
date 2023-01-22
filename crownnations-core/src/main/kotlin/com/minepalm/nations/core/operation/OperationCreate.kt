package com.minepalm.nations.core.operation

import com.minepalm.nations.*

class OperationCreate(
    val name: String,
    private val commander: NationMember,
    val service: NationService
) : AbstractNationOperation<Nation>() {

    val config = service.config.member

    override fun checkOrThrow() {
        val nameExistsFuture = service.nationRegistry.direct.exists(name)

        if (commander.direct.hasNation().join()) {
            fail(ResultCode.ALREADY_HAS_NATION, "해당 플레이어는 이미 국가를 가지고 있습니다.")
        }

        if (nameExistsFuture.join()) {
            fail(ResultCode.ALREADY_NAME_EXIST, "이미 해당 국가명이 존재합니다.")
        }

        if (name.length > config.displayMaxLength) {
            fail(ResultCode.DISPLAY_NAME_TOO_LONG, "생성하려는 국가의 이름이 너무 깁니다.")
        }

        if (!config.regex.matches(name)) {
            fail(ResultCode.INVALID_DISPLAY_NAME, "올바르지 않은 국가 이름입니다.")
        }

        config.bannedKeywords.forEach {
            if (name.contains(it)) {
                set("keyword", it)
                fail(ResultCode.DISPLAY_NAME_BAD_WORD, "국가 이름에 금지된 키워드가 들어가 있습니다. ($it)")
            }
        }

    }

    override fun process0() {

        val nation = service.createNewNation(name)?.apply {
            val success = unsafe.setMemberRank(commander.uniqueId, NationRank.OWNER).join()
        }

        if (nation == null) {
            fail(ResultCode.ALREADY_NAME_EXIST, "이미 해당 국가명이 존재합니다.")
            return
        }

        success(ResultCode.SUCCESSFUL, nation)
    }

}