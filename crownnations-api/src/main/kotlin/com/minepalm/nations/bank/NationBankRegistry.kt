package com.minepalm.nations.bank

interface NationBankRegistry {

    operator fun get(nationId: Int): NationBank
}