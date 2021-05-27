package my.demo.currencyservice.domain

data class CurrencyImp(
    override val code: String,
    override val name: String,
    override val isActive: Boolean,
    override val isDisabledManually: Boolean
): Currency {
    override fun getCopy(code: String, name: String, isActive: Boolean, isDisabledManually: Boolean): Currency {
        return this.copy(code, name, isActive, isDisabledManually)
    }
}