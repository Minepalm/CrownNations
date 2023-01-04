package kr.rendog.nations.bank

interface NationBankRegistry {

    operator fun get(nationId : Int) : NationBank
}